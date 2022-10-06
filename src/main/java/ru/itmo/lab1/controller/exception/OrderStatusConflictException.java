package ru.itmo.lab1.controller.exception;

import ru.itmo.lab1.model.enums.OrderStatus;

import java.util.Optional;
import java.util.UUID;

public class OrderStatusConflictException extends RuntimeException {
    public OrderStatusConflictException(UUID id, Optional<OrderStatus> cur, OrderStatus next) {
        super("Could not change order " + id + " status to: " + next.toString() + ". Reason: current status is " + cur.map(val -> val.toString()).orElse("new"));
    }
}