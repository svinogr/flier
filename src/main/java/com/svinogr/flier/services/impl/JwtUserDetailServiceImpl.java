package com.svinogr.flier.services.impl;

import com.svinogr.flier.config.jwt.JwtUser;
import com.svinogr.flier.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserServiceImpl userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User subscribe = (User) userService.findUserByName(username).subscribe();
         return new JwtUser(subscribe);
    }
}
