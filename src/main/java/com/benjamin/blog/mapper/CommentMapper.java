package com.benjamin.blog.mapper;

import com.benjamin.blog.entity.Comment;
import com.benjamin.blog.payload.CommentPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentPayload payload);
    CommentPayload toCommentPayload(Comment comment);
}
