package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.*;
import com.svinogr.flier.repo.RoleRepo;
import com.svinogr.flier.repo.UserRepo;
import com.svinogr.flier.repo.UserRolesRepo;
import com.svinogr.flier.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(RoleRepo roleRepo, UserRepo userRepo, UserRolesRepo userRolesRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRolesRepo = userRolesRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Flux<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public Mono<User> findUserById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public Mono<User> registerUser(User user) {
        return saveUser(user, UserRole.ROLE_USER);
    }

    @Override
    public Mono<User> update(User user) {
        return userRepo.save(user);
    }

    private Mono<User> saveUser(User user, UserRole userRole) {
        return roleRepo.findByName(userRole.name())
                .flatMap(role -> {
                    user.getRoles().add(role);
                    user.setPassword(passwordEncoder.encode(user.getPassword()));

                    return Mono.just(user);
                })
                .flatMap(userRepo::save)
                .flatMap(u -> {
                    UserRoles uR = new UserRoles(u.getId(), u.getRoles().get(0).getId());
                    userRolesRepo.save(uR).subscribe();

                    return Mono.just(u);
                });
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
        return userRepo.findByUsername(name);
    }

    @Override
    public Mono<User> deleteUser(Long id) {
        return userRepo.findById(id).
                flatMap(u -> {
                    u.setStatus(Status.NON_ACTIVE.name());
                    return userRepo.save(u);
                });
    }
}
