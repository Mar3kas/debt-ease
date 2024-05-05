package com.dm.debtease.config.security;

import com.dm.debtease.model.Role;
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
            "/ws/**",
            "/api/files/**",
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
                        .requestMatchers(HttpMethod.GET, "/api/creditors/*")
                        .hasAnyAuthority(Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/creditors/profile/*")
                        .hasAnyAuthority(Role.CREDITOR.name(),
                                Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debt/cases/*")
                        .hasAnyAuthority(Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debt/case/types")
                        .hasAnyAuthority(Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debt/cases").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/creditors").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debtors").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/creditor/* /debt/cases")
                        .hasAnyAuthority(Role.CREDITOR.name(),
                                Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debtor/* /debt/cases")
                        .hasAnyAuthority(Role.DEBTOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debt/cases/generate/report/debtor/*").hasAnyAuthority
                                (Role.DEBTOR.name())
                        .requestMatchers(HttpMethod.GET, "/api/debtors/*")
                        .hasAnyAuthority(Role.DEBTOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/debtors/profile/*")
                        .hasAnyAuthority(Role.DEBTOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/payments/*")
                        .hasAnyAuthority(Role.DEBTOR.name(), Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/creditors/*")
                        .hasAnyAuthority(Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/debt/cases/* /creditors/*").hasAnyAuthority
                                (Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/debtors/*")
                        .hasAnyAuthority(Role.CREDITOR.name(), Role.ADMIN.name(),
                                Role.DEBTOR.name())
                        .requestMatchers(HttpMethod.POST, "/api/debt/cases/creditors/* /file").hasAnyAuthority
                                (Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/debt/cases/debtor/* /payment/strategy").hasAuthority
                                (Role.DEBTOR.name())
                        .requestMatchers(HttpMethod.POST, "/api/payments/* /pay").hasAuthority(Role.DEBTOR.name())
                        .requestMatchers(HttpMethod.POST, "/api/creditors/").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/logout")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.DEBTOR.name(),
                                Role.CREDITOR.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/debt/cases/* /creditors/*").hasAnyAuthority
                                (Role.CREDITOR.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/creditor/* /debt/case/* /debtors/*").hasAuthority
                                (Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/creditors/*").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/debtors/*").hasAuthority(Role.ADMIN.name())
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
