package com.svinogr.flier.services.impl;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.CoordHelper;
import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.repo.ShopRepo;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class service implementation {@link ShopService}
 */
@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepo shopRepo;

    @Autowired
    private UserService userService;

    @Override
    public Mono<Shop> createShop(Shop shop) {
        return shopRepo.save(shop);
    }

    @Override
    public Mono<Shop> updateShop(Shop shop) {
        return shopRepo.updateShop(shop).
                flatMap(ok -> {
                    if (ok) return getShopById(shop.getId());
                    //TODO возвращать пустой хорощо ли это??
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Shop> deleteShopById(Long id) {
        //TODO сделать удаление акции при удалении магазина
        return shopRepo.findById(id).flatMap(shop -> {
            shop.setStatus(Status.NON_ACTIVE.name());
            return shopRepo.updateShop(shop).
                    flatMap(ok -> {
                        if (ok) return getShopById(shop.getId());
                        //TODO возвращать пустой хорощо ли это??
                        return Mono.empty();
                    });
        });
    }

    @Override
    public Mono<Shop> getShopById(Long id) {
        return shopRepo.findById(id);
    }

    @Override
    public Flux<Shop> getAllShops() {
        return shopRepo.findAll();
    }

    //TODO переделать под актив саму sql
    @Override
    public Flux<Shop> getAllActiveShops() {
        return shopRepo.findAll().filter(s -> s.getStatus().equals(Status.ACTIVE.name()));
    }

    @Override
    public Flux<Shop> getShopsByUserId(Long id) {
        return shopRepo.findAllByUserId(id);
    }

    @Override
    public Mono<Boolean> isOwnerOfShop(Long shopId) {
        return getShopById(shopId).
                flatMap(shop -> userService.getPrincipal().
                        flatMap(user -> Mono.just(shop.getUserId() == user.getId()))).switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<Shop> restoreShop(Long shopId) {
        return shopRepo.findById(shopId).
                flatMap(shop -> {
                    shop.setStatus(Status.ACTIVE.name());
                    return shopRepo.updateShop(shop).
                            flatMap(ok -> {
                                if (ok) return shopRepo.findById(shop.getId());
                                //TODO возвращать пустой хорощо ли это??
                                return Mono.empty();
                            });
                });
    }


    private Flux<Shop> getPersonalShopByTitle(String title) {
        return userService.getPrincipal().
                flatMapMany(principal -> {
                    return shopRepo.findByTitleContainsIgnoreCaseAndUserId(title, principal.getId());
                });
    }


    private Flux<Shop> getPersonalShopByAddress(String address) {
        System.out.println(address);
        return userService.getPrincipal().
                flatMapMany(principal -> {
                    return shopRepo.findByAddressContainingIgnoreCaseAndUserId(address, principal.getId());
                });
    }

    private Mono<Shop> getPersonalShopById(Long id) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.findByIdAndUserId(id, principal.getId());
                });
    }

    @Override
    public Mono<Long> getCountShopsByUserId(Long userId) {
        return shopRepo.countByUserId(userId).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Long> getCountSearchPersonalByValue(SearchType type, String value) {
        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.just(0L);
                }

                return getCountPersonalShopById(id);
            case BY_TITLE:
                return getCountPersonalShopByTitle(value);
            case BY_ADDRESS:
                return getCountPersonalShopByAddress(value);
            default:
                return Mono.empty();
        }
    }

    @Override
    public Mono<Long> getCountShops() {
        return shopRepo.count();
    }

    @Override
    public Mono<Long> getCountSearchByValue(SearchType type, String value) {
        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.just(0L);
                }

                return getCountShopById(id);
            default:
                return Mono.empty();
        }
    }

    private Mono<Long> getCountShopById(long id) {
        return shopRepo.countById(id);
    }

    @Override
    public Flux<Shop> searchByValueAndType(SearchType type, String value) {
        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }

                return getShopById(id).flux();
            default:
                return Flux.empty();
        }
    }

    @Override
    public Flux<Shop> getAllShopsAroundCoord(Coord coord) {
        CoordHelper coordHelper = new CoordHelper(coord);

        return shopRepo.getShopsAroundCoord(coordHelper);
    }

    private Mono<Long> getCountPersonalShopByAddress(String address) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.countByUserIdAndAddressContainsIgnoreCase(principal.getId(), address);
                });
    }

    private Mono<Long> getCountPersonalShopByTitle(String title) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.countByUserIdAndTitleContainsIgnoreCase(principal.getId(), title);
                });
    }

    private Mono<Long> getCountPersonalShopById(long id) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.countByUserIdAndId(principal.getId(), id);
                });
    }

    @Override
    public Flux<Shop> searchPersonalByValueAndType(SearchType type, String value) {
        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }

                return getPersonalShopById(id).flux();

            case BY_TITLE:
                return getPersonalShopByTitle(value);
            case BY_ADDRESS:

                return getPersonalShopByAddress(value);
            default:
                return userService.getPrincipal().
                        flatMapMany(principal -> getShopsByUserId(principal.getId()));
        }
    }
}
