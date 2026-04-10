package com.astrocode.backend.infrastructure.whatsapp;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FinancialMessageExtractorTest {

    @Test
    void extraiDespesaSimples() {
        var e = FinancialMessageExtractor.parseManually("gastei 50 no mercado");
        assertEquals("despesa", e.tipo());
        assertEquals(new BigDecimal("50"), e.valor());
        assertEquals(FinancialMessageExtractor.INTENT_TRANSACAO, e.intent());
    }

    @Test
    void reconheceResumoMensal() {
        assertEquals("resumo_mensal", FinancialMessageExtractor.parseManually("me manda um resumo do mês").intent());
    }

    @Test
    void extraiReceita() {
        var e = FinancialMessageExtractor.parseManually("recebi 3000 de salário");
        assertEquals("receita", e.tipo());
        assertEquals(new BigDecimal("3000"), e.valor());
    }

    @Test
    void reconheceConsultaSaldo() {
        var e = FinancialMessageExtractor.parseManually("qual meu saldo?");
        assertEquals("consulta_saldo", e.intent());
    }
}
