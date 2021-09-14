package com.svinogr.flier.repo;

import com.svinogr.flier.model.User;
import com.svinogr.flier.model.shop.Stock;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class Reactive repository for {@link User} implements {@link ReactiveCrudRepository}
 */
@Repository
public interface UserRepo extends ReactiveCrudRepository<User, Long> {
    /**
     * Method searching by username
     *
     * @param name value for search in column "user_name"
     * @return found user. Mono<User>
     */
    Mono<User> findByUsername(String name);

    /**
     * Method searching by email
     *
     * @param email value for search in column "email"
     * @return found user. Mono<User>
     */
    Mono<User> findByEmail(String email);

    /**
     * Custom query for update entity {@link User}.
     * Sets column updated by default.
     *
     *
     * @param user {@link User}
     * @return boolean status of operation. Mono<Boolean>
     */
    @Modifying
    @Query("update usr set" +
            " user_name = :#{#user.username}," +
            " email = :#{#user.email}," +
            " name = :#{#user.name}," +
            " surname = :#{#user.surname}," +
            " phone = :#{#user.phone}," +
            " status = :#{#user.status}," +
            " updated = DEFAULT where id= :#{#user.id}")
    Mono<Boolean> update(User user);
}
