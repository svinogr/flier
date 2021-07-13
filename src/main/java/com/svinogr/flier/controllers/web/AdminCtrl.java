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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

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
    @Value("${upload.stock.defaultImg}")
    private String defaultStockImg;

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
    public Mono<String> getUpdateShopPage(@PathVariable String id, Model model) {
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

        boolean isOwner = isOwnerOrAdminOfShop(parseId);
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

    //TODO сделать не тольео для админа
    private boolean isOwnerOrAdminOfShop(Long parseId) {
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

        if (isOwnerOrAdminOfShop(parseId)) {
            delShop = shopService.deleteShopById(parseId);
        } else {
            return Mono.just("forbidenpage");
        }

        return delShop.flatMap(s -> Mono.just("redirect:/admin/shops")).
                switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }

    @GetMapping("shop/{idSh}/stockpage/{idSt}")
    public Mono<String> getStockPage(@PathVariable String idSh, @PathVariable String idSt, Model model) {
        Long shopId, stockId;

        try {
            shopId = Long.parseLong(idSh);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops/");
        }

        try {
            stockId = Long.parseLong(idSt);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/updateshoppage/" + shopId);
        }

        Mono<Stock> stockById;
        if (stockId == 0) {
            Stock stock = new Stock();
            stock.setId(stockId);
            stock.setShopId(shopId);
            stock.setImg(defaultStockImg);
            stock.setStatus(Status.ACTIVE.name());
            stock.setDateStart(LocalDateTime.now());
            stock.setDateFinish(LocalDateTime.now());
            stockById = Mono.just(stock);
        } else {
            stockById = stockService.findStockById(stockId);
        }

        //TODO можно сделать проверку на существование магазина?!
        return stockById
                .flatMap(s -> {
                    if (s.getShopId() != shopId) {
                        Stock stock = new Stock();
                        stock.setId(0L);
                        stock.setShopId(shopId);
                        stock.setImg(defaultStockImg);
                        stock.setStatus(Status.ACTIVE.name());
                        stock.setDateStart(LocalDateTime.now());
                        stock.setDateFinish(LocalDateTime.now());
                        return Mono.just(stock);
                    }

                    return Mono.just(s);
                })
                .flatMap(s -> {
                    model.addAttribute("admin", isAdmin());
                    model.addAttribute("stock", s);

                    return Mono.just("stockpage");
                })
                .switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }

    /**
     * value imgTypeAction.
     * 0 - nothing to do
     * -1 set default
     * 1 set new from file
     */
    @PostMapping("shop/{idSh}/stockpage/{idSt}")
    public Mono<String> updateStock(@PathVariable String idSh, @PathVariable String idSt, @RequestPart("imgTypeAction") String imgTypeAction, @RequestPart("file") Mono<FilePart> file, Stock stock) {
        long shopId, stockId;
        try {
            shopId = Long.parseLong(idSh);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        try {
            stockId = Long.parseLong(idSt);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/updateshoppage/" + shopId);
        }

        boolean isOwner = isOwnerOrAdminOfShop(shopId);
        if (!isOwner) {
            return Mono.just("forbidenpage");
        }
        System.out.println(stock.getDateFinish());

        if (stock.getId() == 0) { // создание нового
            stock.setId(null);
            stock.setShopId(shopId);
            return stockService.createStock(stock).flatMap(s -> {
                switch (imgTypeAction) {
                    case "0":
                        return Mono.just("redirect:/admin/shoppage/" + shopId);// подозрительное место почему строка а не обьект
                    case "1":
                        return file.flatMap(f -> {
                            if (f.filename().equals("")) {
                                s.setImg(defaultStockImg);
                                return Mono.just(s);
                            }

                            return fileService.saveImgByIdForStock(f, s.getId()).flatMap(
                                    n -> {
                                        s.setImg(n);
                                        return Mono.just(s);
                                    }
                            );
                        })
                                .flatMap(sh -> stockService.updateStock(sh)
                                        .flatMap(sf -> Mono.just("redirect:/admin/shoppage/" + shopId)));

                    case "-1":
                        // сброс на дефолтную картинку и удаление старой из базы
                        // странное место тоже
                        return Mono.just("redirect:/admin/shoppage/" + shopId);
                    default:
                        return Mono.just("forbidenpage");
                }
            });
        } else { // обновление уже созданого
            return stockService.updateStock(stock).flatMap(s -> {
                switch (imgTypeAction) {
                    case "0":
                        return Mono.just("redirect:/admin/shoppage/" + shopId);
                    case "1":
                        System.out.println(1);
                        return file.flatMap(f -> {
                            if (f.filename().equals("")) {
                                stock.setImg(defaultStockImg);
                                return Mono.just(stock);
                            }
                            return
                                    fileService.deleteImageForStock(stock.getImg())
                                            .flatMap(n -> fileService.saveImgByIdForStock(f, stock.getId())
                                                    .flatMap(
                                                            name -> {
                                                                s.setImg(name);
                                                                return Mono.just(s);
                                                            }));
                        }).flatMap(sh -> {
                            stockService.updateStock(sh).subscribe();
                            return Mono.just("redirect:/admin/shoppage/" + shopId);
                        });
                    case "-1":
                        System.out.println(2);
                        // сброс на дефолтную картинку и удаление старой из базы
                        return fileService.deleteImageForStock(stock.getImg()).flatMap(
                                n -> {
                                    stock.setImg(n);
                                    return Mono.just(stock);
                                }
                        ).flatMap(sh1 -> stockService.updateStock(stock).flatMap(sh -> Mono.just("redirect:/admin/shoppage/" + shopId)));
                    default:
                        return Mono.just("forbidenpage");
                }
            });
        }
    }

    @PostMapping("stock/del/{id}")
    public Mono<String> delStockById(@PathVariable String id) {
        Long stockId;
        try {
            stockId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/admin/shops");
        }

        if (isOwnerOrAdminOfStock(stockId)) {
            return stockService.deleteStockById(stockId)
                    .flatMap(s -> Mono.just("redirect:/admin/shoppage/" + s.getShopId()))
                    .switchIfEmpty(Mono.just("redirect:/admin/shoppage/" + stockId));
        } else {
            return Mono.just("forbidenpage");
        }
    }

    //TODO сделать не тольео для админа
    private boolean isOwnerOrAdminOfStock(Long parseId) {
        return true;
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
            model.addAttribute("admin", isAdmin());
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

        if (isOwnerOrAdminOfShop(parseId)) {
            delUser = userService.deleteUser(parseId);
        } else {
            return Mono.just("forbidenpage");
        }

        return delUser.flatMap(s -> Mono.just("redirect:/admin/users")).
                switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

}
