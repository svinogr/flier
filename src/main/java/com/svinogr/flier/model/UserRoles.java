package com.svinogr.flier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

@Data
@NoArgsConstructor
public class UserRoles {
    @Column("user_id")
    private Long userId;

    @Column("role_id")
    private Long roleId;
}
