package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.TradeDto;
import ru.itmo.lab1.service.TradeService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/trade")
@AllArgsConstructor
public class TradeController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private TradeService tradeService;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<TradeDto> tradePage = tradeService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        if (tradePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(tradePage.getTotalElements()))
                    .body(tradePage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", tradePage.getContent(),
                "hasMore", !tradePage.isLast()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(tradeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TradeDto> createTrade(@RequestBody TradeDto trade) {
        return new ResponseEntity<>(tradeService.createExternal(trade), HttpStatus.CREATED);
    }
}
