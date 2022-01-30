package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TagService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
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
public class TagTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    Tag tag;
    PagedResponse<Tag> tagList;
    User user;
    Role rolAdmin;
    Role rolUser;
    Tag tagRequest;


    @BeforeEach
    void initTest() {

        tag = new Tag();
        tag.setName("Nombre Tag");
        tag.setId(1L);

        tagList = new PagedResponse(List.of(tag), 1, 1, 1, 1, true);

        user =  new User();
        user.setUsername("user");
        user.setId(1L);

        rolAdmin = new Role();
        rolAdmin.setName(RoleName.ROLE_ADMIN);

        rolUser = new Role();
        rolUser.setName(RoleName.ROLE_USER);

        tagRequest = new Tag();
        tagRequest.setName("Tag cambiada");
        tagRequest.setId(1L);
    }

    @Test
    void getAllTags_success() throws Exception {
        when(tagService.getAllTags(1,1)).thenReturn(tagList);

        mockMvc.perform(get("/api/tags")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(tagList)))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithUserDetails("user")
    @Test
    void addTag_success() throws Exception {
        List<Role> roles = Arrays.asList(rolUser);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(tagService.addTag(tag, userPrincipal)).thenReturn(tag);
        mockMvc.perform(post("/api/tags")
                        .content(objectMapper.writeValueAsString(tag))
                        .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    void addTag_thenReturn401() throws Exception {

        mockMvc.perform(post("/api/tags")
                        .content(objectMapper.writeValueAsString(tag))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }
    @WithUserDetails("admin")
    @Test
    void addTag_thenReturn403() throws Exception {

        mockMvc.perform(post("/api/tags")
                        .content(objectMapper.writeValueAsString(tag))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    void getTag_success() throws Exception {
        when(tagService.getTag(1L)).thenReturn(tag);
        mockMvc.perform(get("/api/tags/{id}",1L)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithUserDetails("user")
    @Test
    void updateTag_success() throws Exception {

        List<Role> roles = Arrays.asList(rolUser, rolAdmin);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(tagService.updateTag(1L,tagRequest,userPrincipal)).thenReturn(tag);

        mockMvc.perform(put("/api/tags/{id}",1L)
                        .content(objectMapper.writeValueAsString(tagRequest))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void updateTag_thenReturn401() throws Exception {
        mockMvc.perform(put("/api/albums/{id}",1L)
                        .content(objectMapper.writeValueAsString(tagRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }


    @Test
    void updateTag_thenReturn403() throws Exception {
        mockMvc.perform(put("/api/albums/{id}",1L)
                        .content(objectMapper.writeValueAsString(tagRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }
}
