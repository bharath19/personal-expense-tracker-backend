package com.expense.tracker.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
