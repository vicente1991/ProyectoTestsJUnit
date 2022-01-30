package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TodoTest {

    @Autowired
    private TodoRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void test_findByCreatedBy() {

        Todo todo = new Todo();
        todo.setCreatedBy(10L);
        todo.setTitle("Titulo de todo");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setCompleted(true);
        testEntityManager.persist(todo);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        assertNotEquals(0,repository.findByCreatedBy(todo.getCreatedBy(),pageable).getTotalElements());
    }
}
