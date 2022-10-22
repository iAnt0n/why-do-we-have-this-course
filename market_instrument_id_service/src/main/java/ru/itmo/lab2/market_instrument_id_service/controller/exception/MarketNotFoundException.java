package ru.itmo.lab2.market_instrument_id_service.controller.exception;

import java.util.UUID;

public class MarketNotFoundException extends RuntimeException {
    public MarketNotFoundException(UUID id) {
        super("Could not find market " + id);
    }
}
