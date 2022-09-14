package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class TradeManualCreationException extends RuntimeException {
    public TradeManualCreationException() {
        super("Could not create trade with order id or portfolio id already set");
    }
}