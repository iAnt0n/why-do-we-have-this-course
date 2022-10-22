package ru.itmo.lab1.instrument_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.instrument_service.model.Instrument;

import java.util.UUID;

public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {
}