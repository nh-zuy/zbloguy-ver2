package com.benjamin.blog.payload.responses;

import com.benjamin.blog.payload.PostPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private List<PostPayload> content;
    private int pageNo;
    private int pageSize;
    private long totalPosts;
    private int totalPages;
    private boolean last;
}
