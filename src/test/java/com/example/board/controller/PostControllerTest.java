package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void listPosts() throws Exception {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("첫 번째 게시글");
        post1.setAuthor("작성자1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("두 번째 게시글");
        post2.setAuthor("작성자2");

        when(postService.getAllPosts()).thenReturn(Arrays.asList(post1, post2));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/list"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/form"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void createPost() throws Exception {
        Post savedPost = new Post();
        savedPost.setId(1L);
        when(postService.createPost(any(Post.class))).thenReturn(savedPost);

        mockMvc.perform(post("/posts")
                .param("title", "새 게시글")
                .param("content", "내용")
                .param("author", "작성자"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void viewPost() throws Exception {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("게시글 제목");
        post.setContent("게시글 내용");
        post.setAuthor("작성자");

        when(postService.getPostById(anyLong())).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void showEditForm() throws Exception {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("수정할 게시글");
        post.setContent("내용");
        post.setAuthor("작성자");

        when(postService.getPostById(anyLong())).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/form"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void updatePost() throws Exception {
        Post updatedPost = new Post();
        updatedPost.setId(1L);
        when(postService.updatePost(anyLong(), any(Post.class))).thenReturn(updatedPost);

        mockMvc.perform(post("/posts/1")
                .param("title", "수정된 제목")
                .param("content", "수정된 내용")
                .param("author", "수정된 작성자"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void deletePost() throws Exception {
        mockMvc.perform(post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }
} 