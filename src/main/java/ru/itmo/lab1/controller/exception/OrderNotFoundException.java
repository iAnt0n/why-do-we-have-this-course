package ru.itmo.lab1.controller.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID id) {
        super("Could not find order " + id);
    }
}