package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.MarketDto;
import ru.itmo.lab1.service.MarketService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/market")
@AllArgsConstructor
public class MarketController {
    private MarketService marketService;

    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<MarketDto> marketPage = marketService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        if (marketPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(marketPage.getTotalElements()))
                    .body(marketPage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", marketPage.getContent(),
                "hasMore", !marketPage.isLast()
        ));
    }

    @PostMapping
    public ResponseEntity<MarketDto> createMarket(@RequestBody MarketDto market) {
        return new ResponseEntity<>(marketService.save(market), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMarket(@PathVariable UUID id) {
        marketService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<MarketDto> putMarket(@RequestBody MarketDto marketDTO) {
        return ResponseEntity.ok(marketService.modify(marketDTO));
    }
}
