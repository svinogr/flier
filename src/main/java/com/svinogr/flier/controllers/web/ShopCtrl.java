package com.svinogr.flier.controllers.web;

import com.svinogr.flier.controllers.web.utils.ImageTypeAction;
import com.svinogr.flier.controllers.web.utils.PaginationUtil;
import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.controllers.web.utils.Util;
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
import reactor.core.publisher.Mono;

import java.security.AccessControlException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class for managing web pages of shops
 */
@Controller
@RequestMapping("shop")
public class ShopCtrl {
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
     * GET method for getting page of shop by id
     *
     * @param id    shop id from db
     * @param model {@link Model}
     * @return name of  page for shop by id
     */
    @GetMapping("shoppage/{id}")
    public Mono<String> getShopPage(@PathVariable String id, @RequestParam(value = "page", defaultValue = "1") String page, Model model) {
        Long shopId;
        int numberPage;

        try {
            shopId = Long.parseLong(id);
            numberPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return userService.getPrincipal().flatMap(user -> Mono.just("redirect:/account/accountpage/" + user.getId()));
        }

        return userService.getPrincipal().flatMap(user -> {
            return shopService.isOwnerOfShop(shopId).
                    flatMap(ok -> {
                        System.out.println(ok);
                        if (!ok) return Mono.error(new AccessControlException("access denied"));

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

                                                return Mono.just("shoppage");
                                            });
                                });
                    }).
                    onErrorReturn(utilService.FORBIDDEN_PAGE);
        });

    }

    /**
     * GET method for getting page for update or create shop
     *
     * @param id    shop id from db. 0 for new shop
     * @param model {@link Model}
     * @return name of page of shop by id
     */
    @GetMapping("shoppage/{id}/update")
    public Mono<String> getUpdateShopPage(@PathVariable String id, Model model) {
        Long shopid;
        try {
            shopid = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        if (shopid == 0) {
            Shop shop = new Shop();
            shop.setId(shopid);
            shop.setImg(utilService.defaultShopImg);
            shop.setStocks(new ArrayList());
            shop.setStatus(Status.ACTIVE.name());

            return userService.getPrincipal().
                    flatMap(user -> {
                        shop.setUserId(user.getId());
                        model.addAttribute("shop", shop);
                        return Mono.just("updateshoppage");
                    });
        } else {

            return userService.getPrincipal().flatMap(user -> shopService.isOwnerOfShop(shopid).
                    flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDDEN_PAGE);

                        return shopService.getShopById(shopid).flatMap(s -> {
                            model.addAttribute("shop", s);
                            return Mono.just("updateshoppage");
                        });
                    }));
        }
    }


    /**
     * POST method for creating or updating shop
     *
     * @param id            shop id from db
     * @param imgTypeAction {@link ImageTypeAction}
     * @param file          file with img
     * @param shop          for update
     * @return name of web page after saving or creating shop
     */
    @PostMapping("shoppage/{id}/update")
    public Mono<String> updateShop(@PathVariable String id, @RequestPart("imgTypeAction") String imgTypeAction,
                                   @RequestPart("file") Mono<FilePart> file, Shop shop) {
        long parseShopId;
        ImageTypeAction imageTypeAction;

        try {
            parseShopId = Long.parseLong(id);
            imageTypeAction = ImageTypeAction.valueOf(imgTypeAction);
        } catch (IllegalArgumentException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.getPrincipal().
                flatMap(userPrincipal -> {
                    if (parseShopId == 0) { // создание нового
                        shop.setId(null);
                        shop.setUserId(userPrincipal.getId());
                        return shopService.createShop(shop).flatMap(s -> {
                            switch (imageTypeAction) {
                                case NOTHING:
                                    return Mono.just("redirect:/account/accountpage/" + userPrincipal.getId());
                                case IMG:
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

                                case DEFAULT:
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    return Mono.just("redirect:/account/accountpage");
                                default:
                                    return Mono.just(utilService.FORBIDDEN_PAGE);
                            }
                        });
                    } else { // обновление уже созданого
                        return shopService.isOwnerOfShop(parseShopId).flatMap(ok -> {
                            if (!ok) {
                                return Mono.just(utilService.FORBIDDEN_PAGE);
                            } else {
                                return shopService.updateShop(shop).flatMap(s -> {
                                    switch (imageTypeAction) {
                                        case NOTHING:
                                            return Mono.just("redirect:/account/accountpage/" + userPrincipal.getId());
                                        case IMG:
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
                                        case DEFAULT:
                                            // сброс на дефолтную картинку и удаление старой из базы
                                            return fileService.deleteImageForShop(shop.getImg()).flatMap(
                                                    n -> {
                                                        shop.setImg(n);
                                                        return Mono.just(shop);
                                                    }
                                            ).flatMap(sh1 -> shopService.updateShop(shop).flatMap(sh -> Mono.just("redirect:/account/accountpage/" + userPrincipal.getId())));

                                        default:
                                            return Mono.just(utilService.FORBIDDEN_PAGE);
                                    }
                                });
                            }

                        });

                    }
                });
    }

    /**
     * GET method for deleting shop by id
     *
     * @param id shop id from db
     * @return name of web page after deleting shop
     */
    //TODO возможно стоит изменить на Post
    @GetMapping("shoppage/{id}/delete")
    public Mono<String> deleteShop(@PathVariable String id) {
        Long shopId;

        try {
            shopId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(owner -> {
                    if (!owner) return Mono.just(utilService.FORBIDDEN_PAGE);

                    return userService.getPrincipal().
                            flatMap(principal -> {
                                return shopService.deleteShopById(shopId).
                                        flatMap(shop -> {
                                            return Mono.just("redirect:/account/accountpage/" + principal.getId());
                                        }).switchIfEmpty(Mono.just("redirect:/account/accountpage/" + principal.getId()));
                            });
                });
    }

    /**
     * GET method for restoring shop by id
     *
     * @param id shop id from db
     * @return name of web page after restoring shop
     */
    @GetMapping("shoppage/{id}/restore")
    public Mono<String> restoreShop(@PathVariable String id) {
        Long shopId;

        try {
            shopId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId)
                .flatMap(owner -> {
                    if (!owner) return Mono.just(utilService.FORBIDDEN_PAGE);

                    return userService.getPrincipal().
                            flatMap(principal -> {
                                return shopService.restoreShop(shopId).
                                        flatMap(shop -> {
                                            return Mono.just("redirect:/account/accountpage/" + principal.getId());
                                        }).switchIfEmpty(Mono.just("redirect:/account/accountpage/" + principal.getId()));
                            });
                })
                .switchIfEmpty(Mono.just(utilService.FORBIDDEN_PAGE));
    }

    /**
     * GET method for getting web page of stock by id shop and stock id
     *
     * @param idSh  shop id from db
     * @param idSt  stock id from db
     * @param model {@link Model}
     * @return name of web page stock by id
     */
    @GetMapping("shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> getStockPage(@PathVariable String idSh, @PathVariable String idSt, Model model) {
        Long shopId, stockId;

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
                    //  model.addAttribute("admin", userService.isAdmin());
                    model.addAttribute("stock", s);

                    return Mono.just("stockpage");
                })
                .switchIfEmpty(Mono.just(utilService.FORBIDDEN_PAGE));
    }


    /**
     * POST method for creating or updating stock by shop id and shop id
     *
     * @param idSh          shop id from db
     * @param idSt          stock id from db
     * @param imgTypeAction {@link ImageTypeAction}
     * @param file          file file with img
     * @param stock         {@link Stock}
     * @return name of web page after creating or updating stock
     */
    @PostMapping("shoppage/{idSh}/stockpage/{idSt}")
    public Mono<String> updateStock(@PathVariable String idSh, @PathVariable String
            idSt, @RequestPart("imgTypeAction") String imgTypeAction, @RequestPart("file") Mono<FilePart> file, Stock
                                            stock) {
        long shopId, stockId;
        ImageTypeAction imageTypeAction;

        try {
            shopId = Long.parseLong(idSh);
            stockId = Long.parseLong(idSt);
            imageTypeAction = ImageTypeAction.valueOf(imgTypeAction);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(shopOwner -> {
                    if (!shopOwner) return Mono.just(utilService.FORBIDDEN_PAGE);

                    if (stockId == 0) { // создание нового
                        stock.setId(null);
                        stock.setShopId(shopId);

                        return stockService.createStock(stock).flatMap(s -> {
                            switch (imageTypeAction) {
                                case NOTHING:
                                    return Mono.just("redirect:/shop/shoppage/" + shopId);// подозрительное место почему строка а не обьект
                                case IMG:
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

                                case DEFAULT:
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    // странное место тоже
                                    return Mono.just("redirect:/shop/shoppage/" + shopId);
                                default:
                                    return Mono.just(utilService.FORBIDDEN_PAGE);
                            }
                        });
                    } else { // обновление уже созданого
                        return stockService.isOwnerOfStock(shopId, stockId).
                                flatMap(stockOwner -> {
                                    if (!stockOwner) return Mono.just(utilService.FORBIDDEN_PAGE);

                                    return stockService.updateStock(stock).flatMap(s -> {
                                        System.out.println(stock);
                                        switch (imageTypeAction) {
                                            case NOTHING:
                                                return Mono.just("redirect:/shop/shoppage/" + shopId);
                                            case IMG:
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
                                                    return stockService.updateStock(sh).flatMap(stock1 -> {
                                                        return Mono.just("redirect:/shop/shoppage/" + shopId);
                                                    });

                                                });
                                            case DEFAULT:
                                                System.out.println(2);
                                                // сброс на дефолтную картинку и удаление старой из базы
                                                return fileService.deleteImageForStock(stock.getImg()).flatMap(
                                                        n -> {
                                                            stock.setImg(n);
                                                            return Mono.just(stock);
                                                        }
                                                ).flatMap(sh1 -> stockService.updateStock(stock).flatMap(sh -> Mono.just("redirect:/shop/shoppage/" + shopId)));
                                            default:
                                                return Mono.just(utilService.FORBIDDEN_PAGE);
                                        }
                                    });

                                });
                    }
                });
    }

    /**
     * POST method for deleting stock by shop id and stock id
     *
     * @param idSh stock id from db
     * @param idSt shop id from db
     * @return name of web page after deleteing stock
     */
    @PostMapping("shoppage/{idSh}/stockpage/{idSt}/delete")
    public Mono<String> delStockById(@PathVariable String idSh, @PathVariable String idSt) {
        long stockId;
        long shopId;
        try {
            stockId = Long.parseLong(idSt);
            shopId = Long.parseLong(idSh);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(shopOwner -> {
                    if (!shopOwner) return Mono.just(utilService.FORBIDDEN_PAGE);

                    return stockService.isOwnerOfStock(shopId, stockId).
                            flatMap(stockOwner -> {
                                if (!stockOwner) return Mono.just(utilService.FORBIDDEN_PAGE);

                                return stockService.deleteStockById(stockId).
                                        flatMap(stock -> Mono.just("redirect:/shop/shoppage/" + shopId)).
                                        switchIfEmpty(Mono.just("redirect:/shop/shoppage/" + shopId));
                            });
                });
    }

    /**
     * POST method for restoring stock by shop id and stock id
     *
     * @param idSh shop id from db
     * @param idSt stock id from db
     * @return name of web page after restoring stock
     */
    @PostMapping("shoppage/{idSh}/stockpage/{idSt}/restore")
    public Mono<String> restoreStockById(@PathVariable String idSh, @PathVariable String idSt) {
        long stockId;
        long shopId;

        try {
            stockId = Long.parseLong(idSt);
            shopId = Long.parseLong(idSh);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return shopService.isOwnerOfShop(shopId).
                flatMap(shopOwner -> {
                    if (!shopOwner) return Mono.just(utilService.FORBIDDEN_PAGE);

                    return stockService.isOwnerOfStock(shopId, stockId).
                            flatMap(stockOwner -> {
                                if (!stockOwner) return Mono.just(utilService.FORBIDDEN_PAGE);

                                return stockService.restoreStockById(stockId).
                                        flatMap(stock -> Mono.just("redirect:/shop/shoppage/" + shopId)).
                                        switchIfEmpty(Mono.just("redirect:/shop/shoppage/" + shopId));
                            });
                });
    }

    /**
     * GET method for searching stock's shop by page
     *
     * @param type {@link SearchType}
     * @param value searching value
     * @param page number of page
     * @param model {@link Model}
     * @param id shop id from db
     * @return name of web page with result of searching
     */
    @GetMapping("shoppage/{id}/searchstocks")
    public Mono<String> searchShops(@RequestParam("type") String type,
                                    @RequestParam(value = "value", defaultValue = "") String value,
                                    @RequestParam(value = "page", defaultValue = "1") String page, Model model, @PathVariable String id) {
        long shopId;
        int numberPage;
        SearchType searchType;

        try {
            shopId = Long.parseLong(id);
            numberPage = Integer.parseInt(page);
            searchType = SearchType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return Mono.just(utilService.FORBIDDEN_PAGE);
        }

        return userService.getPrincipal().flatMap(user -> {
            return shopService.isOwnerOfShop(shopId).
                    flatMap(ok -> {
                        System.out.println(ok);
                        if (!ok) return Mono.error(new AccessControlException("access denied"));

                        return shopService.getShopById(shopId).
                                flatMap(shop -> {
                                    model.addAttribute("shop", shop);
                                    return Mono.just(shop);
                                }).flatMap(shop -> {
                            return stockService.getCountSearchPersonalByValue(searchType, value, shop.getId()).
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
                                            model.addAttribute("stocks", stockService.searchPersonalByValueAndType(searchType, value, shopId).sort(Comparator.comparingLong(Stock::getId))
                                                    .skip((numberPage - 1) * PaginationUtil.ITEM_ON_PAGE).take(PaginationUtil.ITEM_ON_PAGE));
                                        }

                                        return Mono.just("shoppage");
                                    });
                        });
                    }).
                    switchIfEmpty(
                            userService.getPrincipal().flatMap(principal -> Mono.just("redirect:/shop/shoppage/" + shopId))).
                    onErrorReturn(utilService.FORBIDDEN_PAGE);
        });
    }
}
