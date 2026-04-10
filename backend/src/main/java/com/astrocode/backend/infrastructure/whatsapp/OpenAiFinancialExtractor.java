package com.astrocode.backend.infrastructure.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Fallback com OpenAI quando o extrator manual tem baixa confiança.
 */
@Component
public class OpenAiFinancialExtractor {

    private static final Logger log = LoggerFactory.getLogger(OpenAiFinancialExtractor.class);

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiFinancialExtractor(
            @Value("${openai.api-key:}") String apiKey,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    /**
     * Chama GPT-4o-mini e devolve JSON parseado como {@link FinancialMessageExtractor.Extracted} ou null.
     */
    public FinancialMessageExtractor.Extracted extract(String message) {
        if (!isConfigured()) {
            return null;
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "temperature", 0,
                    "max_tokens", 200,
                    "messages", List.of(
                            Map.of("role", "system", "content", """
                                    Você é um extrator de dados financeiros. Analise a mensagem e retorne APENAS um JSON válido com os campos:
                                    tipo (receita|despesa|null), valor (number|null), categoria (string|null), intent (transacao|resumo_mensal|consulta_saldo|desconhecido).
                                    Sem texto fora do JSON."""),
                            Map.of("role", "user", "content", message)
                    )
            );
            String raw = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            JsonNode json = objectMapper.readTree(content.trim());
            String tipo = json.has("tipo") && !json.get("tipo").isNull() ? json.get("tipo").asText() : null;
            BigDecimal valor = null;
            if (json.has("valor") && !json.get("valor").isNull()) {
                var v = json.get("valor");
                valor = v.isNumber() ? v.decimalValue() : new BigDecimal(v.asText());
            }
            String cat = json.has("categoria") && !json.get("categoria").isNull() ? json.get("categoria").asText() : null;
            String intent = json.has("intent") ? json.get("intent").asText("desconhecido") : "desconhecido";
            return new FinancialMessageExtractor.Extracted(tipo, valor, cat, intent, 0.75);
        } catch (Exception e) {
            log.warn("[OPENAI] Extração falhou: {}", e.getMessage());
            return null;
        }
    }
}
