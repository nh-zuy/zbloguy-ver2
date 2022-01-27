package com.benjamin.blog.service;

import com.benjamin.blog.entity.Role;
import com.benjamin.blog.entity.User;
import com.benjamin.blog.exception.BadAPIException;
import com.benjamin.blog.exception.NotFoundException;
import com.benjamin.blog.payload.requests.RegisterRequest;
import com.benjamin.blog.repository.RoleRepository;
import com.benjamin.blog.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Email is already taken!");
        }

        String username = request.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Username is already taken!");
        }

        Role roles = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new NotFoundException("Role", "role", "ROLE_USER"));
        User user = User.builder()
                .name(request.getName())
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Arrays.asList(roles))
                .build();
        userRepository.save(user);

        return user;
    }
}
