package com.svinogr.flier.controllers.web;

import com.svinogr.flier.config.jwt.JwtUtil;
import com.svinogr.flier.model.Role;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.UserRole;
import com.svinogr.flier.services.UserService;
import io.netty.handler.codec.http.cookie.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.HttpCookie;
import java.net.http.HttpResponse;
import java.util.Map;

@Controller
@RequestMapping
public class RegistrationCtrl {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Util util;


    @GetMapping("/register")
    public Mono<String> register(Model model) {
        User user = new User();

        return Mono.just(user).flatMap(u -> {
            model.addAttribute("user", user);
            return Mono.just("register");
        });
    }


    @PostMapping("/register")
    public Mono<String> saveOrUpdateUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = CtrlUtils.getErrors(bindingResult);
            model.addAttribute("error", errors);
            model.addAttribute("user", user);
            return Mono.just("register");
        }
// TODO проверить есть ли такой юзер
        UserRole roleAccount = UserRole.ROLE_ACCOUNT;

        Role role = new Role();
        role.setName(roleAccount.name());

        user.getRoles().add(role);

        return userService.registerUser(user)
                .flatMap(u -> Mono.just("redirect:/admin/users"))
                .switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @PostMapping("/login")
    public Mono<String> login(User user, ServerWebExchange swe) {
        System.out.println("login");
        System.out.println(user);
        System.out.println(swe.getRequest().getCookies());
  /*      User u = new User();
        Role r = new Role();
        r.setName(UserRole.ROLE_USER.name());
        u.getRoles().add(r);*/
  /*      MultiValueMap<String, ResponseCookie> cookies = swe.getResponse().getCookies();
        ResponseCookie jwt = ResponseCookie.from("jwt", jwtUtil.createJwtToken(u)).httpOnly(true).build();*/
        //     swe.getResponse().getCookies().add("SESSION", jwt);
        //   return  Mono.just("accountpage");
        return userService.findUserByEmail(user.getEmail())
                .flatMap(u -> {
                            if (passwordEncoder.matches(user.getPassword(), u.getPassword())) {
                                System.out.println("user found");

                                ResponseCookie jwt = ResponseCookie.from("jwt", jwtUtil.createJwtToken(u)).httpOnly(true).build();
                                swe.getResponse().getCookies().add("jwt", jwt);

                                return Mono.just("redirect:/account/accountpage");
                            } else {
                                System.out.println("user not found");
                                return Mono.just("redirect:/loginpage");
                            }


                        }
                        //               Mono.just("accountpage") : Mono.just("loginpage")
                        //  Mono.just(ResponseEntity.ok().build()) : Mono.just(ResponseEntity.badRequest().build())

                ).defaultIfEmpty("redirect:/loginpage");

   /*     return webExchange.getFormData().
                flatMap(credential ->
                        userService.findUserByName(credential.getFirst("username"))
                                .cast(User.class)
                                .flatMap(userDetails ->
                                        passwordEncoder.matches(credential.getFirst("password"), userDetails.getPassword()) ?
                                                *//*   ResponseEntity.ok(jwtUtil.createJwtToken(userDetails)) : ResponseEntity.badRequest()*//*
                                                Mono.just("accountpage") : Mono.just(util.FORBIDEN_PAGE)
                                ));*/


    }
}
