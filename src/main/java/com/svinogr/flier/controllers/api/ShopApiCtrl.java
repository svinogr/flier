package com.svinogr.flier.controllers.api;

import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api")
public class ShopApiCtrl {
    @Autowired
    ShopService shopService;

    /**
     * Returns json found shops by around coord
     *
     * @param coord {@link Coord}
     * @return json shops. Flux<Shop>
     */
    @PostMapping("shop")
    public Flux<Shop> getAllShopByCoord(@RequestBody Coord coord) {
        return shopService.getAllShopsAroundCoord(coord);
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
