package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
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

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostTest {
    @Autowired
    private PostRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void test_findByCreatedBy() {
        Post post = new Post();
        post.setTitle("Titulo Post");
        post.setBody("Cuerpo del Post");
        post.setCreatedBy(10L);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        testEntityManager.persist(post);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        assertNotEquals(0,repository.findByCreatedBy(post.getCreatedBy(),pageable).getTotalElements());
    }


    @Test
    void test_findByCategory() {

        Category category = new Category();
        category.setName("Nombre de Categoria");
        category.setCreatedBy(10L);
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());
        testEntityManager.persist(category);

        Post post = new Post();
        post.setTitle("Titulo Post");
        post.setBody("Cuerpo del Post");
        post.setCreatedBy(10L);
        post.setCategory(category);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        testEntityManager.persist(post);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        assertNotEquals(0L, repository.findByCategoryId(category.getId(), pageable).getTotalElements());
    }

    @Test
    void test_findByTagsIn() {

        Tag tag = new Tag();
        tag.setName("Nombre del Tag");
        tag.setCreatedAt(Instant.now());
        tag.setUpdatedAt(Instant.now());

        testEntityManager.persist(tag);
        List<Tag> tagList = Arrays.asList(tag);


        Post post = new Post();
        post.setTitle("Titulo Post");
        post.setBody("Cuerpo del Post");
        post.setCreatedBy(10L);
        post.setTags(tagList);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        testEntityManager.persist(post);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        assertNotEquals(0L,repository.findByTagsIn(tagList,pageable).getTotalElements());
    }


    @Test
    void test_countByCreatedBy() {

        Role rolAdmin = new Role();
        rolAdmin.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rolAdmin);

        User user = new User();
        user.setUsername("Jucalox");
        user.setRoles(roles);
        user.setFirstName("Primer");
        user.setLastName("Last");
        user.setEmail("email@gmai.com");
        user.setPassword("1234");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);


        Post post = new Post();
        post.setTitle("Titulo Post");
        post.setBody("Cuerpo del Post");
        post.setCreatedBy(user.getId());
        post.setUser(user);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        testEntityManager.persist(post);

        assertNotEquals(0,repository.countByCreatedBy(user.getId()));
    }

}
