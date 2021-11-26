package com.svinogr.flier.services.impl;

import com.svinogr.flier.controllers.web.utils.SearchType;
import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.CoordHelper;
import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.repo.ShopRepo;
import com.svinogr.flier.services.ShopService;
import com.svinogr.flier.services.StockService;
import com.svinogr.flier.services.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class service implementation {@link ShopService}
 */
@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepo shopRepo;

    @Autowired
    private StockService stockService;

    @Autowired
    private UserService userService;

    @Override
    public Mono<Shop> createShop(Shop shop) {
        return shopRepo.save(shop);
    }

    @Override
    public Mono<Shop> updateShop(Shop shop) {
        return shopRepo.updateShop(shop).
                flatMap(ok -> {
                    if (ok) return getShopById(shop.getId());
                    //TODO возвращать пустой хорощо ли это??
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Shop> deleteShopById(Long id) {
        //TODO сделать удаление акции при удалении магазина
        return shopRepo.findById(id).flatMap(shop -> {
            shop.setStatus(Status.NON_ACTIVE.name());
            return shopRepo.updateShop(shop).
                    flatMap(ok -> {
                        if (ok) return getShopById(shop.getId());
                        //TODO возвращать пустой хорощо ли это??
                        return Mono.empty();
                    });
        });
    }

    @Override
    public Mono<Shop> getShopById(Long id) {
        return shopRepo.findById(id).flatMap(shop -> {
            List<Stock> list = new ArrayList<>();
            shop.setStocks(list);

            return stockService.findStocksByShopId(shop.getId()).
                    flatMap(stock -> {
                        list.add(stock);
                        return Mono.just(stock);
                    }).
                    then(Mono.just(shop));
        });
    }

    @Override
    public Flux<Shop> getAllShops() {
        return shopRepo.findAll();
    }

    //TODO переделать под актив саму sql
    @Override
    public Flux<Shop> getAllActiveShops() {
        return shopRepo.findAll().filter(s -> s.getStatus().equals(Status.ACTIVE.name()));
    }

    @Override
    public Flux<Shop> getShopsByUserId(Long id) {
        return shopRepo.findAllByUserId(id);
    }

    @Override
    public Mono<Boolean> isOwnerOfShop(Long shopId) {
        return getShopById(shopId).
                flatMap(shop -> userService.getPrincipal().
                        flatMap(user -> Mono.just(shop.getUserId() == user.getId()))).switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<Shop> restoreShop(Long shopId) {
        return shopRepo.findById(shopId).
                flatMap(shop -> {
                    shop.setStatus(Status.ACTIVE.name());
                    return shopRepo.updateShop(shop).
                            flatMap(ok -> {
                                if (ok) return shopRepo.findById(shop.getId());
                                //TODO возвращать пустой хорощо ли это??
                                return Mono.empty();
                            });
                });
    }

    private Flux<Shop> getPersonalShopByTitle(String title) {
        return userService.getPrincipal().
                flatMapMany(principal -> {
                    return shopRepo.findByTitleContainsIgnoreCaseAndUserId(title, principal.getId());
                });
    }


    private Flux<Shop> getPersonalShopByAddress(String address) {
        System.out.println(address);
        return userService.getPrincipal().
                flatMapMany(principal -> {
                    return shopRepo.findByAddressContainingIgnoreCaseAndUserId(address, principal.getId());
                });
    }

    private Mono<Shop> getPersonalShopById(Long id) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.findByIdAndUserId(id, principal.getId());
                });
    }

    @Override
    public Mono<Long> getCountShopsByUserId(Long userId) {
        return shopRepo.countByUserId(userId).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Long> getCountSearchPersonalByValue(SearchType type, String value) {
        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.just(0L);
                }

                return getCountPersonalShopById(id);
            case BY_TITLE:
                return getCountPersonalShopByTitle(value);
            case BY_ADDRESS:
                return getCountPersonalShopByAddress(value);
            default:
                return Mono.empty();
        }
    }

    @Override
    public Mono<Long> getCountShops() {
        return shopRepo.count();
    }

    @Override
    public Mono<Long> getCountSearchByValue(SearchType type, String value) {
        if (value.equals(Strings.EMPTY)) return Mono.empty();

        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Mono.just(0L);
                }

                return getCountShopById(id);
            default:
                return Mono.empty();
        }
    }

    private Mono<Long> getCountShopById(long id) {
        return shopRepo.countById(id);
    }

    @Override
    public Flux<Shop> searchByValueAndType(SearchType type, String value) {
        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }

                return getShopById(id).flux();
            default:
                return Flux.empty();
        }
    }

