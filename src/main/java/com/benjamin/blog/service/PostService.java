package com.benjamin.blog.service;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.exception.NotFoundException;
import com.benjamin.blog.mapper.PostMapper;
import com.benjamin.blog.payload.PostPayload;
import com.benjamin.blog.payload.responses.PostResponse;
import com.benjamin.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostMapper postMapper, PostRepository postRepository) {
        this.postMapper = postMapper;
        this.postRepository = postRepository;
    }

    public PostResponse getPosts(int pageNo, int pageSize, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<Post> pageablePosts = postRepository.findAll(PageRequest.of(pageNo, pageSize, sort));
        List<PostPayload> posts = pageablePosts.getContent()
                .stream()
                .map(postMapper::toPostPayload)
                .collect(Collectors.toList());

        return PostResponse.builder()
                .content(posts)
                .pageNo(pageablePosts.getNumber())
                .pageSize(pageablePosts.getSize())
                .totalPosts(pageablePosts.getTotalElements())
                .totalPages(pageablePosts.getTotalPages())
                .last(pageablePosts.isLast())
                .build();
    }

    public PostPayload getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post", "id", String.valueOf(id)));
        return postMapper.toPostPayload(post);
    }

    public PostPayload createPost(PostPayload payload) {
        Post post = postMapper.toPost(payload);
        Post newPost = postRepository.save(post);

        return postMapper.toPostPayload(newPost);
    }

    public PostPayload updatePostById(long id, PostPayload payload) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post", "id", String.valueOf(id)));
        post.update(payload);
        Post updatedPost = postRepository.save(post);

        return postMapper.toPostPayload(updatedPost);
    }

    public void deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post", "id", String.valueOf(id)));
        postRepository.delete(post);
    }
}
