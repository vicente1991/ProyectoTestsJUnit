package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class AlbumTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private PhotoService photoService;

    PagedResponse<AlbumResponse> albumList;
    Album albumResult;
    UserPrincipal userPrincipal;
    User user;
    List<Photo> photoList;
    AlbumResponse albumResponse;
    AlbumRequest albumRequest;
    ApiResponse apiResponse;
    PagedResponse<PhotoResponse> photoResponsePagedResponse;
    @BeforeEach
    void initTest() {
        albumResult = new Album();
        albumResult.setId(1L);
        albumResult.setTitle("Album Controller");


        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        user = new User();
        user.setId(3L);
        user.setRoles(roles);

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        photoList=new ArrayList<>();

        albumResponse = new AlbumResponse();
        albumResponse.setId(20L);
        albumResponse.setTitle("AlbumResponse");
        albumResponse.setUser(user);
        albumResponse.setPhoto(photoList);



        albumList = new PagedResponse(List.of(albumResult), 1, 1, 1, 1, true);
        albumRequest = AlbumRequest.builder().id(10L).user(user).title("Almbun nuevo y bonito").photo(photoList).build();

        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("You successfully deleted category");


        Page<Photo> photos= Page.empty();

        List<PhotoResponse> photoResponses = new ArrayList<>(photos.getContent().size());
        for (Photo photo : photos.getContent()) {
            photoResponses.add(new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                    photo.getThumbnailUrl(), photo.getAlbum().getId()));
        }


        photoResponsePagedResponse = new PagedResponse<>(photoResponses, photos.getNumber(), photos.getSize(), photos.getTotalElements(),
                photos.getTotalPages(), photos.isLast());

    }



    @Test
    void getAllAlbums_success() throws Exception {
        when(albumService.getAllAlbums(1, 1)).thenReturn(albumList);
        mockMvc.perform(get("/api/albums")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(albumList)))
                .andExpect(status().isOk()).andDo(print());
    }



    @WithMockUser(authorities = {"ROLE_USER"})
    @Test
    void addAlbum_success() throws Exception {
        when(albumService.addAlbum(albumRequest, userPrincipal)).thenReturn(albumResult);
        mockMvc.perform(post("/api/albums")
                        .content(objectMapper.writeValueAsString(albumRequest))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void addAlbum_thenReturn401() throws Exception {

        when(albumService.addAlbum(albumRequest, userPrincipal)).thenReturn(albumResult);
        mockMvc.perform(post("/api/albums")
                        .content(objectMapper.writeValueAsString(albumRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }


    @Test
    void getAlbum_success() throws Exception {

        when(albumService.getAlbum(1L)).thenReturn(albumResult);
        mockMvc.perform(get("/api/albums/{id}",1L)
                .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithMockUser(authorities = {"ROLE_USER","ROLE_ADMIN"})
    @Test
    void updateAlbum_success() throws Exception {

        when(albumService.updateAlbum(1L,albumRequest,userPrincipal)).thenReturn(albumResponse);

        mockMvc.perform(put("/api/albums/{id}",1L)
                        .content(objectMapper.writeValueAsString(albumRequest))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void updateAlbum_thenReturn401() throws Exception {

        when(albumService.updateAlbum(1L,albumRequest,userPrincipal)).thenReturn(albumResponse);

        mockMvc.perform(put("/api/albums/{id}",1L)
                        .content(objectMapper.writeValueAsString(albumRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }



    @WithMockUser(authorities = {"ROLE_USER","ROLE_ADMIN"})
    @Test
    void deleteAlbum_success() throws Exception {
        when(albumService.deleteAlbum(1L,userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/albums/{id}",1L)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }


    @Test
    void deleteAlbum_thenReturn401() throws Exception {
        when(albumService.deleteAlbum(1L,userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/albums/{id}",1L)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    void getAllPhotosByAlbum_success() throws Exception {
        when(photoService.getAllPhotosByAlbum(1L,1,1)).thenReturn(photoResponsePagedResponse);
        mockMvc.perform(get("/api/albums/{id}/photos",1L)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }




}
