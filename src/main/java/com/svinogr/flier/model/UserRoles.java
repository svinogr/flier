package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user_roles")
@NoArgsConstructor
public class UserRoles {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("role_id")
    private Long roleId;

    public UserRoles(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
