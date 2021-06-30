package com.svinogr.flier.services;

import com.svinogr.flier.model.shop.Stock;
import reactor.core.publisher.Flux;

public interface StockService {
    Flux<Stock> findStocksByShopId(Long id);
}
