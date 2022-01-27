package com.benjamin.blog.controller.mvc;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.entity.User;
import com.benjamin.blog.exception.NotFoundException;
import com.benjamin.blog.payload.requests.RegisterRequest;
import com.benjamin.blog.repository.PostRepository;
import com.benjamin.blog.repository.UserRepository;
import com.benjamin.blog.service.AuthService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Controller
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserController(AuthService authService, UserRepository userRepository, PostRepository postRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String register(RegisterRequest request) {
        authService.register(request);
        return "redirect:login";
    }

    @GetMapping("/users/")
    public ModelAndView authors() {
        ModelAndView modelAndView = new ModelAndView("users/list");
        List<User> users = userRepository.findAll();
        modelAndView.addObject("authors", users);
        return modelAndView;
    }

    @GetMapping("/users/detail/{id}")
    public ModelAndView detail(@PathVariable("id") long id) {
        User author = userRepository.findById(id).orElse(null);
        if (author == null) {
            throw new NotFoundException("Author", String.valueOf(id), String.valueOf(id));
        }
        List<Post> posts = postRepository.findAllByUser(author);
        ModelAndView modelAndView = new ModelAndView("users/detail");
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("author", author);
        return modelAndView;
    }

    @GetMapping("/profile")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ModelAndView profile() {
        ModelAndView modelAndView = new ModelAndView("users/profile");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User author = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElse(null);
        List<Post> posts = postRepository.findAllByUser(author);
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("author", author);
        return modelAndView;
    }
}
