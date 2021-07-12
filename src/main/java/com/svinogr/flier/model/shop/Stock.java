package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.BaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("stocks")
@Data
public class Stock extends BaseEntity {
    private Long shopId;
    private String title;
    private String description;
    private LocalDate dateStart;
    private LocalDate dateFinish;
    private String img;
    private String url;
}
