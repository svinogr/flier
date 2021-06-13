package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.repo.ShopRepo;
import com.svinogr.flier.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    ShopRepo shopRepo;

    @Override
    public Mono<Shop> createShop(Shop shop) {
        return shopRepo.save(shop);
    }

    @Override
    public Mono<Shop> updateShop(Shop shop) {
        return shopRepo.save(shop);
    }

    @Override
    public Mono<Shop> deleteShopById(Long id) {
        return shopRepo.findById(id).flatMap(s ->{
            s.setStatus(Status.NON_ACTIVE.name());
            return shopRepo.save(s);
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

    @Override
    public Flux<Shop> getAllActiveShops() {
        return shopRepo.findAll().filter(s-> s.getStatus().equals(Status.ACTIVE.name()));
    }
}
