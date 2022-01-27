package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.service.AlbumService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {SpringSecurityTestWebConfig.class},properties = {"spring.main.allow-bean-definition-overriding=true"})
public class AlbumTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    PagedResponse<AlbumResponse> albumList;
    Album albumResult;
    @BeforeEach
    void initTest() {
        Album album= Album.builder()
                .id(1L)
                .title("Album de Controller")
                .build();


        albumResult = album;


    }



    //@WithUserDetails("admin")
    @Test
    @DisplayName("GET /allAlbum/ sin parámetros de filtrado")
    void getAllAlbums_success () throws Exception{

        when(albumService.getAllAlbums(1, 1)).thenReturn(albumList);




        MvcResult mvcResult = mockMvc.perform(post("/api/albums")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher)jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(albumList)))
                .andReturn();
    }
}
