package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public  class Util {
    @Value("${upload.shop.defaultImg}")
    public String defaultShopImg;

    @Value("${upload.stock.defaultImg}")
    public String defaultStockImg;

//TODO удалить после реализации getPrincipal
    @Autowired
    private UserService userService;

    public  final String FORBIDEN_PAGE = "forbidenpage";

    public  Mono<User> getPrincipal(){
        //TODO изменить на настоящего юзера
      return   userService.findUserById(1L).flatMap(user -> Mono.just(user));
    }

    public  boolean isAdmin() {

        //TODO сделать реализацию проверки
        return false;
    }
}
