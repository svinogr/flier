package com.svinogr.flier.controllers.web;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Controller
@RequestMapping("/account/")
public class AccountCtrl {
    @Autowired
    private FileService fileService;

    @Autowired
    private Util utilService;

    @Autowired
    private StockService stockService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;


    /*   @GetMapping("accountpage")
       public Mono<String> accountPage(Model model) {
           return utilService.getPrincipal().
                   flatMap(u -> {
                       IReactiveDataDriverContextVariable shops =
                               new ReactiveDataDriverContextVariable(shopService.getShopByUserId(u.getId()), 1, 1);

                       model.addAttribute("shops", shops);
                       model.addAttribute("user", u);

                       return Mono.just("accountpage");
                   });
       }*/
    //ver 2
    @GetMapping("accountpage/{id}")
    public Mono<String> accountPage(@PathVariable String id, Model model) {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return utilService.getPrincipal().
                flatMap(userPrincipal -> {
                    if (!isOwnerAccount(userPrincipal, userId)) return Mono.just(utilService.FORBIDEN_PAGE);

                    return utilService.isAdmin().flatMap(ok -> {
                                IReactiveDataDriverContextVariable shops =
                                        new ReactiveDataDriverContextVariable(shopService.getShopByUserId(userPrincipal.getId()), 1, 1);

                                model.addAttribute("shops", shops);
                                model.addAttribute("user", userPrincipal);
                                model.addAttribute("admin", ok);

                                return Mono.just("accountpage");
                            }
                    );

                });
    }

/*

    @GetMapping("{id}")
    public Mono<String> updateAccountPage(@PathVariable String id, Model model) {
        Long parsedShopId;
        try {
            parsedShopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just("redirect:/account/accountpage");
        }

        return utilService.getPrincipal().
                flatMap(userPrincipal -> {
                    if (!isOwnerAccount(userPrincipal, parsedShopId)) return Mono.just(utilService.FORBIDEN_PAGE);

                    return userService.findUserById(parsedShopId)
                            .flatMap(user1 -> {
                                model.addAttribute("user", user1);

                                return Mono.just("accountuserpage");
                            });
                });
    }

    private boolean isOwnerAccount(User userPrincipal, Long accountId) {
        return userPrincipal.getId() == accountId;
    }
*/

    // ver 2
    @GetMapping("accountpage/{id}/update")
    public Mono<String> updateAccountPage(@PathVariable String id, Model model) {
        Long userId;
        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return utilService.getPrincipal().
                flatMap(userPrincipal -> {
                    if (!isOwnerAccount(userPrincipal, userId)) return Mono.just(utilService.FORBIDEN_PAGE);

                    return utilService.isAdmin().flatMap(ok -> {

                        model.addAttribute("user", userPrincipal);
                        model.addAttribute("admin", ok);

                        return Mono.just("accountuserpage");
                    });

                });
    }

    private boolean isOwnerAccount(User userPrincipal, Long accountId) {
        return userPrincipal.getId() == accountId;
    }

/*
    @PostMapping("{id}")
    public Mono<String> saveOrUpdateAccount(@PathVariable String id, User user) {
        Long parsedShopId;

        try {
            parsedShopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return utilService.getPrincipal()
                .flatMap(userPrincipal -> {
                    if (!isOwnerAccount(userPrincipal, parsedShopId)) return Mono.just(utilService.FORBIDEN_PAGE);

                    return userService.update(user)
                            .flatMap(u1 -> Mono.just("redirect:/account/accountpage"))
                            .switchIfEmpty(Mono.just("redirect:/account/accountpage"));
                });
    }*/

//ver 2
    @PostMapping("accountpage/{id}/update")
    public Mono<String> saveOrUpdateAccount(@PathVariable String id, User user) {
        Long userId;

        try {
            userId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return utilService.getPrincipal()
                .flatMap(userPrincipal -> {
                    if (!isOwnerAccount(userPrincipal, userId)) return Mono.just(utilService.FORBIDEN_PAGE);

                    return userService.update(user)
                            .flatMap(u1 -> Mono.just("redirect:/account/accountpage/" + userPrincipal.getId()))
                            .switchIfEmpty(Mono.just("redirect:/account/accountpage"+ userPrincipal.getId()));
                });
    }
  /*  private boolean isOwner(User user, Long parseId) {
        if (user.getId() == parseId) {
            return true;
        }
        return false;
    }*/

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
            return Mono.just("redirect:/account/accountpage");
        }

