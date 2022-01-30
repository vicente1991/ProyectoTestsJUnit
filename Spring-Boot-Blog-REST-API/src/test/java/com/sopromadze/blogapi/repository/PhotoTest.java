package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.utils.AppConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PhotoTest {
    @Autowired
    private PhotoRepository repository;


    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testRepoNotNull() {
        assertNotNull(repository);
    }

    @Test
    void test_findByAlbumId() {

        Album album = Album.builder()
                .title("Fotos")
                .build();

        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        testEntityManager.persist(album);

        Photo photo = new Photo();
        photo.setTitle("Photo1");
        photo.setThumbnailUrl("https//photos");
        photo.setUrl("asd");
        photo.setAlbum(album);
        photo.setCreatedAt(Instant.now());
        photo.setUpdatedAt(Instant.now());

        testEntityManager.persist(photo);


        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, AppConstants.CREATED_AT);

        assertNotEquals(0, repository.findByAlbumId(album.getId(), pageable).getTotalElements());

    }
}
