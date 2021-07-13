package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.BaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("stocks")
@Data
public class Stock extends BaseEntity {
    private Long shopId;
    private String title;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateStart;
    //   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFinish;
    private String img;
    private String url;
}
