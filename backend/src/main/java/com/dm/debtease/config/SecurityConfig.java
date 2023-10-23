package com.dm.debtease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui.html",
            "/api/users/login",
            "/api/users/refresh/token"
    };
    private final BCryptPasswordEncoder bCryptEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(BCryptPasswordEncoder bCryptEncoder, UserDetailsService userDetailsService,
                          JwtTokenFilter jwtTokenFilter) {
        this.bCryptEncoder = bCryptEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptEncoder);

        return daoAuthenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/creditors/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditors/profile/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtcases/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtcases").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditors").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtors").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditor/*/debtcases").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/creditor/debtcases/debtor/*").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtors/*").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/debtors/profile/*").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/creditors/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/creditor/*/debtcases/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/creditor/*/debtcase/*/debtors/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/debtors/*").hasAnyAuthority("DEBTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/creditor/*/debtcases/file").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/creditors/").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/creditor/*/debtcases/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/creditor/*/debtcase/*/debtors/*").hasAnyAuthority("CREDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/creditors/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/debtors/*").hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }
}
