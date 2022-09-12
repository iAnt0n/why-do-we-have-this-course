package ru.itmo.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}