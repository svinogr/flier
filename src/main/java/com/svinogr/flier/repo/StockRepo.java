package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Stock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StockRepo extends ReactiveCrudRepository<Stock, Long> {
}
