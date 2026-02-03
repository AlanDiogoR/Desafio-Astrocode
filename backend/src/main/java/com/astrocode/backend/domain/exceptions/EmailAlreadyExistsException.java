package com.astrocode.backend.domain.exceptions;

/**
 * Exceção lançada quando tenta-se cadastrar um usuário com um email que já existe no sistema.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email já cadastrado: " + email);
    }

    public EmailAlreadyExistsException(String email, Throwable cause) {
        super("Email já cadastrado: " + email, cause);
    }
}
