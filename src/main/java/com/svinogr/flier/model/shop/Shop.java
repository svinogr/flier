package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("shops")
@Data
public class Shop extends BaseEntity {
    private Long userId;
    @Column("lat")
    private Float coordLat;
    @Column("lng")
    private Float coordLng;
    private String title;
    private String address;
    private String description;
    private String url;
    private String img;
    @Transient
    private Stock[] stokcs;
}
