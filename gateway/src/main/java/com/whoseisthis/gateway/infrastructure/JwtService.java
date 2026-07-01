package com.whoseisthis.gateway.infrastructure;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.user.core.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
    private final String key;
    private final String issuer;

    public JwtService(@Value("${jwt.secret-key}") String key, @Value("${jwt.issuer}") String issuer) {
        this.key = key;
        this.issuer = issuer;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public JwtPayload validate(String token) {
        SecretKey secretKey = getSecretKey();
        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long id = Long.parseLong(claims.getSubject());
            UserRole role = UserRole.valueOf(claims.get("role", String.class));
            return new JwtPayload(id, role);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
