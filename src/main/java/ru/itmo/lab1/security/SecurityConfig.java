package ru.itmo.lab1.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private UnauthorizedHandler unauthorizedHandler;
    private TokenFilter tokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                //TODO add all endpoints
                .authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers("/instrument/**").hasAnyRole("ADMIN", "TRADER", "MAINTAINER")
                .antMatchers("/market/**").hasAnyRole("ADMIN", "TRADER", "MAINTAINER")
                .antMatchers("/marketInstrumentId/**").hasAnyRole("ADMIN", "TRADER", "MAINTAINER")
                .antMatchers("/portfolio/**").hasAnyRole("ADMIN", "TRADER", "MAINTAINER")
                .anyRequest().authenticated();

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
