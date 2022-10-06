package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.TradeDto;
import ru.itmo.lab1.security.JwtUtils;
import ru.itmo.lab1.security.UserDetailServiceImpl;
import ru.itmo.lab1.security.UserDetailsImpl;
import ru.itmo.lab1.service.TradeService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/trade")
@AllArgsConstructor
public class TradeController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private TradeService tradeService;

    private UserDetailServiceImpl userDetailService;
    private JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {

        UserDetailsImpl user = getUser(header);
        String authority = user.getAuthorities().iterator().next().getAuthority();
        boolean highAuth = authority.equals("ROLE_ADMIN") || authority.equals("ROLE_MAINTAINER");

        boolean isInfiniteScroll = size == null;
        Page<TradeDto> tradePage = highAuth ?
                tradeService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                tradeService.findAllByIdUser(user.getId(), page, isInfiniteScroll ? defaultPageSize : size);

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
    public ResponseEntity<TradeDto> findById(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                             @PathVariable UUID id) {
        TradeDto tradeDto = tradeService.findById(id);
        return isAllowed(getUser(header), tradeDto.getId()) ?
                ResponseEntity.ok(tradeDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<TradeDto> createTrade(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                                @RequestBody TradeDto trade) {
        if (trade.getIdUserId() == null) {
            trade.setIdUserId(getUser(header).getId());
        }

        return isAllowed(getUser(header), trade.getIdUserId()) ?
                new ResponseEntity<>(tradeService.createExternal(trade), HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    private UserDetailsImpl getUser(String header) {
        String token = jwtUtils.getJwtToken(header).orElseThrow(() -> new RuntimeException("Not valid header"));
        String name = jwtUtils.getUserName(token);
        return (UserDetailsImpl) userDetailService.loadUserByUsername(name);
    }

    private boolean isAllowed(UserDetailsImpl user, UUID id) {
        String authority = user.getAuthorities().iterator().next().getAuthority();
        boolean highAuth = authority.equals("ROLE_ADMIN");
        boolean isSameUser = id.equals(user.getId());
        return highAuth || isSameUser;
    }
}
