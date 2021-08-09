package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.services.FileService;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@RequestMapping("shop")
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
    @Autowired
    private Util utilService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private StockService stockService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @ModelAttribute("principal")
    public Mono<User> principal() {
        return userService.getPrincipal();
    }

    @ModelAttribute("admin")
    public Mono<Boolean> isAdmin() {
        return userService.isAdmin();
    }

    /**
     * @param id    id shop
     * @param model
     * @return shop page for shop with id
     */
    @GetMapping("shoppage/{id}")
    public Mono<String> getShopPage(@PathVariable String id, Model model) {
        Long parsedShopId;

        try {
            parsedShopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return userService.getPrincipal().flatMap(user -> Mono.just("redirect:/account/accountpage/" + user.getId()));
        }

        return userService.getPrincipal().
                flatMap(user -> {
                    return shopService.isOwnerOfShop(parsedShopId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);
                        return shopService.getShopById(parsedShopId).
                                flatMap(s -> {
                                    return userService.isAdmin().flatMap(isAdmin -> {
                                        //    model.addAttribute("admin", isAdmin);
                                        model.addAttribute("shop", s);

                                        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                                                new ReactiveDataDriverContextVariable(stockService.findStocksByShopId(parsedShopId), 1, 1);
                                        model.addAttribute("stocks", reactiveDataDrivenMode);

                                        return Mono.just("shoppage");
                                    });
                                }).switchIfEmpty(Mono.just("redirect:account/accountpage/" + user.getId()));
                    });
                });
    }

    @GetMapping("shoppage/{id}/update")
    public Mono<String> getUpdateShopPage(@PathVariable String id, Model model) {
        Long shopid;
        try {
            shopid = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }


        if (shopid == 0) {
            Shop shop = new Shop();
            shop.setId(shopid);
            shop.setImg(utilService.defaultShopImg);
            shop.setStocks(new ArrayList());
            shop.setStatus(Status.ACTIVE.name());

            return Mono.just(shop).flatMap(s -> {
                //   model.addAttribute("admin", userService.isAdmin());
                model.addAttribute("shop", s);
                return Mono.just("updateshoppage");
            });
        } else {

            return userService.getPrincipal().flatMap(user -> shopService.isOwnerOfShop(shopid).flatMap(ok -> {
                if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);

                return shopService.getShopById(shopid).flatMap(s -> {
                    //   model.addAttribute("admin", userService.isAdmin());
                    model.addAttribute("shop", s);
                    return Mono.just("updateshoppage");
                });
            }));
        }

      /*      return shopById.flatMap(s -> {
                model.addAttribute("admin", utilService.isAdmin());
                model.addAttribute("shop", s);
                return Mono.just("updateshoppage");
            }).switchIfEmpty(Mono.just("redirect:/account/accountpage"));*/
    }

    /**
     * value imgTypeAction.
     * 0 - nothing to do
     * -1 set default
     * 1 set new from file
     */
    @PostMapping("shoppage/{id}/update")
    public Mono<String> updateShop(@PathVariable String id, @RequestPart("imgTypeAction") String imgTypeAction,
                                   @RequestPart("file") Mono<FilePart> file, Shop shop) {
        long parseShopId;

        try {
            parseShopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return userService.getPrincipal().
                flatMap(userPrincipal -> {
                    if (parseShopId == 0) { // создание нового
                        shop.setId(null);
                        shop.setUserId(userPrincipal.getId());
                        return shopService.createShop(shop).flatMap(s -> {
                            switch (imgTypeAction) {
                                case "0":
                                    return Mono.just("redirect:/account/accountpage/" + userPrincipal.getId());
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
                                    }).flatMap(sh -> shopService.updateShop(sh).flatMap(sf -> Mono.just("redirect:/account/accountpage/" + userPrincipal.getId())));

                                case "-1":
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    return Mono.just("redirect:/account/accountpage");
                                default:
                                    return Mono.just(utilService.FORBIDEN_PAGE);
                            }
                        });
                    } else { // обновление уже созданого
                        return shopService.isOwnerOfShop(parseShopId).flatMap(ok -> {
                            if (!ok) {
                                return Mono.just(utilService.FORBIDEN_PAGE);
                            } else {
                                return shopService.updateShop(shop).flatMap(s -> {
                                    switch (imgTypeAction) {
                                        case "0":
                                            return Mono.just("redirect:/account/accountpage/" + userPrincipal.getId());
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
                                                return Mono.just("redirect:/account/accountpage/" + userPrincipal.getId());
                                            });
                                        case "-1":
                                            // сброс на дефолтную картинку и удаление старой из базы
                                            return fileService.deleteImageForShop(shop.getImg()).flatMap(
                                                    n -> {
                                                        shop.setImg(n);
                                                        return Mono.just(shop);
                                                    }
                                            ).flatMap(sh1 -> shopService.updateShop(shop).flatMap(sh -> Mono.just("redirect:/account/accountpage/" + userPrincipal.getId())));

                                        default:
                                            return Mono.just(utilService.FORBIDEN_PAGE);
                                    }
                                });
                            }

                        });

                    }
                });
    }

    @GetMapping("shoppage/{id}/delete")
    public Mono<String> deleteShop(@PathVariable String id) {
        Long shopId;

        try {
            shopId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(owner ->{
                    if (!owner) return Mono.just(utilService.FORBIDEN_PAGE);

                    return userService.getPrincipal().
                            flatMap(principal ->{
                              return   shopService.deleteShopById(shopId).
                                      flatMap(shop -> {
                                          return Mono.just("redirect:/account/accountpage/" + principal.getId());
                                      }).switchIfEmpty(Mono.just("redirect:/account/accountpage/" + principal.getId()));
                            });
                });
    }

    @GetMapping("shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> getStockPage(@PathVariable String idSh, @PathVariable String idSt, Model model) {
        Long shopId, stockId;

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
                    //  model.addAttribute("admin", userService.isAdmin());
                    model.addAttribute("stock", s);

                    return Mono.just("stockpage");
                })
                .switchIfEmpty(Mono.just(utilService.FORBIDEN_PAGE));
    }

    /**
     * value imgTypeAction.
     * 0 - nothing to do
     * -1 set default
     * 1 set new from file
     */
    @PostMapping("shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> updateStock(@PathVariable String idSh, @PathVariable String idSt, @RequestPart("imgTypeAction") String imgTypeAction, @RequestPart("file") Mono<FilePart> file, Stock stock) {
        long shopId, stockId;

        try {
            shopId = Long.parseLong(idSh);
            stockId = Long.parseLong(idSt);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(shopOwner -> {
                    if (!shopOwner) return Mono.just(utilService.FORBIDEN_PAGE);

                    if (stockId == 0) { // создание нового
                        stock.setId(null);
                        stock.setShopId(shopId);

                        return stockService.createStock(stock).flatMap(s -> {
                            switch (imgTypeAction) {
                                case "0":
                                    return Mono.just("redirect:/shop/shoppage/" + shopId);// подозрительное место почему строка а не обьект
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
                                                    .flatMap(sf -> Mono.just("redirect:/shop/shoppage/" + shopId)));

                                case "-1":
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    // странное место тоже
                                    return Mono.just("redirect:/shop/shoppage/" + shopId);
                                default:
                                    return Mono.just(utilService.FORBIDEN_PAGE);
                            }
                        });
                    } else { // обновление уже созданого
                        return stockService.isOwnerOfStock(shopId, stockId).flatMap(stockOwner -> {
                            if (!stockOwner) return Mono.just(utilService.FORBIDEN_PAGE);

                            return stockService.updateStock(stock).flatMap(s -> {
                                switch (imgTypeAction) {
                                    case "0":
                                        return Mono.just("redirect:/shop/shoppage/" + shopId);
                                    case "1":

                                        return file.flatMap(f -> {
                                            if (f.filename().equals("")) {
                                                stock.setImg(utilService.defaultStockImg);

                                                return Mono.just(stock);
                                            }
                                            return fileService.deleteImageForStock(stock.getImg())
                                                    .flatMap(n -> fileService.saveImgByIdForStock(f, stock.getId())
                                                            .flatMap(
                                                                    name -> {
                                                                        s.setImg(name);
                                                                        return Mono.just(s);
                                                                    }));
                                        }).flatMap(sh -> {
                                            //   stockService.updateStock(sh).subscribe();
                                            return stockService.updateStock(sh).flatMap(stock1 -> {
                                                return Mono.just("redirect:/shop/shoppage/" + shopId);
                                            });

                                        });
                                    case "-1":
                                        System.out.println(2);
                                        // сброс на дефолтную картинку и удаление старой из базы
                                        return fileService.deleteImageForStock(stock.getImg()).flatMap(
                                                n -> {
                                                    stock.setImg(n);
                                                    return Mono.just(stock);
                                                }
                                        ).flatMap(sh1 -> stockService.updateStock(stock).flatMap(sh -> Mono.just("redirect:/shop/shoppage/" + shopId)));
                                    default:
                                        return Mono.just(utilService.FORBIDEN_PAGE);
                                }
                            });

                        });
                    }
                });
    }

    @PostMapping("shoppage/{idSh}/stockpage/{idSt}/delete")
    public Mono<String> delStockById(@PathVariable String idSh, @PathVariable String idSt) {
        long stockId;
        long shopId;
        try {
            stockId = Long.parseLong(idSt);
            shopId = Long.parseLong(idSh);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(shopOwner -> {
                    if (!shopOwner) return Mono.just(utilService.FORBIDEN_PAGE);

                    return stockService.isOwnerOfStock(shopId, stockId).
                            flatMap(stockOwner -> {
                                if (!stockOwner) return Mono.just(utilService.FORBIDEN_PAGE);

                                return stockService.deleteStockById(stockId).
                                        flatMap(stock -> Mono.just("redirect:/shop/shoppage/" + shopId)).
                                        switchIfEmpty(Mono.just("redirect:/shop/shoppage/" + shopId));
                            });
                });
    }
}
