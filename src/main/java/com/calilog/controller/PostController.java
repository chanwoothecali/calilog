package com.calilog.controller;

import com.calilog.request.PostCreate;
import com.calilog.request.PostEdit;
import com.calilog.request.PostSearch;
import com.calilog.response.PostResponse;
import com.calilog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public List<PostResponse> posts(@PageableDefault PostSearch postSearch) {
        return postService.getPostList(postSearch);
    }

    @PostMapping("/posts")
    public Map<String, Long> postWrite(@RequestBody @Valid PostCreate request) {
        request.validate();
        Long postId = postService.write(request);
        return Map.of("postId", postId);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse post(@PathVariable(name = "postId") Long id) {
        PostResponse postResponse = postService.getPost(id);

        return postResponse;
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable(name = "postId") Long id, @RequestBody @Valid PostEdit request) {
        postService.edit(id, request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable(name = "postId") Long id) {
        postService.delete(id);
    }
}
