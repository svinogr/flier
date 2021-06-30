package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Stock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StockRepo extends ReactiveCrudRepository<Stock, Long> {
    Flux<Stock> findAllByShopId(Long userShop);
}