package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.OrderDto;
import ru.itmo.lab1.model.dto.OrderPatchDto;
import ru.itmo.lab1.service.OrderService;

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
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<OrderDto> orderPage = orderService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
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
    public ResponseEntity<OrderDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto order) {
        return new ResponseEntity<>(orderService.create(order), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> patchOrder(@PathVariable UUID id, @RequestBody OrderPatchDto status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status.getStatus()));
    }
}
