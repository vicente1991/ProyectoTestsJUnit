package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Comment;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;



    @Test
    void findByPostId() {

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        Page<Comment> comment = commentRepository.findByPostId(1L, pageable);

        assertNotEquals(comment.getTotalElements(), 0);
    }
}