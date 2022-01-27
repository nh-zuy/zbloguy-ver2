package com.benjamin.blog.entity;

import com.benjamin.blog.payload.CommentPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "body", nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void belongToPost(Post post) {
        this.post = post;
    }

    public boolean isBelongToPost(Post post) {
        return this.post.getId().equals(post.getId());
    }

    public void update(CommentPayload payload) {
        this.name = payload.getName();
        this.email = payload.getEmail();
        this.body = payload.getBody();
    }
}
