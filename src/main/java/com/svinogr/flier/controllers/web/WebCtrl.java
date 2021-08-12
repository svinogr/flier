package com.svinogr.flier.controllers.web;

import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;

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
    public String loginPage(ServerWebExchange swe) {
   //     swe.getRequest().getCookies().clear();
      //  System.out.println(swe.getResponse().getCookies().getFirst("jwt").);
        return "loginpage";
    }


}

