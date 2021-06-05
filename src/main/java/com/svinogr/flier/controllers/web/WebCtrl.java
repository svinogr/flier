package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class WebCtrl {
    @Autowired
    private UserService userService;

    @GetMapping()
    public String mainPage(Model model) {

        return "mainpage";
    }


    @GetMapping("/loginpage")
    public String loginPage(Model model) {

        return "loginpage";
    }
}

