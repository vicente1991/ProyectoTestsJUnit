package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
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



    static Album album, album2;
    static User user, user2, user3;
    static UserPrincipal userPrincipal;
    static AlbumRequest albumRequest, albumRequest2;
    static final String CREATED_AT = "createdAt";
    static final String ALBUM_STR = "Album";
    static final String YOU_DON_T_HAVE_PERMISSION_TO_MAKE_THIS_OPERATION = "You don't have permission to make this operation";
    static BlogapiException blogapiException;
    static List<Role> roles, roles2;
    static List<Album> listaAlbumes;

    @BeforeEach
    void initData () {

        user = new User ("Inmaculada", "Domínguez", "inmadv", "inma.dvgs@gmail.com", "12345");
        user2 = new User ("Javier", "Domínguez", "javidv", "javierdominguez2006@gmail.com", "54321");
        user3 = new User ("Auxiliadora", "Vargas", "auxivm", "auxivargasmedina1969@gmail.com", "54321");

        user.setId(3L);
        user2.setId(4L);
        user3.setId(6L);

        roles = new ArrayList<Role>();
        roles.add(new Role(RoleName.ROLE_ADMIN));

        roles2 = new ArrayList<Role>();
        roles2.add(new Role(RoleName.ROLE_USER));

        user.setRoles(roles);
        user2.setRoles(roles2);
        user3.setRoles(roles2);

        album = Album.builder()
                .id(1L)
                .title("Love Yourself : Tear")
                .user(user)
                .build();

        album2 = Album.builder()
                .id(5L)
                .title("Love Yourself : Answer")
                .user(user2)
                .build();


        albumRequest = AlbumRequest.builder().user(user).build();
        albumRequest2 = AlbumRequest.builder().user(user2).build();


        listaAlbumes = new ArrayList<>();
        listaAlbumes.add(album);
        user2.setAlbums(listaAlbumes);

        blogapiException = new BlogapiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO_MAKE_THIS_OPERATION);

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
        assertNotNull(albumService.getAlbum(1L));
    }

    @Test
    void doesNotGetAlbum(){
        when(albumRepository.findById(44L)).thenThrow(new ResourceNotFoundException(ALBUM_STR, ID, 44L));
        assertThat(new ResourceNotFoundException(ALBUM_STR, ID, 44L));
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
    void notAdmin_updateAlbum(){
        assertThat(albumService.updateAlbum(album2.getId(), albumRequest2, userPrincipal.create(user2)).equals(blogapiException));
    }

    @Test
    void deletesAlbum() {
        ApiResponse result = new ApiResponse(Boolean.TRUE, "You successfully deleted album");
        assertEquals(result, albumService.deleteAlbum(1L, userPrincipal.create(user)));

    }

    @Test
    void unauthorizedUser_deleteAlbum(){
        assertEquals(albumService.deleteAlbum(1L, userPrincipal.create(user3)), blogapiException);
    }

    @Test
    void getUserAlbums(){

        List<Album> listaAlbumes = new ArrayList<>();
        listaAlbumes.add(album);

        Page<Album> pageable = new PageImpl<Album>(listaAlbumes);

        when(userRepository.getUserByName(any(String.class))).thenReturn(user);
        when(albumRepository.findByCreatedBy(any(Long.class), any(Pageable.class))).thenReturn(pageable);

        assertEquals(1, albumService.getUserAlbums(user.getUsername(), 1, 1).getSize());

    }



}