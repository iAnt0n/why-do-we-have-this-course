package ru.itmo.lab2.trade_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab2.trade_service.model.Trade;

import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {
    Page<Trade> findAllByIdUser(UUID id, Pageable pageable);
}