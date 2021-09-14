package com.svinogr.flier.services;

import com.svinogr.flier.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Interface for user services
 */
public interface UserService {
    Flux<User> findAll();
    Mono<User> findUserById(Long id);
    Mono<User> registerUser(User user);
    Mono<User>  update(User user);
    Mono<User> findUserByName(String name);
    Mono<User> findUserByEmail(String email);
    Mono<User> findUserByIdSafety(long name);
    Mono<User> deleteUser(Long id);
    Mono<User> getPrincipal();
    Mono<Boolean> isAdmin();
    Mono<Boolean> isOwnerOfAccount(Long userId);
    Mono<Long> getCountUsers();
}
