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
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class AuthenticationManager for auth with JWT token. Implements ReactiveAuthenticationManager.
 * {@link JwtUtil}
 */
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
