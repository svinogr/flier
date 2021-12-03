package com.svinogr.flier.services;

import com.svinogr.flier.model.PropertiesShops;
import com.svinogr.flier.model.PropertyShop;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Interface for tabs services
 */
public interface PropertyShopService {
    /**
     * Get all tabs
     *
     * @return found {@link PropertyShop}
     */
    Flux<PropertyShop> getAllTabs();

    Flux<PropertyShop> getByIdsPropertiesShops(List<PropertiesShops> list);
}
