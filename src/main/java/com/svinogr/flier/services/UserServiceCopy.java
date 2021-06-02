package com.svinogr.flier.services;

import com.svinogr.flier.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
@Service
public class UserService implements ReactiveUserDetailsService {
    @Autowired
    private UserRepo userRepo;

    public Flux<User> getAlluser(){
        return userRepo.findAll();
    }

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return userRepo.findByName(s).cast(UserDetails.class);
    }
}
*/
