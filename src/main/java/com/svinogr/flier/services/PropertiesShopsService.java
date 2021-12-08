package com.svinogr.flier.services;

import com.svinogr.flier.model.PropertiesShops;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface PropertiesShopsService {
    Flux<PropertiesShops> getByShopId(Long shopId);

    Mono<PropertiesShops> save(PropertiesShops propertiesShops);

    Flux<PropertiesShops> saveAll(List<PropertiesShops> propertiesShopsList);

    Flux<PropertiesShops> updateAll(List<PropertiesShops> propShopslist);
}
