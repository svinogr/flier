package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.*;
import com.svinogr.flier.repo.RoleRepo;
import com.svinogr.flier.repo.UserRepo;
import com.svinogr.flier.repo.UserRolesRepo;
import com.svinogr.flier.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    private final RoleRepo roleRepo;

    private final UserRolesRepo userRolesRepo;

    private final BCryptPasswordEncoder passwordEncoder;


    public UserServiceImpl(RoleRepo roleRepo, UserRepo userRepo, UserRolesRepo userRolesRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRolesRepo = userRolesRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Flux<User> findAll() {
        return null;
    }

    @Override
    public Mono<User> findUserById(Long id) {
        return null;
    }

    @Override
    public Mono<User> registerUser(User user) {
        return saveUser(user, UserRole.ROLE_USER);
    }

    private Mono<User> saveUser(User user, UserRole userRole) {
       return roleRepo.findByName(userRole.name())
                .flatMap(role -> {
                    user.getRoles().add(role);
                    return Mono.just(user);
                })
                .flatMap(userRepo::save)
                .flatMap(u -> {
                    UserRoles uR = new UserRoles(u.getId(), u.getRoles().get(0).getId());
                    return Mono.zip(Mono.just(u), userRolesRepo.save(uR));
                })
                .flatMap(data -> Mono.just(data.getT1()));
    }

    @Override
    public Mono<User> registerAdmin(User user) {
        return saveUser(user, UserRole.ROLE_ADMIN);
    }

    @Override
    public Mono<User> registerAccount(User user) {
        return saveUser(user, UserRole.ROLE_ACCOUNT);
    }

    @Override
    public Mono<User> findUserByName(String name) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
