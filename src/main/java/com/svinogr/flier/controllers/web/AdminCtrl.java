package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.services.ShopService;
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
        Mono<Shop> shopById;
        if (parseId == 0) {
            shopById = Mono.just(new Shop());
        } else {
            shopById = shopService.getShopById(parseId);
        }

        return shopById.flatMap(s -> {
                model.addAttribute("admin", true);
                model.addAttribute("shop", s);
                return Mono.just("shoppage");
        }).switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }

    @PostMapping("shops/{id}")
    public Mono<String> updateShop(@PathVariable String id, Shop shop) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        boolean isOwner = isOwnerOrAdmin(parseId);
        if (!isOwner) {
            return Mono.just("forbidenpage");
        }

        Mono<Shop> updateShop;

        if (shop.getId() != null) {
            updateShop = shopService.updateShop(shop);
        }else {
            updateShop = shopService.createShop(shop);
        }

        return updateShop.flatMap(s -> Mono.just("redirect:/admin/shops")).
                switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }

    private boolean isOwnerOrAdmin(Long parseId) {
        //Todo проверка на собственника или админ
        return true;
    }

    @GetMapping("shops/del/{id}")
    public Mono<String> delShopById(@PathVariable String id) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        Mono<Shop> delShop;

        if(isOwnerOrAdmin(parseId)) {
            delShop = shopService.deleteShopById(parseId);
        } else {
            return  Mono.just("forbidenpage");
        }

        return delShop.flatMap(s->  Mono.just("redirect:/admin/shops")).
                switchIfEmpty( Mono.just("redirect:/admin/shops"));
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
    public Mono<String> getUserById(@PathVariable String id, Model model) {
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

    @GetMapping("users/del/{id}")
    public Mono<String> deleteUserById(@PathVariable String id) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/users");
        }

        Mono<User> delUser;

        if(isOwnerOrAdmin(parseId)) {
            delUser = userService.deleteUser(parseId);
        } else {
            return  Mono.just("forbidenpage");
        }

        return delUser.flatMap(s->  Mono.just("redirect:/admin/users")).
                switchIfEmpty( Mono.just("redirect:/admin/users"));
    }
}
