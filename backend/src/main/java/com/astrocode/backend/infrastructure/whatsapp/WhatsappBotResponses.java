package com.astrocode.backend.infrastructure.whatsapp;

import com.astrocode.backend.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Mensagens de resposta do bot (formato WhatsApp).
 */
public final class WhatsappBotResponses {

    private WhatsappBotResponses() {
    }

    public static String transacaoRegistrada(TransactionType tipo, BigDecimal valor, String categoria) {
        String t = tipo == TransactionType.EXPENSE ? "Despesa" : "Receita";
        String v = valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
        return "✅ Anotado! " + t + " de R$ " + v + " em *" + categoria + "* registrada com sucesso. Acesse grivy.netlify.app para ver o detalhamento.";
    }

    public static String resumoMensal(String mes, BigDecimal despesas, BigDecimal receitas, BigDecimal saldoContas) {
        DateTimeFormatter pt = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR"));
        String label = mes != null ? mes : LocalDate.now().format(pt);
        String d = despesas.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
        String r = receitas.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
        String s = saldoContas.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
        return "📊 *Seu resumo de " + label + ":*\n\n"
                + "💸 Despesas: R$ " + d + "\n"
                + "💰 Receitas: R$ " + r + "\n"
                + "📈 Saldo em contas: R$ " + s + "\n\n"
                + "Para mais detalhes, acesse grivy.netlify.app";
    }

    public static String consultaSaldo(BigDecimal saldo) {
        String s = saldo.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
        return "💰 Seu saldo atual nas contas é de *R$ " + s + "*";
    }

    public static String naoEntendido() {
        return """
                🤔 Não entendi muito bem. Você pode me dizer coisas como:
                • "Gastei R$ 50 no mercado"
                • "Recebi R$ 3000 de salário"
                • "Me manda um resumo do mês"
                • "Qual meu saldo?"
                """;
    }

    public static String usuarioNaoEncontrado() {
        return "👋 Olá! Para usar o Grivy pelo WhatsApp, acesse *grivy.netlify.app*, crie sua conta e vincule seu número nas configurações (editar perfil).";
    }
}
