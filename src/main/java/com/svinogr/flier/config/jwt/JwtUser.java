/*
package com.svinogr.flier.config.jwt;

import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class JwtUser implements UserDetails {
    private final User user;

    public JwtUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().
                map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getStatus().equals(Status.ACTIVE.name());
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus().equals(Status.ACTIVE.name());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getStatus().equals(Status.ACTIVE.name());
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals(Status.ACTIVE.name());
    }
}
*/
