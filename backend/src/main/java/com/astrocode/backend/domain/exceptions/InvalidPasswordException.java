package com.astrocode.backend.domain.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("Senha atual incorreta");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
