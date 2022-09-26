package com.calilog.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends CalilogException {
    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequestException() {
        super(MESSAGE);
    }

    public InvalidRequestException(Throwable cause) {
        super(MESSAGE, cause);
    }

    public InvalidRequestException(String fieldName, String message) {
        super(MESSAGE);
        addErrors(fieldName, message);
    }

    @Override
    public String getStatusCode() {
        return "400";
    }
}
