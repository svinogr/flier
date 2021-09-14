package com.svinogr.flier.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class for managing login
 */
@Controller
@RequestMapping("/")
public class WebCtrl {

    /**
     * GET method for mainpage
     *
     * @param model {@link Model}
     * @return name of web main page
     */
    @GetMapping()
    public String mainPage(Model model) {
        return "adminmainpage";
    }

    /**
     * GET method for loginpage
     *
     * @param swe
     * @return name of web page for login
     */
    @GetMapping("/loginpage")
    public String loginPage(ServerWebExchange swe) {
        return "loginpage";
    }
}

