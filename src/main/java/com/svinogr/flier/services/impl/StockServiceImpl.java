package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.repo.StockRepo;
import com.svinogr.flier.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    StockRepo stockRepo;

    @Override
    public Flux<Stock> findStocksByShopId(Long id) {
        return stockRepo.findAllByShopId(id);
    }
}
