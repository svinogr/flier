package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Stock;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StockRepo extends ReactiveCrudRepository<Stock, Long> {
    Flux<Stock> findAllByShopId(Long userShop);

    @Modifying()
    @Query("update stocks set" +
            " shop_id = :#{#stock.shopId}," +
            " title = :#{#stock.title}," +
            " description = :#{#stock.description}," +
            " img = :#{#stock.img}," +
            " url = :#{#stock.url}," +
            " status = :#{#stock.status}," +
            " date_start = :#{#stock.dateStart}," +
            " date_finish = :#{#stock.dateFinish}," +
            " updated = DEFAULT" +
            " where id= :#{#stock.id}")
    Mono<Boolean> updateStock(Stock stock);

    Mono<Long> countByShopId(Long id);

    Mono<Long> countByShopIdAndDescriptionContainsIgnoreCase(Long id, String description);

    Mono<Long> countByShopIdAndTitleContainsIgnoreCase(Long id, String title);

    Mono<Long> countByShopIdAndId(Long id, long id1);

    Mono<Stock> findByShopIdAndId(long shopId, long id);

    Flux<Stock> findByTitleContainingIgnoreCaseAndShopId(String title, long shopId);
}