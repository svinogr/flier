package com.svinogr.flier.services;

import com.svinogr.flier.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Flux<Shop> getShopsByUserId(Long shopId);

    Mono<Boolean> isOwnerOfShop(Long shopId);

    Mono<Shop> restoreShop(Long shopId);

    Flux<Shop> getPersonalShopByTitle(String title);

    Flux<Shop> getPersonalShopByAddress(String address);

    Flux<Shop> searchPersonalByValueAndType(String type, String value);

    Mono<Shop> getPersonalShopById(Long shopId);

    Mono<Long> getCountShopsByUserId(Long userId);

    Mono<Long>  getCountSearchPersonalByValue(String type, String value);
}
