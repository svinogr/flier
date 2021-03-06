package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class Role entity. Table named "role". Use {@link UserRole} for value name
 */
@Data
@Table
@NoArgsConstructor
public class Role {
    @Id
    private Long id;

    private LocalDate created;

    private LocalDate updated;

    private Status status;

    private String name;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

