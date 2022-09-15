package ru.itmo.lab1.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.model.dto.PortfolioDto;
import ru.itmo.lab1.model.dto.PortfolioPatchDto;
import ru.itmo.lab1.security.JwtUtils;
import ru.itmo.lab1.security.UserDetailServiceImpl;
import ru.itmo.lab1.security.UserDetailsImpl;
import ru.itmo.lab1.service.PortfolioService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioController {
    @Value("${spring.data.web.pageable.default-page-size}")
    private final int defaultPageSize = 50;
    private PortfolioService portfolioService;

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
        Page<PortfolioDto> portfolioPage = highAuth ?
                portfolioService.findAll(page, isInfiniteScroll ? defaultPageSize : size) :
                portfolioService.findAllByIdUser(user.getId(), page, isInfiniteScroll ? defaultPageSize : size);

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
    public ResponseEntity<PortfolioDto> findById(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                                 @PathVariable UUID id) {
        PortfolioDto portfolioDto = portfolioService.findById(id);
        return isAllowed(getUser(header), portfolioDto.getId()) ?
                ResponseEntity.ok(portfolioDto) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                             @RequestBody PortfolioDto portfolio) {
        return isAllowed(getUser(header), portfolio.getIdUserId()) ?
                new ResponseEntity<>(portfolioService.create(portfolio), HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolio(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                             @PathVariable UUID id) {
        if (isAllowed(getUser(header), id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        portfolioService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PortfolioDto> patchPortfolio(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                                       @PathVariable UUID id, @RequestBody PortfolioPatchDto patch) {
        if (!isAllowed(getUser(header), id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(portfolioService.update(id, patch));
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
