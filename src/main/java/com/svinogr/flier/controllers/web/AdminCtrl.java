package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCtrl {
    @Autowired
    UserRepo userRepo;

    @GetMapping("users")
    public String getAllUser(Model model){

        Flux<User> all = Flux.just(new User(), new User());
     //   Flux<User> all = userRepo.findAll();


        Flux<String> just = Flux.just("dddd", "ddd");
        List<String> list = new ArrayList<>();
       all.subscribe(System.out::println);

        model.addAttribute("user", all.collectList().block());
        return "mainpage";
    }
}
