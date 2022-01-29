package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TodoService;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TodoService todoService;

    private Role rol;
    private Todo todo;
    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void initTest(){

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        List<Role> roles = Arrays.asList(rol);

        user = new User();
        user.setId(3L);
        user.setRoles(roles);

        userPrincipal= new UserPrincipal(1L,"Vicente","bla bla","Vicent","vicente@mail.com","123456",user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()));

        todo= new Todo();
        todo.setTitle("Que chaval");
        todo.setId(1l);


    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whengetAllTodo_Succes() throws Exception {
        mockMvc.perform(get("/api/todos")
                .contentType("application/json")
                    .param("page","1")
                    .param("size","10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whenPostTodo_Succes() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType("application/json")
                    .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whenGetOneTodo_Succes() throws Exception {
        mockMvc.perform(get("/api/todos/{id}",1)
                        .contentType("application/json"))
                        .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whenPutTodo_Succes() throws Exception {
        mockMvc.perform(put("/api/todos/{id}",1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whenDeleteTodo_Succes() throws Exception {
        mockMvc.perform(delete("/api/todos/{id}",1)
                        .contentType("application/json"))
                        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whenPutTodoComplete_Succes() throws Exception {
        mockMvc.perform(put("/api/todos/{id}/complete",1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void whenPutTodoUncomplete_Succes() throws Exception {
        mockMvc.perform(put("/api/todos/{id}/unComplete",1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

}
