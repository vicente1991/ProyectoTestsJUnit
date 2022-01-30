package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.sopromadze.blogapi.utils.AppConstants.ID;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

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
    static Page<Album> albumPage;
    static Pageable pageable;
    static PagedResponse emptyPagedResponse;

    @BeforeEach
    void initData () {

        albumPage = Page.empty();

        emptyPagedResponse = new PagedResponse(Collections.emptyList(), albumPage.getNumber(), albumPage.getSize(), albumPage.getTotalElements(),
                albumPage.getTotalPages(), albumPage.isLast());

        pageable = PageRequest.of(1, 1, Sort.Direction.DESC, CREATED_AT);

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

        album = new Album();
        album.setId(1L);
        album.setTitle("Love Yourself : Tear");
        album.setUser(user);


        album2 =  new Album();
        album.setId(5L);
        album.setTitle("Love Yourself : Answer");
        album.setUser(user2);

        albumRequest = AlbumRequest.builder().user(user).build();
        albumRequest2 = AlbumRequest.builder().user(user2).build();


        listaAlbumes = new ArrayList<>();
        listaAlbumes.add(album);
        user2.setAlbums(listaAlbumes);

        blogapiException = new BlogapiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO_MAKE_THIS_OPERATION);

    }

    @Test
    void isAlbumListEmpty() {

        when(albumRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        assertThat(emptyPagedResponse.getSize() == albumRepository.findAll().size());


    }

    @Test
    void getAllAlbums(){

        Page<Album> albums2 = new PageImpl<>(Arrays.asList(album));

        when(albumRepository.findAll(pageable)).thenReturn(albums2);

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(9L);
        albumResponse.setTitle("Título original");

        List<AlbumResponse> albumResponses = Arrays.asList(albumResponse);

        AlbumResponse [] arrayResponse = {albumResponse};

        when(modelMapper.map(albums2.getContent(), AlbumResponse[].class)).thenReturn(arrayResponse);

        PagedResponse pagedResponse = new PagedResponse(albumResponses, albums2.getNumber(), albums2.getSize(), albums2.getTotalElements(), albums2.getTotalPages(),
                albums2.isLast());

        assertThat(albumService.getAllAlbums(1, 1).equals(pagedResponse));
    }


    @Test
    void addsAlbum() {
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        assertThat(albumRepository.findById(album.getId()).isPresent());
    }


    @Test
    void getAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
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

        when(albumRepository.findById(any(Long.class))).thenReturn(Optional.of(album));

        AlbumRequest nuevoAlbum = AlbumRequest.builder().title("Love Yourself 轉 'Tear'").build();
        album.setTitle(nuevoAlbum.getTitle());

        Album albumActualizado = album;

        when(albumRepository.save(any(Album.class))).thenReturn(albumActualizado);

        when(userRepository.getUser(userPrincipal.create(user))).thenReturn(user);

        AlbumResponse albumResponse = new AlbumResponse();

        when(modelMapper.map(any(), any())).thenReturn(albumResponse);

        assertThat(albumService.updateAlbum(albumActualizado.getId(), albumRequest, userPrincipal.create(user)));


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
        assertEquals(albumService.deleteAlbum(any(Long.class), userPrincipal.create(user3)), blogapiException);
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