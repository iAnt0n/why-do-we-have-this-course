package ru.itmo.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.MarketInstrumentId;

import java.util.UUID;

public interface MarketInstrumentIdRepository extends JpaRepository<MarketInstrumentId, UUID> {
}