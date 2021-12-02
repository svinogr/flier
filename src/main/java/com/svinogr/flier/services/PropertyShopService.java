package com.svinogr.flier.services;

import com.svinogr.flier.model.PropertyShop;
import reactor.core.publisher.Flux;

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

    /**
     * Get all tabs of shop by id
     *
     * @return found {@link PropertyShop}
     */
    Flux<PropertyShop> getAllTabsByShopId(Long shopId);
}
