package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    void test_findAll() {

        Album album = new Album();
        album.setTitle("Título");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        testEntityManager.persist(album);

        assertNotEquals(0, albumRepository.findAll().size());

    }

    @Test
    void findCreated_Succes(){
        Album a= new Album();
        a.setTitle("album mejor que el anterior");
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());

        testEntityManager.persist(a);

        Pageable pageable= (Pageable) PageRequest.of(1,5);

        List<Album> album= Arrays.asList(a);

        User u= new User();
        u.setUsername("Vicent");
        u.setEmail("nuevoemail@mail.com");
        u.setAlbums(album);
        u.setCreatedAt(Instant.now());

        assertNotEquals(0,albumRepository.findByCreatedBy(u.getId(),pageable).getTotalElements());

    }


}
