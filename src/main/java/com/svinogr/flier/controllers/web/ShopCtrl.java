package com.svinogr.flier.controllers.web;

import com.svinogr.flier.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class ShopCtrl {
/*    @Autowired
    ShopService shopService;
    @GetMapping("shops")
    public String getAllShop(Model model) {
        Flux<User> all = userService.findAll().sort(Comparator.comparingLong(User::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1,1);
        model.addAttribute("users", reactiveDataDrivenMode);

        return "accountmainpage";
}*/
}
