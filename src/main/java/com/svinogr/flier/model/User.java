package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;


@Data
@Table("usr")
public class User extends BaseEntity {
    @Column("user_name")
    private String username;

    private String email;

    private String password;

    @Transient
    private List<Role> roles = new ArrayList<>();

}
