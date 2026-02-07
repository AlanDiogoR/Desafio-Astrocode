package com.astrocode.backend.domain.exceptions;

public class AccountNotOwnedException extends RuntimeException {

    public AccountNotOwnedException(String message) {
        super(message);
    }

    public AccountNotOwnedException(String message, Throwable cause) {
        super(message, cause);
    }
}
