package com.huawei.nce.website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        var user = User.builder()
                .username("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/", "/login.html", "/rest/api/auth/**", "/rest/api/templates/**/download", "/css/**", "/js/**", "/images/**", "/rest/swagger-ui/**", "/rest/api-docs/**", "/rest/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/rest/api/auth/login")
                .successHandler((req, res, auth) -> {
                    res.setStatus(200);
                    res.sendRedirect("/templates.html");
                })
                .failureHandler((req, res, exp) -> res.setStatus(401))
            )
            .logout(logout -> logout
                .logoutUrl("/rest/api/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setStatus(200);
                    res.sendRedirect("/login.html");
                })
            )
            .csrf().disable()
            .cors().disable();
        return http.build();
    }
}
