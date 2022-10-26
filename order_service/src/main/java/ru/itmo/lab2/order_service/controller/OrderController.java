package ru.itmo.lab2.order_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab2.order_service.model.dto.OrderDto;
import ru.itmo.lab2.order_service.model.dto.OrderPatchDto;
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
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {

        boolean isInfiniteScroll = size == null;
        Page<OrderDto> orderPage = orderService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
//                orderService.findAllByIdUser(user.getId(), page, isInfiniteScroll ? defaultPageSize : size);

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
    public ResponseEntity<OrderDto> findById(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                             @PathVariable UUID id) {
        OrderDto orderDto = orderService.findById(id);
        return
//                isAllowed(getUser(header), orderDto.getId()) ?
                ResponseEntity.ok(orderDto);
//                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto order) {
//        if (order.getIdUserId() == null) {
//            order.setIdUserId(getUser(header).getId());
//        }

        return
//                isAllowed(getUser(header), order.getIdUserId()) ?
                new ResponseEntity<>(orderService.create(order), HttpStatus.CREATED);
//                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> patchOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                               @PathVariable UUID id, @RequestBody OrderPatchDto status) {
//        if (isAllowed(getUser(header), id)) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
        return ResponseEntity.ok(orderService.updateStatus(id, status.getStatus()));
    }

//    private UserDetailsImpl getUser(String header) {
//        String token = jwtUtils.getJwtToken(header).orElseThrow(() -> new RuntimeException("Not valid header"));
//        String name = jwtUtils.getUserName(token);
//        return (UserDetailsImpl) userDetailService.loadUserByUsername(name);
//    }
//
//    private boolean isAllowed(UserDetailsImpl user, UUID id) {
//        String authority = user.getAuthorities().iterator().next().getAuthority();
//        boolean highAuth = authority.equals("ROLE_ADMIN");
//        boolean isSameUser = id.equals(user.getId());
//        return highAuth || isSameUser;
//    }
}
