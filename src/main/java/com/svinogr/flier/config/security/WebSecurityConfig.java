package com.svinogr.flier.config.security;

import com.svinogr.flier.config.jwt.JwtUtil;
import com.svinogr.flier.model.Role;
import com.svinogr.flier.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.*;
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter.Directive.CACHE;
import static org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter.Directive.COOKIES;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@Data
public class WebSecurityConfig {
    @Autowired
    private final AuthenticationManager authentificationManager;

    @Autowired
    private final SecurityContextRepository securityContextRepository;

    @Autowired
    JwtUtil jwtUtil;

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
        ServerLogoutHandler securityContext = new SecurityContextServerLogoutHandler();
        ClearSiteDataServerHttpHeadersWriter writer = new ClearSiteDataServerHttpHeadersWriter(CACHE, COOKIES);
        ServerLogoutHandler clearSiteData = new HeaderWriterServerLogoutHandler(writer);
        DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(securityContext, clearSiteData);

        return serverHttpSecurity.
                csrf().tokenFromMultipartDataEnabled(true).and().
                logout(logout -> {
                    logout.logoutHandler(logoutHandler).
                            logoutUrl("/signout")
                            .logoutSuccessHandler(new ServerLogoutSuccessHandler() {
                                @Override
                                public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
                                    System.out.println("onLogoutSuccess");
                                    ServerHttpResponse response = exchange.getExchange().getResponse();
                                    response.setStatusCode(HttpStatus.FOUND);
                                    response.getHeaders().setLocation(URI.create("/loginpage"));
                                    ResponseCookie jwt = ResponseCookie.from("jwt", jwtUtil.createExpiredJwtToken()).maxAge(1).build(); // установил время 1 чтоб кукис был просроченым и не позволял себя использовать иначе хер знает как его удалить
                                    response.addCookie(jwt);

                                    return exchange.getExchange().getSession()
                                            .flatMap(WebSession::invalidate);
                                }
                            });
                }).
                /* .logoutSuccessHandler(new ServerLogoutSuccessHandler() {
           @Override
           public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
               ServerHttpResponse response = exchange.getExchange().getResponse();
               response.setStatusCode(HttpStatus.FOUND);
               response.getHeaders().setLocation(URI.create("/loginpage"));
               response.getCookies().remove("SESSION");
               System.out.println(response.getCookies());
               response.getCookies().remove("jwt");
               System.out.println(response.getCookies().getFirst("jwt"));
               System.out.println("securityWebFilterChain");
             //TODO Костыль не знаю как исправить чтоб не было старого куки
               User u = new User();
               u.setName("vas");
               u.setEmail("ddsd");
               u.getRoles().add(new Role());
           //    ResponseCookie jwt = ResponseCookie.from("jwt", jwtUtil.createJwtToken(u)).httpOnly(true).build();
             //  response.addCookie(jwt);
               response.getCookies().remove(exchange.getExchange().getRequest().getCookies().getFirst("jwt"));
               *//*return exchange.getExchange().getSession()
                        .flatMap(WebSession::invalidate);*//*
                return Mono.empty();
            }
        })*/
                //.and().
                        formLogin().loginPage("/loginpage").
                        and().
                        httpBasic().disable().
                        authenticationManager(authentificationManager).
                        securityContextRepository(securityContextRepository).
                        authorizeExchange().
                        pathMatchers("/webjars/**", "/loginpage", "/login", "/logout", "/favicon.ico", "/register").permitAll().
                // pathMatchers("/d").hasAnyRole("ACCOUNT", "ADMIN").
                        pathMatchers("/admin/**").hasRole("ADMIN").
                        pathMatchers("/account/**").hasAnyRole("ACCOUNT", "ADMIN").
                        anyExchange().authenticated().
                //   anyExchange().permitAll().
                        and().
                        build();
    }

}
