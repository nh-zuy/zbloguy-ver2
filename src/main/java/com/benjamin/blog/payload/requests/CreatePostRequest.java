package com.benjamin.blog.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CreatePostRequest {
    private Long id;

    @NotEmpty
    @Size(min = 2, message = "Must have at least 2 characters")
    private String title;

    @NotEmpty
    @Size(min = 10, message = "Must have at least 10 characters")
    private String description;

    @NotEmpty
    private String content;

    private String tag;
}
