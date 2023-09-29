package com.dm.debtease.service.impl;

import com.dm.debtease.model.CustomUser;
import com.dm.debtease.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final CustomUserRepository customUserRepository;

    @Autowired
    public CustomUserDetailsServiceImpl(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<CustomUser> optionalCustomUser = customUserRepository.findByUsername(username);
        if (optionalCustomUser.isPresent()) {
            CustomUser customUser = optionalCustomUser.get();

            GrantedAuthority authority = new SimpleGrantedAuthority(customUser.getRole().getName());

            return new User(customUser.getUsername(), customUser.getPassword(), Collections.singleton(authority));
        }

        throw new UsernameNotFoundException("User not found with username " + username);
    }
}