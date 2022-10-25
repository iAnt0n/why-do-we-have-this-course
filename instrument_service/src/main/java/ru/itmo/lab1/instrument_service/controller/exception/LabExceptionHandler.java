package ru.itmo.lab1.instrument_service.controller.exception;

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
    @ResponseStatus(HttpStatus.CONFLICT)
    public void runtimeException(Exception ex) {}

    @ResponseBody
    @ExceptionHandler({
            InstrumentNotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String objectNotFoundException(Exception ex) {
        return ex.getMessage();
    }
}
