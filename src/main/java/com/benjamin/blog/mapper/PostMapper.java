package com.benjamin.blog.mapper;

import com.benjamin.blog.entity.Post;
import com.benjamin.blog.payload.PostPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toPost(PostPayload payload);
    PostPayload toPostPayload(Post post);
}
