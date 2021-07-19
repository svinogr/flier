package com.svinogr.flier.config.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

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

                formLogin().loginPage("loginpage").
                and().
                httpBasic().disable().
                authenticationManager(authentificationManager).
                securityContextRepository(securityContextRepository).
                authorizeExchange().
                pathMatchers("/webjars/**", "/loginpage", "/favicon.ico", "/**").permitAll().
                // pathMatchers("/d").hasAnyRole("ACCOUNT", "ADMIN").
                //  pathMatchers("/admin").hasRole("ADMIN").
                // anyExchange().authenticated().
                        anyExchange().permitAll().
                        and().
                        build();
    }

}
