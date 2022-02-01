package com.sopromadze.blogapi.repository;


import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;


import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    static Comment comment;
    static Post post;

    @BeforeEach
    void initData(){

        post = new Post();
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        testEntityManager.persist(post);

        comment = new Comment();
        comment.setName("Comentario chulo");
        comment.setEmail("inma.dvgs@gmail.com");
        comment.setBody("Comentario muy chulo y descriptivo");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);

        testEntityManager.persist(comment);

    }


    @Test
    void findByPostId() {

        Page<Comment> comment = commentRepository.findCommentByPostId(post.getId(), any(Pageable.class));

        assertNotEquals(comment.getNumberOfElements(), 0);
    }
}