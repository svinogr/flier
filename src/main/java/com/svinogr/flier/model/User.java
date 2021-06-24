package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@Data
@Table("usr")
public class User extends BaseEntity {
    @Column("user_name")
    @NotBlank(message = "поле не должно быть пустым")
    private String username;
    @NotBlank(message = "поле не должно быть пустым")
    private String name;
    @NotBlank(message = "поле не должно быть пустым")
    private String surname;
    @NotBlank(message = "поле не должно быть пустым")
    private String phone;
    @NotBlank(message = "поле не должно быть пустым")
    private String email;
    @NotBlank(message = "поле не должно быть пустым")
    private String password;

    @Transient
    private List<Role> roles = new ArrayList<>();

}
