package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {
    private HttpErrorInfo createHttpErrorInfo(HttpStatus status, ServerHttpRequest request, Exception ex){
        final String path = request.getPath().value();
        final String msg = ex.getMessage();
        log.debug("Returning HTTP Status: {} for path {} with message {}", status, path, msg);
        return new HttpErrorInfo(status, path, msg);
    }

    private HttpErrorInfo createHttpErrorInfo(HttpStatus status, ServerHttpRequest request, String msg, List<ObjectError> errors){
        final String path = request.getPath().value();
        final List<String> errorsList = errors.stream().map(ObjectError::getDefaultMessage).toList();
        log.debug("Returning HTTP Status: {} for path {} with message {}", status, path, msg);
        return new HttpErrorInfo(status, path, msg, errorsList);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(WebExchangeBindException.class)
    public HttpErrorInfo handleMethodArgumentNotValidException(ServerHttpRequest request, WebExchangeBindException ex){

        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, "Some required fields did not pass validation", ex.getAllErrors());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public HttpErrorInfo handleNotFoundException(ServerHttpRequest request, Exception ex){
        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, Exception ex){
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }

}
