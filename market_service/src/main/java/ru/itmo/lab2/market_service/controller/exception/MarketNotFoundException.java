package ru.itmo.lab2.market_service.controller.exception;

import java.util.UUID;

public class MarketNotFoundException extends RuntimeException {
    public MarketNotFoundException(UUID id) {
        super("Could not find market " + id);
    }
}
