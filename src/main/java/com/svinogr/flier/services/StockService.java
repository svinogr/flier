package com.svinogr.flier.services;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.shop.Stock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockService {
    Flux<Stock> findStocksByShopId(Long id);

    Mono<Stock> findStockById(Long stockId);

    Mono<Stock> createStock(Stock stock);

    Mono<Stock> updateStock(Stock stock);

    Mono<Stock> deleteStockById(Long parseId);

    Mono<Stock> restoreStockById(Long parseId);

    Mono<Boolean> isOwnerOfStock(long shopId, long stockId);

    Mono<Long> getCountSearchPersonalByValue(SearchType type, String value, long shopId);

    Flux<Stock> searchPersonalByValueAndType(SearchType type, String value, long shopId);

    Mono<Long> getCountStocksByShopId(Long id);

    Mono<Long> getCountStocks();

    Flux<Stock> findAll();
}
