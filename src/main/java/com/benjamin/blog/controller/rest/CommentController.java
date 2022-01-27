package com.benjamin.blog.controller.rest;

import com.benjamin.blog.payload.CommentPayload;
import com.benjamin.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentPayload> all(@PathVariable("postId") long postId) {
        return commentService.getComments(postId);
    }

    @GetMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentPayload> get(@PathVariable("postId") long postId, @PathVariable("id") long id) {
        CommentPayload comment = commentService.getCommentById(postId, id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentPayload> create(@PathVariable(value = "postId") long postId,
                                                 @Valid @RequestBody CommentPayload payload) {
        CommentPayload newComment = commentService.createComment(postId, payload);
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    @PutMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentPayload> update(@PathVariable("postId") long postId,
                                                 @PathVariable("id") long id,
                                                 @Valid @RequestBody CommentPayload payload) {
        CommentPayload updatedComment = commentService.updateCommentById(postId, id, payload);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<String> delete(@PathVariable("postId") long postId, @PathVariable("id") long id) {
        commentService.deleteCommentById(postId, id);
        return new ResponseEntity<>("Delete successfully.", HttpStatus.OK);
    }
}
