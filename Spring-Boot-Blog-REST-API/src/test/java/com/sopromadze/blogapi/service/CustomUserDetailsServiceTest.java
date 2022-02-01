package com.sopromadze.blogapi.service;


import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CategoryServiceImpl;
import com.sopromadze.blogapi.service.impl.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsServiceImpl customUserDetailsService;


    User user;
    UserDetails userDetails;
    Role rolAdmin;
    Role rolUser;
    @BeforeEach
    void initTest() {


        user =  new User();
        user.setPassword("1234jc");
        user.setEmail("user");
        user.setLastName("hola");
        user.setFirstName("hola");
        user.setUsername("user");
        user.setId(1L);

        rolAdmin = new Role();
        rolAdmin.setName(RoleName.ROLE_ADMIN);

        rolUser = new Role();
        rolUser.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rolUser);
        user.setRoles(roles);

        userDetails = UserPrincipal.create(user);

    }

    @Test
    void loadUserByUsername_success(){
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(user));
        assertEquals(userDetails, customUserDetailsService.loadUserByUsername(user.getUsername()));
    }


    @Test
    void loadUserByUsername_throwUsernameNotFoundException(){
        when(userRepository.findByUsernameOrEmail("user2", "user2")).thenReturn(Optional.of(user));
        assertThrows(UsernameNotFoundException.class,()-> customUserDetailsService.loadUserByUsername(user.getUsername()));
    }


    @Test
    void loadUserById_success(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(userDetails, customUserDetailsService.loadUserById(user.getId()));
    }

    @Test
    void loadUserById_throwUsernameNotFoundException(){
        when(userRepository.findByUsernameOrEmail("user2", "user2")).thenReturn(Optional.of(user));
        assertThrows(UsernameNotFoundException.class,()-> customUserDetailsService.loadUserById(user.getId()));
    }

}
