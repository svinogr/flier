package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.Role;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.UserRole;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(value = "/account")
public class AcountCtrl {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public Mono<String> register(Model model) {
        User user = new User();

        return Mono.just(user).flatMap(u -> {
            model.addAttribute("user", user);
            return Mono.just("accountpage");
        });
    }

    @PostMapping("/users")
    public Mono<String> saveOrUpdateUser(User user, Model model) {
        UserRole roleAccount = UserRole.ROLE_ACCOUNT;

        Role role = new Role();
        role.setName(roleAccount.name());

        user.getRoles().add(role);

       return userService.registerUser(user)
               .flatMap(u -> Mono.just("redirect:/admin/users"))
               .switchIfEmpty(Mono.just("redirect:/admin/users"));
    }
}
