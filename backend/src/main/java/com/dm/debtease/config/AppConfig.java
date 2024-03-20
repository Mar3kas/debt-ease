package com.dm.debtease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@SuppressWarnings("unused")
public class AppConfig {
    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }
}
