package com.svinogr.flier.controllers.web;

import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;


import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class WebCtrl {
    @Autowired
    private UserService userService;

    @GetMapping()
    public String mainPage(Model model) {

        return "adminmainpage";
    }


    @GetMapping("/loginpage")
    public String loginPage(Model model) {

        return "loginpage";
    }


}

