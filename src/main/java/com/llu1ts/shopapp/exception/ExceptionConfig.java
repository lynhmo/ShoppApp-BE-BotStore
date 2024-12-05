package com.llu1ts.shopapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class ExceptionConfig {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataNotFoundException(DataNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMsg(e.getMessage());
        errorResponse.setErrorCode("1");
        return errorResponse;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMsg(e.getMessage());
        errorResponse.setErrorCode("2");
        e.printStackTrace();
        return errorResponse;
    }

    @ExceptionHandler(ExceedDataException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExceedDataException(ExceedDataException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMsg(e.getMessage());
        errorResponse.setErrorCode("3");
        return errorResponse;
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIOException(IOException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMsg(e.getMessage());
        errorResponse.setErrorCode("4");
        return errorResponse;
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthorizationException(AuthorizationException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMsg(e.getMessage());
        errorResponse.setErrorCode("5");
        return errorResponse;
    }
}
