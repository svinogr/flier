package com.svinogr.flier.services;

import com.svinogr.flier.model.shop.Shop;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShopService {
    Mono<Shop> createShop(Shop shop);

    Mono<Shop> updateShop(Shop shop);

    Mono<Shop> deleteShopById(Long shopId);

    Mono<Shop> getShopById(Long shopId);

    Flux<Shop> getAllShops();

    Flux<Shop> getAllActiveShops();

    Flux<Shop> getShopByUserId(Long shopId);

    Mono<Boolean> isOwnerOfShop(Long shopId);

    Mono<Shop> restoreShop(Long shopId);

    Flux<Shop> getPersonalShopByTitle(String title);

    Flux<Shop> getPersonalShopByAddress(String address);

    Flux<Shop> searchPersonalByValue(MultiValueMap<String, String> map);
    Flux<Shop> searchPersonalByValue(String type, String value);

    Mono<Shop> getPersonalShopById(Long shopId);
}
