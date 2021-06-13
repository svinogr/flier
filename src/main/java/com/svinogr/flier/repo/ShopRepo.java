package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Shop;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepo extends ReactiveCrudRepository<Shop, Long> {
}
