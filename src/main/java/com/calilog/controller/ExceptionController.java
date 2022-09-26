package com.calilog.controller;

import com.calilog.exception.CalilogException;
import com.calilog.exception.InvalidRequestException;
import com.calilog.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        List<FieldError> fieldErrors = e.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            errors.put(field, message);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .errors(errors)
                .build();

        return errorResponse;
    }

    @ExceptionHandler(CalilogException.class)
    public ResponseEntity<ErrorResponse> postNotFoundExceptionHandler(CalilogException e) {
        String statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(statusCode)
                .message(e.getMessage())
                .errors(e.getErrors())
                .build();

        ResponseEntity<ErrorResponse> errorResponse = ResponseEntity.status(Integer.parseInt(statusCode))
                .body(body);

        return errorResponse;
    }
}
