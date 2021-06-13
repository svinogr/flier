package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
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

    @Autowired
    ShopService shopService;

    @GetMapping("shops")
    public String getAllShop(Model model) {
        Flux<Shop> all = shopService.getAllShops().sort(Comparator.comparingLong(Shop::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1, 1);
        model.addAttribute("shops", reactiveDataDrivenMode);

        return "adminshoppage";
    }

    @GetMapping("shops/{id}")
    public Mono<String> getAllShop(@PathVariable String id, Model model) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        Mono<Shop> shopById = shopService.getShopById(parseId);
        return shopById.flatMap(s -> {
                model.addAttribute("shop", s);
                return Mono.just("redirect:/admin/shops");
        }).switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }

    @GetMapping("users")
    public String getAllUser(Model model) {
        Flux<User> all = userService.findAll().sort(Comparator.comparingLong(User::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1, 1);
        model.addAttribute("users", reactiveDataDrivenMode);

        return "adminmainpage";
    }

    @GetMapping("users/{id}")
    public Mono<String> getUserById(ServerWebExchange exchange, @PathVariable String id, Model model) {
        Mono<User> userById;
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/users");
        }

        if (parseId == 0) {
            userById = Mono.just(new User());
        } else {
            userById = userService.findUserById(parseId);
        }

        return userById.flatMap(u ->{
            model.addAttribute("user", u);
            return Mono.just("userpage");
        } ).switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @PostMapping("users/{id}")
    public Mono<String> saveOrUpdateUser(User user, Model model) {
        Mono<User> userDb;
        if (user.getId() == null) {
            userDb = userService.registerUser(user);
        } else {
            userDb = userService.update(user);
        }

        return userDb.flatMap(u -> {
            return Mono.just("redirect:/admin/users");
        }).switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @PostMapping("users/del/{id}")
    public Mono<String> deleteUserById(@PathVariable String id, Model model) {
        return userService.deleteUser(Long.parseLong(id)).flatMap(u -> {
            return Mono.just("redirect:/admin/users");
        });
    }
}
