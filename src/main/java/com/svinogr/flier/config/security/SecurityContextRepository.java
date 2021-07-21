package com.svinogr.flier.config.security;

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

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
      throw  new IllegalStateException("not supported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        List<HttpCookie> jwt = exchange.getRequest().getCookies().get("jwt");

        if(jwt == null) return Mono.empty();

        String authCookie =jwt.toString();
    //    System.out.println("jwt cokie " + authCookie);

        if (authCookie != null ) {
            String authToken = authCookie.substring(5, authCookie.length() - 1);
       //     System.out.println("jwt  " + authToken);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

         //   System.out.println("name " + auth.getName());
         //   System.out.println(auth.getCredentials());
            return authenticationManager.authenticate(auth).
                    map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}
