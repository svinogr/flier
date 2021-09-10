package com.svinogr.flier.services;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.shop.Shop;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShopService {
    Mono<Shop> createShop(Shop shop);

    Mono<Shop> updateShop(Shop shop);

    Mono<Shop> deleteShopById(Long shopId);

    Mono<Shop> getShopById(Long shopId);

    Flux<Shop> getAllShops();

    Flux<Shop> getAllActiveShops();

    Flux<Shop> getShopsByUserId(Long shopId);

    Mono<Boolean> isOwnerOfShop(Long shopId);

    Mono<Shop> restoreShop(Long shopId);

    Flux<Shop> getPersonalShopByTitle(String title);

    Flux<Shop> getPersonalShopByAddress(String address);

    Flux<Shop> searchPersonalByValueAndType(SearchType type, String value);

    Mono<Shop> getPersonalShopById(Long shopId);

    Mono<Long> getCountShopsByUserId(Long userId);

    Mono<Long>  getCountSearchPersonalByValue(SearchType type, String value);

    Mono<Long> getCountShops();

    Mono<Long> getCountSearchByValue(SearchType type, String value);

    Flux<Shop> searchByValueAndType(SearchType type, String value);

}
