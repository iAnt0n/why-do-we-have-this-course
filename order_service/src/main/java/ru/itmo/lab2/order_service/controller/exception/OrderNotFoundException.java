package ru.itmo.lab2.order_service.controller.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID id) {
        super("Could not find order " + id);
    }
}