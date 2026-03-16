package com.astrocode.backend.domain.exceptions;

/**
 * Exceção lançada quando tenta-se criar ou atualizar um cartão de crédito com um nome que já existe para o usuário.
 */
public class DuplicateCreditCardNameException extends RuntimeException {

    public DuplicateCreditCardNameException(String name) {
        super("Já existe um cartão de crédito com o nome \"" + name + "\". Escolha outro nome.");
    }

    public DuplicateCreditCardNameException(String name, Throwable cause) {
        super("Já existe um cartão de crédito com o nome \"" + name + "\". Escolha outro nome.", cause);
    }
}
