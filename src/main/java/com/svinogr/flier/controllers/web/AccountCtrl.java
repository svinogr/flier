package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/account/")
public class AccountCtrl {
    @Autowired
    private Util utilService;

    @Autowired
    private StockService stockService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    @ModelAttribute("admin")
    public Mono<Boolean> isAdmin() {
        return userService.isAdmin();
    }

    @ModelAttribute("principal")
    public Mono<User> principal() {
        return userService.getPrincipal();
    }

    //ver 2
    @GetMapping("accountpage/{id}")
    public Mono<String> accountPage(@PathVariable String id, Model model) {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return userService.getPrincipal().
                flatMap(userPrincipal -> {

                    return userService.isOwnerOfAccount(userId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);

                        return userService.isAdmin().flatMap(admin -> {
                                    IReactiveDataDriverContextVariable shops =
                                            new ReactiveDataDriverContextVariable(shopService.
                                                    getShopByUserId(userPrincipal.getId()), 1, 1);

                                    model.addAttribute("shops", shops);
                                    model.addAttribute("user", userPrincipal);
                                  //  model.addAttribute("admin", admin);

                                    return Mono.just("accountpage");
                                }
                        );
                    });
                });
    }

    // ver 2
    @GetMapping("accountpage/{id}/update")
    public Mono<String> updateAccountPage(@PathVariable String id, Model model) {
        Long userId;
        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return userService.getPrincipal().
                flatMap(userPrincipal -> {
                    return userService.isOwnerOfAccount(userId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);
                        return userService.isAdmin().flatMap(admin -> {

                                    model.addAttribute("user", userPrincipal);
                                 //   model.addAttribute("admin", admin);

                                    return Mono.just("accountuserpage");

                                }
                        );
                    });
                });
    }

    //ver 2
    @PostMapping("accountpage/{id}/update")
    public Mono<String> saveOrUpdateAccount(@PathVariable String id, User user) {
        Long userId;

        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return userService.getPrincipal()
                .flatMap(userPrincipal -> {
                    return userService.isOwnerOfAccount(userId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);

                        return userService.update(user)
                                .flatMap(u1 -> Mono.just("redirect:/account/accountpage/" + userPrincipal.getId()))
                                .switchIfEmpty(Mono.just("redirect:/account/accountpage" + userPrincipal.getId()));
                    });
                });
    }

    // ver2
    @GetMapping("accountpage/{id}/delete")
    public Mono<String> deleteUserById(@PathVariable String id) {
        long userId;
        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/users");
        }

        return userService.isAdmin().flatMap(ok -> {
            if (!ok) return Mono.just("forbidenpage");

            return userService.deleteUser(userId).flatMap(u -> {
                return Mono.just("redirect:/account/accountpage/" + userId);
            });
        });
    }

}

