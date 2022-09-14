package ru.itmo.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.Trade;

import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {
}