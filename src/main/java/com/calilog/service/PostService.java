package com.calilog.service;

import com.calilog.domain.PostEditor;
import com.calilog.exception.PostNotFoundException;
import com.calilog.repository.PostRepository;
import com.calilog.request.PostCreate;
import com.calilog.request.PostEdit;
import com.calilog.request.PostSearch;
import com.calilog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    public Long write(PostCreate postCreate) {

        com.calilog.domain.Post post = com.calilog.domain.Post
                .builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);

        return post.getId();
    }

    public PostResponse getPost(Long id) {
        com.calilog.domain.Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public List<PostResponse> getPostList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::from)
                .toList();
    }

    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        com.calilog.domain.Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();

        // 이 방법은 타이블이나 컨텐트가 null로 넘어오면 데이터도 null로 저장되니 앞단에서 무조건 데이터를 받는 식으로 짜야한다.
        PostEditor postEditor = editorBuilder.title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
    }

    public void delete(Long id) {
        com.calilog.domain.Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        postRepository.deleteById(id);
    }
}
