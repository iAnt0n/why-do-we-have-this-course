package ru.itmo.lab2.market_instrument_id_service.client;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentDto;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentPatchDto;

import java.util.UUID;

@Component
public class InstrumentServiceClientFallback implements InstrumentServiceClient {
    @Override
    public Mono<InstrumentDto> findById(UUID id) {
        return Mono.empty();
    }

    @Override
    public Mono<InstrumentDto> createInstrument(InstrumentDto instrument) {
        return Mono.empty();
    }

    @Override
    public Mono<InstrumentDto> patchInstrument(UUID id, InstrumentPatchDto status) {
        return Mono.empty();
    }
}
