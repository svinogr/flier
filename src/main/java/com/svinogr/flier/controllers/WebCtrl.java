package com.svinogr.flier.controllers;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebCtrl {
    @Autowired
    private UserService userService;

    @GetMapping()
    public String mainPage() {
        User user = new User();
        user.setPassword("123");
        userService.registerUser(user);
        return "mainpage";
    }
}
