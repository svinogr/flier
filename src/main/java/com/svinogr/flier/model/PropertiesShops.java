package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(value = "properties_shops")
public class PropertiesShops extends BaseEntity {
    @Column("shop_id")
    private Long shopId;

    @Column("property_id")
    private Long propertyId;

    public PropertiesShops(Long shopId, Long propertyId) {
        this.shopId = shopId;
        this.propertyId = propertyId;
    }

    public PropertiesShops() {
    }
}
