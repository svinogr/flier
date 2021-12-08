package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
