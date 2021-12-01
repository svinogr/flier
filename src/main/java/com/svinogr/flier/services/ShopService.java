package com.svinogr.flier.services;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Interface for shop services
 */
public interface ShopService {
    /**
     * Create new shop {@link Shop} entity in db
     *
     * @param shop {@link Shop}
     * @return saved shop
     */
    Mono<Shop> createShop(Shop shop);

    /**
     * Update shop {@link Shop} entity in db
     *
     * @param shop {@link Shop}
     * @return updated shop
     */
    Mono<Shop> updateShop(Shop shop);

    /**
     * Delete shop {@link Shop} entity in db
     *
     * @param shopId {@link Shop}
     * @return deleted shop
     */
    Mono<Shop> deleteShopById(Long shopId);

    /**
     * Get shop {@link Shop} by id
     *
     * @param shopId id from db {@link Shop}
     * @return found shop {@link Shop}
     */
    Mono<Shop> getShopById(Long shopId);

    /**
     * Get all shops {@link Shop} from db
     *
     * @return found all shops. Flux<Shop>
     */
    Flux<Shop> getAllShops();

    /**
     * Get all active shop {@link Shop} with status {@link com.svinogr.flier.model.Status} "ACTIVE" from db
     *
     * @return found all shops. Flux<Shop>
     */
    Flux<Shop> getAllActiveShops();

    /**
     * Get shop {@link Shop} by field userId from db
     *
     * @param shopId searching value
     * @return found shop. Flux<Shop>
     */
    Flux<Shop> getShopsByUserId(Long shopId);

    /**
     * Check of signed user is owner of {@link Shop}
     *
     * @param shopId id of shop {@link Shop} in bd
     * @return status checking. Mono<Boolean>
     */
    Mono<Boolean> isOwnerOfShop(Long shopId);

    /**
     * Restore deleted shop {@link Shop}
     *
     * @param shopId id of shop {@link Shop} in bd
     * @return restoring shop {@link Shop}
     */
    Mono<Shop> restoreShop(Long shopId);

/*  @Deprecated
    Flux<Shop> getPersonalShopByTitle(String title);*/

   /* @Deprecated
    Flux<Shop> getPersonalShopByAddress(String address);
*/

    /**
     * Get shops {@link Shop} only for signed user by searching value and type searching from db
     *
     * @param type  @{@link SearchType}
     * @param value string searching value
     * @return found shops {@link Shop}. Flux<Shop>
     */
    Flux<Shop> searchPersonalByValueAndType(SearchType type, String value);

/*    @Deprecated
    Mono<Shop> getPersonalShopById(Long shopId);*/

    /**
     * Get count of shops {@link com.svinogr.flier.model.User} has searching user id
     *
     * @param userId searching value
     * @return count found shops. Mono<Long>
     */
    Mono<Long> getCountShopsByUserId(Long userId);

    /**
     * Get count of shops {@link com.svinogr.flier.model.User} has searching user id only for signed user
     *
     * @param type  @{@link SearchType}
     * @param value string searching value
     * @return count found shops. Mono<Long>
     */
    Mono<Long> getCountSearchPersonalByValue(SearchType type, String value);

    /**
     * Get count all shops from db
     *
     * @return count found shops. Mono<Long>
     */
    Mono<Long> getCountShops();

    /**
     * Get count of shops {@link Shop} by searching value and type searching from db
     *
     * @param type  @{@link SearchType}
     * @param value string searching value
     * @return count found shops. Mono<Long>
     */
    Mono<Long> getCountSearchByValue(SearchType type, String value);

    /**
     * Get shops {@link Shop} by searching value and type searching from db
     *
     * @param type  @{@link SearchType}
     * @param value string searching value
     * @return found shops. Flux<Shop>
     */
    Flux<Shop> searchByValueAndType(SearchType type, String value);

    /**
     * Get all around point shops by coord
     *
     * @param coord {@link Coord}
     * @return found shops. Flux<Shop>
     */
    Flux<Shop> getAllShopsAroundCoord(Coord coord);

    /**
     * Get stock by searching value from db by all searching fields "TAGS" (title, description)
     * In first use for API mobile
     *
     * @param value string value
     * @return found stocks
     */
    Flux<Stock> searchByValueTags(String value);

    /**
     * Get shops by searching value from db by all searching fields  (title, description) and all shops around coord
     * In first use for API mobile
     *
     * @param searchText string value
     * @param coord {@link Coord}
     * @return found stocks
     */
    Flux<Shop> getSearchAllShopsAroundCoord(Coord coord, String searchText);

    /**
     * Get shops by searching value from db by all searching fields  (title, description)
     *
     * @param searchText string value
     * @return found stocks
     */
    Flux<Shop> searchShopsBySearchingTextInShopsAndStocks(String searchText);
}
