package ru.itmo.lab2.market_instrument_id_service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentDto;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentPatchDto;

import java.util.UUID;

//todo change url
@ReactiveFeignClient(name = "instrument", url = "http://localhost:8081")
public interface InstrumentServiceClient {

//    @GetMapping("/instrument")
//    Mono<ResponseEntity<?>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
//                                    @RequestParam(value = "size", required = false) Integer size);

    @GetMapping("/instrument/{id}")
    Mono<InstrumentDto> findById(@PathVariable UUID id);

    @PostMapping("/instrument")
    Mono<InstrumentDto> createInstrument(@RequestBody InstrumentDto instrument);

    @PatchMapping("/instrument/{id}")
    Mono<InstrumentDto> patchInstrument(@PathVariable UUID id, @RequestBody InstrumentPatchDto status);
}
