package com.myfinwallet.finwallet_api.security;

import com.myfinwallet.finwallet_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override 
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .map(user -> org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .accountLocked(user.isLocked())
                .roles("USER")
                .build())
            .orElseThrow(() -> new UsernameNotFoundException ("Usuario no encontrado: " + email));
    }
}
