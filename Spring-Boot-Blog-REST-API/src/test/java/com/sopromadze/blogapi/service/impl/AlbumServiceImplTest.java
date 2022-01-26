package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(SpringExtension.class)
class AlbumServiceImplTest {

    @Mock
    AlbumRepository albumRepository;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRepository userRepository;

    @Test
    void isAlbumListEmpty() {
        List<Album> lista = albumRepository.findAll();

        assertEquals(lista.size(), 0);
    }

    @Test
    void isAlbumListFull() {
        List<Album> lista = albumRepository.findAll();

        assertNotEquals(lista.size(), 0);
    }


    @Test
    void addsAlbum() {


        UserPrincipal user = UserPrincipal
                                .builder()
                                .id(2L)
                                .email("inma.dvgs@gmail.com")
                                .username("inmadv")
                                .build();


        User usuarioNormal = userRepository.getUser(user);

        Album album = Album.builder()
                        .title("Taylor Swift")
                        .id(1L)
                        .user(usuarioNormal)
                        .build();

        AlbumRequest albumRequest = AlbumRequest.builder().user(usuarioNormal).build();

        lenient().when(albumRepository.save(any(Album.class))).thenReturn(album);

        Album result = albumService.addAlbum(albumRequest, user);

        assertThat(albumRepository.findById(album.getId()).isPresent());
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