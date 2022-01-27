package com.benjamin.blog.service;

import com.benjamin.blog.entity.Comment;
import com.benjamin.blog.entity.Post;
import com.benjamin.blog.exception.BadAPIException;
import com.benjamin.blog.exception.NotFoundException;
import com.benjamin.blog.mapper.CommentMapper;
import com.benjamin.blog.payload.CommentPayload;
import com.benjamin.blog.repository.CommentRepository;
import com.benjamin.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentMapper commentMapper, PostRepository postRepository, CommentRepository commentRepository) {
        this.commentMapper = commentMapper;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public List<CommentPayload> getComments(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(commentMapper::toCommentPayload)
                .collect(Collectors.toList());
    }

    public CommentPayload createComment(long postId, CommentPayload payload) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", "id", String.valueOf(postId)));

        Comment comment = commentMapper.toComment(payload);
        comment.belongToPost(post);

        Comment newComment = commentRepository.save(comment);

        return commentMapper.toCommentPayload(newComment);
    }

    public CommentPayload getCommentById(long postId, long id) {
        Comment comment = findById(postId, id);
        return commentMapper.toCommentPayload(comment);
    }

    private Comment findById(long postId, long id) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", "id", String.valueOf(postId)));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment", "id", String.valueOf(id)));

        if (!comment.isBelongToPost(post)) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post.");
        }

        return comment;
    }

    public CommentPayload updateCommentById(long postId, long id, CommentPayload payload) {
        Comment comment = findById(postId, id);
        comment.update(payload);
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toCommentPayload(updatedComment);
    }

    public void deleteCommentById(long postId, long id) {
        Comment comment = findById(postId, id);
        commentRepository.delete(comment);
    }
}
