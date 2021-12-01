package com.svinogr.flier.repo;

import com.svinogr.flier.model.PropertyShop;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class Reactive repository for {@link com.svinogr.flier.model.TabsOfShopProperty} implements {@link ReactiveCrudRepository}
 */
@Repository
public interface TabsRepo extends ReactiveCrudRepository<PropertyShop, Long> {
    Flux<PropertyShop> findByShopId(Long shopId);
}
