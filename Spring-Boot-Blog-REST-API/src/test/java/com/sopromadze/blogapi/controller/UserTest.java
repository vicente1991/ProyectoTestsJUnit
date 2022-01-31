package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.config.SpringSecurityTestWebConfig;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PostService;
import com.sopromadze.blogapi.service.UserService;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.Instant;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private AlbumService albumService;


    User user;
    User userRequest;
    Role rolAdmin;
    Role rolUser;
    UserSummary userSummary;
    UserIdentityAvailability userIdentityAvailability;
    UserProfile userProfile;
    Post post;
    PagedResponse<Post> postPagedResponse;
    PagedResponse<Album> albumPagedResponse;
    ApiResponse apiResponse;
    InfoRequest infoRequest;

    @BeforeEach
    void initTest() {


        user =  new User();
        user.setPassword("1234jc1");
        user.setEmail("hola@gmail.com1");
        user.setLastName("hola21");
        user.setFirstName("hola11");
        user.setUsername("user2");
        user.setId(1L);

        userRequest =  new User();
        userRequest.setId(5L);
        userRequest.setPassword("1234jc");
        userRequest.setEmail("hola@gmail.com");
        userRequest.setLastName("hola2");
        userRequest.setFirstName("hola1");
        userRequest.setUsername("holax");

        rolAdmin = new Role();
        rolAdmin.setName(RoleName.ROLE_ADMIN);

        rolUser = new Role();
        rolUser.setName(RoleName.ROLE_USER);

        userSummary = new UserSummary(1L, "user", "user", "user");

        userIdentityAvailability = new UserIdentityAvailability(true);

        userProfile = new UserProfile();
        userProfile.setUsername("user");
        userProfile.setId(3L);

        post = new Post();
        post.setCreatedBy(3L);

        postPagedResponse = new PagedResponse(List.of(post), 1, 1, 1, 1, true);
        albumPagedResponse = new PagedResponse(List.of(post), 1, 1, 1, 1, true);

        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setStatus(HttpStatus.OK);
        apiResponse.setMessage("You successfully deleted comment");

        infoRequest = new InfoRequest();
        infoRequest.setStreet("Street Prueba");
        infoRequest.setSuite("Suite Prueba");
        infoRequest.setCity("City Prueba");
        infoRequest.setZipcode("Zipcode Prueba");

    }

    @WithUserDetails("user")
    @Test
    void getCurrentUser_success() throws Exception{
        List<Role> roles = Arrays.asList(rolUser);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(userService.getCurrentUser(userPrincipal)).thenReturn(userSummary);

        mockMvc.perform(get("/api/users/me")
                .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }


    @Test
    void getCurrentUser_thenReturn401() throws Exception{
        mockMvc.perform(get("/api/users/me")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @WithUserDetails("admin")
    @Test
    void getCurrentUser_thenReturn403() throws Exception{
        mockMvc.perform(get("/api/users/me")
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    void checkUsernameAvailability_success() throws Exception{
        when(userService.checkUsernameAvailability("user")).thenReturn(userIdentityAvailability);
        mockMvc.perform(get("/api/users/checkUsernameAvailability")
                .param("username","user")
                .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    void checkEmailAvailability_success() throws Exception{
        when(userService.checkEmailAvailability("email@gmail.com")).thenReturn(userIdentityAvailability);
        mockMvc.perform(get("/api/users/checkEmailAvailability")
                        .param("email","email@gmail.com")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void getUSerProfile_success() throws Exception{
        when(userService.getUserProfile("user")).thenReturn(userProfile);
        mockMvc.perform(get("/api/users/{username}/profile", "user")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void getPostsCreatedBy_success() throws Exception{
        when(postService.getPostsByCreatedBy("user",1,1)).thenReturn(postPagedResponse);
        mockMvc.perform(get("/api/users/{username}/posts", "user")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void getUserAlbums_success() throws Exception{
        when(albumService.getUserAlbums("user",1,1)).thenReturn(albumPagedResponse);
        mockMvc.perform(get("/api/users/{username}/albums", "user")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithUserDetails("admin")
    @Test
    void addUser_success() throws Exception{

        user.setId(3L);
        user.setPassword("1234");
        user.setEmail("hola@gmail.com");
        user.setLastName("hola2");
        user.setFirstName("hola1");
        user.setUsername("holax");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        when(userService.addUser(user)).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());
    }


    @Test
    void addUser_thenReturn401() throws Exception{
        mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @WithUserDetails("user")
    @Test
    void addUser_thenReturn403() throws Exception{
        mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());
    }


    @WithUserDetails("admin")
    @Test
    void updateUser_success() throws Exception{

        List<Role> roles = Arrays.asList(rolAdmin, rolUser);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

       when(userService.updateUser(userRequest, "user2", userPrincipal)).thenReturn(user);

       mockMvc.perform(put("/api/users/{username}", "user2")
                       .content(objectMapper.writeValueAsString(userRequest))
                       .contentType("application/json"))
               .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    void updateUser_thenReturn401() throws Exception{
        mockMvc.perform(put("/api/users/{username}", "user2")
                        .content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }



    @WithUserDetails("admin")
    @Test
    void deleteUser_success() throws Exception{

        List<Role> roles = Arrays.asList(rolAdmin, rolUser);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(userService.deleteUser( "user2", userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/users/{username}", "user2")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }



    @Test
    void deleteUser_thenReturn401() throws Exception{
        mockMvc.perform(delete("/api/users/{username}", "user2")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @WithUserDetails("admin")
    @Test
    void giveAdmin_success() throws Exception{
        when(userService.giveAdmin("user2")).thenReturn(apiResponse);
        mockMvc.perform(put("/api/users/{username}/giveAdmin","user2")
                .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }


    @Test
    void giveAdmin_thenReturn401() throws Exception{
        when(userService.giveAdmin("user2")).thenReturn(apiResponse);
        mockMvc.perform(put("/api/users/{username}/giveAdmin","user2")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @WithUserDetails("user")
    @Test
    void giveAdmin_thenReturn403() throws Exception{
        when(userService.giveAdmin("user2")).thenReturn(apiResponse);
        mockMvc.perform(put("/api/users/{username}/giveAdmin","user2")
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());
    }

    @WithUserDetails("admin")
    @Test
    void takeAdmin_success() throws Exception{
        when(userService.removeAdmin("user2")).thenReturn(apiResponse);
        mockMvc.perform(put("/api/users/{username}/takeAdmin","user2")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }


    @Test
    void takeAdmin_thenReturn401() throws Exception{
        when(userService.removeAdmin("user2")).thenReturn(apiResponse);
        mockMvc.perform(put("/api/users/{username}/takeAdmin","user2")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }
    @WithUserDetails("user")
    @Test
    void takeAdmin_thenReturn403() throws Exception{
        when(userService.removeAdmin("user2")).thenReturn(apiResponse);
        mockMvc.perform(put("/api/users/{username}/takeAdmin","user2")
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());
    }



    @WithUserDetails("admin")
    @Test
    void setAddress_success() throws Exception{

        List<Role> roles = Arrays.asList(rolAdmin, rolUser);
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(userService.setOrUpdateInfo(userPrincipal,infoRequest)).thenReturn(userProfile);
        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                        .content(objectMapper.writeValueAsString(infoRequest))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }


    @Test
    void setAddress_thenReturn401() throws Exception{
        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                        .content(objectMapper.writeValueAsString(infoRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }



}
