package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.MarketInstrumentIdDto;
import ru.itmo.lab1.service.MarketInstrumentIdService;

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
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<MarketInstrumentIdDto> marketInstrumentIdPage = marketInstrumentIdService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        if (marketInstrumentIdPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(marketInstrumentIdPage.getTotalElements()))
                    .body(marketInstrumentIdPage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", marketInstrumentIdPage.getContent(),
                "hasMore", !marketInstrumentIdPage.isLast()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketInstrumentIdDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(marketInstrumentIdService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MarketInstrumentIdDto> createMarketInstrumentId(@RequestBody MarketInstrumentIdDto marketInstrumentId) {
        return new ResponseEntity<>(marketInstrumentIdService.create(marketInstrumentId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMarketInstrumentId(@PathVariable UUID id) {
        marketInstrumentIdService.delete(id);
        return ResponseEntity.ok().build();
    }
}
