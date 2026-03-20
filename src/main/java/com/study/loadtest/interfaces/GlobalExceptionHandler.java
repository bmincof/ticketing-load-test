package com.study.loadtest.interfaces;

import com.study.loadtest.domain.event.exception.SoldOutException;
import com.study.loadtest.shared.exception.InvalidStateException;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoSuchEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(NoSuchEntityException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(SoldOutException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSoldOut(SoldOutException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidState(InvalidStateException e) {
        return new ErrorResponse(e.getMessage());
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {

        private String message;
    }
}
