package com.calilog.exception;

public class PostNotFoundException extends CalilogException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    public PostNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public String getStatusCode() {
        return "404";
    }
}
