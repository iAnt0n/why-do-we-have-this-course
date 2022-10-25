package ru.itmo.lab2.market_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab2.market_service.model.MarketDto;
import ru.itmo.lab2.market_service.service.MarketService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/market")
@AllArgsConstructor
public class MarketController {
    //    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private MarketService marketService;

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

    @GetMapping("/{id}")
    public ResponseEntity<MarketDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(marketService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MarketDto> createMarket(@RequestBody MarketDto market) {
        return new ResponseEntity<>(marketService.create(market), HttpStatus.CREATED);
    }
}
