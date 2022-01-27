package com.benjamin.blog.payload.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtAuthResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
}
