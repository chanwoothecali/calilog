package com.calilog.repository;

import com.calilog.domain.Post;
import com.calilog.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
