package com.astrocode.backend.domain.exceptions;

public class InvalidResetCodeException extends RuntimeException {

    public InvalidResetCodeException() {
        super("Código inválido ou expirado");
    }

    public InvalidResetCodeException(String message) {
        super(message);
    }
}
