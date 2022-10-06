package ru.itmo.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.Market;

import java.util.UUID;

public interface MarketRepository extends JpaRepository<Market, UUID> {
}
