package com.svinogr.flier.services.impl;

import com.svinogr.flier.model.Role;
import com.svinogr.flier.model.Status;
import com.svinogr.flier.model.User;
import com.svinogr.flier.model.UserRole;
import com.svinogr.flier.repo.RoleRepo;
import com.svinogr.flier.repo.UserRepo;
import com.svinogr.flier.services.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    private final RoleRepo roleRepo;

    private final BCryptPasswordEncoder passwordEncoder;


    public UserServiceImpl( RoleRepo roleRepo, UserRepo userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
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
        Mono<Role> roleM = roleRepo.findByName(userRole.name());

        List<Role> listRoles = new ArrayList<>();
        listRoles.add(roleM.block());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
           user.setRoles(listRoles);
        user.setStatus(Status.ACTIVE);

        Mono<User> saveUser = userRepo.save(user);

        log.info("IN register - user: {} successfully registered with role:{}", saveUser, roleM);

        return saveUser;
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
