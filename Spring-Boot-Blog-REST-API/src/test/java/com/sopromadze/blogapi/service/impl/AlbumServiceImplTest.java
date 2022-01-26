package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sopromadze.blogapi.utils.AppConstants.ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceImplTest {

    @Mock
    AlbumRepository albumRepository;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRepository userRepository;

    static Album album;
    static User user;
    static UserPrincipal userPrincipal;
    static AlbumRequest albumRequest;
    static final String CREATED_AT = "createdAt";
    static final String ALBUM_STR = "Album";
    static List<Role> roles;

    @BeforeEach
    void initData () {

        user = new User ("Inmaculada", "Domínguez", "inmadv", "inma.dvgs@gmail.com", "12345");

        user.setId(3L);
        roles = new ArrayList<Role>();

        roles.add(new Role(RoleName.ROLE_ADMIN));

        user.setRoles(roles);

        album = Album.builder()
                .id(1L)
                .title("Love Yourself : Tear")
                .user(user)
                .build();

        albumRequest = AlbumRequest.builder().user(user).build();
    }

    @Test
    void isAlbumListEmpty() {
        List<Album> lista = albumRepository.findAll();

        assertEquals(lista.size(), 0);
    }


    @Test
    void addsAlbum() {

        when(albumRepository.save(any(Album.class))).thenReturn(album);

        assertThat(albumRepository.findById(album.getId()).isPresent());
    }


    @Test
    void getAlbum() {
        when(albumRepository.findById(any(Long.class))).thenReturn(Optional.of(album));
        assertEquals(album, albumService.getAlbum(1L));
    }

    @Test
    void doesNotGetAlbum(){
        when(albumRepository.findById(44L)).thenThrow(new ResourceNotFoundException(ALBUM_STR, ID, 44L));

    }

    @Test
    void getAlbumUser() {
        assertEquals(album.getUser().getId(), user.getId());
    }

    @Test
    void userIsAdmin(){
        assertThat(user.getRoles().contains(RoleName.ROLE_ADMIN));
    }

    @Test
    void updatesAlbum(){
        Album nuevoAlbum = Album.builder().title("Love Yourself 轉 'Tear'").build();
        album.setTitle(nuevoAlbum.getTitle());

        userPrincipal.create(user);

        AlbumResponse albumResponse = new AlbumResponse();

        when(albumRepository.save(any(Album.class))).thenReturn(album);

        modelMapper.map(album, albumResponse);

        when(albumService.updateAlbum(album.getId(), albumRequest, userPrincipal.create(user))).thenReturn(albumResponse);

        assertThat(albumResponse);


    }

    @Test
    void deleteAlbum() {
    }

    @Test
    void getUserAlbums() {
    }
}