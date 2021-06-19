package com.svinogr.flier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class WebSecurityConfig {


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

                formLogin().loginPage("loginpage").
                and().
                httpBasic().disable().
                authorizeExchange().
                pathMatchers("/webjars/**", "/loginpage", "/favicon.ico", "/**").permitAll().
                //  pathMatchers("/").hasAnyRole("ACCOUNT", "ADMIN").
                //  pathMatchers("/admin").hasRole("ADMIN").
                // anyExchange().authenticated().
                        anyExchange().permitAll().
                        and().
                        build();
    }

}
