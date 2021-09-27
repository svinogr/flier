package com.svinogr.flier.services;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Interface for stock services
 */
public interface StockService {
    /**
     * Get stocks {@link Stock} by shop id from db
     *
     * @param id shop id  from db
     * @return found stocks. Flux<Stock>
     */
    Flux<Stock> findStocksByShopId(Long id);

    /**
     * Get stock {@link Stock} by id from db
     *
     * @param stockId id from db
     * @return found stock. Mono<Stock>
     */
    Mono<Stock> findStockById(Long stockId);

    /**
     * Creating new stock {@link Stock} in db
     *
     * @param stock  {@link Stock}
     * @return created stock {@link Stock}
     */
    Mono<Stock> createStock(Stock stock);

    /**
     * Updating stock {@link Stock} from db
     *
     * @param stock {@link Stock}
     * @return updated stock. Mono<Stock>
     */
    Mono<Stock> updateStock(Stock stock);

    /**
     * Deleting stock {@link Stock} by id from db
     *
     * @param stockId  stock id from ddb
     * @return deleted stock
     */
    Mono<Stock> deleteStockById(Long stockId);

    /**
     * Restoring stock {@link Stock} by id
     *
     * @param stockId stock id from db
     * @return restored stock
     */
    Mono<Stock> restoreStockById(Long stockId);

    /**
     * Indicating of the shop {@link com.svinogr.flier.model.shop.Shop} with this id has the stock with  this id
     *
     * @param shopId shop id
     * @param stockId stock id
     * @return result of check. Mono<Boolean>
     */
    Mono<Boolean> isOwnerOfStock(long shopId, long stockId);

    /**
     * Get count of stock {@link Stock} by searching type {@link SearchType} and value only for signed user
     *
     * @param type {@link SearchType}
     * @param value searching value
     * @param shopId shop id
     * @return count of found stocks
     */
    Mono<Long> getCountSearchPersonalByValue(SearchType type, String value, long shopId);

    /**
     * Get stocks {@link Stock} by searching type {@link SearchType} and value only for signed user
     *
     * @param type {@link SearchType}
     * @param value searching value
     * @param shopId shop id
     * @return found stocks
     */
    Flux<Stock> searchPersonalByValueAndType(SearchType type, String value, long shopId);

    /**
     * Get count of stock {@link Stock} by searching type {@link SearchType} and value
     *
     * @param id id shop
     * @return count of found stock
     */
    Mono<Long> getCountStocksByShopId(Long id);

    /**
     * Get count of all stock {@link Stock} in db
     *
     * @return count of found
     */
    Mono<Long> getCountStocks();

    /**
     * Get all stock {@link Stock}
     *
     * @return found all stocks
     */
    Flux<Stock> findAll();

    /**
     * Get count of stock {@link Stock} by searching value and type searching from db
     *
     * @param type  @{@link SearchType}
     * @param value string searching value
     * @return count found shops. Mono<Long>
     */
    Mono<Long> getCountSearchByValue(SearchType type, String value);

    /**
     * Get stocks {@link Stock} by searching value and type searching from db
     *
     * @param type @{@link SearchType}
     * @param value string searching value
     * @return found stocks. Flux<Stock>
     */
    Flux<Stock> searchByValueAndType(SearchType type, String value);

    /**
     * Get stock by searching value from db by all searching fields (title, description)
     * In first use for API mobile
     * @param value string value
     * @return found stocks
     */
    Flux<Stock> searchByValueTags(String value);
}
