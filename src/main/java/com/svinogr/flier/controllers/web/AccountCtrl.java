package com.svinogr.flier.controllers.web;

import com.svinogr.flier.controllers.web.utils.PaginationUtil;
import com.svinogr.flier.controllers.web.utils.Util;
import com.svinogr.flier.model.User;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public Mono<String> accountPage(@PathVariable String id,
                                    @RequestParam(value = "page", defaultValue = "1") String page,
                                    Model model) {
        long userId;
        int numberPage;

        try {
            userId = Long.parseLong(id);
            numberPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.getPrincipal().
                flatMap(principal -> {
                    return userService.isOwnerOfAccount(userId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDDEN_PAGE);

                        // с этим не работает обьект в нексколькх местах сразу
                                  /*  IReactiveDataDriverContextVariable shops =
                                            new ReactiveDataDriverContextVariable(shopService.
                                                    getShopByUserId(userPrincipal.getId()), 10, 1);*/

                        model.addAttribute("shops", shopService.
                                getShopsByUserId(principal.getId()).skip((numberPage -1) * utilService.LIMIT_ENTITY_REQUEST).take(utilService.LIMIT_ENTITY_REQUEST));

                        Mono<PaginationUtil> myPageMono = shopService.getCountShopsByUserId(principal.getId()).flatMap(count -> {
                            PaginationUtil myPage = new PaginationUtil(numberPage, count);
                            System.out.println(myPage);

                            return Mono.just(myPage);
                        });

                        model.addAttribute("pagination", myPageMono);

                        return Mono.just("accountpage");
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
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.getPrincipal().
                flatMap(userPrincipal -> {
                    return userService.isOwnerOfAccount(userId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDDEN_PAGE);

                        return userService.isAdmin().flatMap(admin -> {

                                    //   model.addAttribute("user", userPrincipal);
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
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.getPrincipal()
                .flatMap(userPrincipal -> {
                    return userService.isOwnerOfAccount(userId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDDEN_PAGE);

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

 /*   @PostMapping("accountpage/{id}/searchshops")
    public Mono<String> searchShops(ServerWebExchange webExchange, Model model, @PathVariable String id) {
        Mono<MultiValueMap<String, String>> formData = webExchange.getFormData();

        return formData.
                flatMap(map -> {
                    //     model.addAttribute("user", userService.getPrincipal());
                    //       model.addAttribute("shops", shopService.searchByValue(map).filter(shop -> shop.getUserId() == principal.getId()));
                    model.addAttribute("shops", shopService.searchPersonalByValue(map));

                    return Mono.just("accountpage");
                });
    }
*/
    @GetMapping("accountpage/{id}/searchshops")
    public Mono<String> searchShops2(@RequestParam("type") String type, @RequestParam("value") String value, @RequestParam(value = "page", defaultValue = "0") String page, Model model, @PathVariable String id) {
        System.out.println(type + " " + value);
        long userId;
        int numberPage;

        try {
            userId = Long.parseLong(id);
            numberPage = Integer.parseInt(page) - 1;
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        model.addAttribute("shops", shopService.searchPersonalByValue(type, value)
                .skip(numberPage * utilService.LIMIT_ENTITY_REQUEST).take(utilService.LIMIT_ENTITY_REQUEST));

        Mono<PaginationUtil> myPageMono = shopService.getCountSearchPersonalByValue(type, value).flatMap(count -> {
            PaginationUtil myPage = new PaginationUtil(numberPage, count);

            return Mono.just(myPage);
        });

        model.addAttribute("pagination", myPageMono);

        return Mono.just("accountpage");
    }
}
