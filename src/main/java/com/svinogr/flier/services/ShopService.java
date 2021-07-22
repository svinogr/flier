package com.svinogr.flier.services;

import com.svinogr.flier.model.shop.Shop;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShopService {
    Mono<Shop> createShop(Shop shop);
    Mono<Shop> updateShop(Shop shop);
    Mono<Shop> deleteShopById(Long id);
    Mono<Shop> getShopById(Long id);
    Flux<Shop> getAllShops();
    Flux<Shop> getAllActiveShops();

    Flux<Shop> getShopByUserId(Long id);
}
