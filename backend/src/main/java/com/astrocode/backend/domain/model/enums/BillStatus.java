package com.astrocode.backend.domain.model.enums;

/**
 * Status de uma fatura de cartão de crédito.
 * OPEN: fatura do mês atual em aberto
 * CLOSED: fechou, aguardando pagamento
 * PAID: paga
 */
public enum BillStatus {
    OPEN,
    CLOSED,
    PAID
}
