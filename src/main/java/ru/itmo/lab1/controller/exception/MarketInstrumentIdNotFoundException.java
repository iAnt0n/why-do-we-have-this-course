package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class MarketInstrumentIdNotFoundException extends RuntimeException {
    public MarketInstrumentIdNotFoundException(UUID id) {
        super("Could not find market instrument identifier " + id);
    }
}