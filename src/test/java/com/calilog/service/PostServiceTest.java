package com.calilog.service;

import com.calilog.domain.Post;
import com.calilog.exception.PostNotFoundException;
import com.calilog.repository.PostRepository;
import com.calilog.request.PostCreate;
import com.calilog.request.PostEdit;
import com.calilog.request.PostSearch;
import com.calilog.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void beforeTest() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    public void givenPostData_whenWrite_thenInsertDB() throws Exception {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        // when
        postService.write(postCreate);

        // then
        assertThat(postRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("글 단 건 조회")
    public void givenPostId_thenGetPost() throws Exception {
        // given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        // when
        PostResponse postResponse = postService.getPost(requestPost.getId());

        // then
        assertThat(postResponse).isNotNull();
        assertThat(postResponse.getTitle()).isEqualTo("foo");
        assertThat(postResponse.getContent()).isEqualTo("bar");
    }

    @Test
    @DisplayName("글 1페이지 조회")
    public void whenSearchPosts_thenReturnFirstPostPage() throws Exception {
        // given
        List<Post> requestPost = IntStream.range(0, 30)
                .mapToObj(i ->
                     Post.builder()
                            .title("title" + i)
                            .content("content" + i)
                            .build()
                )
                .collect(Collectors.toList());
        postRepository.saveAll(requestPost);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        // when
        List<PostResponse> postList = postService.getPostList(postSearch);

        // then
        assertThat(postList.size()).isEqualTo(10L);
        assertThat(postList.get(0).getTitle()).isEqualTo("title0");
    }

    @Test
    @DisplayName("글 수정")
    public void whenChangedPostData_thenUpdatePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("박병호")
                .content("홈런왕")
                .build();
        postRepository.save(post);

        // when
        PostEdit postEdit = PostEdit.builder()
                .title("이정후")
                .content("타격왕")
                .build();
        postService.edit(post.getId(), postEdit);

        // then
        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost.getTitle()).isEqualTo(postEdit.getTitle());
        assertThat(findPost.getContent()).isEqualTo(postEdit.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    public void delete_post() throws Exception {
        // given
        Post post = Post.builder()
                .title("박병호")
                .content("홈런왕")
                .build();
        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("글 조회시 존재하지 않는 글이면 오류 발생")
    public void whenSearchNotExistPost_thenThrowException() throws Exception {
        // given
        Post post = Post.builder()
                .title("박병호")
                .content("홈런왕")
                .build();
        postRepository.save(post);

        // when & then
        assertThrows(PostNotFoundException.class, () -> postService.getPost(post.getId() + 1L));
    }

    @Test
    @DisplayName("글 수정시 존재하지 않는 글이면 오류 발생")
    public void whenModifyNotExistPost_thenThrowException() throws Exception {
        // given
        Post post = Post.builder()
                .title("박병호")
                .content("홈런왕")
                .build();
        postRepository.save(post);

        // when & then
        assertThrows(PostNotFoundException.class, () -> postService.edit(post.getId() + 1L, new PostEdit()));
    }

    @Test
    @DisplayName("글 삭제시 존재하지 않는 글이면 오류 발생")
    public void whenDeleteNotExistPost_thenThrowException() throws Exception {
        // given
        Post post = Post.builder()
                .title("박병호")
                .content("홈런왕")
                .build();
        postRepository.save(post);

        // when & then
        assertThrows(PostNotFoundException.class, () -> postService.delete(post.getId() + 1L));
    }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다")
    @Test
    void whenCountingArticles_thenReturnsArticleCount() {
        // Given
        Post post = Post.builder()
                .title("박병호")
                .content("홈런왕")
                .build();
        postRepository.save(post);
        long expected = 1L;

        // When
        Long postsCount = postService.getPostsCount();

        // Then
        assertThat(postsCount).isEqualTo(expected);
    }
}