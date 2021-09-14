package com.svinogr.flier.repo;

import com.svinogr.flier.model.UserRoles;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class Reactive repository for {@link UserRoles} implements {@link ReactiveCrudRepository}
 */
@Repository
public interface UserRolesRepo extends ReactiveCrudRepository<UserRoles, Long> {
    /**
     * Method searching by user id
     *
     * @param userId value for search in column "user_id"
     * @return found user roles. Mono<UserRoles>
     */
    Mono<UserRoles> findByUserId(Long userId);
}
