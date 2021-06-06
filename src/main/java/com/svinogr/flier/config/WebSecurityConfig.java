package com.svinogr.flier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class WebSecurityConfig{
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
        return serverHttpSecurity.

csrf().disable().
                formLogin().loginPage("loginpage").
                and().
                httpBasic().disable().
                authorizeExchange().
                pathMatchers( "/webjars/**", "/loginpage", "/favicon.ico", "/").permitAll().
              //  pathMatchers("/").hasAnyRole("ACCOUNT", "ADMIN").
              //  pathMatchers("/admin").hasRole("ADMIN").
                       // anyExchange().authenticated().
                        anyExchange().permitAll().
                        and().
                build();
    }

}
