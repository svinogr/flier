package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.PropertiesShops;
import com.svinogr.flier.model.PropertyShop;
import com.svinogr.flier.repo.TabsRepo;
import com.svinogr.flier.services.PropertyShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyShopServiceImpl implements PropertyShopService {
    @Autowired
    private TabsRepo tabsRepo;

    @Override
    public Flux<PropertyShop> getAllTabs() {
        return tabsRepo.findAll();
    }

    @Override
    public Flux<PropertyShop> getByIdsPropertiesShops(List<PropertiesShops> list) {
        List<Long> listIds = new ArrayList<>();
        for (PropertiesShops p : list) {
            listIds.add(p.getPropertyId());
        }

        return tabsRepo.findAllById(listIds);
    }

    @Override
    public Mono<PropertyShop> getPropertyById(long idBD) {
        return tabsRepo.findById(idBD);
    }
}
