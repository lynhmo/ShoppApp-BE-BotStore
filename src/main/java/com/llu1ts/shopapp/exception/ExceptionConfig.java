package com.llu1ts.shopapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionConfig {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataNotFoundExveption(DataNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMsg(e.getMessage());
        errorResponse.setErrorCode("1");
        return errorResponse;
    }
}
