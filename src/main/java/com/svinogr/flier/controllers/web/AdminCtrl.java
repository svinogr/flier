package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.Role;
import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.UserRole;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.services.FileService;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCtrl {
    @Autowired
    UserService userService;

    @Autowired
    ShopService shopService;

    @Autowired
    StockService stockService;

    @Autowired
    private FileService fileService;

    @Value("${upload.shop.imgPath}")
    private String upload;

    @Value("${upload.shop.defaultImg}")
    private String defaultShopImg;

    @GetMapping("shops")
    public String getAllShop(Model model) {
        Flux<Shop> all = shopService.getAllShops().sort(Comparator.comparingLong(Shop::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1, 1);
        model.addAttribute("shops", reactiveDataDrivenMode);

        return "adminshoppage";
    }

    @GetMapping("shoppage/{id}")
    public Mono<String> getShopPage(@PathVariable String id, Model model) {
        Long parseId;

        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        Mono<Shop> shopById = shopService.getShopById(parseId);

        Flux<Stock> stocks = stockService.findStocksByShopId(parseId);

        return shopById.flatMap(s -> {
            model.addAttribute("admin", isAdmin());
            model.addAttribute("shop", s);

            IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                    new ReactiveDataDriverContextVariable(stocks, 1, 1);
            model.addAttribute("stocks", reactiveDataDrivenMode);

            return Mono.just("shoppage");
        }).switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }

    private boolean isAdmin() {
        return true;
    }


    @GetMapping("updateshoppage/{id}")
    public Mono<String> getUpdateshopPage(@PathVariable String id, Model model) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }
        Mono<Shop> shopById;
        if (parseId == 0) {
            Shop shop = new Shop();
            shop.setId(parseId);
            shop.setImg(defaultShopImg);
            shop.setStocks(new ArrayList());
            shop.setStatus(Status.ACTIVE.name());
            shopById = Mono.just(shop);
        } else {
            shopById = shopService.getShopById(parseId);
        }

        return shopById.flatMap(s -> {
            model.addAttribute("admin", isAdmin());
            model.addAttribute("shop", s);
            return Mono.just("updateshoppage");
        }).switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }



   /* @GetMapping("image/shop/{id}")
    public Mono<Resource> getImage(@PathVariable String id) throws IOException {
    //    System.out.println( request.getURI().getPath());
        File file = new File("./img/shop/" + id);
        System.out.println(file.length());
      *//*  String path = "./img/shop/" + id;
        byte[] data = Files.readAllBytes(Path.of(classPathResource.getPath()));*//*
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        return Mono.just(fileSystemResource);
    }*/

    /**
     * value imgTypeAction.
     * 0 - nothing to do
     * -1 set default
     * 1 set new from file
     */
    @PostMapping("shops/{id}")
    public Mono<String> updateShop(@PathVariable String id, @RequestPart("imgTypeAction") String imgTypeAction, @RequestPart("file") Mono<FilePart> file, Shop shop) {

        long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        boolean isOwner = isOwnerOrAdmin(parseId);
        if (!isOwner) {
            return Mono.just("forbidenpage");
        }

        if (shop.getId() == 0) { // создание нового
            shop.setId(null);
            return shopService.createShop(shop).flatMap(s -> {
                switch (imgTypeAction) {
                    case "0":
                        return Mono.just("redirect:/admin/shops");
                    case "1":
                        return file.flatMap(f -> {
                            if (f.filename().equals("")) {
                                s.setImg(defaultShopImg);
                                return Mono.just(s);// поменять на стринг
                            }

                            return fileService.saveImgByIdForShop(f, s.getId()).flatMap(
                                    n -> {
                                        s.setImg(n);
                                        return Mono.just(s);
                                    }
                            );
                        }).flatMap(sh -> shopService.updateShop(sh).flatMap(sf -> Mono.just("redirect:/admin/shops")));

                    case "-1":
                        // сброс на дефолтную картинку и удаление старой из базы
                        return Mono.just("redirect:/admin/shops");
                    default:
                        return Mono.just("forbidenpage");
                }
            });
        } else { // обновление уже созданого
            return shopService.createShop(shop).flatMap(s -> {
                switch (imgTypeAction) {
                    case "0":
                        return Mono.just("redirect:/admin/shops");
                    case "1":
                        return file.flatMap(f -> {
                            if (f.filename().equals("")) {
                                shop.setImg(defaultShopImg);
                                return Mono.just(shop);
                            }
                            return
                                    fileService.deleteImageForShop(shop.getImg()).flatMap(n -> fileService.saveImgByIdForShop(f, shop.getId()).flatMap(
                                            name -> {
                                                s.setImg(name);
                                                return Mono.just(s);
                                            }));
                        }).flatMap(sh -> {
                            shopService.updateShop(sh).subscribe();
                            return Mono.just("redirect:/admin/shops");
                        });
                    case "-1":
                        // сброс на дефолтную картинку и удаление старой из базы
                        return fileService.deleteImageForShop(shop.getImg()).flatMap(
                                n -> {
                                    shop.setImg(n);
                                    return Mono.just(shop);
                                }
                        ).flatMap(sh1 -> shopService.updateShop(shop).flatMap(sh -> Mono.just("redirect:/admin/shops")));
                 /*
                        shop.setImg(defaultShopImg);
                        shopService.updateShop(shop).subscribe();
                        return Mono.just("redirect:/admin/shops");*/
                    default:
                        return Mono.just("forbidenpage");
                }
            });

        }

    }

    private boolean isOwnerOrAdmin(Long parseId) {
        //Todo проверка на собственника или админ
        return true;
    }

    @PostMapping("shops/del/{id}")
    public Mono<String> delShopById(@PathVariable String id) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        Mono<Shop> delShop;

        if (isOwnerOrAdmin(parseId)) {
            delShop = shopService.deleteShopById(parseId);
        } else {
            return Mono.just("forbidenpage");
        }

        return delShop.flatMap(s -> Mono.just("redirect:/admin/shops")).
                switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }


    @GetMapping("users")
    public String getAllUser(Model model) {
        Flux<User> all = userService.findAll().sort(Comparator.comparingLong(User::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1, 1);
        model.addAttribute("users", reactiveDataDrivenMode);

        return "adminmainpage";
    }

    @GetMapping("users/{id}")
    public Mono<String> getUserById(@PathVariable String id, Model model) {
        Mono<User> userById;
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/users");
        }

        if (parseId == 0) {
            User user = new User();
            Role role = new Role();
            role.setName(UserRole.ROLE_USER.name());
            user.getRoles().add(role);
            userById = Mono.just(user);
        } else {
            userById = userService.findUserById(parseId);
        }

        return userById.flatMap(u -> {
            model.addAttribute("user", u);
            return Mono.just("userpage");
        }).switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @PostMapping("users/{id}")
    public Mono<String> saveOrUpdateUser(User user, Model model) {
        Mono<User> userDb;

        if (user.getId() == null) {
            userDb = userService.registerUser(user);
        } else {
            userDb = userService.update(user);
        }

        return userDb.flatMap(u -> {
            return Mono.just("redirect:/admin/users");
        }).switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @GetMapping("users/del/{id}")
    public Mono<String> deleteUserById(@PathVariable String id) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/users");
        }

        Mono<User> delUser;

        if (isOwnerOrAdmin(parseId)) {
            delUser = userService.deleteUser(parseId);
        } else {
            return Mono.just("forbidenpage");
        }

        return delUser.flatMap(s -> Mono.just("redirect:/admin/users")).
                switchIfEmpty(Mono.just("redirect:/admin/users"));
    }
}
