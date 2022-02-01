package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {SpringSecurityTestWebConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PostControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostController postController;

    @MockBean
    private PostService postService;

    static PagedResponse<Post> postPagedResponse;
    static Post post;
    static Category category;
    static Tag tag;
    static PostResponse postResponse;
    static UserPrincipal userPrincipal;
    static PostRequest postRequest;
    static ApiResponse apiResponse;

    @BeforeEach
    void initData(){

        post = new Post();
        category = new Category("Mis posts");
        tag = new Tag("Álbumes");

        category.setId(5L);
        tag.setId(3L);

        post.setTitle("Mis álbumes más escuchados este mes");
        post.setBody("Me gustan mucho los siguientes álbumes: ...");
        post.setId(1L);
        post.setCategory(category);
        post.setTags(List.of(tag));

        postPagedResponse = new PagedResponse(List.of(post), 1, 1, 1, 1,true );


        userPrincipal = UserPrincipal.builder()
                .id(2L)
                .username("user")
                .authorities(List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())))
                .build();

        postRequest = new PostRequest();

        postRequest.setCategoryId(category.getId());
        postRequest.setTitle("Título chulo");
        postRequest.setBody("Descripción llamativa y original para rellenar un poco y que no falle el test");
        postRequest.setTags(List.of("Álbumes"));


        apiResponse = new ApiResponse();

    }


    @Test
    void getAllPosts_Success() throws Exception{

        when(postService.getAllPosts(1,1)).thenReturn(postPagedResponse);

        mockMvc.perform(get("/api/posts")
                                .param("page", "1")
                                .param("size", "1")
                                .contentType("application/json"))
                    .andExpect(jsonPath("$.content[0].id", is(1)))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(content().json(objectMapper.writeValueAsString(postPagedResponse)))
                    .andExpect(status().isOk()).andDo(print());

    }

    @Test
    void getPostsByCategory_Success() throws Exception{

        when(postService.getPostsByCategoryId(5L, 1, 1)).thenReturn(postPagedResponse);

        mockMvc.perform(get("/api/posts/category/{id}", 5L)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postPagedResponse)))
                .andExpect(status().isOk()).andDo(print());


    }

    @Test
    void getPostsByTag_Success() throws Exception{

        when(postService.getPostsByTagId(3L, 1, 1)).thenReturn(postPagedResponse);

        mockMvc.perform(get("/api/posts/category/{id}", 3L)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postPagedResponse)))
                .andExpect(status().isOk()).andDo(print());


    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void addPost_Success() throws Exception{

        postResponse = new PostResponse();

        postResponse.setTitle("Agregamos un nuevo post");
        postResponse.setBody("Creamos y guardamos un post en la API a través de este método.");
        postResponse.setCategory(category.getName());
        postResponse.setTags(List.of("Álbumes"));

        when(postService.addPost(postRequest, userPrincipal)).thenReturn(postResponse);

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    void getPost_Success() throws Exception{

        when(postService.getPost(1L)).thenReturn(post);

        mockMvc.perform(get("/api/posts/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    void updatePost() throws Exception{

        Post updateAlbum = new Post();
        updateAlbum.setTitle("Post actualizado");

        post.setTitle(updateAlbum.getTitle());

        when(postService.updatePost(1L, postRequest, userPrincipal)).thenReturn(post);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    void deletePost() throws Exception{

        when(postService.deletePost(1L, userPrincipal)).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/posts/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk()).andDo(print());


    }
}