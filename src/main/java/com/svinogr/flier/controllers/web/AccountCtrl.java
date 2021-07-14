package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/account/")
public class AccountCtrl {
    @Autowired
    private StockService stockService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;


    @GetMapping("accountpage")
    public Mono<String> accountPage(Model model) {
        return Mono.just(Util.getPrincipal()).flatMap(u -> {
            model.addAttribute("user", u);
            //TODO удалить тестовые акции
            Flux<Shop> stocksByShopId = shopService.getAllShops();
            model.addAttribute("shops", stocksByShopId);

            return Mono.just("accountpage");
        });
    }

    @GetMapping("{id}")
    public Mono<String> updateAccount(@PathVariable String id,  Model model) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/account/accountpage");
        }

        return Util.getPrincipal().
                flatMap(user -> {
                  if(user.getId() != parseId){
                      return Mono.just("forbidenpage");
                  }

                  return userService.findUserById(parseId)
                          .flatMap(user1 -> {
                              model.addAttribute("user", user1);

                              return Mono.just("accountuserpage");
                          });


                });

    }



}

