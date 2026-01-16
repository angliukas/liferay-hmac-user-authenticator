package com.example.hmacauth.config;

import com.example.hmacauth.security.CustomAuthenticationProvider;
import com.example.hmacauth.security.HmacAuthenticationFilter;
import com.example.hmacauth.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService(passwordEncoder());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(customUserDetailsService(), passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HmacAuthenticationFilter hmacAuthenticationFilter(AuthenticationManager authenticationManager) {
        HmacAuthenticationFilter filter = new HmacAuthenticationFilter(customUserDetailsService());
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/crm/**", "/login", "/hmac/success").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll();

        http.addFilterBefore(hmacAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }
}
