package ru.itmo.lab2.market_instrument_id_service.client;

import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentDto;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentPatchDto;

import java.util.UUID;

@ReactiveFeignClient(name = "INSTRUMENT-SERVICE", fallback = InstrumentServiceClientFallback.class)
public interface InstrumentServiceClient {
    @GetMapping("/instrument/{id}")
    Mono<InstrumentDto> findById(@PathVariable("id") UUID id);

    @PostMapping("/instrument")
    Mono<InstrumentDto> createInstrument(@RequestBody InstrumentDto instrument);

    @RequestMapping(method = RequestMethod.PUT, value = "/instrument/{id}", consumes = "application/json")
    Mono<InstrumentDto> patchInstrument(@PathVariable("id") UUID id, InstrumentPatchDto status);
}
