package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(UUID id) {
        super("Could not find portfolio " + id);
    }
}