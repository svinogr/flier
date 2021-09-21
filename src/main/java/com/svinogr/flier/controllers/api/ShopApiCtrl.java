package com.svinogr.flier.controllers.api;

import com.svinogr.flier.model.Coord;
import com.svinogr.flier.model.shop.Shop;
import com.svinogr.flier.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api")
public class ShopApiCtrl {
    @Autowired
    ShopService shopService;

    /**
     * Returne json found shops by around coord
     *
     * @param coord {@link Coord}
     * @return json shops
     */
    @PostMapping("shop")
    public Flux<Shop> getAllShopByCoord(@RequestBody Coord coord) {
        return shopService.getAllShopsAroundCoord(coord);
    }
}
