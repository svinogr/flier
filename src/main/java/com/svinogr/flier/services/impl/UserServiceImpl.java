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

import java.util.ArrayList;
import java.util.List;

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
        return userRepo.findAll().flatMap(user -> {
            System.out.println("user id " + user.getId());
            return userRolesRepo.findByUserId(user.getId()).
                    flatMap(ur -> {
                        System.out.println("ur id " + ur.getId());
                        return roleRepo.findById(ur.getRoleId()).
                                flatMap(r -> {
                                    Role role = new Role();
                                    role.setName(UserRole.valueOf(r.getName()).name());
                                    System.out.println(role.getName());
                                    user.getRoles().add(role);
                                    return Mono.just(user);

                                });

                    });

        });
    }


    @Override
    public Mono<User> findUserById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public Mono<User> registerUser(User user) {
        Role role = user.getRoles().get(0);
        UserRole userRole = UserRole.valueOf(role.getName());

        return saveUser(user, userRole);
    }

    @Override
    public Mono<User> update(User user) {
        return userRepo.save(user);
    }

    private Mono<User> saveUser(User user, UserRole userRole) {
        return roleRepo.findByName(userRole.name())
                .flatMap(role -> {
                    List<Role> roles = new ArrayList();
                    roles.add(role);
                    user.setRoles(roles);
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
