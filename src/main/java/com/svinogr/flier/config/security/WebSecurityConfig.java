package com.svinogr.flier.config.security;

import io.netty.handler.codec.http.cookie.Cookie;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@Data
public class WebSecurityConfig {
    @Autowired
    private final AuthenticationManager authentificationManager;

    @Autowired
    private final SecurityContextRepository securityContextRepository;


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }


/*     ServerCsrfTokenRepository csrfTokenRepository() {
         ServerCsrfTokenRepository repository = new WebSessionServerCsrfTokenRepository();
        repository. setSessionAttributeName("_csrf");
        return repository;
    }*/

//    @Bean
//    public RouterFunction<ServerResponse> imgRouter() {
//        System.out.println("imgRouter");
//        return RouterFunctions
//                .resources("/img/shop/**", new ClassPathResource("/static/img/"));
//    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity.
                csrf().tokenFromMultipartDataEnabled(true).and().
                logout()
                  .logoutSuccessHandler(new ServerLogoutSuccessHandler() {
            @Override
            public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
                ServerHttpResponse response = exchange.getExchange().getResponse();
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create("/loginpage"));
                response.getCookies().remove("SESSION");
                System.out.println(response.getCookies());
                response.getCookies().remove("jwt");
                System.out.println(response.getCookies().getFirst("jwt"));
                return exchange.getExchange().getSession()
                        .flatMap(WebSession::invalidate);
            }
        }).and().
                formLogin().loginPage("/loginpage").
                and().
                httpBasic().disable().
                authenticationManager(authentificationManager).
                securityContextRepository(securityContextRepository).
                authorizeExchange().
                pathMatchers("/webjars/**", "/loginpage", "/login", "/favicon.ico", "/register").permitAll().
                // pathMatchers("/d").hasAnyRole("ACCOUNT", "ADMIN").
                        pathMatchers("/admin/**").hasRole("ADMIN").
                        pathMatchers("/account/**").hasAnyRole("ACCOUNT", "ADMIN").
                        anyExchange().authenticated().
                //   anyExchange().permitAll().
                        and().
                        build();
    }

}
