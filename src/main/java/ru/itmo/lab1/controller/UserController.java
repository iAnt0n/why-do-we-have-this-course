package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.UserDto;
import ru.itmo.lab1.service.UserService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;

    private UserService userService;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<UserDto> userPage = userService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        if (userPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(userPage.getTotalElements()))
                    .body(userPage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", userPage.getContent(),
                "hasMore", !userPage.isLast()
        ));
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        return userService.register(userDto) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMarket(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
