package com.liondance.liondance_backend.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HttpErrorInfo {
    private final String timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;
    private final List<String> errors;

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this.timestamp = LocalDateTime.now().toString();
        this.httpStatus = httpStatus;
        this.message = message;
        this.path = path;
        this.errors = new ArrayList<>();
    }

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message, List<String> errors) {
        this.timestamp = LocalDateTime.now().toString();
        this.httpStatus = httpStatus;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }
}
