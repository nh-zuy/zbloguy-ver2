package com.benjamin.blog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    private String resource;
    private String name;
    private String value;

    public NotFoundException(String resource, String name, String value) {
        super(String.format("%s not found with %s : %s", resource, name, value));
        this.resource = resource;
        this.name = name;
        this.value = value;
    }
}
