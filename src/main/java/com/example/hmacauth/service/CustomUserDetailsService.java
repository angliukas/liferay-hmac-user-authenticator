package com.example.hmacauth.service;

import java.util.Collections;

import com.example.hmacauth.model.LiferayUser;
import com.example.hmacauth.repository.LiferayUserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LiferayUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(LiferayUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LiferayUser liferayUser = userRepository
            .findByScreenNameOrEmailAddress(username, username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String encodedPassword = liferayUser.isPasswordEncrypted()
            ? liferayUser.getPassword()
            : passwordEncoder.encode(liferayUser.getPassword());

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        boolean enabled = liferayUser.getStatus() == 0;

        return new User(
            liferayUser.getScreenName(),
            encodedPassword,
            enabled,
            true,
            true,
            true,
            Collections.singleton(authority)
        );
    }
}
