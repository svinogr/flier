package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class Base entity for entity
 */
@Data
public class BaseEntity  {
    @Id
    private Long id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String status;

}
