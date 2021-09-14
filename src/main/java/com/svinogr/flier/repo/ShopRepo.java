package com.svinogr.flier.repo;

import com.svinogr.flier.model.shop.Shop;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class  Reactive repository for {@link Shop} implements {@link ReactiveCrudRepository}
 */
@Repository
public interface ShopRepo extends ReactiveCrudRepository<Shop, Long> {
    /**
     * Find all shops by user id
     *
     * @param id value for search shop id
     * @return  found all shops by id Flux<Shop>
     */
    Flux<Shop> findAllByUserId(Long id);

    /**
     * Custom query for update entity {@link Shop}
     *
     * @param shop shop
     * @return  boolean status of operation
     */
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

    /**
     * Method searching by title and id
     *
     * @param title value for search in column "title"
     * @param userId value for search in column "user_id"
     * @return found shops Flex<Shop>
     */
    Flux<Shop> findByTitleContainsIgnoreCaseAndUserId(String title, Long userId);

    /**
     * Method searching by address and id
     *
     * @param address value for search in column "address"
     * @param userId value for search in column "user_id"
     * @return found shops Flex<Shop>
     */
    Flux<Shop> findByAddressContainingIgnoreCaseAndUserId(String address, Long userId);

    /**
     * Method searching by address and id
     *
     * @param id value for search in column "id"
     * @param userId value for search in column "user_id"
     * @return found shop. Mono<Shop>
     */
    Mono<Shop> findByIdAndUserId(Long id, Long userId);

    /**
     * Method searching count by id
     *
     * @param userId value for search in column "user_id"
     * @return count of found shops. Mono<Long>
     */
    Mono<Long> countByUserId(Long userId);

    /**
     * Method searching count by user id and id
     *
     * @param userId value for search in column "user_id"
     * @param id value for search in column "id"
     * @return count of found shops. Mono<Long>
     */
    Mono<Long> countByUserIdAndId(long userId, Long id);

    /**
     * Method searching count by user id and title with ignore case
     *
     * @param userId value for search in column "user_id"
     * @param title value for search in column "title"
     * @return count of found shops. Mono<Long>
     */
    Mono<Long> countByUserIdAndTitleContainsIgnoreCase(Long userId, String title);

    /**
     * Method searching count by user id and address with ignore case
     *
     * @param userId  value for search in column "user_id"
     * @param address value for search in column "address"
     * @return count of found shops. Mono<Long>
     */
    Mono<Long> countByUserIdAndAddressContainsIgnoreCase(Long userId, String address);

    /**
     * Method searching count by id
     *
     * @param id  value for search in column "id"
     * @return count of found shops. Mono<Long>
     */
    Mono<Long> countById(long id);
}
