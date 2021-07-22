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

import java.util.ArrayList;
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

            /*Claims claims = jwtUtil.getClaims(authToken);

            String role = (String) claims.get("role");
            String email = (String) claims.get("email");
            */
            return   jwtUtil.getFullUserFromToken(authToken).flatMap(user -> {
                  SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRoles().get(0).getName());
                  List<SimpleGrantedAuthority> auth = new ArrayList<>();
                  auth.add(simpleGrantedAuthority);
                  return Mono.just(new UsernamePasswordAuthenticationToken(user, null, auth));
              });

        } else {
            return Mono.empty();
        }
    }
}
