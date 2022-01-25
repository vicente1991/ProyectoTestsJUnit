package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AlbumServiceImplTest {

    @MockBean
    AlbumRepository albumRepository;

    @Test
    void isAlbumListEmpty() {
        List<Album> lista = albumRepository.findAll();

        assertEquals(lista.size(), 0);
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