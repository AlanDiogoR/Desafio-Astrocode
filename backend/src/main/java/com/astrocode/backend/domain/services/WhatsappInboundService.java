package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.entities.WhatsappMessage;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.astrocode.backend.domain.repositories.WhatsappMessageRepository;
import com.astrocode.backend.infrastructure.whatsapp.FinancialMessageExtractor;
import com.astrocode.backend.infrastructure.whatsapp.OpenAiFinancialExtractor;
import com.astrocode.backend.infrastructure.whatsapp.WhatsappBotResponses;
import com.astrocode.backend.infrastructure.whatsapp.WhatsappGraphClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Processa webhooks da WhatsApp Cloud API: extrai intenção, persiste e responde ao usuário.
 */
@Service
public class WhatsappInboundService {

    private static final Logger log = LoggerFactory.getLogger(WhatsappInboundService.class);

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final WhatsappMessageRepository whatsappMessageRepository;
    private final TransactionService transactionService;
    private final DashboardService dashboardService;
    private final CategoryRepository categoryRepository;
    private final WhatsappGraphClient graphClient;
    private final OpenAiFinancialExtractor openAiFinancialExtractor;

    public WhatsappInboundService(
            ObjectMapper objectMapper,
            UserRepository userRepository,
            WhatsappMessageRepository whatsappMessageRepository,
            TransactionService transactionService,
            DashboardService dashboardService,
            CategoryRepository categoryRepository,
            WhatsappGraphClient graphClient,
            OpenAiFinancialExtractor openAiFinancialExtractor) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.whatsappMessageRepository = whatsappMessageRepository;
        this.transactionService = transactionService;
        this.dashboardService = dashboardService;
        this.categoryRepository = categoryRepository;
        this.graphClient = graphClient;
        this.openAiFinancialExtractor = openAiFinancialExtractor;
    }

    @Transactional
    public void processWebhookJson(String jsonBody) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            for (JsonNode entry : root.path("entry")) {
                for (JsonNode change : entry.path("changes")) {
                    JsonNode messages = change.path("value").path("messages");
                    if (messages.isArray()) {
                        for (JsonNode msg : messages) {
                            String from = digitsOnly(msg.path("from").asText());
                            String text = msg.path("text").path("body").asText("");
                            handleIncoming(from, text);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[WHATSAPP] Parse webhook: {}", e.getMessage());
        }
    }

    private void handleIncoming(String phoneDigits, String text) {
        WhatsappMessage logRow = WhatsappMessage.builder()
                .phone(phoneDigits)
                .rawMessage(text)
                .status("pending")
                .build();

        Optional<User> userOpt = userRepository.findByWhatsappPhone(phoneDigits)
                .filter(User::isWhatsappVerified);

        FinancialMessageExtractor.Extracted ext = FinancialMessageExtractor.parseManually(text);
        if (ext.confidence() < 0.8 && openAiFinancialExtractor.isConfigured()) {
            FinancialMessageExtractor.Extracted ai = openAiFinancialExtractor.extract(text);
            if (ai != null) {
                ext = ai;
            }
        }

        try {
            logRow.setExtractedDataJson(objectMapper.writeValueAsString(Map.of(
                    "tipo", ext.tipo() != null ? ext.tipo() : "",
                    "intent", ext.intent() != null ? ext.intent() : "",
                    "confidence", ext.confidence()
            )));
        } catch (Exception ignored) {
        }

        if (userOpt.isEmpty()) {
            logRow.setStatus("processed");
            whatsappMessageRepository.save(logRow);
            graphClient.sendTextMessage(phoneDigits, WhatsappBotResponses.usuarioNaoEncontrado());
            return;
        }

        User user = userOpt.get();
        logRow.setUser(user);

        String reply;
        try {
            reply = buildReply(user, ext, text);
            logRow.setStatus("processed");
        } catch (Exception e) {
            log.warn("[WHATSAPP] Erro ao processar: {}", e.getMessage());
            logRow.setStatus("failed");
            reply = WhatsappBotResponses.naoEntendido();
        }

        whatsappMessageRepository.save(logRow);
        graphClient.sendTextMessage(phoneDigits, reply);
    }

    private String buildReply(User user, FinancialMessageExtractor.Extracted ext, String raw) {
        String intent = ext.intent() != null ? ext.intent() : "desconhecido";

        if ("resumo_mensal".equals(intent)) {
            var dash = dashboardService.getDashboardData(user.getId());
            String mes = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR")));
            return WhatsappBotResponses.resumoMensal(mes, dash.totalExpenseMonth(), dash.totalIncomeMonth(), dash.totalBalance());
        }

        if ("consulta_saldo".equals(intent)) {
            var dash = dashboardService.getDashboardData(user.getId());
            return WhatsappBotResponses.consultaSaldo(dash.totalBalance());
        }

        if (ext.valor() != null && ext.tipo() != null
                && !"resumo_mensal".equals(intent) && !"consulta_saldo".equals(intent)) {
            TransactionType type = "despesa".equalsIgnoreCase(ext.tipo()) ? TransactionType.EXPENSE : TransactionType.INCOME;
            String catName = ext.categoriaGuess() != null ? ext.categoriaGuess() : "Outro";
            var cat = categoryRepository.findByUserIdAndTypeAndNameIgnoreCase(user.getId(), type, catName)
                    .or(() -> categoryRepository.findByUserIdAndTypeAndNameIgnoreCase(user.getId(), type, "Outro"))
                    .orElseThrow(() -> new IllegalStateException("Categoria não encontrada"));
            transactionService.createFromWhatsapp(user, ext.valor(), type, cat.getId(),
                    firstWords(raw, 40));
            return WhatsappBotResponses.transacaoRegistrada(type, ext.valor(), cat.getName());
        }

        return WhatsappBotResponses.naoEntendido();
    }

    private static String firstWords(String s, int max) {
        if (s == null) return "WhatsApp";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }

    private static String digitsOnly(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("\\D", "");
    }
}
