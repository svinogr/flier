package com.svinogr.flier.controllers.api;

import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class Restcontroller for {@link Stock}
 */

@RestController
@RequestMapping("api")
public class StockApiCtrl {
    @Autowired
    StockService stockService;

    /**
     * Returns json stocks by shop id and parameters of getting
     *
     * @param f number of start getting
     * @param q quantity of getting
     * @param idSh shop id from db
     * @return found stocks
     */
    @GetMapping("shop/{idSh}/stock")
    public Flux<Stock> getAllStocksByShopId(@PathVariable long idSh,
                                            @RequestParam("from") String f,
                                            @RequestParam("quantity") String q) {
        long from, quantity;

        try {
            from = Long.parseLong(f);
            quantity = Long.parseLong(q);
        } catch (NumberFormatException e) {
            return Flux.empty();
        }

        return stockService.findStocksByShopId(idSh).
                sort(Comparator.comparingLong(Stock::getId)).
                skip(from).take(quantity);
    }

    /**
     * Returns stock by id
     *
     * @param id stock id in db
     * @return found stock
     */
    @GetMapping("stock/{id}")
    public Mono<Stock> getStockById(@PathVariable long id) {
        return stockService.findStockById(id);
    }

    /**
     * Returns json stocks by searching value and parameters of getting
     *
     * @param searchValue value for searching
     * @param f number of start getting
     * @param q quantity of getting
     * @return found stocks
     */
    @GetMapping("stock/search")
    public Flux<Stock> search(@RequestParam("searchValue") String searchValue,
                              @RequestParam("from") String f,
                              @RequestParam("quantity") String q) {
        long from, quantity;

        try {
            from = Long.parseLong(f);
            quantity = Long.parseLong(q);
        } catch (NumberFormatException e) {
            return Flux.empty();
        }

        return stockService.searchByValueTags(searchValue).
                sort(Comparator.comparingLong(Stock::getId)).
                skip(from).take(quantity);
    }
}

