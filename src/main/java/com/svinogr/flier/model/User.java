package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class User entity. Table named "usr". Implements {@link UserDetails}
 */
@Data
@Table("usr")
public class User extends BaseEntity implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role ->
          new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.getStatus().equals(Status.ACTIVE.name());
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.getStatus().equals(Status.ACTIVE.name());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.getStatus().equals(Status.ACTIVE.name());
    }

    @Override
    public boolean isEnabled() {
        return this.getStatus().equals(Status.ACTIVE.name());
    }
}
