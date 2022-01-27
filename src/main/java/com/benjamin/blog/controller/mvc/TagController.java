package com.benjamin.blog.controller.mvc;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.entity.Tag;
import com.benjamin.blog.exception.NotFoundException;
import com.benjamin.blog.repository.PostRepository;
import com.benjamin.blog.repository.TagRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/tags/")
public class TagController {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagController(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @GetMapping(value = {"/", "/list"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("tags/list");
        List<Tag> tags = tagRepository.findAll();
        modelAndView.addObject("tags", tags);
        return modelAndView;
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView("tags/detail");
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null) {
            throw new NotFoundException("Tag", String.valueOf(id), String.valueOf(id));
        }
        modelAndView.addObject("tag", tag);

        List<Post> posts = postRepository.findAllByTags(tag);
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }
}
