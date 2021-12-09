package com.svinogr.flier.repo;

import com.svinogr.flier.model.PropertiesShops;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PropertiesShopsRepo extends ReactiveCrudRepository<PropertiesShops, Long> {
    Flux<PropertiesShops> findAllByShopId(Long shopId);
    Mono<Boolean> deleteAllByShopId(Long shopId);
}
