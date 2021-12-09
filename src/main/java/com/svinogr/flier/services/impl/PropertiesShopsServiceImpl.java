package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.PropertiesShops;
import com.svinogr.flier.repo.PropertiesShopsRepo;
import com.svinogr.flier.services.PropertiesShopsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PropertiesShopsServiceImpl implements PropertiesShopsService {
    @Autowired
    private PropertiesShopsRepo propertiesShopsRepo;

    @Override
    public Flux<PropertiesShops> getByShopId(Long shopId) {
        return propertiesShopsRepo.findAllByShopId(shopId);
    }

    @Override
    public Mono<PropertiesShops> save(PropertiesShops propertiesShops) {
        return propertiesShopsRepo.save(propertiesShops);
    }

    @Override
    public Flux<PropertiesShops> saveAll(List<PropertiesShops> propertiesShopsList) {
        return propertiesShopsRepo.saveAll(propertiesShopsList);
    }

    @Override
    public Flux<PropertiesShops> updateAll(List<PropertiesShops> propertiesShopsList, Long shopId) {
         return propertiesShopsRepo.deleteAllByShopId(shopId)
                 .flatMapMany( ok -> propertiesShopsRepo.saveAll(propertiesShopsList));
    }
}
