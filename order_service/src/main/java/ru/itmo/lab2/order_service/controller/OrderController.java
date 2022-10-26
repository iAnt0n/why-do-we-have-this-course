package ru.itmo.lab2.order_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab2.order_service.model.dto.OrderDto;
import ru.itmo.lab2.order_service.model.dto.OrderPatchDto;
import ru.itmo.lab2.order_service.model.enums.Role;
import ru.itmo.lab2.order_service.service.OrderService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("x-user-id") UUID userId,
                                          @RequestHeader("x-user-role") Role userRole,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN || userRole == Role.ROLE_MAINTAINER);
        boolean isInfiniteScroll = size == null;
        Page<OrderDto> orderPage = highAuth ?
                orderService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                orderService.findAllByIdUser(userId, page, isInfiniteScroll ? defaultPageSize : size);

        if (orderPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(orderPage.getTotalElements()))
                    .body(orderPage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", orderPage.getContent(),
                "hasMore", !orderPage.isLast()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> findById(@RequestHeader("x-user-id") UUID userId,
                                             @RequestHeader("x-user-role") Role userRole,
                                             @PathVariable UUID id) {
        OrderDto orderDto = orderService.findById(id);
        return isAllowed(userId, userRole, orderDto.getIdUser()) ?
                ResponseEntity.ok(orderDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestHeader("x-user-id") UUID userId,
                                                @RequestHeader("x-user-role") Role userRole,
                                                @RequestBody OrderDto order) {
        if (order.getIdUser() == null) {
            order.setIdUser(userId);
        }

        return isAllowed(userId, userRole, order.getIdUser()) ?
                new ResponseEntity<>(orderService.create(order), HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> patchOrder(@RequestHeader("x-user-id") UUID userId,
                                               @RequestHeader("x-user-role") Role userRole,
                                               @PathVariable UUID id, @RequestBody OrderPatchDto status) {
        OrderDto orderDto = orderService.findById(id);
        if (!isAllowed(userId, userRole, orderDto.getIdUser())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(orderService.updateStatus(id, status.getStatus()));
    }

    private boolean isAllowed(UUID userId, Role userRole, UUID portfolioUserId) {
        boolean highAuth = (userRole == Role.ROLE_ADMIN);
        boolean isSameUser = userId.equals(portfolioUserId);
        return highAuth || isSameUser;
    }
}
