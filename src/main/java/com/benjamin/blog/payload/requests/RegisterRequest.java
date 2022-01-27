package com.benjamin.blog.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String password;
}
