package com.benjamin.blog.controller.rest;

import com.benjamin.blog.entity.User;
import com.benjamin.blog.payload.responses.JwtAuthResponse;
import com.benjamin.blog.payload.requests.LoginRequest;
import com.benjamin.blog.payload.requests.RegisterRequest;
import com.benjamin.blog.security.JwtTokenProvider;
import com.benjamin.blog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticate(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(),
                request.getPassword());
        Authentication authentication = authenticationManager.authenticate(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User registeredUser = authService.register(request);

        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }
}
