package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CategoryService;
import com.sopromadze.blogapi.service.CommentService;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {SpringSecurityTestWebConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CategoryControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    static PagedResponse<Category> categoryPagedResponse;
    static Category category;
    static List<Category> categoryList;
    static UserPrincipal userPrincipal, userPrincipal2;
    static ApiResponse apiResponse;


    @BeforeEach
    void initTest(){

        category = new Category("Novedades");
        category.setId(1L);

        categoryList = new ArrayList<>();
        categoryList.add(category);

        categoryPagedResponse = new PagedResponse(categoryList, 1, 1, 1, 1,true );


        userPrincipal = UserPrincipal.builder()
                            .id(2L)
                            .username("user")
                            .authorities(List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())))
                            .build();

        apiResponse = new ApiResponse();

    }


    @Test
    void getAllCategories_Success() throws Exception {

        when(categoryService.getAllCategories(1, 1)).thenReturn(categoryPagedResponse);

        mockMvc.perform(get("/api/categories")
                        .param("page", "1")
                        .param("size", "1")
                .contentType("application/json"))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(categoryPagedResponse)))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void addCategory_Success() throws Exception{

     when(categoryService.addCategory(category, userPrincipal)).thenReturn(category);

     mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated());


    }

    @Test
    void addCategory_thenReturns401() throws Exception {
        when(categoryService.addCategory(category, userPrincipal)).thenReturn(category);
        mockMvc.perform(post("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCategory_Success() throws Exception{
        when(categoryService.getCategory(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void getCategory_thenReturns404() throws Exception {

        mockMvc.perform(get("/api/categories/{id}", 15L)
                        .contentType("application/json"))
                .andExpect(status().isNotFound()).andDo(print());

        assertThrows(NotFoundException.class, () -> categoryService.getCategory(15L), "ID no existe");

    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    void updateCategory_Success() throws Exception{
        when(categoryService.updateCategory(1L, category, userPrincipal)).thenReturn(category);

        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    void updateCategory_thenReturns401() throws Exception{
        when(categoryService.updateCategory(1L, category, userPrincipal)).thenReturn(category);
        mockMvc.perform(put("/api/categories/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    void deleteCategory_Success() throws Exception{

        when(categoryService.deleteCategory(1L, userPrincipal)).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/categories/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void deleteCategory_thenReturns401() throws Exception{
        when(categoryService.deleteCategory(1L, userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/categories/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}