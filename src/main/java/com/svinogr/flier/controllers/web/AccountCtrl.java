package com.svinogr.flier.controllers.web;

import com.svinogr.flier.controllers.web.utils.PaginationUtil;
import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.controllers.web.utils.Util;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.AccessControlException;
import java.util.Comparator;

/**
 * @author SVINOGR
 * version 0.0.1
 *
 * Class for managing web pages of account
 */
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

    /**
     * GET method for getting page of account
     *
     * @param id    id account from db
     * @param page  number page received from request
     * @param model {@link Model model
     * @return name of web page of account
     */
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
        // с этим не работает обьект в нексколькх местах сразу
                                  /*  IReactiveDataDriverContextVariable shops =
                                            new ReactiveDataDriverContextVariable(shopService.
                                                    getShopByUserId(userPrincipal.getId()), 10, 1);*/
        return userService.getPrincipal().
                flatMap(principal -> {
                    return userService.isOwnerOfAccount(userId).
                            flatMap(ok -> {
                                if (!ok) return Mono.error(new AccessControlException("access denied"));

                                return Mono.just(ok);
                            }).
                            flatMap(ok -> {
                                return shopService.getCountShopsByUserId(principal.getId()).
                                        flatMap(count -> {
                                            PaginationUtil myPage = new PaginationUtil(numberPage, count);

                                            if (myPage.getPages() > 0) {
                                                model.addAttribute("pagination", myPage);
                                            }

                                            return Mono.just(myPage);
                                        }).
                                        flatMap(myPage -> {
                                            if (myPage.getPages() > 0) {
                                                model.addAttribute("shops", shopService.getShopsByUserId(principal.getId()).sort(Comparator.comparingLong(Shop::getId))
                                                        .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                                            }

                                            return Mono.just("accountpage");
                                        });
                            }).onErrorReturn(utilService.FORBIDDEN_PAGE);
                });
    }

    /**
     * GET method for getting page of updating signed user's account
     *
     * @param id    id account from db
     * @param model {@link Model model
     * @return name of web page for update account
     */
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
                                    return Mono.just("accountuserpage");
                                }
                        );
                    });
                });
    }

    /**
     * POST method for saving changes of user's account
     *
     * @param id   id account from db
     * @param user {@link User user
     * @return name of web page with result of update account
     */
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

    /**
     * GET method for deleting page of user's account
     *
     * @param id id account from db
     * @return name of page with result of delete account
     */
    //TODO возможно стоит изменить на POST
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

    /**
     * GET method for getting page result of searching shops of signed user
     *
     * @param type  {@link com.svinogr.flier.controllers.web.utils.SearchType}
     * @param value value of searching
     * @param page  number page of result searching
     * @param model {@link Model}
     * @param id    id account from db
     * @return name of page with result of searching with params
     */
    @GetMapping("accountpage/{id}/searchshops")
    public Mono<String> searchShops(@RequestParam("type") String type,
                                    @RequestParam(value = "value", defaultValue = "") String value,
                                    @RequestParam(value = "page", defaultValue = "1") String page, Model model, @PathVariable String id) {
        int numberPage;
        SearchType searchType;
        try {
            numberPage = Integer.parseInt(page);
            searchType = SearchType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.getCountSearchPersonalByValue(searchType, value).
                flatMap(count -> {
                    System.out.println(count);
                    PaginationUtil myPage = new PaginationUtil(numberPage, count);

                    if (myPage.getPages() > 0) {
                        model.addAttribute("pagination", myPage);
                    }

                    return Mono.just(myPage);
                }).
                flatMap(myPage -> {
                    if (myPage.getPages() > 0) {

                        model.addAttribute("shops", shopService.searchPersonalByValueAndType(searchType, value).sort(Comparator.comparingLong(Shop::getId))
                                .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                    }

                    return Mono.just("accountpage");
                }).
                switchIfEmpty(
                        userService.getPrincipal().flatMap(principal -> Mono.just("redirect:/account/accountpage/" + principal.getId())));
    }
}
