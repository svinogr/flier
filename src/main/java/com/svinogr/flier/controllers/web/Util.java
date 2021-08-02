package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.Role;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.UserRole;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class Util {
    @Value("${upload.shop.defaultImg}")
    public String defaultShopImg;

    @Value("${upload.stock.defaultImg}")
    public String defaultStockImg;

    public final String FORBIDEN_PAGE = "forbidenpage";

/*    public Mono<User> getPrincipal() {
        //TODO изменить на настоящего юзера
        return ReactiveSecurityContextHolder.getContext().
                flatMap(sC -> Mono.just(sC.getAuthentication().getPrincipal())).cast(User.class);


        *//* *//**//*ReactiveSecurityContextHolder.getContext().flatMap(sC -> Mono.just(sC.getAuthentication().getPrincipal())).subscribe(s->{
         System.out.println("principal " + s);
     });*//**//*
        return   userService.findUserById(1L).flatMap(user -> {
            return   ReactiveSecurityContextHolder.getContext().flatMap(sC -> Mono.just(sC.getAuthentication().getPrincipal())).flatMap(s->{

                System.out.println(s);
                return Mono.just(user);});

            });*//*

    }*/

   /* public Mono<Boolean> isAdmin() {
        return getPrincipal().
                flatMap(u -> Mono.just(u.getRoles().get(0).getName().equals(UserRole.ROLE_ADMIN.name())));
    }*/

/*    private Mono<Boolean> isOwnerShop(User userPrincipal, Long shopId) {
        return shopService.getShopById(shopId).
                flatMap(shop ->
                        Mono.just(shop.getUserId() == userPrincipal.getId())
                );

    }*/
}
