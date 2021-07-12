package com.svinogr.flier.repo;

import com.svinogr.flier.model.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepo extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String name);

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
