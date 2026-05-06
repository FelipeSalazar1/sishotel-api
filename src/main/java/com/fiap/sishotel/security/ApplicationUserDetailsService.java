package com.fiap.sishotel.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final InMemoryUserDetailsManager manager;

    public ApplicationUserDetailsService(PasswordEncoder passwordEncoder) {
        this.manager = new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password(passwordEncoder.encode("123456"))
                        .roles("ADMIN")
                        .build()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return manager.loadUserByUsername(username);
    }
}
