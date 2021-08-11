package com.svinogr.flier.config.security;

import com.svinogr.flier.config.jwt.JwtUtil;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
/*
      throw  new IllegalStateException("not supported");
*/
     return Mono.empty() ;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        List<HttpCookie> jwt = exchange.getRequest().getCookies().get("jwt");

        if(jwt == null) return Mono.empty();

        String authCookie =jwt.toString();

        if (authCookie != null ) {
            String authToken = authCookie.substring(5, authCookie.length() - 1);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

            return authenticationManager.authenticate(auth).
                    map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}
