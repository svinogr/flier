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
    /**
     * Get all users {@link User} from db
     *
     * @return found users
     */
    Flux<User> findAll();

    /**
     * Get user {@link User} form db by id
     *
     * @param id user id from db
     * @return found user. Mono<User>
     */
    Mono<User> findUserById(Long id);

    /**
     * Creating new user in db
     *
     * @param user {@link User}
     * @return created user
     */
    Mono<User> registerUser(User user);

    /**
     * Updating user in db
     *
     * @param user {@link User}
     * @return updated user
     */
    Mono<User>  update(User user);

    /**
     * @param name
     * @return
     */
    Mono<User> findUserByName(String name);

    /**
     * Get user {@link User} by email from db
     *
     * @param email email of user from db
     * @return found user
     */
    Mono<User> findUserByEmail(String email);

    /**
     * Get user {@link User} by name without PASSWORD
     *
     * @param name username
     * @return found user
     */
    Mono<User> findUserByIdSafety(long name);

    /**
     * Deleting user {@link User} by id from db
     *
     * @param id user id in db
     * @return deleted user
     */
    Mono<User> deleteUser(Long id);

    /**
     * Get signed user {@link User}
     *
     * @return signed user
     */
    Mono<User> getPrincipal();

    /**
     * Indicating of signed user has role {@link com.svinogr.flier.model.UserRole} ROLE_ADMIN
     *
     * @return result of check. Mono<Boolean>
     */
    Mono<Boolean> isAdmin();

    /**
     * Indicating of value userId belongs to signed user
     *
     * @param userId user id
     * @return result of check. Mono<Boolean>
     */
    Mono<Boolean> isOwnerOfAccount(Long userId);

    /**
     * Get count of users {@link User} in db
     *
     * @return count of user
     */
    Mono<Long> getCountUsers();
}
