package com.calilog.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class PostSearch {

    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 10;

    public PostSearch(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public String toString() {
        return "PostSearch{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
