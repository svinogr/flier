package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.repo.StockRepo;
import com.svinogr.flier.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    StockRepo stockRepo;

    @Override
    public Flux<Stock> findStocksByShopId(Long id) {
        return stockRepo.findAllByShopId(id);
    }

    @Override
    public Mono<Stock> findStockById(Long stockId) {
        return stockRepo.findById(stockId);
    }

    @Override
    public Mono<Stock> createStock(Stock stock) {
        return stockRepo.save(stock);
    }

    @Override
    public Mono<Stock> updateStock(Stock stock) {
        return stockRepo.save(stock);
    }

    @Override
    public Mono<Stock> deleteStockById(Long stockId) {
        return stockRepo.findById(stockId)
                .flatMap(stock -> {
                    stock.setStatus(Status.NON_ACTIVE.name());
                    return Mono.just(stock);
                })
                .flatMap(stock -> stockRepo.save(stock));
    }
}
