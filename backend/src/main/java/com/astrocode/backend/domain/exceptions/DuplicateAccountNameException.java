package com.astrocode.backend.domain.exceptions;

/**
 * Exceção lançada quando tenta-se criar ou atualizar uma conta com um nome que já existe para o usuário.
 */
public class DuplicateAccountNameException extends RuntimeException {

    public DuplicateAccountNameException(String name) {
        super("Já existe uma conta com o nome \"" + name + "\". Escolha outro nome.");
    }

    public DuplicateAccountNameException(String name, Throwable cause) {
        super("Já existe uma conta com o nome \"" + name + "\". Escolha outro nome.", cause);
    }
}
