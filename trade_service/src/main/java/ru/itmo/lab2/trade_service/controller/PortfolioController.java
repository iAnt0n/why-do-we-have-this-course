package ru.itmo.lab2.trade_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
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
    public Mono<ResponseEntity<?>> findAll(@RequestHeader("x-user-id") UUID userId,
                                                @RequestHeader("x-user-role") Role userRole,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", required = false) Integer size) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN || userRole == Role.ROLE_MAINTAINER);

        boolean isInfiniteScroll = size == null;
        Mono<Page<PortfolioDto>> portfolioPageMono = highAuth ?
                portfolioService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                portfolioService.findAllByIdUser(userId, page, isInfiniteScroll ? defaultPageSize : size);

        return portfolioPageMono.map(portfolioPage -> {
            if (!isInfiniteScroll) {
                return ResponseEntity.ok()
                        .header("x-total-count", String.valueOf(portfolioPage.getTotalElements()))
                        .body(portfolioPage.getContent());
            }
            return ResponseEntity.ok().body(Map.of(
                    "items", portfolioPage.getContent(),
                    "hasMore", !portfolioPage.isLast())
            );
        }).switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PortfolioDto>> findById(@RequestHeader("x-user-id") UUID userId,
                                                 @RequestHeader("x-user-role") Role userRole,
                                                 @PathVariable UUID id) {
        return portfolioService.findById(id).map(portfolioDto -> isAllowed(userId, userRole, portfolioDto.getId()) ?
                ResponseEntity.ok(portfolioDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @PostMapping
    public Mono<ResponseEntity<PortfolioDto>> createPortfolio(@RequestHeader("x-user-id") UUID userId,
                                                        @RequestHeader("x-user-role") Role userRole,
                                                        @RequestBody PortfolioDto portfolio) {

        if (portfolio.getIdUserId() == null) {
            portfolio.setIdUserId(userId);
        }

        if (!isAllowed(userId, userRole, portfolio.getIdUserId())) {
            return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        return portfolioService.create(portfolio)
                .map(portfolioDto -> new ResponseEntity<>(portfolioDto, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePortfolio(@RequestHeader("x-user-id") UUID userId,
                                             @RequestHeader("x-user-role") Role userRole,
                                             @PathVariable UUID id) {
        if (!isAllowed(userId, userRole, id)) {
            return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        return portfolioService.delete(id).map(o -> ResponseEntity.ok().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PortfolioDto>> patchPortfolio(@RequestHeader("x-user-id") UUID userId,
                                                       @RequestHeader("x-user-role") Role userRole,
                                                       @PathVariable UUID id, @RequestBody PortfolioPatchDto patch) {
        if (!isAllowed(userId, userRole, id)) {
            return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        return portfolioService.update(id, patch).map(ResponseEntity::ok);
    }

    private boolean isAllowed(UUID userId, Role userRole, UUID portfolioUserId) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN);
        boolean isSameUser = userId.equals(portfolioUserId);
        return highAuth || isSameUser;
    }
}
