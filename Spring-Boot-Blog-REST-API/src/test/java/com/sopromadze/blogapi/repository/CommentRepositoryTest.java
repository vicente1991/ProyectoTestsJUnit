package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Comment;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByPostId() {

        Pageable pageable = PageRequest.of(1, 10, Sort.Direction.DESC, "createdAt");

        Page<Comment> comment = commentRepository.findByPostId(1L, pageable);

        assertEquals(1, comment);
    }
}