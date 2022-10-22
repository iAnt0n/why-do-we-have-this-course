package ru.itmo.lab2.market_instrument_id_service.controller.exception;

import java.util.UUID;

public class InstrumentNotFoundException extends RuntimeException {
    public InstrumentNotFoundException(UUID id) {
        super("Could not find instrument " + id);
    }
}