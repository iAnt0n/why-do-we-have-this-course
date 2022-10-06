package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class MarketNotFoundException extends RuntimeException {
    public MarketNotFoundException(UUID id) {
        super("Could not find market " + id);
    }
}
