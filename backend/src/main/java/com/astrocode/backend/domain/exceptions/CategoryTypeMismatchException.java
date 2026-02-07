package com.astrocode.backend.domain.exceptions;

public class CategoryTypeMismatchException extends RuntimeException {

    public CategoryTypeMismatchException(String message) {
        super(message);
    }

    public CategoryTypeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
