package com.svinogr.flier.config.jwt;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.xml.crypto.Data;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expired}")
    private String expiredTime;

    @Autowired
    UserService userService;

    public String extractUserName(String authToken) {
        return getClaims(authToken).getSubject();
    }

    public Mono<User> getFullUserFromToken(String authToken) {
        Claims claims = getClaims(authToken);
        String email = (String) claims.get("email");
        return userService.findUserByEmail(email);
    }

    private String getKeyFromToken(String authToken) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public Claims getClaims(String authToken) {
        return Jwts.parser().
                setSigningKey(getKeyFromToken(authToken)).
                parseClaimsJws(authToken).
                getBody();
    }

    public boolean validateToken(String authToken) {
        return getClaims(authToken).
                getExpiration().
                after(new Date());
    }

    public String createJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRoles().get(0).getName());
        claims.put("email", user.getEmail());

        Date creationDate = new Date();

        long parseExpirationValue = Long.parseLong(expiredTime);

        Date expirationDate = new Date(creationDate.getTime() + parseExpirationValue * 1000);

        return Jwts.builder().
                setClaims(claims).
                setSubject(user.getUsername()).
                setIssuedAt(creationDate).
                setExpiration(expirationDate).
                signWith(SignatureAlgorithm.HS256, secret.getBytes()).
                compact();
    }

    public String createExpiredJwtToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "EXPIRED");
        claims.put("email", "EXPIRED");

        Date creationDate = new Date();

        long parseExpirationValue = Long.parseLong(expiredTime);

        Date expirationDate = new Date(creationDate.getTime() - 1000);

        return Jwts.builder().
                setClaims(claims).
                setSubject("EXPIRED").
                setIssuedAt(creationDate).
                setExpiration(expirationDate).
                signWith(SignatureAlgorithm.HS256, secret.getBytes()).
                compact();
    }
}
