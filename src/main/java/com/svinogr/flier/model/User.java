package com.svinogr.flier.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "usr")
@Table("usr")
public class User extends BaseEntity {
    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    // связь через таблицу "user_roles" в которой есть колонки "user_id" и "role_id"
    // первая замапена на "id" из User, вторая замапена на "id" из Role( в ней тоже есть мапинг ведущий сюда)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;
}
