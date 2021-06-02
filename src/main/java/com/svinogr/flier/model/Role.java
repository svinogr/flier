package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.List;



@Table("roles")
@Entity(name = "roles")
@Data
public class Role extends BaseEntity {
    @Column(name = "name")
    private String name;

    // связь для User
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;
}

