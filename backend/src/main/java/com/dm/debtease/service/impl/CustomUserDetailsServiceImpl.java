package com.dm.debtease.service.impl;

import com.dm.debtease.model.CustomUser;
import com.dm.debtease.repository.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final CustomUserRepository customUserRepository;

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