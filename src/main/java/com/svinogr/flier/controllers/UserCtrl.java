package com.svinogr.flier.controllers;

import com.svinogr.flier.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController()
@RequestMapping("/api/user")
public class UserCtrl {
/*    @Autowired
    private UserService userService;

    @GetMapping
    public Flux<User> allUser(){
        return userService.getAlluser();
    }*/
}
