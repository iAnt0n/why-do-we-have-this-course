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
    @ResponseStatus(HttpStatus.CONFLICT)
    public void runtimeException(Exception ex) {}

    @ResponseBody
    @ExceptionHandler({
            OrderStatusConflictException.class,
            TradeManualCreationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflictExceptionWithMessage(Exception ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler({
            MarketNotFoundException.class,
            InstrumentNotFoundException.class,
            MarketInstrumentIdNotFoundException.class,
            OrderNotFoundException.class,
            TradeNotFoundException.class,
            PortfolioNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String objectNotFoundException(Exception ex) {
        return ex.getMessage();
    }
    //TODO add other exception handlers
}
