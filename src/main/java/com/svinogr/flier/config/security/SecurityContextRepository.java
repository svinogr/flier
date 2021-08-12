package com.svinogr.flier.config.security;

import com.svinogr.flier.config.jwt.JwtUtil;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
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
  /*      ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create("/loginpage"));
        response.getCookies().remove("SESSION");
        System.out.println(response.getCookies());
        response.getCookies().remove("jwt");*/
        System.out.println("save");
      /*  System.out.println(exchange.getRequest().getCookies());
        System.out.println(exchange.getResponse().getCookies());
        context.getAuthentication().setAuthenticated(false);*/
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
