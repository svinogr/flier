package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class entity for union tables by userId and by roleId. Table named "user_roles"
 */
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
