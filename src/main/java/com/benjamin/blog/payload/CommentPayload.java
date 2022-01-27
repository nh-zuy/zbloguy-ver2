package com.benjamin.blog.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentPayload {
    private long id;

    @NotEmpty(message = "Must not be null or empty")
    private String name;

    @NotEmpty(message = "Must not be null or empty")
    @Email
    private String email;

    @NotEmpty
    @Size(min = 10, message = "Must have at least 10 characters")
    private String body;
}
