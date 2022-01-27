package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class PhotoTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    PagedResponse<AlbumResponse> albumList;
    Album albumResult;
    UserPrincipal userPrincipal;
    User user;
    List<Photo> photoList;
    AlbumResponse albumResponse;
    AlbumRequest albumRequest;
    ApiResponse apiResponse;

    @BeforeEach
    void initTest() {
        Album album = Album.builder()
                .id(1L)
                .title("Album de Controller")
                .build();


        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User userPrueba = new User();
        userPrueba.setId(3L);
        userPrueba.setRoles(roles);

        UserPrincipal Principal = UserPrincipal.builder()
                .id(userPrueba.getId())
                .authorities(userPrueba.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        photoList = new ArrayList<>();


        AlbumResponse albumResponsePrueba = new AlbumResponse();
        albumResponsePrueba.setId(20L);
        albumResponsePrueba.setTitle("AlbumResponse");
        albumResponsePrueba.setUser(userPrueba);
        albumResponsePrueba.setPhoto(photoList);
        albumResponse = albumResponsePrueba;

        userPrincipal = Principal;
        user = userPrueba;

        albumResult = album;
        albumList = new PagedResponse(List.of(albumResult), 1, 1, 1, 1, true);
        albumRequest = AlbumRequest.builder().id(10L).user(user).title("Almbun nuevo y bonito").photo(photoList).build();

        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("You successfully deleted category");

    }
}
