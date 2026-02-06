package com.astrocode.backend.domain.exceptions;

public class ResourceAccessDeniedException extends RuntimeException {

    public ResourceAccessDeniedException(String message) {
        super(message);
    }

    public ResourceAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
