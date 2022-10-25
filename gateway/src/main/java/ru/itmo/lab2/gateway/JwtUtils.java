package ru.itmo.lab2.gateway;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

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

    public Optional<String> getJwtToken(String header) {
        if (isEmpty(header) || !header.startsWith("Bearer")) {
            return Optional.empty();
        }
        return Optional.of(header.split(" ")[1].trim());
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID getId(String token) {
        return UUID.fromString(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject());
    }
}
