package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Stock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockRepo extends ReactiveCrudRepository<Stock, Long> {
    Flux<Stock> findAllByShopId(Long shopId);
}
