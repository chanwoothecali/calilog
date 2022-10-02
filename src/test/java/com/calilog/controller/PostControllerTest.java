package com.calilog.controller;

import com.calilog.repository.PostRepository;
import com.calilog.request.PostCreate;
import com.calilog.request.PostEdit;
import com.calilog.request.PostSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 게시글 컨트롤러")
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("[POST] /posts 요청시 정상적인 response를 받는다.")
    public void whenPostMethodCallPosts_thenReturnData() throws Exception {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();
        String postJson = objectMapper.writeValueAsString(postCreate);

        // when & then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(postJson)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[POST] /posts 요청시 title값은 필수다.")
    public void whenPostsWithoutTitle_thenThrowException() throws Exception {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("")
                .content("내용")
                .build();
        String postJson = objectMapper.writeValueAsString(postCreate);

        // when & then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(postJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.errors.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("[POST] /posts 요청시 DB에 값이 저장된다.")
    public void givenPostRequestData_whenCallPosts_thenInsertDataInDB() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();
        String postJson = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(postJson)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(postRepository.count()).isEqualTo(1L);
        com.calilog.domain.Post post = postRepository.findAll().get(0);
        assertThat(post.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("[GET] 단 건 조회시 post 한 건이 조회된다.")
    public void whenSearchPost_thenReturnOnePost() throws Exception {
        // given
        com.calilog.domain.Post post = com.calilog.domain.Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        // when & then
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("foo"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[GET] 여러 건 조회시 post 여러 건이 조회된다.")
    public void whenSearchPosts_thenReturnPosts() throws Exception {
        // given
        com.calilog.domain.Post post1 = com.calilog.domain.Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post1);

        com.calilog.domain.Post post2 = com.calilog.domain.Post.builder()
                .title("foo2")
                .content("bar2")
                .build();
        postRepository.save(post2);

        // when & then
        mockMvc.perform(get("/posts?page=1&size=11")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id").value(post1.getId()))
                .andExpect(jsonPath("$[0].title").value("foo1"))
                .andExpect(jsonPath("$[1].title").value("foo2"))
                .andDo(print());
    }

    @Test
    @DisplayName("[GET] 페이지1 조회시 post 첫 페이지가 조회된다.")
    public void whenSearchPostsPageNumberOne_thenReturnPostFirstPage() throws Exception {
        // given
        List<com.calilog.domain.Post> requestPost = IntStream.range(0, 30)
                .mapToObj(i ->
                        com.calilog.domain.Post.builder()
                                .title("title" + i)
                                .content("content" + i)
                                .build()
                )
                .collect(Collectors.toList());
        postRepository.saveAll(requestPost);

        // when & then
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("title0"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 제목 수정")
    public void whenPatchTitleData_thenTitleChanged() throws Exception {
        // given
        com.calilog.domain.Post post = com.calilog.domain.Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        // when
        PostEdit postEdit = PostEdit.builder()
                .title("foo2")
                .content("bar1")
                .build();

        // then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    public void delete_post_by_id() throws Exception {
        // given
        com.calilog.domain.Post post = com.calilog.domain.Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        // when & then
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회시 오류 발생")
    public void whenSearchNotExistPost_thenThrowException() throws Exception {
        // when & then
        mockMvc.perform(get("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정시 오류 발생")
    public void whenUpdateNotExistPost_thenThrowException() throws Exception {
        // given
        PostEdit postEdit = PostEdit.builder()
                .title("이정후")
                .content("MVP")
                .build();

        // when & then
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제시 오류 발생")
    public void whenDeleteNotExistPost_thenThrowException() throws Exception {
        // when & then
        mockMvc.perform(delete("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 내용에 바보라는 단어는 들어갈 수 없다.")
    public void whenInvalidContentPost_thenThrowInvalidRequestException() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목")
                .content("바보자식")
                .build();
        String postJson = objectMapper.writeValueAsString(request);

        System.out.println("rebase test");

        // when & then
        mockMvc.perform(post("/posts/")
                        .contentType(APPLICATION_JSON)
                        .content(postJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}