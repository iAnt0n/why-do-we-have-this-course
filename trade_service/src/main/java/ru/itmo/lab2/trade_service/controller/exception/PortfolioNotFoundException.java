package ru.itmo.lab2.trade_service.controller.exception;

import java.util.UUID;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(UUID id) {
        super("Could not find portfolio " + id);
    }
}