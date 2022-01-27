package com.benjamin.blog.repository;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.entity.Tag;
import com.benjamin.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
    List<Post> findAllByTags(Tag tag);
}
