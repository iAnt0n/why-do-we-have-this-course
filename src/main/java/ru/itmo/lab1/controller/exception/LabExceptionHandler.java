package ru.itmo.lab1.controller.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class LabExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void runtimeException(Exception ex) {}

    @ResponseBody
    @ExceptionHandler(MarketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String marketNotFoundException(Exception ex) {
        return ex.getMessage();
    }
    //TODO add other exception handlers
}
