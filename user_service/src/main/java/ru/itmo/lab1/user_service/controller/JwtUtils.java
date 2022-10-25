package ru.itmo.lab1.user_service.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@Scope("singleton")
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private Key key;

    @PostConstruct
    void postConstruct() {
        key = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(UUID id) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + (30 * 60 * 1000)))
                .signWith(key)
                .compact();
    }
}