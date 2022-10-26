package ru.itmo.lab2.trade_service.controller.exception;

public class TradeManualCreationException extends RuntimeException {
    public TradeManualCreationException() {
        super("Could not create trade with order id or portfolio id already set");
    }
}