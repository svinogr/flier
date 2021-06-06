package com.svinogr.flier.services;

import com.svinogr.flier.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<User> findAll();
    Mono<User> findUserById(Long id);
    Mono<User> registerUser(User user);
    Mono<User>  update(User user);
    Mono<User> registerAdmin(User user);
    Mono<User> registerAccount(User user);
    Mono<User> findUserByName(String name);
    Mono<User> deleteUser(Long id);
}
