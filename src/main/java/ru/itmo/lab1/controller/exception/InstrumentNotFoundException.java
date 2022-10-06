package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class InstrumentNotFoundException extends RuntimeException {
    public InstrumentNotFoundException(UUID id) {
        super("Could not find instrument " + id);
    }
}