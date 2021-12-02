package com.svinogr.flier.controllers.api;


import com.svinogr.flier.model.PropertyShop;
import com.svinogr.flier.services.PropertyShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class Restcontroller for {@link com.svinogr.flier.model.PropertyShop}
 */
@RestController
@RequestMapping("api")
public class PropertyShopApi {
    @Autowired
    private PropertyShopService propertyShopService;

    /**
     * Returns json found properties shop
     *
     * @return found properties shop
     */
    @GetMapping("tab")
    public Flux<PropertyShop> getAllProperties() {
        return propertyShopService.getAllTabs();
    }
}