/*
    @Override
    public Flux<Shop> getAllShopsAroundCoord(Coord coord) {
        CoordHelper coordHelper = new CoordHelper(coord);

        return shopRepo.getShopsAroundCoord(coordHelper).
                flatMap(shop -> {
                    List<Stock> list = new ArrayList<>();
                    shop.setStocks(list);
                    return stockService.findStocksByShopId(shop.getId()).
                            flatMap(stock -> {
                                list.add(stock);
                                System.out.println(list);
                                return Mono.just(shop);
                            });
                });
    }
*/

    @Override
    public Flux<Shop> getAllShopsAroundCoord(Coord coord) {
        CoordHelper coordHelper = new CoordHelper(coord);

        return shopRepo.getShopsAroundCoord(coordHelper).
                flatMap(shop -> {
                    List<Stock> list = new ArrayList<>();
                    shop.setStocks(list);
                    stockService.findStocksByShopId(shop.getId()).
                            flatMap(stock -> {
                                list.add(stock);
                                System.out.println(list);
                                return Mono.empty();
                            });
                    return Mono.just(shop);
                });
    }

    @Override
    public Flux<Stock> searchByValueTags(String value) {
        return null;
    }

    @Override
    public Flux<Shop> getSearchAllShopsAroundCoord(Coord coord, String searchText) {
        CoordHelper coordHelper = new CoordHelper(coord);
        return shopRepo.getShopsAroundCoord(coordHelper).
                flatMap(shop -> {
                    Flux<List<Stock>> listFlux = stockService.
                            findStocksByShopId(shop.getId()).
                            //проверяем если акции содержащие в название или в описании искомый текст
                                    filter(stock -> {
                                if (stock.getDescription().contains(searchText) || stock.getTitle().contains(searchText)) {
                                    System.out.println(true);
                                    return true;
                                } else {
                                    System.out.println(false);
                                    return false;
                                }
                                //собираем в лист и прикрепляем к магазину
                            }).collectList().flatMapMany(Flux::just);
                    shop.setStocks((List) listFlux);

                    return Mono.just(shop);


                }).
                flatMap(shop -> {
                    //проверяем сожержить ли название магазина или описание искомы текст или акции
                    if (shop.getStocks().size() > 0) return Mono.just(shop);

                    if (shop.getTitle().contains(searchText) || shop.getDescription().contains(searchText))
                        return Mono.just(shop);

                    return Mono.empty();
                });
    }

    @Override
    public Flux<Shop> searchShopsBySearchingTextInShopsAndStocks(String searchText) {
        return shopRepo.findAll().
                flatMap(shop -> {
                    System.out.println(shop);
                    if (shop.getTitle().contains(searchText) || shop.getDescription().contains(searchText)) {
                        System.out.println(true);
                        List<Stock> stocks = new ArrayList<>();
                        stocks.add(new Stock());
                        shop.setStocks(stocks);
                        return Mono.just(shop);
                    } else {
                        System.out.println(false);
                        shop.setStocks(new ArrayList<>());

                        return stockService.
                                findStocksByShopId(shop.getId()).
                                filter(stock -> stock.getDescription().contains(searchText) || stock.getTitle().contains(searchText)).
                                // subscribe(shop.getStocks()::add);
                                        flatMap(s -> {
                                    shop.getStocks().add(s);
                                    return Mono.just(shop);

                                });




/*
                     //   shop.setStocks(stocks);
                  //      System.out.println(stocks.size());
                    //    System.out.println(shop.getId());
                        System.out.println(shop.getStocks().size() + "yuiy");
                        if (shop.getStocks().size() == 0) return Mono.empty();

                        System.out.println("end");
                        return Mono.just(shop);*/

                    }
                }).filter(shop -> {
            return shop.getStocks().size() > 0;
        });
    }

    private Mono<Long> getCountPersonalShopByAddress(String address) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.countByUserIdAndAddressContainsIgnoreCase(principal.getId(), address);
                });
    }

    private Mono<Long> getCountPersonalShopByTitle(String title) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.countByUserIdAndTitleContainsIgnoreCase(principal.getId(), title);
                });
    }

    private Mono<Long> getCountPersonalShopById(long id) {
        return userService.getPrincipal().
                flatMap(principal -> {
                    return shopRepo.countByUserIdAndId(principal.getId(), id);
                });
    }

    @Override
    public Flux<Shop> searchPersonalByValueAndType(SearchType type, String value) {
        switch (type) {
            case BY_ID:
                long id;

                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return Flux.empty();
                }

                return getPersonalShopById(id).flux();

            case BY_TITLE:
                return getPersonalShopByTitle(value);
            case BY_ADDRESS:

                return getPersonalShopByAddress(value);
            default:
                return userService.getPrincipal().
                        flatMapMany(principal -> getShopsByUserId(principal.getId()));
        }
    }
}
