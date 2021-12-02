package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.BaseEntity;
import com.svinogr.flier.model.PropertyShop;
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
    private List<PropertyShop> listOfProperty;
    @Transient
    private List stocks;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(double coordLat) {
        this.coordLat = coordLat;
    }

    public double getCoordLng() {
        return coordLng;
    }

    public void setCoordLng(double coordLng) {
        this.coordLng = coordLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<PropertyShop> getListOfProperty() {
        return listOfProperty;
    }

    public void setListOfProperty(List<PropertyShop> listOfProperty) {
        this.listOfProperty = listOfProperty;
    }

    public List getStocks() {
        return stocks;
    }

    public void setStocks(List stocks) {
        this.stocks = stocks;
    }
}
