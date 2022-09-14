package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.PortfolioDto;
import ru.itmo.lab1.model.dto.PortfolioPatchDto;
import ru.itmo.lab1.service.PortfolioService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<PortfolioDto> portfolioPage = portfolioService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        if (portfolioPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(portfolioPage.getTotalElements()))
                    .body(portfolioPage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", portfolioPage.getContent(),
                "hasMore", !portfolioPage.isLast()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(portfolioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestBody PortfolioDto portfolio) {
        return new ResponseEntity<>(portfolioService.create(portfolio), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolio(@PathVariable UUID id) {
        portfolioService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PortfolioDto> patchPortfolio(@PathVariable UUID id, @RequestBody PortfolioPatchDto patch) {
        return ResponseEntity.ok(portfolioService.update(id, patch));
    }
}
