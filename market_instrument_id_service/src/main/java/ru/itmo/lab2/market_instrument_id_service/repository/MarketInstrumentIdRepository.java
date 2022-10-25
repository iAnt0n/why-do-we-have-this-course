package ru.itmo.lab2.market_instrument_id_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.itmo.lab2.market_instrument_id_service.model.MarketInstrumentId;

import java.util.UUID;

//@Repository
public interface MarketInstrumentIdRepository extends ReactiveCrudRepository<MarketInstrumentId, UUID> {
    Flux<MarketInstrumentId> findAllBy(Pageable pageable);
}