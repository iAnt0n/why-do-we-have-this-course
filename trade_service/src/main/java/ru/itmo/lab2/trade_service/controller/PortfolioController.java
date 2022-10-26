package ru.itmo.lab2.trade_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab2.trade_service.model.dto.PortfolioDto;
import ru.itmo.lab2.trade_service.model.dto.PortfolioPatchDto;
import ru.itmo.lab2.trade_service.model.enums.Role;
import ru.itmo.lab2.trade_service.service.PortfolioService;

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
    public ResponseEntity<Object> findAll(@RequestHeader("x-user-id") UUID userId,
                                          @RequestHeader("x-user-role") Role userRole,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN || userRole == Role.ROLE_MAINTAINER);

        boolean isInfiniteScroll = size == null;
        Page<PortfolioDto> portfolioPage = highAuth ?
                portfolioService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                portfolioService.findAllByIdUser(userId, page, isInfiniteScroll ? defaultPageSize : size);

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
    public ResponseEntity<PortfolioDto> findById(@RequestHeader("x-user-id") UUID userId,
                                                 @RequestHeader("x-user-role") Role userRole,
                                                 @PathVariable UUID id) {
        PortfolioDto portfolioDto = portfolioService.findById(id);
        return isAllowed(userId, userRole, id) ?
                ResponseEntity.ok(portfolioDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestHeader("x-user-id") UUID userId,
                                                        @RequestHeader("x-user-role") Role userRole,
                                                        @RequestBody PortfolioDto portfolio) {
        if (portfolio.getIdUserId() == null) {
            portfolio.setIdUserId(userId);
        }

        return isAllowed(userId, userRole, portfolio.getIdUserId()) ?
                new ResponseEntity<>(portfolioService.create(portfolio), HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolio(@RequestHeader("x-user-id") UUID userId,
                                             @RequestHeader("x-user-role") Role userRole,
                                             @PathVariable UUID id) {
        if (!isAllowed(userId, userRole, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        portfolioService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PortfolioDto> patchPortfolio(@RequestHeader("x-user-id") UUID userId,
                                                       @RequestHeader("x-user-role") Role userRole,
                                                       @PathVariable UUID id, @RequestBody PortfolioPatchDto patch) {
        if (!isAllowed(userId, userRole, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(portfolioService.update(id, patch));
    }

    private boolean isAllowed(UUID userId, Role userRole, UUID portfolioUserId) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN);
        boolean isSameUser = userId.equals(portfolioUserId);
        return highAuth || isSameUser;
    }
}
