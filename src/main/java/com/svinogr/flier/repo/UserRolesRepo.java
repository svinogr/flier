package com.svinogr.flier.repo;

import com.svinogr.flier.model.User;
import com.svinogr.flier.model.UserRoles;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.function.Consumer;

@Repository
public interface UserRolesRepo extends ReactiveCrudRepository<UserRoles, Long> {

}
