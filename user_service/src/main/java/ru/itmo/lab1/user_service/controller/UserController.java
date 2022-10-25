package ru.itmo.lab1.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.user_service.model.dto.UserDto;
import ru.itmo.lab1.user_service.model.dto.UserRegDto;
import ru.itmo.lab1.user_service.service.UserDetailServiceImpl;
import ru.itmo.lab1.user_service.service.UserDetailsImpl;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;

    private UserDetailServiceImpl userService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> registerUser(@RequestBody UserRegDto userRegDto) {
        return userService.register(userRegDto) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> authenticateUser(@RequestBody UserRegDto userRegDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRegDto.getName(), userRegDto.getPassword()));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getId());
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt).build();
    }
}
