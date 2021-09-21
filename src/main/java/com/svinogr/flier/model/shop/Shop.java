package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import java.util.List;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class Shop entity. Table named "shops"
 */
@Table("shops")
@Data
public class Shop extends BaseEntity {
    private Long userId;
    @Column("lat")
    private double coordLat;
    @Column("lng")
    private double coordLng;
    @NotBlank(message = "поле не должно быть пустым")
    private String title;
    @NotBlank(message = "поле не должно быть пустым")
    private String address;
    @NotBlank(message = "поле не должно быть пустым")
    private String description;

    private String url;
    private String img;
    @Transient
    private List stocks;
}
