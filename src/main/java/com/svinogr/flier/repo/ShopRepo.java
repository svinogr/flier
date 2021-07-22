package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Shop;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ShopRepo extends ReactiveCrudRepository<Shop, Long> {
    Flux<Shop> findAllByUserId(Long id);
}
