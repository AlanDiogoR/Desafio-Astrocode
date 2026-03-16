package com.astrocode.backend.domain.exceptions;

/**
 * Exceção lançada quando a origem da transação é inválida:
 * - Deve ser exatamente uma: conta bancária OU cartão de crédito
 * - Não pode ter ambas preenchidas ou nenhuma preenchida
 */
public class InvalidTransactionSourceException extends RuntimeException {

    public InvalidTransactionSourceException() {
        super("A transação deve ter exatamente uma origem: conta bancária (débito) ou cartão de crédito (crédito). Informe bankAccountId ou creditCardId, mas não ambos.");
    }

    public InvalidTransactionSourceException(String message) {
        super(message);
    }
}
