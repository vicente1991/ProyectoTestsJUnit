package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CommentTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;


    PagedResponse<Comment> commentList;
    Comment comment;
    Post post;
    User user;
    Role rolAdmin;
    Role rolUser;
    CommentRequest commentRequest;
    ApiResponse apiResponse;
    @BeforeEach
    void initTest() {
        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setName("Nombre del comentario");
        comment.setBody("Cuerpo del comentario");
        comment.setId(1L);
        comment.setPost(post);

        commentList = new PagedResponse(List.of(comment), 1, 1, 1, 1, true);


        user =  new User();
        user.setUsername("user");
        user.setId(1L);

        rolAdmin = new Role();
        rolAdmin.setName(RoleName.ROLE_ADMIN);

        rolUser = new Role();
        rolUser.setName(RoleName.ROLE_USER);

        commentRequest = new CommentRequest();
        commentRequest.setBody("Cuerpo del comentario");



        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setStatus(HttpStatus.OK);
        apiResponse.setMessage("You successfully deleted comment");



    }

    @Test
    void getAllComments_success() throws Exception {
        when(commentService.getAllComments(1L,1,1)).thenReturn(commentList);
        mockMvc.perform(get("/api/posts/{postId}/comments",1L)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(commentList)))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithUserDetails("user")
    @Test
    void addComment_success() throws Exception {
        List<Role> roles = Arrays.asList(rolUser);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(commentService.addComment(commentRequest,1L, userPrincipal)).thenReturn(comment);
        mockMvc.perform(post("/api/posts/{postId}/comments",1L)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    void addComment_thenReturn401() throws Exception {
        mockMvc.perform(post("/api/posts/{postId}/comments",1L)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @WithUserDetails("admin")
    @Test
    void addComment_thenReturn403() throws Exception {
        mockMvc.perform(post("/api/posts/{postId}/comments",1L)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());
    }


    @Test
    void getComment_success() throws Exception {
        when(commentService.getComment(1L,1L)).thenReturn(comment);
        mockMvc.perform(get("/api/posts/{postId}/comments/{id}",1L,1L)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }


    @WithUserDetails("user")
    @Test
    void updateComment_success() throws Exception {

        List<Role> roles = Arrays.asList(rolUser, rolAdmin);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(commentService.updateComment(1L,1L,commentRequest,userPrincipal)).thenReturn(comment);

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}",1L,1L)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void updateComment_thenReturn401() throws Exception {
        mockMvc.perform(put("/api/posts/{postId}/comments/{id}",1L,1L)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }


    @WithUserDetails("admin")
    @Test
    void deleteComment_success() throws Exception {

        List<Role> roles = Arrays.asList(rolUser, rolAdmin);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();


        when(commentService.deleteComment(1L,1L,userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}",1L,1L)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void deleteComment_thenReturn401() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}",1L,1L)
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

}
