package com.benjamin.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BadAPIException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
