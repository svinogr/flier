package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;


/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class PropertyShop entity. Table named "property_shop".
 */
@Data
@Table(value = "property_shop")
public class PropertyShop extends BaseEntity {
    private String name;

    public PropertyShop() {
    }

    public PropertyShop(String name, Long id) {
        this.name = name;
        super.setId(id);
    }
}