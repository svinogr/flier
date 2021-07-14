package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import reactor.core.publisher.Mono;

public class Util {
    public static Mono<User> getPrincipal(){
        //TODO изменить на настоящего юзера
        User user = new User();
        user.setId(1L);
        user.setUsername("vasya");
        user.setEmail("vasya");
        user.setName("vasya");
        user.setPhone("vasya");
        user.setSurname("vasya");
        return Mono.just(user);
    }
}
