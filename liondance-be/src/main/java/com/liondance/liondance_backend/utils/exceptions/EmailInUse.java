package com.liondance.liondance_backend.utils.exceptions;

public class EmailInUse extends RuntimeException {
    public EmailInUse() {
    }

    public EmailInUse(String message) {
        super(message);
    }

    public EmailInUse(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailInUse(Throwable cause) {
        super(cause);
    }
}