        return utilService.getPrincipal().
                flatMap(user -> {
                    return isOwnerShop(user, parsedShopId).flatMap(ok -> {
                        if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);
                        return shopService.getShopById(parsedShopId).
                                flatMap(s -> {
                                    return utilService.isAdmin().flatMap(isAdmin -> {
                                        System.out.println(isAdmin);
                                        model.addAttribute("admin", isAdmin);
                                        model.addAttribute("shop", s);

                                        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                                                new ReactiveDataDriverContextVariable(stockService.findStocksByShopId(parsedShopId), 1, 1);
                                        model.addAttribute("stocks", reactiveDataDrivenMode);

                                        return Mono.just("shoppage");
                                    });


                                }).switchIfEmpty(Mono.just("redirect:/accountpage"));
                    });

                });
    }

    private Mono<Boolean> isOwnerShop(User userPrincipal, Long shopId) {
        return shopService.getShopById(shopId).
                flatMap(shop ->
                        Mono.just(shop.getUserId() == userPrincipal.getId())
                );

    }

    @GetMapping("updateshoppage/{id}")
    public Mono<String> getUpdateShopPage(@PathVariable String id, Model model) {
        Long parseId;
        try {
            parseId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }


        if (parseId == 0) {
            Shop shop = new Shop();
            shop.setId(parseId);
            shop.setImg(utilService.defaultShopImg);
            shop.setStocks(new ArrayList());
            shop.setStatus(Status.ACTIVE.name());

            return Mono.just(shop).flatMap(s -> {
                model.addAttribute("admin", utilService.isAdmin());
                model.addAttribute("shop", s);
                return Mono.just("updateshoppage");
            });
        } else {

            return utilService.getPrincipal().flatMap(user -> isOwnerShop(user, parseId).flatMap(ok -> {
                if (!ok) return Mono.just(utilService.FORBIDEN_PAGE);

                return shopService.getShopById(parseId).flatMap(s -> {
                    model.addAttribute("admin", utilService.isAdmin());
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
    @PostMapping("updateshoppage/{id}")
    public Mono<String> updateShop(@PathVariable String id, @RequestPart("imgTypeAction") String imgTypeAction,
                                   @RequestPart("file") Mono<FilePart> file, Shop shop) {
        long parseShopId;

        try {
            parseShopId = Long.parseLong(id);

        } catch (NumberFormatException e) {
            return Mono.just(utilService.FORBIDEN_PAGE);
        }

        return utilService.getPrincipal().
                flatMap(userPrincipal -> {
                    if (parseShopId == 0) { // создание нового
                        shop.setId(null);
                        shop.setUserId(userPrincipal.getId());
                        return shopService.createShop(shop).flatMap(s -> {
                            switch (imgTypeAction) {
                                case "0":
                                    return Mono.just("redirect:/account/accountpage");
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
                                    }).flatMap(sh -> shopService.updateShop(sh).flatMap(sf -> Mono.just("redirect:/account/accountpage")));

                                case "-1":
                                    // сброс на дефолтную картинку и удаление старой из базы
                                    return Mono.just("redirect:/account/accountpage");
                                default:
                                    return Mono.just(utilService.FORBIDEN_PAGE);
                            }
                        });
                    } else { // обновление уже созданого
                        return isOwnerShop(userPrincipal, parseShopId).flatMap(ok -> {
                            if (!ok) {
                                return Mono.just(utilService.FORBIDEN_PAGE);
                            } else {
                                return shopService.updateShop(shop).flatMap(s -> {
                                    switch (imgTypeAction) {
                                        case "0":
                                            return Mono.just("redirect:/account/accountpage");
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
                                                return Mono.just("redirect:/account/accountpage");
                                            });
                                        case "-1":
                                            // сброс на дефолтную картинку и удаление старой из базы
                                            return fileService.deleteImageForShop(shop.getImg()).flatMap(
                                                    n -> {
                                                        shop.setImg(n);
                                                        return Mono.just(shop);
                                                    }
                                            ).flatMap(sh1 -> shopService.updateShop(shop).flatMap(sh -> Mono.just("redirect:/account/accountpage")));

                                        default:
                                            return Mono.just(utilService.FORBIDEN_PAGE);
                                    }
                                });
                            }

                        });

                    }

                });
    }
}

