package com.benjamin.blog.controller.mvc;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.entity.Tag;
import com.benjamin.blog.entity.User;
import com.benjamin.blog.exception.NotFoundException;
import com.benjamin.blog.payload.requests.CreatePostRequest;
import com.benjamin.blog.payload.requests.UpdatePostRequest;
import com.benjamin.blog.repository.PostRepository;
import com.benjamin.blog.repository.TagRepository;
import com.benjamin.blog.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts/")
public class PostMVCController {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public PostMVCController(PostRepository postRepository, TagRepository tagRepository,
                             UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("posts/list");
        List<Post> posts = postRepository.findAll(Sort.by("id").descending());
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView("posts/detail");
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            throw new NotFoundException("Post", "id", String.valueOf(id));
        }
        modelAndView.addObject("post", post);

        List<Post> relativePosts = postRepository.findAllByTags(post.getTags().get(0)).stream()
                .filter(relativePost -> !relativePost.getId().equals(post.getId()))
                .collect(Collectors.toList());

        modelAndView.addObject("relativePosts", relativePosts);

        return modelAndView;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ModelAndView addForm() {
        ModelAndView modelAndView = new ModelAndView("posts/add");
        List<Tag> tags = tagRepository.findAll();
        modelAndView.addObject("tags", tags);
        return modelAndView;
    }

    @PostMapping(value = "/add", consumes = "application/x-www-form-urlencoded")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String add(CreatePostRequest request) {
        String tagName = request.getTag();
        Tag tag = tagRepository.findByName(tagName).orElse(null);
        if (tag == null) {
            Tag newTag = Tag.builder().name(tagName).build();
            tag = tagRepository.save(newTag);
        }
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        User author = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElse(null);
        Post newPost = Post.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .tags(tags)
                .user(author)
                .status(isAdmin ? 1 : 0)
                .build();
        postRepository.save(newPost);

        return "redirect:/posts/manage";
    }

    @GetMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ModelAndView updateForm(@PathVariable("id") long id) throws Exception {
        Post post = postRepository.findById(id).orElse(null);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        if (!isAdmin) {
            User author = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                    .orElse(null);
            if (!Objects.equals(author.getId(), post.getUser().getId())) {
                throw new Exception("403 Forbidden");
            }
        }
        ModelAndView modelAndView = new ModelAndView("posts/update");
        if (post == null) {
            return modelAndView;
        }
        modelAndView.addObject("post", post);

        List<Tag> tags = tagRepository.findAll();
        modelAndView.addObject("tags", tags);
        return modelAndView;
    }

    @PostMapping(value = "/update/{id}", consumes = "application/x-www-form-urlencoded")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public String update(@PathVariable("id") long id, UpdatePostRequest request) throws Exception {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post", "id", String.valueOf(id)));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        if (!isAdmin) {
            User author = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                    .orElse(null);
            if (!Objects.equals(author.getId(), post.getUser().getId())) {
                throw new Exception("403 Forbidden");
            }
        }

        String tagName = request.getTag();
        Tag tag = tagRepository.findByName(tagName).orElse(null);
        if (tag == null) {
            Tag newTag = Tag.builder().name(tagName).build();
            tag = tagRepository.save(newTag);
        }
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setDescription(request.getDescription());
        post.setTags(tags);
        post.setStatus(request.getStatus());

        postRepository.save(post);

        return "redirect:/posts/update/" + id;
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(@PathVariable("id") long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            throw new NotFoundException("Post", String.valueOf(id), String.valueOf(id));
        }
        postRepository.deleteById(id);
        return "redirect:/posts/manage";
    }

    @GetMapping("/manage")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ModelAndView manage() {
        ModelAndView modelAndView = new ModelAndView("posts/manage");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));

        List<Post> posts;
        if (isAdmin) {
            posts = postRepository.findAll();
        } else {
            User author = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                    .orElse(null);
            posts = postRepository.findAllByUser(author);
        }

        modelAndView.addObject("posts", posts);

        return modelAndView;
    }
}
