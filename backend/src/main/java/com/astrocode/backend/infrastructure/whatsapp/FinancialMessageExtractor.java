package com.astrocode.backend.infrastructure.whatsapp;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrator manual (regex) de intenções financeiras a partir de texto livre em português.
 */
public final class FinancialMessageExtractor {

    /** Intent padronizado para transações detectadas manualmente. */
    public static final String INTENT_TRANSACAO = "transacao";

    private FinancialMessageExtractor() {
    }

    public record Extracted(
            String tipo,
            BigDecimal valor,
            String categoriaGuess,
            String intent,
            double confidence
    ) {
    }

    private static final Pattern MONEY = Pattern.compile(
            "(\\d+[.,]?\\d*)\\s*(reais?|real|r\\$|rs\\.?)?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern GASTEI = Pattern.compile(
            "gastei|paguei|dei\\s+dinheiro|comprei",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern RECEBI = Pattern.compile(
            "recebi|ganhei|entrou|sal[áa]rio",
            Pattern.CASE_INSENSITIVE);

    /**
     * Tenta extrair dados com confiança entre 0 e 1.
     */
    public static Extracted parseManually(String message) {
        if (message == null || message.isBlank()) {
            return new Extracted(null, null, null, "desconhecido", 0);
        }
        String m = message.trim();
        String lower = m.toLowerCase(Locale.ROOT);

        if (lower.contains("resumo") && (lower.contains("mês") || lower.contains("mes") || lower.contains("mensal"))) {
            return new Extracted(null, null, null, "resumo_mensal", 0.95);
        }
        if (lower.contains("saldo") || lower.matches(".*qual\\s+.*\\s+saldo.*")) {
            return new Extracted(null, null, null, "consulta_saldo", 0.9);
        }

        boolean expense = GASTEI.matcher(lower).find();
        boolean income = RECEBI.matcher(lower).find();
        if (!expense && !income) {
            return new Extracted(null, null, null, "desconhecido", 0.2);
        }

        Matcher num = MONEY.matcher(m.replace("R$", "").replace("r$", ""));
        BigDecimal val = null;
        if (num.find()) {
            String g = num.group(1).replace(".", "").replace(",", ".");
            try {
                val = new BigDecimal(g);
            } catch (Exception ignored) {
            }
        }
        if (val == null) {
            return new Extracted(null, null, null, "desconhecido", 0.35);
        }

        String cat = guessCategory(lower);
        String tipo = expense ? "despesa" : "receita";
        return new Extracted(tipo, val, cat, INTENT_TRANSACAO, 0.88);
    }

    private static String guessCategory(String lower) {
        if (lower.contains("mercado") || lower.contains("supermercado")) return "Mercado";
        if (lower.contains("uber") || lower.contains("transporte") || lower.contains("ônibus") || lower.contains("onibus"))
            return "Transporte";
        if (lower.contains("salário") || lower.contains("salario")) return "Salário";
        if (lower.contains("freela") || lower.contains("freelance")) return "Freelance";
        if (lower.contains("aliment") || lower.contains("restaurante") || lower.contains("lanche")) return "Alimentação";
        return "Outro";
    }
}
