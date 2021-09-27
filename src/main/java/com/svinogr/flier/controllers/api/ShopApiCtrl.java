package com.svinogr.flier.controllers.api;

import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.model.shop.Stock;
import com.svinogr.flier.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class Restcontroller for {@link Shop}
 */
@RestController
@RequestMapping("api")
public class ShopApiCtrl {
    @Autowired
    ShopService shopService;


    /**
     * Returns json found shops by around coord and parameters of getting
     *
     * @param lat coord of latitude
     * @param lng coord of latitude
     * @param f number of start getting
     * @param q quantity of getting
     * @return found shops
     */
    @GetMapping("shop")
    public Flux<Shop> getAllShopByCoord(
            @RequestParam String lat,
            @RequestParam String lng,
            @RequestParam("from") String f,
            @RequestParam("quantity") String q) {
        long from, quantity;
        double latitude, langitude;

        try {
            from = Long.parseLong(f);
            quantity = Long.parseLong(q);
            langitude = Double.parseDouble(lng);
            latitude = Double.parseDouble(lat);

        } catch (NumberFormatException e) {
            return Flux.empty();
        }

        Coord coord = new Coord(langitude, latitude);
        System.out.println(coord);

        return shopService.getAllShopsAroundCoord(coord).
                sort(Comparator.comparingLong(Shop::getId)).
                skip(from).take(quantity);
    }

    /**
     * Returns json ALL shops by parameters of getting
     *
     *
     * @param f number of start getting
     * @param q quantity of getting
     * @return found shops
     */
    @GetMapping("shop/all")
    public Flux<Shop> getAllShops(@RequestParam("from") String f,
                                     @RequestParam("quantity") String q) {
        long from, quantity;
        double latitude, langitude;

        try {
            from = Long.parseLong(f);
            quantity = Long.parseLong(q);
        } catch (NumberFormatException e) {
            return Flux.empty();
        }

        return shopService.getAllShops().
                sort(Comparator.comparingLong(Shop::getId)).
                skip(from).take(quantity);
    }

    /**
     * Returns json found shops by id
     *
     * @param id id shop from db
     * @return found shop. Mono<Shop>
     */
    @GetMapping("shop/{id}")
    public Mono<Shop> getShopById(@PathVariable long id) {
        return shopService.getShopById(id);
    }
}
