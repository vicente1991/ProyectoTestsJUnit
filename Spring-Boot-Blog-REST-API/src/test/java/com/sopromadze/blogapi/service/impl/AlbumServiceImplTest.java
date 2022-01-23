package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.repository.AlbumRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AlbumServiceImplTest {

    @MockBean
    AlbumRepository albumRepository;

    @Test
    void whenAlbumListZero_thenReturn() {

    }

    @Test
    void addAlbum() {
    }

    @Test
    void getAlbum() {
    }

    @Test
    void updateAlbum() {
    }

    @Test
    void deleteAlbum() {
    }

    @Test
    void getUserAlbums() {
    }
}