package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.PropertyShop;
import com.svinogr.flier.model.TabsOfShopProperty;
import com.svinogr.flier.repo.TabsRepo;
import com.svinogr.flier.services.TabsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TabsServiceImpl implements TabsService {
    @Autowired
    private TabsRepo tabsRepo;

    @Override
    public Flux<PropertyShop> getAllTabs() {
        return tabsRepo.findAll().flatMap(
                p -> {
                    p.setProperty(TabsOfShopProperty.valueOf(p.getName()));
                    return Mono.just(p);
                });
    }

    @Override
    public Flux<PropertyShop> getAllTabsByShopId(Long shopId) {
        return tabsRepo.findByShopId(shopId).flatMap(
                p -> {
                    p.setProperty(TabsOfShopProperty.valueOf(p.getName()));

                    return Mono.just(p);
                }
        );
    }
}
