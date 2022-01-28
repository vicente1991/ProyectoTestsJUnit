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
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    @MockBean
    private PhotoService photoService;


    private PhotoRequest photoRequest;
    private Role rol;
    private Photo photo;
    private List<Photo> photoList;
    private User user;
    private UserPrincipal userPrincipal;
    private Album album;

    @BeforeEach
    void initTest() {

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        List<Role> roles = Arrays.asList(rol);

        photo= new Photo();
        photo.setId(2L);

        user = new User();
        user.setId(3L);
        user.setRoles(roles);

        photoList= new ArrayList<>();

        userPrincipal= new UserPrincipal(1L,"Vicente","bla bla","Vicent","vicente@mail.com","123456",user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()));

        album = new Album();
        album.setTitle("Nuevo");
        album.setId(1L);
        album.setUpdatedAt(Instant.now());
        album.setCreatedAt(Instant.now());


        photoRequest= new PhotoRequest();
        photoRequest.setTitle("Nuevo titulo");
        photoRequest.setAlbumId(1L);
        photoRequest.setUrl("blablablakbndkjbj");
        photoRequest.setThumbnailUrl("blaoniuhihoo88johj8");

    }

    @Test
    void whenGetAllPhotos_Success() throws Exception {
        mockMvc.perform(get("/api/photos")
                .contentType("application/json")
                        .param("page","1")
                        .param("size","10"))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetPhoto_Success() throws Exception {

        mockMvc.perform(get("/api/photos/{id}",1)
                .contentType("application/json"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_USER","ROLE_ADMIN"})
    void whenPostPhoto_Success() throws Exception {

        mockMvc.perform(post("/api/photos")
                .contentType("application/json")
                        .content(objectMapper.writeValueAsString(photoRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN","ROLE_ADMIN"})
    void whenPutPhoto_Succes() throws Exception{
        mockMvc.perform(put("/api/photos/{id}",1)
                .contentType("application/json")
                        .content(objectMapper.writeValueAsString(photoRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN","ROLE_ADMIN"})
    void whenDeletePhoto_Succes() throws Exception {
        mockMvc.perform(delete("/api/photos/{id}",1)
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

}
