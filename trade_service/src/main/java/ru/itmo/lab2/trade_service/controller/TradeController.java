package ru.itmo.lab2.trade_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Object> findAll(@RequestHeader("x-user-id") UUID userId,
                                          @RequestHeader("x-user-role") Role userRole,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN || userRole == Role.ROLE_MAINTAINER);

        boolean isInfiniteScroll = size == null;
        Page<TradeDto> tradePage = highAuth ?
                tradeService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                tradeService.findAllByIdUser(userId, page, isInfiniteScroll ? defaultPageSize : size);

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
    public ResponseEntity<TradeDto> findById(@RequestHeader("x-user-id") UUID userId,
                                             @RequestHeader("x-user-role") Role userRole,
                                             @PathVariable UUID id) {
        TradeDto tradeDto = tradeService.findById(id);
        return isAllowed(userId, userRole, tradeDto.getId()) ?
                ResponseEntity.ok(tradeDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<TradeDto> createTrade(@RequestHeader("x-user-id") UUID userId,
                                                @RequestHeader("x-user-role") Role userRole,
                                                @RequestBody TradeDto trade) {
        if (trade.getIdUserId() == null) {
            trade.setIdUserId(userId);
        }

        return isAllowed(userId, userRole, trade.getIdUserId()) ?
                new ResponseEntity<>(tradeService.createExternal(trade), HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    private boolean isAllowed(UUID userId, Role userRole, UUID portfolioUserId) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN);
        boolean isSameUser = userId.equals(portfolioUserId);
        return highAuth || isSameUser;
    }
}
