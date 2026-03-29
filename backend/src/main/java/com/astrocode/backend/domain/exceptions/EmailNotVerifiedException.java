package com.astrocode.backend.domain.exceptions;

public class EmailNotVerifiedException extends RuntimeException {

    public EmailNotVerifiedException() {
        super("Confirme seu email antes de fazer login.");
    }
}
