package com.liondance.liondance_backend.utils.exceptions;

public class StudentNotPending extends RuntimeException {
    public StudentNotPending() {
    }

    public StudentNotPending(String message) {
        super(message);
    }

    public StudentNotPending(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentNotPending(Throwable cause) {
        super(cause);
    }
}
