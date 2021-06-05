package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;


@Data
public class BaseEntity  {
    @Id
    private Long id;

    private LocalDate created;

    private LocalDate updated;

    private String status;

}
