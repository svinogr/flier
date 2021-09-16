package com.svinogr.flier.config.jwt;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.StockService;
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

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class for auth with JWT token
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expired}")
    private String expiredTime;

    @Autowired
    UserService userService;

    /**
     * Returns string with username from token
     *
     * @param authToken string with jwt token
     * @return username
     */
    public String extractUserName(String authToken) {
        return getClaims(authToken).getSubject();
    }

    /**
     * Returns user {@link User} from db by jwt token. From token get email user and with it value get user from db
     *
     * @param authToken string with jwt token
     * @return found user. Mono<User>
     */
    public Mono<User> getFullUserFromToken(String authToken) {
        Claims claims = getClaims(authToken);
        String email = (String) claims.get("email");
        return userService.findUserByEmail(email);
    }

    /**
     * Returns signed key from string with jwt token using secret key
     *
     * @param authToken string with jwt token
     * @return signed key
     */
    private String getKeyFromToken(String authToken) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    /**
     * Returns claims from string with jwt token
     *
     * @param authToken string with jwt token
     * @return claims
     */
    public Claims getClaims(String authToken) {
        return Jwts.parser().
                setSigningKey(getKeyFromToken(authToken)).
                parseClaimsJws(authToken).
                getBody();
    }

    /**
     * Validate jwt token by date
     *
     * @param authToken string with jwt token
     * @return result of check. Boolean
     */
    public boolean validateToken(String authToken) {
        return getClaims(authToken).
                getExpiration().
                after(new Date());
    }

    /**
     * Returns string with jwt token from user {@link User}
     *
     * @param user {@link User}
     * @return jwt token
     */
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

    /**
     * Returns created jwt token with EXPIRED date. Use for deleting token from cookies browser
     *
     * @return jwt token
     */
    public String createExpiredJwtToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "EXPIRED");
        claims.put("email", "EXPIRED");

        Date creationDate = new Date();

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
