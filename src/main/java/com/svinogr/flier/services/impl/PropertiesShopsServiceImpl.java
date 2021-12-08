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
    public Flux<PropertiesShops> updateAll(List<PropertiesShops> propShopslist) {
        System.out.println(propShopslist);

        return propertiesShopsRepo.findAllByShopId(propShopslist.get(0).getShopId())
                .flatMap(propertiesShops -> {
                    System.out.println(propertiesShops.getId());
                   return propertiesShopsRepo.delete(propertiesShops).then(
                           Mono.just(propertiesShops));

                }).flatMap(p ->{
                    return propertiesShopsRepo.saveAll(propShopslist);
                });
/*
        propertiesShopsRepo.s



                Flux < List < PropertiesShops >> just = Flux.just(propShopslist);

        return just
                .flatMap(l -> {
                    System.out.println(l);
                    return propertiesShopsRepo.findAllByShopId(l.get(0).getShopId());

                })

                .flatMap(p -> {
                    return propertiesShopsRepo.deleteById(p.getId());
                })*/


    }


}
