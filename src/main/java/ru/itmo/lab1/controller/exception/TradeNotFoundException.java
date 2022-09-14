package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class TradeNotFoundException extends RuntimeException {
    public TradeNotFoundException(UUID id) {
        super("Could not find trade " + id);
    }
}