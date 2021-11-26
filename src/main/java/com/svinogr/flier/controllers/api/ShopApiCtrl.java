package com.svinogr.flier.controllers.api;

import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.TabsOfShopProperty;
import com.svinogr.flier.model.shop.Shop;
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
     * @param f   number of start getting
     * @param q   quantity of getting
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
     * @param f number of start getting
     * @param q quantity of getting
     * @return found shops
     */
    @GetMapping("shop/all")
    public Flux<Shop> getAllShops(@RequestParam("from") String f,
                                  @RequestParam("quantity") String q) {
        long from, quantity;

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


    /**
     * Returns json ALL nearest shops by parameters of getting
     *
     * @param lng        longitude
     * @param lat        latitude
     * @param tabString  selected searching parameter
     * @param searchText searching text in shops and stocks
     * @param f          start of getting
     * @param q          quantity of getting
     * @return found shops
     */
    @GetMapping("shop/nearest/search/")
    public Flux<Shop> getAllSearchingNearestShop(@RequestParam("lng") String lng,
                                                 @RequestParam("lat") String lat,
                                                 @RequestParam("tab") String tabString,
                                                 @RequestParam("searchText") String searchText,
                                                 @RequestParam("from") String f,
                                                 @RequestParam("quantity") String q) {

        long from, quantity;
        double latitude, langitude;
        TabsOfShopProperty tab;
        if (!tabString.isBlank()) {
            tab = TabsOfShopProperty.valueOf(tabString);
        } else {
            tab = TabsOfShopProperty.FOOD;
        }
        searchText.trim();

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


        return shopService.getSearchAllShopsAroundCoord(coord, searchText).
                sort(Comparator.comparingLong(Shop::getId)).
                filter(s -> s.getProperty() == tab).
                skip(from).take(quantity);
    }

    /**
     * Returns json ALL  shops by parameters of getting
     *
     * @param tabString  selected searching parameter
     * @param searchText searching text in shops and stocks
     * @param f          start of getting
     * @param q          quantity of getting
     * @return found shops
     */
    @GetMapping("shop/search")
    public Flux<Shop> getAllSearchingShops(@RequestParam("tab") String tabString,
                                           @RequestParam("searchText") String searchText,
                                           @RequestParam("from") String f,
                                           @RequestParam("quantity") String q) {

        long from, quantity;
        double latitude, langitude;
        TabsOfShopProperty tab;
        if (!tabString.isBlank()) {
            tab = TabsOfShopProperty.valueOf(tabString);
        } else {
            tab = TabsOfShopProperty.FOOD;
        }
        searchText.trim();

        try {
            from = Long.parseLong(f);
            quantity = Long.parseLong(q);

        } catch (NumberFormatException e) {
            return Flux.empty();
        }

        return shopService.searchShopsBySearchingTextInShopsAndStocks(searchText).
                sort(Comparator.comparingLong(Shop::getId)).
                //filter(s -> s.getProperty() == tab).
                skip(from).take(quantity);
    }


}
