package ru.itmo.lab2.market_instrument_id_service.controller.exception;

import java.util.UUID;

public class MarketInstrumentIdNotFoundException extends RuntimeException {
    public MarketInstrumentIdNotFoundException(UUID id) {
        super("Could not find market instrument identifier " + id);
    }
}