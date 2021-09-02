package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.repo.StockRepo;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.AccessControlException;

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
    public Mono<Long> getCountSearchPersonalByValue(String type, String value, long shopId) {
        System.out.println(type + "--" + value);

        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case "searchId":
                long id;
                System.out.println(1);
                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.empty();
                }
                System.out.println("type " + type + "*" + "value " + id);
                return getCountPersonalStockById(id, shopId);

            case "searchTitle":

                System.out.println(2);

                return getCountPersonalStockByTitle(value, shopId);
            case "searchAddress":

                System.out.println(3);

                return getCountPersonalStockByDescription(value, shopId);
            default:
                System.out.println(4);
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
/*
        return userService.getPrincipal().
                flatMap(principal -> {
                    return stockRepo.countByShopIdAndTitleContainsIgnoreCase(principal.getId(), title);
                });
        return isOwnerOfStock(shopId, id).
                flatMap(owner ->{
                    // if (!owner) return  Mono.error(new AccessControlException("access denied"));
                    if (!owner) return  Mono.just(0L);

                    return stockRepo.countByShopIdAndId(shopId, id);
                });
*/

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
    public Flux<Stock> searchPersonalByValueAndType(String type, String value, long shopId) {
        System.out.println(type + "--" + value);

     /*   if (value.equals(Strings.EMPTY)) return userService.getPrincipal().
                flatMapMany(principal -> getShopsByUserId(principal.getId()));*/

        switch (type) {
            case "searchId":
                long id;
                System.out.println(1);
                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }
                System.out.println("type " + type + "*" + "value " + id);
                return getPersonalStockById(id, shopId).flux();

            case "searchTitle":

                System.out.println(2);

                return getPersonalStockByTitle(value, shopId);
            case "searchAddress":

                System.out.println(3);

                return getPersonalStockByDescription(value, shopId);
            default:
                return Flux.empty();
             /*   System.out.println(4);
                return userService.getPrincipal().
                        flatMapMany(principal -> getShopsByUserId(principal.getId()));*/
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
    public Mono<Stock> restoreStockById(Long stockId) {
        return stockRepo.findById(stockId)
                .flatMap(stock -> {
                    stock.setStatus(Status.ACTIVE.name());
                    return Mono.just(stock);
                })
                .flatMap(stock -> updateStock(stock));
    }
}
