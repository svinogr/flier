/*
package com.svinogr.flier.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expired}")
    private Long expiredMilliSeconds;

    @Autowired
    private UserDetailsService userDetailsService;

    public String createJwtToken(JwtUser jwtUser) {
        Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
        claims.put("roles", getRolles(jwtUser.getAuthorities()));

        Date date = new Date();
        Date validity = new Date(date.getTime() + expiredMilliSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.ES256, secret)
                .compact();
    }

    public Authentication getAutentification(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUserName(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    private Object getRolles(Collection<? extends GrantedAuthority> authorities) {
        List<String> result = new ArrayList<>();

        authorities.forEach(roles ->{
            result.add(roles.getAuthority());
        });
        return result;
    }

    public String resolveToken(HttpServle)


}
*/
