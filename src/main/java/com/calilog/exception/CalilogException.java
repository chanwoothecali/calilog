package com.calilog.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class CalilogException extends RuntimeException {

    private final Map<String, String> errors = new HashMap<>();

    public CalilogException(String message) {
        super(message);
    }

    public CalilogException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getStatusCode();

    public void addErrors(String fieldName, String message) {
        errors.put(fieldName, message);
    }
}
