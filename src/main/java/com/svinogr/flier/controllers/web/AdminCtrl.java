package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Controller
@RequestMapping("/admin")
public class AdminCtrl {
    @Autowired
    UserService userService;

    @GetMapping("users")
    public String getAllUser(Model model) {

        //   Flux<User> all = Flux.just(new User(), new User());
        Flux<User> all = userService.findAll().sort(Comparator.comparingLong(User::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1);
        model.addAttribute("users", reactiveDataDrivenMode);
        return "mainpage";
    }

    @GetMapping("users/{id}")
    public String getUserById(@PathVariable String id, Model model) {
        Mono<User> userById;
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        }catch (NumberFormatException e){
            return "redirect:/admin/users";
        }

        if (parseId == 0) {
            userById = Mono.just(new User());
        } else {
            userById = userService.findUserById(parseId);
        }

        model.addAttribute("user", userById);

        return "userpage";
    }

    @PostMapping("users/{id}")
    public String saveOrUpdateUser(User user, Model model) {
        Mono<User> userDb;
        if (user.getId() == null) {
            userDb = userService.registerUser(user);
        } else {
            userDb = userService.update(user);
        }
        userDb.subscribe();
   /*     Flux<User> all = userService.findAll().sort(Comparator.comparingLong(User::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1);
        model.addAttribute("users", reactiveDataDrivenMode);*/
        return "redirect:/admin/users";
    }

    @PostMapping("users/del/{id}")
    public String deleteUserById(@PathVariable String id, Model model) {
        userService.deleteUser(Long.parseLong(id)).subscribe();
       /* Flux<User> all = userService.deleteUser(Long.parseLong(id)).
                flatMapMany(i -> userService.findAll());

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1);
        model.addAttribute("users", reactiveDataDrivenMode);*/
        return "redirect:/admin/users";
    }
}
