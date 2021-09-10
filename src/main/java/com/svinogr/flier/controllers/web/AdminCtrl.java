package com.svinogr.flier.controllers.web;

import com.svinogr.flier.controllers.web.utils.PaginationUtil;
import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.controllers.web.utils.Util;
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
    public Mono<String> getAllShop(@RequestParam(value = "page", defaultValue = "1") String page, Model model) {
        int numberPage;

        try {
            numberPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.getCountShops().
                flatMap(count -> {
                    PaginationUtil myPage = new PaginationUtil(numberPage, count);
                    System.out.println(myPage);
                    if (myPage.getPages() > 0) {
                        model.addAttribute("pagination", myPage);
                    }

                    return Mono.just(myPage);
                }).
                flatMap(myPage -> {
                    if (myPage.getPages() > 0) {
                        model.addAttribute("shops", shopService.getAllShops().sort(Comparator.comparingLong(Shop::getId))
                                .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                    }
                    System.out.println(myPage);
                    return Mono.just("adminshopspage");
                }).
                onErrorReturn(utilService.FORBIDDEN_PAGE);
    }

    @GetMapping("shop/shoppage/{id}")
    public Mono<String> getShopPage(@PathVariable String id, @RequestParam(value = "page", defaultValue = "1") String page, Model model) {
        Long shopId;
        int numberPage;

        try {
            shopId = Long.parseLong(id);
            numberPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.getShopById(shopId).
                flatMap(shop -> {
                    model.addAttribute("shop", shop);
                    return Mono.just(shop);
                }).
                flatMap(shop -> {
                    return stockService.getCountStocksByShopId(shop.getId()).
                            flatMap(count -> {
                                PaginationUtil myPage = new PaginationUtil(numberPage, count);
                                System.out.println(myPage);
                                if (myPage.getPages() > 0) {
                                    model.addAttribute("pagination", myPage);
                                }

                                return Mono.just(myPage);
                            }).
                            flatMap(myPage -> {
                                if (myPage.getPages() > 0) {
                                    model.addAttribute("stocks", stockService.findStocksByShopId(shopId).sort(Comparator.comparingLong(Stock::getId))
                                            .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                                }

                                return Mono.just("adminshoppage");
                            });
                });
    }


    @GetMapping("shop/shoppage/{id}/update")
    public Mono<String> getUpdateShopPage(@PathVariable String id, Model model) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
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
            return Mono.just(utilService.FORBIDDEN_PAGE);
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
                    default:
                        return Mono.just("forbidenpage");
                }
            });
        }

    }

    @GetMapping("shop/shoppage/{id}/delete")
    public Mono<String> delShopById(@PathVariable String id) {
        Long shopId;
        try {
            shopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.deleteShopById(shopId).
                flatMap(shop -> {
                    return Mono.just("redirect:/admin/shops");
                });
    }

    @GetMapping("shop/searchshops")
    public Mono<String> searchShops(@RequestParam("type") String type,
                                    @RequestParam(value = "value", defaultValue = "") String value,
                                    @RequestParam(value = "page", defaultValue = "1") String page, Model model) {
        int numberPage;
        SearchType searchType;

        try {
            numberPage = Integer.parseInt(page);
            searchType = SearchType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }
        return shopService.getCountSearchByValue(searchType, value).
                flatMap(count -> {
                    System.out.println(count);
                    PaginationUtil myPage = new PaginationUtil(numberPage, count);

                    if (myPage.getPages() > 0) {
                        model.addAttribute("pagination", myPage);
                    }

                    return Mono.just(myPage);
                }).
                flatMap(myPage -> {
                    if (myPage.getPages() > 0) {

                        model.addAttribute("shops", shopService.searchByValueAndType(searchType, value)
                                .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                    }

                    return Mono.just("adminshopspage");
                }).
                switchIfEmpty(
                         Mono.just("redirect:/admin/shops"));
    }


    @GetMapping("shop/shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> getStockPage(@PathVariable String idSh, @PathVariable String idSt, Model model) {
        long shopId, stockId;

        try {
            shopId = Long.parseLong(idSh);
            stockId = Long.parseLong(idSt);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
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

    @GetMapping("shop/shoppage/{id}/restore")
    public Mono<String> restoreShop(@PathVariable String id) {
        Long shopId;

        try {
            shopId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }
        return shopService.restoreShop(shopId).
                flatMap(shop -> {
                    return Mono.just("redirect:/admin/shops/");
                }).switchIfEmpty(Mono.just("redirect:/admin/shops/"));
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
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        if (stock.getId() == 0) { // создание нового
            stock.setId(null);
            stock.setShopId(shopId);
            return stockService.createStock(stock).flatMap(s -> {
                switch (imgTypeAction) {
                    case "0":
                        if (shopId == 0) return  Mono.just("redirect:/admin/stocks/");

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
                                        .flatMap(sf ->{
                                            if (shopId == 0) return  Mono.just("redirect:/admin/stocks/");

                                            return Mono.just("redirect:/admin/shop/shoppage/" + shopId);
                                        }));

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

    }

    @PostMapping("stock/stockpage/{id}/delete")
    public Mono<String> delStockById(@PathVariable String id) {
        Long stockId;
        try {
            stockId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return stockService.deleteStockById(stockId)
                .flatMap(s -> Mono.just("redirect:/admin/shop/shoppage/" + s.getShopId()))
                .switchIfEmpty(Mono.just("redirect:/admin/shop/shoppage/" + stockId));

    }

    @PostMapping("shop/shoppage/{idSh}/stockpage/{idSt}/restore")
    public Mono<String> restoreStockById(@PathVariable String idSh, @PathVariable String idSt) {
        long stockId;
        long shopId;
        try {
            stockId = Long.parseLong(idSt);
            shopId = Long.parseLong(idSh);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return stockService.restoreStockById(stockId).
                flatMap(stock -> Mono.just("redirect:/admin/shop/shoppage/" + shopId)).
                switchIfEmpty(Mono.just("redirect:/admin/shop/shoppage/" + shopId));
    }

    @GetMapping("users")
    public Mono<String> getAllUser(@RequestParam(value = "page", defaultValue = "1") String page,  Model model) {
        int numberPage;

        try {
            numberPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.getCountUsers().
                flatMap(count -> {
                    PaginationUtil myPage = new PaginationUtil(numberPage, count);
                    System.out.println(myPage);
                    if (myPage.getPages() > 0) {
                        model.addAttribute("pagination", myPage);
                    }

                    return Mono.just(myPage);
                }).
                flatMap(myPage -> {
                    if (myPage.getPages() > 0) {
                        model.addAttribute("users", userService.findAll().sort(Comparator.comparingLong(User::getId))
                                .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                    }
                    System.out.println(myPage);
                    return Mono.just("adminuserspage");
                }).
                onErrorReturn(utilService.FORBIDDEN_PAGE);

    }

    @GetMapping("accountpage/{id}")
    public Mono<String> getUserById(@PathVariable String id, Model model) {
        Mono<User> userById;
        Long userId;

        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
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
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.deleteUser(userId).
                flatMap(user -> {
                    return Mono.just("redirect:/admin/users");
                });
    }

    @GetMapping("stocks")
    public Mono<String> getAllStocks(@RequestParam(value = "page", defaultValue = "1") String page,  Model model) {
        int numberPage;

        try {
            numberPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return stockService.getCountStocks().
                flatMap(count -> {
                    PaginationUtil myPage = new PaginationUtil(numberPage, count);
                    System.out.println(myPage);
                    if (myPage.getPages() > 0) {
                        model.addAttribute("pagination", myPage);
                    }

                    return Mono.just(myPage);
                }).
                flatMap(myPage -> {
                    if (myPage.getPages() > 0) {
                        model.addAttribute("stocks", stockService.findAll().sort(Comparator.comparingLong(Stock::getId))
                                .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                    }
                    System.out.println(myPage);
                    return Mono.just("adminstockspage");
                }).
                onErrorReturn(utilService.FORBIDDEN_PAGE);

    }

}
