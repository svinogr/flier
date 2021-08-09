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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

@Controller
@RequestMapping("/admin")
public class AdminCtrl {
    @Autowired
    private Util utilService;

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

    @ModelAttribute("principal")
    public Mono<User> principal() {
        return userService.getPrincipal();
    }

    @ModelAttribute("admin")
    public Mono<Boolean> isAdmin() {
        return userService.isAdmin();
    }

    @GetMapping("shops")
    public String getAllShop(Model model) {
        Flux<Shop> all = shopService.getAllShops().sort(Comparator.comparingLong(Shop::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1, 1);
        model.addAttribute("shops", reactiveDataDrivenMode);

        return "adminshopspage";
    }

    @GetMapping("shop/shoppage/{id}")
    public Mono<String> getShopPage(@PathVariable String id, Model model) {
        Long shopId;

        try {
            shopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return shopService.getShopById(shopId).
                flatMap(shop -> {
                    model.addAttribute("shop", shop);

                    IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                            new ReactiveDataDriverContextVariable(stockService.findStocksByShopId(shop.getId()), 1, 1);
                    model.addAttribute("stocks", reactiveDataDrivenMode);

                    return Mono.just("adminshoppage");
                });
    }


    @GetMapping("shop/shoppage/{id}/update")
    public Mono<String> getUpdateShopPage(@PathVariable String id, Model model) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        Mono<Shop> shopById;

        if (parseId == 0) {
            Shop shop = new Shop();
            shop.setId(parseId);
            shop.setImg(utilService.defaultShopImg);
            shop.setStocks(new ArrayList());
            shop.setStatus(Status.ACTIVE.name());
            shopById = Mono.just(shop);
        } else {
            shopById = shopService.getShopById(parseId);
        }

        return shopById.flatMap(s -> {
            model.addAttribute("shop", s);
            return Mono.just("adminupdateshoppage");
        }).switchIfEmpty(Mono.just("redirect:/admin/shops"));
    }


    /**
     * value imgTypeAction.
     * 0 - nothing to do
     * -1 set default
     * 1 set new from file
     */
    @PostMapping("shop/shoppage/{id}/update")
    public Mono<String> updateShop(@PathVariable String id, @RequestPart("imgTypeAction") String imgTypeAction,
                                   @RequestPart("file") Mono<FilePart> file, Shop shop) {
        long shopId;
        try {
            shopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        if (shopId == 0) { // создание нового
            shop.setId(null);
            return shopService.createShop(shop).flatMap(s -> {
                switch (imgTypeAction) {
                    case "0":
                        return Mono.just("redirect:/admin/shops");
                    case "1":
                        return file.flatMap(f -> {
                            if (f.filename().equals("")) {
                                s.setImg(utilService.defaultShopImg);
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
                        return Mono.just("redirect:/admin/shop/shoppage/" + shop.getId());
                    case "1":
                        return file.flatMap(f -> {
                            if (f.filename().equals("")) {
                                shop.setImg(utilService.defaultShopImg);
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
                            return Mono.just("redirect:/admin/shop/shoppage/" + shop.getId());
                        });
                    case "-1":
                        // сброс на дефолтную картинку и удаление старой из базы
                        return fileService.deleteImageForShop(shop.getImg()).flatMap(
                                n -> {
                                    shop.setImg(n);
                                    return Mono.just(shop);
                                }
                        ).flatMap(sh1 -> shopService.updateShop(shop).flatMap(sh -> Mono.just("redirect:/admin/shop/shoppage/" + shop.getId())));
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

    @PostMapping("shops/del/{id}")
    public Mono<String> delShopById(@PathVariable String id) {
        Long shopId;
        try {
            shopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }
        return shopService.deleteShopById(shopId).
                flatMap(shop -> {
                    return Mono.just("redirect:/admin/shops");
                });
    }

    @GetMapping("shop/shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> getStockPage(@PathVariable String idSh, @PathVariable String idSt, Model model) {
        long shopId, stockId;

        try {
            shopId = Long.parseLong(idSh);
            stockId = Long.parseLong(idSt);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        Mono<Stock> stockById;
        if (stockId == 0) {
            Stock stock = new Stock();
            stock.setId(stockId);
            stock.setShopId(shopId);
            stock.setImg(utilService.defaultStockImg);
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
                        stock.setImg(utilService.defaultStockImg);
                        stock.setStatus(Status.ACTIVE.name());
                        stock.setDateStart(LocalDateTime.now());
                        stock.setDateFinish(LocalDateTime.now());
                        return Mono.just(stock);
                    }

                    return Mono.just(s);
                })
                .flatMap(s -> {
                    model.addAttribute("stock", s);

                    return Mono.just("adminstockpage");
                })
                .switchIfEmpty(Mono.just("redirect:/admin/shop/shoppage/" + shopId));
    }

    /**
     * value imgTypeAction.
     * 0 - nothing to do
     * -1 set default
     * 1 set new from file
     */
    @PostMapping("shop/shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> updateStock(@PathVariable String idSh, @PathVariable String idSt, @RequestPart("imgTypeAction") String imgTypeAction, @RequestPart("file") Mono<FilePart> file, Stock stock) {
        long shopId, stockId;
        try {
            shopId = Long.parseLong(idSh);
            stockId = Long.parseLong(idSt);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return userService.isAdmin().
                flatMap(admin -> {
                    if (!admin) return Mono.just(utilService.FORBIDEN_PAGE);

                    if (stock.getId() == 0) { // создание нового
                        stock.setId(null);
                        stock.setShopId(shopId);
                        return stockService.createStock(stock).flatMap(s -> {
                            switch (imgTypeAction) {
                                case "0":
                                    return Mono.just("redirect:/admin/shop/shoppage/" + shopId);
                                case "1":
                                    return file.flatMap(f -> {
                                        if (f.filename().equals("")) {
                                            s.setImg(utilService.defaultStockImg);
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
                                                    .flatMap(sf -> Mono.just("redirect:/admin/shop/shoppage/" + shopId)));

                                case "-1":
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    // странное место тоже
                                    return Mono.just("redirect:/admin/shop/shoppage/" + shopId);
                                default:
                                    return Mono.just("forbidenpage");
                            }
                        });
                    } else { // обновление уже созданого
                        return stockService.updateStock(stock).flatMap(s -> {
                            switch (imgTypeAction) {
                                case "0":
                                    return Mono.just("redirect:/admin/shop/shoppage/" + shopId);
                                case "1":
                                    System.out.println(1);
                                    return file.flatMap(f -> {
                                        if (f.filename().equals("")) {
                                            stock.setImg(utilService.defaultStockImg);
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
                                        return Mono.just("redirect:/admin/shop/shoppage/" + shopId);
                                    });
                                case "-1":
                                    System.out.println(2);
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    return fileService.deleteImageForStock(stock.getImg()).flatMap(
                                            n -> {
                                                stock.setImg(n);
                                                return Mono.just(stock);
                                            }
                                    ).flatMap(sh1 -> stockService.updateStock(stock).flatMap(sh -> Mono.just("redirect:/admin/shop/shoppage/" + shopId)));
                                default:
                                    return Mono.just("forbidenpage");
                            }
                        });
                    }
                });
    }

    @PostMapping("stock/stockpage/{id}/delete")
    public Mono<String> delStockById(@PathVariable String id) {
        Long stockId;
        try {
            stockId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return stockService.deleteStockById(stockId)
                .flatMap(s -> Mono.just("redirect:/admin/shop/shoppage/" + s.getShopId()))
                .switchIfEmpty(Mono.just("redirect:/admin/shop/shoppage/" + stockId));

    }


    @GetMapping("users")
    public String getAllUser(Model model) {
        Flux<User> all = userService.findAll().sort(Comparator.comparingLong(User::getId));

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(all, 1, 1);
        model.addAttribute("users", reactiveDataDrivenMode);

        return "adminuserspage";
    }

    @GetMapping("accountpage/{id}")
    public Mono<String> getUserById(@PathVariable String id, Model model) {
        Mono<User> userById;
        Long userId;

        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        if (userId == 0) {
            User user = new User();
            Role role = new Role();
            role.setName(UserRole.ROLE_USER.name());
            user.getRoles().add(role);
            userById = Mono.just(user);
        } else {
            userById = userService.findUserById(userId);
        }

        return userById.flatMap(u -> {
            model.addAttribute("user", u);
            return Mono.just("adminuserpage");
        }).switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @PostMapping("accountpage/{id}")
    public Mono<String> saveOrUpdateUser(User user, Model model) {
        Mono<User> userDb;

        if (user.getId() == null) {
            userDb = userService.registerUser(user);
        } else {
            userDb = userService.update(user);
        }

        return userDb.flatMap(u -> Mono.just("redirect:/admin/users")).switchIfEmpty(Mono.just("redirect:/admin/users"));
    }

    @GetMapping("accountpage/{id}/delete")
    public Mono<String> deleteUserById(@PathVariable String id) {
        long userId;
        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return userService.deleteUser(userId).
                flatMap(user -> {
                    return Mono.just("redirect:/admin/users");
                });
    }
}
