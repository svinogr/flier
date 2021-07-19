package com.svinogr.flier.config.security;

import com.svinogr.flier.config.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        String username;
        try {
            username = jwtUtil.extractUserName(authToken);

        } catch (Exception e) {
            username = null;
        }

        if (username != null && jwtUtil.validateToken(authToken)) {

            Claims claims = jwtUtil.getClaims(authToken);
            List<String> roles = claims.get("role", List.class);

            List<SimpleGrantedAuthority> auth = roles.stream().
                    map(SimpleGrantedAuthority::new
                    ).collect(Collectors.toList());

            return Mono.just(new UsernamePasswordAuthenticationToken(username, null, auth));

        } else {
            return Mono.empty();
        }
    }
}
