package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class PropertyShop entity. Table named "property_shop". Use {@link TabsOfShopProperty} for value name
 */
@Data
@Table
@NoArgsConstructor
public class PropertyShop {
    @Id
    private Long id;

    private LocalDate created;

    private LocalDate updated;

    @Transient
    private TabsOfShopProperty property;

    private String name;

    public PropertyShop(Long id, String name) {
        this.id = id;
        this.name = name;
        this.property = TabsOfShopProperty.valueOf(name);

    }

}