package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class BaseEntity  {
    @Id
    private Long id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String status;

}
