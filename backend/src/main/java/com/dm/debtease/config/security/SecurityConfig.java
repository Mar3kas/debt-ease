package com.dm.debtease.config.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityConfig implements WebMvcConfigurer {
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui.html",
            "/api/login",
            "/api/refresh",
            "/ws/**"
    };

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final BCryptPasswordEncoder bCryptEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**").allowedOrigins("http://localhost:3000").allowedMethods("*");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        //.anyRequest().permitAll())
                        .requestMatchers(HttpMethod.GET, "/api/creditors/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditors/profile/*").hasAnyAuthority("CREDITOR",
                                "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtcases/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtcases").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditors").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtors").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditor/* /debtcases").hasAnyAuthority("CREDITOR",
                                "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtor/* /debtcases").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtcases/generate/report/debtor/*").hasAnyAuthority
                                ("DEBTOR")
                        .requestMatchers(HttpMethod.GET, "/api/debtors/*").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtors/profile/*").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/creditors/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/debtcases/* /creditors/*").hasAnyAuthority
                                ("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/debtors/*").hasAnyAuthority("CREDITOR", "ADMIN",
                                "DEBTOR")
                        .requestMatchers(HttpMethod.POST, "/api/debtcases/creditors/* /file").hasAnyAuthority
                                ("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/creditors/").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/logout").hasAnyAuthority("ADMIN", "DEBTOR",
                                "CREDITOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/debtcases/* /creditors/*").hasAnyAuthority
                                ("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/creditor/* /debtcase/* /debtors/*").hasAuthority
                                ("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/creditors/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/debtors/*").hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException)
                                -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }
}
