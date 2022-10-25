package ru.itmo.lab2.gateway;

import lombok.Data;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import ru.itmo.lab2.gateway.model.enums.Role;

import java.util.*;

@Component
public class UserRoleGatewayFilterFactory extends
        AbstractGatewayFilterFactory<UserRoleGatewayFilterFactory.Config> {
    private final ObjectProvider<UserClient> userClient;
    private final JwtUtils jwtUtils;

    public UserRoleGatewayFilterFactory(ObjectProvider<UserClient> userClient, JwtUtils jwtUtils) {
        super(Config.class);
        this.userClient = userClient;
        this.jwtUtils = jwtUtils;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Set<Role> allowedRoles = config.getRoleSet();

            // assume that no roles = allow even unauthenticated
            if (allowedRoles.isEmpty()) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();

            List<String> headerList = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

            if (headerList == null || headerList.size() != 1) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String header = headerList.get(0);
            Optional<String> tokenOpt = jwtUtils.getJwtToken(header);
            if (tokenOpt.isEmpty() ||!jwtUtils.validateJwtToken(tokenOpt.get())) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String token = tokenOpt.get();
            UUID userId = jwtUtils.getId(token);
            UserClient client = userClient.getIfAvailable();

            if (client == null) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                return response.setComplete();
            }

            return client.findById(userId).flatMap( u -> {
                if (allowedRoles.contains(u.getRole())) {
                    ServerHttpRequest updatedRequest = exchange.getRequest()
                            .mutate()
                            .header("x-user-id", u.getId().toString())
                            .header("x-user-role", u.getRole().name())
                            .build();
                    ServerWebExchange updatedExchange = exchange.mutate().request(updatedRequest).build();
                    return chain.filter(updatedExchange);
                }
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            });
        };
    }

    @Data
    public static class Config {
        private String role1;
        private String role2;
        private String role3;

        Set<Role> getRoleSet() {
            if (roleSet.isEmpty()) {
                if (role1 != null && !role1.isEmpty()) {
                    roleSet.add(Role.valueOf(role1));
                }
                if (role2 != null && !role2.isEmpty()) {
                    roleSet.add(Role.valueOf(role2));
                }
                if (role3 != null && !role3.isEmpty()) {
                    roleSet.add(Role.valueOf(role3));
                }
            }
            return roleSet;
        }

        Set<Role> roleSet = new HashSet<>();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        // we need this to use shortcuts in the application.yml
        return Arrays.asList("role1", "role2", "role3");
    }
}