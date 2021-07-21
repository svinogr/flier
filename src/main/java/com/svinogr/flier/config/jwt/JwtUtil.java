package com.svinogr.flier.config.jwt;

import com.svinogr.flier.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public String extractUserName(String authToken) {
        String key = getKeyFromToken(authToken);
        System.out.println("key " + key);
        System.out.println("subject " + getClaims(authToken).getSubject());
        return getClaims(authToken).getSubject();
    }

    private String getKeyFromToken(String authToken) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public Claims getClaims(String authToken) {
        return Jwts.parser().
                setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes())).
                parseClaimsJws(authToken).
                getBody();
    }

    public boolean validateToken(String authToken) {
       boolean re = getClaims(authToken).
                getExpiration().
                before(new Date());
        System.out.println("valid" + re);
        return  re;

    }

    public String createJwtToken(User user) {
        Map<String, Object>  claims = new HashMap<>();
        claims.put("role", user.getRoles().get(0).getName());

        Date creationDate = new Date();

        long parseExpirationValue = Long.parseLong(expiredTime);

        Date expirationDate = new Date(creationDate.getTime() + parseExpirationValue *1000);

       return Jwts.builder().
                setClaims(claims).
                setSubject(user.getUsername()).
                setIssuedAt(creationDate).
               setExpiration(expirationDate).
                signWith(SignatureAlgorithm.HS256, secret.getBytes()).
                compact();
    }
}
