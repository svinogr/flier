package com.svinogr.flier.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    public String extractUserName(String authToken) {
        String key = getKeyFromToken(authToken);

        return getClaims(authToken).getSubject();
    }

    private String getKeyFromToken(String authToken) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public Claims getClaims(String authToken) {
        return Jwts.parser().
                setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes())).
                parseClaimsJwt(authToken).
                getBody();
    }

    public boolean validateToken(String authToken) {
        return getClaims(authToken).
                getExpiration().
                before(new Date());
    }
}
