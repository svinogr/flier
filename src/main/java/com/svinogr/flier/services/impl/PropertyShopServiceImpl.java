package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.PropertyShop;
import com.svinogr.flier.repo.TabsRepo;
import com.svinogr.flier.services.PropertyShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class PropertyShopServiceImpl implements PropertyShopService {
    @Autowired
    private TabsRepo tabsRepo;

    @Override
    public Flux<PropertyShop> getAllTabs() {
        return tabsRepo.findAll();
    }

    @Override
    public Flux<PropertyShop> getAllTabsByShopId(Long shopId) {
        return tabsRepo.findByShopId(shopId);
    }
}
