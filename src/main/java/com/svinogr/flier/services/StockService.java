package com.svinogr.flier.services;

import com.svinogr.flier.model.shop.Stock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockService {
    Flux<Stock> findStocksByShopId(Long id);

    Mono<Stock> findStockById(Long stockId);

    Mono<Stock> createStock(Stock stock);

    Mono<Stock> updateStock(Stock stock);

    Mono<Stock> deleteStockById(Long parseId);
}
