package com.benjamin.blog.controller.mvc;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.repository.PostRepository;
import com.benjamin.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {

    private final PostService postService;
    private final PostRepository postRepository;

    public PageController(PostService postService, PostRepository postRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
    }

    @GetMapping({"/", "index", "index.html"})
    public String index(final Model model) {
        List<Post> posts = postRepository.findAll(Sort.by("id").ascending());
        model.addAttribute("posts", posts);

        Page<Post> pageablePosts = postRepository.findAll(PageRequest.of(0, 6, Sort.by("id").descending()));
        List<Post> newestPosts = pageablePosts.getContent();
        model.addAttribute("newestPosts", newestPosts);

        return "index";
    }

    @GetMapping("/post.html")
    public String posts() {
        return "post";
    }

    @GetMapping("/author.html")
    public String author() {
        return "author";
    }
}
