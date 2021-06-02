package com.svinogr.flier.repo;

import com.svinogr.flier.model.Role;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepo extends ReactiveCrudRepository<Role, Long> {
     Mono<Role> findByName(String name);
}

