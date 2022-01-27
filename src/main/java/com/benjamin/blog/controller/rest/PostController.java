package com.benjamin.blog.controller.rest;

import com.benjamin.blog.payload.PostPayload;
import com.benjamin.blog.payload.responses.PostResponse;
import com.benjamin.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PostResponse all(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "order", defaultValue = "asc", required = false) String order
    ) {
        return postService.getPosts(pageNo, pageSize, sortBy, order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostPayload> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<PostPayload> create(@Valid @RequestBody PostPayload payload) {
        PostPayload postResponse = postService.createPost(payload);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostPayload> update(@PathVariable("id") long id, @Valid @RequestBody PostPayload payload) {
        PostPayload updatedPost = postService.updatePostById(id, payload);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        postService.deletePostById(id);
        return new ResponseEntity<>("Delete successfully.", HttpStatus.OK);
    }
}
