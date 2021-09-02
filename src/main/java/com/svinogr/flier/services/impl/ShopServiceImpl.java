package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.repo.ShopRepo;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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

    @Override
    public Flux<Shop> getPersonalShopByTitle(String title) {
        return userService.getPrincipal().
                flatMapMany(principal -> {
                    return shopRepo.findByTitleContainsIgnoreCaseAndUserId(title, principal.getId());
                });
    }

    @Override
    public Flux<Shop> getPersonalShopByAddress(String address) {
        System.out.println(address);
        return userService.getPrincipal().
                flatMapMany(principal -> {
                    return shopRepo.findByAddressContainingIgnoreCaseAndUserId(address, principal.getId());
                });
    }

    @Override
    public Mono<Shop> getPersonalShopById(Long id) {
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
    public Mono<Long> getCountSearchPersonalByValue(String type, String value) {
        System.out.println(type + "--" + value);

        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case "searchId":
                long id;
                System.out.println(1);
                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.empty();
                }
                System.out.println("type " + type + "*" + "value " + id);
                return getCountPersonalShopById(id);

            case "searchTitle":

                System.out.println(2);

                return getCountPersonalShopByTitle(value);
            case "searchAddress":

                System.out.println(3);

                return getCountPersonalShopByAddress(value);
            default:
                System.out.println(4);
                return Mono.empty();
        }
    }

    private Mono<Long> getCountPersonalShopByAddress(String address) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo. countByUserIdAndAddressContainsIgnoreCase(principal.getId(), address);
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
    public Flux<Shop> searchPersonalByValueAndType(String type, String value) {
        System.out.println(type + "--" + value);

        if (value.equals(Strings.EMPTY)) return userService.getPrincipal().
                flatMapMany(principal -> getShopsByUserId(principal.getId()));

        switch (type) {
            case "searchId":
                long id;
                System.out.println(1);
                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }
                System.out.println("type " + type + "*" + "value " + id);
                return getPersonalShopById(id).flux();

            case "searchTitle":

                System.out.println(2);

                return getPersonalShopByTitle(value);
            case "searchAddress":

                System.out.println(3);

                return getPersonalShopByAddress(value);
            default:
                System.out.println(4);
                return userService.getPrincipal().
                        flatMapMany(principal -> getShopsByUserId(principal.getId()));
        }
    }
}
