package ru.itmo.lab2.market_instrument_id_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.market_instrument_id_service.model.dto.MarketInstrumentIdDto;
import ru.itmo.lab2.market_instrument_id_service.service.MarketInstrumentIdService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/marketInstrumentId")
@AllArgsConstructor
public class MarketInstrumentIdController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private MarketInstrumentIdService marketInstrumentIdService;

    @GetMapping
    public Mono<ResponseEntity<?>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = (size == null);
        Mono<Page<MarketInstrumentIdDto>> marketInstrumentIdPageDto = marketInstrumentIdService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        return marketInstrumentIdPageDto.map(marketInstrumentIdPage -> {
                    if (!isInfiniteScroll) {
                        return ResponseEntity.ok()
                                .header("x-total-count", String.valueOf(marketInstrumentIdPage.getTotalElements()))
                                .body(marketInstrumentIdPage.getContent());
                    }
                    return ResponseEntity.ok().body(Map.of(
                            "items", marketInstrumentIdPage.getContent(),
                            "hasMore", !marketInstrumentIdPage.isLast()
                    ));
                })
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MarketInstrumentIdDto>> findById(@PathVariable UUID id) {
        return marketInstrumentIdService.findById(id).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<MarketInstrumentIdDto>> createMarketInstrumentId(@RequestBody MarketInstrumentIdDto marketInstrumentId) {
        return marketInstrumentIdService.create(marketInstrumentId).map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteMarketInstrumentId(@PathVariable UUID id) {
        return marketInstrumentIdService.delete(id).then(Mono.just(ResponseEntity.ok().build()));
    }
}
