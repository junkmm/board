package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setTitle("테스트 제목");
        testPost.setContent("테스트 내용");
        testPost.setAuthor("테스트 작성자");
    }

    @Test
    void createPost() {
        // when
        Post savedPost = postService.createPost(testPost);

        // then
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("테스트 제목");
        assertThat(savedPost.getContent()).isEqualTo("테스트 내용");
        assertThat(savedPost.getAuthor()).isEqualTo("테스트 작성자");
        assertThat(savedPost.getCreatedAt()).isNotNull();
    }

    @Test
    void getAllPosts() {
        // given
        postService.createPost(testPost);
        Post anotherPost = new Post();
        anotherPost.setTitle("다른 제목");
        anotherPost.setContent("다른 내용");
        anotherPost.setAuthor("다른 작성자");
        postService.createPost(anotherPost);

        // when
        List<Post> posts = postService.getAllPosts();

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts).extracting("title").containsExactlyInAnyOrder("테스트 제목", "다른 제목");
    }

    @Test
    void getPostById() {
        // given
        Post savedPost = postService.createPost(testPost);

        // when
        Optional<Post> foundPost = postService.getPostById(savedPost.getId());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    void updatePost() {
        // given
        Post savedPost = postService.createPost(testPost);
        Post updatePost = new Post();
        updatePost.setTitle("수정된 제목");
        updatePost.setContent("수정된 내용");
        updatePost.setAuthor("수정된 작성자");

        // when
        Post updatedPost = postService.updatePost(savedPost.getId(), updatePost);

        // then
        assertThat(updatedPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정된 내용");
        assertThat(updatedPost.getAuthor()).isEqualTo("수정된 작성자");
    }

    @Test
    void deletePost() {
        // given
        Post savedPost = postService.createPost(testPost);

        // when
        postService.deletePost(savedPost.getId());

        // then
        assertThat(postService.getPostById(savedPost.getId())).isEmpty();
    }

    @Test
    void updatePost_NotFound() {
        // given
        Post updatePost = new Post();
        updatePost.setTitle("수정된 제목");

        // when & then
        assertThrows(RuntimeException.class, () -> {
            postService.updatePost(999L, updatePost);
        });
    }

    @Test
    void deletePost_NotFound() {
        // when & then
        assertThrows(RuntimeException.class, () -> {
            postService.deletePost(999L);
        });
    }
}