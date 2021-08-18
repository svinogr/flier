package com.svinogr.flier.repo;

import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
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

    Flux<Shop> findByTitleContains(String title);

    Flux<Shop> findByAddressContains(String address);
    @Query("select * where title like %#{title}% and user_id = :#{user.id}")
    Flux<Shop> findByPersonalTitleContains(String title, User user);

    Flux<Shop> findByPersonalAddressContains(String address, User user);

    Flux<Shop> findPersonalBydContains(Long id, User user);



}
