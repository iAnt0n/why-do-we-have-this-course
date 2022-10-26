package ru.itmo.lab2.trade_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.trade_service.model.dto.TradeDto;
import ru.itmo.lab2.trade_service.model.enums.Role;
import ru.itmo.lab2.trade_service.service.TradeService;

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
    public Mono<ResponseEntity<?>> findAll(@RequestHeader("x-user-id") UUID userId,
                                           @RequestHeader("x-user-role") Role userRole,
                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", required = false) Integer size) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN || userRole == Role.ROLE_MAINTAINER);

        boolean isInfiniteScroll = size == null;
        Mono<Page<TradeDto>> tradePageMono = highAuth ?
                tradeService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                tradeService.findAllByIdUser(userId, page, isInfiniteScroll ? defaultPageSize : size);

        return tradePageMono.map(tradePage -> {
            if (!isInfiniteScroll) {
                return ResponseEntity.ok()
                        .header("x-total-count", String.valueOf(tradePage.getTotalElements()))
                        .body(tradePage.getContent());
            }
            return ResponseEntity.ok().body(Map.of(
                    "items", tradePage.getContent(),
                    "hasMore", !tradePage.isLast())
            );
        }).switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TradeDto>> findById(@RequestHeader("x-user-id") UUID userId,
                                                   @RequestHeader("x-user-role") Role userRole,
                                                   @PathVariable UUID id) {
        return tradeService.findById(id).map(tradeDto -> isAllowed(userId, userRole, tradeDto.getId()) ?
                ResponseEntity.ok(tradeDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @PostMapping
    public Mono<ResponseEntity<TradeDto>> createTrade(@RequestHeader("x-user-id") UUID userId,
                                                      @RequestHeader("x-user-role") Role userRole,
                                                      @RequestBody TradeDto trade) {
        if (trade.getIdUserId() == null) {
            trade.setIdUserId(userId);
        }
        if (!isAllowed(userId, userRole, trade.getIdUserId())) {
            return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        return tradeService.createExternal(trade).map(tradeDto -> new ResponseEntity<>(tradeDto, HttpStatus.CREATED));
    }

    private boolean isAllowed(UUID userId, Role userRole, UUID portfolioUserId) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN);
        boolean isSameUser = userId.equals(portfolioUserId);
        return highAuth || isSameUser;
    }
}
