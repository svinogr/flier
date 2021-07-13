package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class AccountCtrl {
    @GetMapping("/accountpage")
    public Mono<String> accountPage(Model model) {
        return Mono.just(Util.getPrincipal()).flatMap(u -> {
            model.addAttribute("user", u);
            return Mono.just("accountpage");
        });
    }

}

