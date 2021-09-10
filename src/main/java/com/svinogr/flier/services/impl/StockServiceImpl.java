package com.svinogr.flier.services.impl;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.repo.StockRepo;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    StockRepo stockRepo;

    @Autowired
    UserService userService;

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
        // return Mono.just(new Stock());
        return stockRepo.updateStock(stock)
                .flatMap(ok -> {
                    if (ok) return findStockById(stock.getId());
//TODO плохой вариант возвоащать пустой сток
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Stock> deleteStockById(Long stockId) {
        return stockRepo.findById(stockId)
                .flatMap(stock -> {
                    stock.setStatus(Status.NON_ACTIVE.name());
                    return Mono.just(stock);
                })
                .flatMap(stock -> updateStock(stock));
    }

    @Override
    public Mono<Boolean> isOwnerOfStock(long shopId, long stockId) {
        return findStockById(stockId).
                flatMap(stock -> Mono.just(stock.getShopId() == shopId));
    }

    @Override
    public Mono<Long> getCountSearchPersonalByValue(SearchType type, String value, long shopId) {
        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.just(0L);
                }

                return getCountPersonalStockById(id, shopId);
            case BY_TITLE:
                return getCountPersonalStockByTitle(value, shopId);
            case BY_ADDRESS:
                return getCountPersonalStockByDescription(value, shopId);
            default:
                return Mono.empty();
        }
    }

    private Mono<Long> getCountPersonalStockByDescription(String description, long shopId) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return stockRepo.countByShopIdAndDescriptionContainsIgnoreCase(principal.getId(), description);
                });
    }

    private Mono<Long> getCountPersonalStockByTitle(String title, long shopId) {
        return stockRepo.findByTitleContainingIgnoreCaseAndShopId(title, shopId).filterWhen(stock -> {
          return isOwnerOfStock(shopId, stock.getId());
          }).count();




    }

    private Mono<Long> getCountPersonalStockById(long id, long shopId) {
       return isOwnerOfStock(shopId, id).
               flatMap(owner ->{
                  // if (!owner) return  Mono.error(new AccessControlException("access denied"));
                   if (!owner) return  Mono.just(0L);

                   return stockRepo.countByShopIdAndId(shopId, id);
               });
    }

    @Override
    public Flux<Stock> searchPersonalByValueAndType(SearchType type, String value, long shopId) {
         switch (type) {
             case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }

                return getPersonalStockById(id, shopId).flux();
             case BY_TITLE:

                return getPersonalStockByTitle(value, shopId);
             case BY_ADDRESS:

                return getPersonalStockByDescription(value, shopId);
            default:
                return Flux.empty();
        }
    }

    private Flux<Stock> getPersonalStockByDescription(String value, long shopId) {
        return null;
    }

    private Flux<Stock> getPersonalStockByTitle(String title, long shopId) {
        return stockRepo.findByTitleContainingIgnoreCaseAndShopId(title, shopId).filterWhen(stock -> {
            return isOwnerOfStock(shopId, stock.getId());
        });
    }

    private Mono<Stock> getPersonalStockById(long id, long shopId) {
        return isOwnerOfStock(shopId, id).
                flatMap(owner ->{
                    // if (!owner) return  Mono.error(new AccessControlException("access denied"));
                    if (!owner) return  Mono.empty();

                    return stockRepo.findByShopIdAndId(shopId, id);
                });
    }

    @Override
    public Mono<Long> getCountStocksByShopId(Long id) {
        return stockRepo.countByShopId(id) ;
    }

    @Override
    public Mono<Long> getCountStocks() {
       return stockRepo.count();
    }

    @Override
    public Flux<Stock> findAll() {
        return stockRepo.findAll();
    }

    @Override
    public Mono<Stock> restoreStockById(Long stockId) {
        return stockRepo.findById(stockId)
                .flatMap(stock -> {
                    stock.setStatus(Status.ACTIVE.name());
                    return Mono.just(stock);
                })
                .flatMap(stock -> updateStock(stock));
    }
}
