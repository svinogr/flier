package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.*;
import com.svinogr.flier.repo.RoleRepo;
import com.svinogr.flier.repo.UserRepo;
import com.svinogr.flier.repo.UserRolesRepo;
import com.svinogr.flier.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class service implementation {@link UserService}
 */
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
            return userRolesRepo.findByUserId(user.getId()).
                    flatMap(ur -> {
                        return roleRepo.findById(ur.getRoleId()).
                                flatMap(r -> {
                                    Role role = new Role();
                                    role.setName(UserRole.valueOf(r.getName()).name());
                                    user.getRoles().add(role);

                                    return Mono.just(user);
                                });
                    });
        });
    }
 
    @Override
    public Mono<User> findUserById(Long id) {
        return userRepo.findById(id)
                .flatMap(u -> {
                    return userRolesRepo.findByUserId(u.getId())
                            .flatMap(ur -> {
                                return roleRepo.findById(ur.getRoleId())
                                        .flatMap(r -> {
                                            UserRole userRole = UserRole.valueOf(r.getName());

                                            Role role = new Role();
                                            role.setName(userRole.name());

                                            u.getRoles().add(role);

                                            return Mono.just(u);
                                        });
                            });
                });
    }

    @Override
    public Mono<User> registerUser(User user) {
        Role role = user.getRoles().get(0);
        UserRole userRole = UserRole.valueOf(role.getName());

        return saveUser(user, userRole);
    }

    @Override
    public Mono<User> update(User user) {
        Role role = user.getRoles().get(0);
        UserRole userRole = UserRole.valueOf(role.getName());

        return updateUser(user, userRole);
    }

    private Mono<User> updateUser(User user, UserRole userRole) {
        return userRolesRepo.findByUserId(user.getId())
                .flatMap(ur -> {
                    return roleRepo.findById(ur.getRoleId())
                            .flatMap(r -> {
                                if (!r.getName().equals(userRole.name())) {
                                    return roleRepo.findByName(userRole.name())
                                            .flatMap(r1 -> {
                                                ur.setRoleId(r1.getId());
                                                return userRolesRepo.save(ur);
                                            });
                                }
                                return Mono.just(r);
                            });
                })
                .flatMap(r -> {
                    return userRepo.update(user)
                                    .flatMap(ok -> {
                                        if (ok) {
                                            return findUserByIdSafety(user.getId());
                                        }

                                        return Mono.just(new User());
                                    });
                });
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
        return userRepo.findByUsername(name)
                .flatMap(user -> userRolesRepo.findByUserId(user.getId()). // находим юзерроль по юзер id
                        flatMap(uR -> roleRepo.findById(uR.getId()). // находим роль по по роль id
                        flatMap(r -> {
                    Role role = new Role(r.getId(), r.getName());

                    user.getRoles().add(role);
                    return Mono.just(user);
                })));
    }

    @Override
    public Mono<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .flatMap(user -> userRolesRepo.findByUserId(user.getId()). // находим юзерроль по юзер id
                        flatMap(uR -> roleRepo.findById(uR.getRoleId()). // находим роль по по роль id
                        flatMap(r -> {
                    Role role = new Role(r.getId(), r.getName());

                    user.getRoles().add(role);
                    return Mono.just(user);
                })));
    }

    @Override
    public Mono<User> findUserByIdSafety(long id) {
        return userRepo.findById(id).
                flatMap(user -> {
                    user.setPassword("secret"); // чтобы не отдать настоящий пароль
                    return Mono.just(user);
                });
    }

    @Override
    public Mono<User> deleteUser(Long id) {
        return userRepo.findById(id).
                flatMap(u -> {
                    u.setStatus(Status.NON_ACTIVE.name());
                    return userRepo.save(u);
                });
    }

    @Override
    public Mono<User> getPrincipal() {
        return ReactiveSecurityContextHolder.getContext().
                flatMap(sC -> Mono.just(sC.getAuthentication().getPrincipal())).cast(User.class);
    }

    @Override
    public Mono<Boolean> isAdmin() {
        return getPrincipal().
                flatMap(u -> Mono.just(u.getRoles().get(0).getName().equals(UserRole.ROLE_ADMIN.name())));
    }

    @Override
    public Mono<Boolean> isOwnerOfAccount(Long accountId) {
        return  getPrincipal().flatMap(user -> Mono.just(user.getId() == accountId));
    }

    @Override
    public Mono<Long> getCountUsers() {
        return userRepo.count();
    }
}