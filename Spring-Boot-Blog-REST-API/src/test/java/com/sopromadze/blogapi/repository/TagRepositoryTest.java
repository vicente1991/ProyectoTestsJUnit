package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    void whenFindByName_Success(){

        Post p= new Post();
        p.setTitle("Muchos posts");

        List<Post> postList= Arrays.asList(p);

        Tag t = new Tag();
        t.setName("Nuevo Tag");
        t.setCreatedAt(Instant.now());
        t.setUpdatedAt(Instant.now());
        t.setCreatedBy(2L);
        t.setUpdatedBy(2L);
        t.setPosts(postList);

        testEntityManager.persist(t);

        assertEquals(t.getName(),tagRepository.findByName(t.getName()).getName());

    }
}
