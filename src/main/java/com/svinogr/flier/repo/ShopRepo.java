package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ShopRepo extends ReactiveCrudRepository<Shop, Long> {
    Flux<Shop> findAllByUserId(Long id);

    @Modifying()
    @Query("update shops set" +
            " user_id = :#{#shop.userId}," +
            " title = :#{#shop.title}," +
            " description = :#{#shop.description}," +
            " address = :#{#shop.address}," +
            " lat = :#{#shop.coordLat}," +
            " lng = :#{#shop.coordLng}," +
            " img = :#{#shop.img}," +
            " url = :#{#shop.url}," +
            " status = :#{#shop.status}," +
            " updated = DEFAULT" +
            " where id= :#{#shop.id}")
    Mono<Boolean> updateShop(Shop shop);

    /*@Query("select * from shops where title like concat('%',:title,'%') and user_id = :userId")
    Flux<Shop> findByPersonalTitleContains2(String title, Long userId);*/

    Flux<Shop> findByTitleContainsIgnoreCaseAndUserId(String title, Long userId);

    Flux<Shop> findByAddressContainingIgnoreCaseAndUserId(String address, Long userId);

    Mono<Shop> findByIdAndUserId(Long id, Long userId);

    Mono<Long> countByUserId(Long userId);

    Flux<Shop> findByUserId(Long userId, Pageable pageable);

    Mono<Long> countByUserIdAndId(long userId, Long id);
}
