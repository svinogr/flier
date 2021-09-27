package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class Reactive repository for {@link Stock} implements {@link ReactiveCrudRepository}
 */
@Repository
public interface StockRepo extends ReactiveCrudRepository<Stock, Long> {
    /**
     * Method searching all stocks by shop id
     *
     * @param shopId value for search in column "shop_id"
     * @return all found stocks. Flux<Stock>
     */
    Flux<Stock> findAllByShopId(Long shopId);

    /**
     * Custom query for update entity {@link Stock}.
     * Sets column updated by default.
     *
     * @param stock  {@link Stock}
     * @return  boolean status of operation. Mono<Boolean>
     */
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

    /**
     * Method searching count by id
     *
     * @param id value for search in column "id"
     * @return count of found stocks. Mono<Long>
     */
    Mono<Long> countByShopId(Long id);

    /**
     *
     * Method searching count by id and description with ignore case
     *
     * @param id  value for search in column "id"
     * @param description value for search in column "description"
     * @return count of found stocks. Mono<Long>
     */
    Mono<Long> countByShopIdAndDescriptionContainsIgnoreCase(Long id, String description);

    /**
     * Method searching count by id and title with ignore case
     *
     * @param id value for search in column "id"
     * @param title value for search in column "title"
     * @return count of found stocks. Mono<Long>
     */
    Mono<Long> countByShopIdAndTitleContainsIgnoreCase(Long id, String title);

    /**
     * Method searching count by shop id  and id
     *
     * @param shopId value for search in column "shop_id"
     * @param id1 value for search in column "id"
     * @return count of found stocks. Mono<Long>
     */
    Mono<Long> countByShopIdAndId(Long shopId, long id1);

    /**
     * Method searching stock by shop id  and id
     *
     * @param shopId value for search in column "shop_id"
     * @param id value for search in column "id"
     * @return  found stock. Mono<Stock>
     */
    Mono<Stock> findByShopIdAndId(long shopId, long id);

    /**
     * Method searching stock by title with ignore case and shop id
     *
     * @param title  value for search in column "title"
     * @param shopId value for search in column "shop_id"
     * @return found stocks. Flux<Stock>
     */
    Flux<Stock> findByTitleContainingIgnoreCaseAndShopId(String title, long shopId);

 //   @Query("select * from stocks where title like any (array['%1%','%2%'])")
    //  @Query("select * from stocks where title like any (array[:arr[0]])")
      @Query("select * from stocks where title like any (:arr)")
    Flux<Stock> searchByArrayValueIgnoreCase(@Param("arr") Integer[] arr);

    /**
     *
     * Method searching stock title with ignore case
     *
     * @param title value for search in column "title"
     * @return found stocks. Flux<Stock>
     */
    Flux<Stock> findByTitleContainingIgnoreCase(String title);

    /**
     * Method searching stock title with ignore case
     *
     * @param title value for search in column "title"
     * @return found stocks. Flux<Stock>
     */
    Flux<Stock> findByDescriptionContainingIgnoreCase(String title);
}