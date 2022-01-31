package com.sopromadze.blogapi.service;


import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.service.impl.CategoryServiceImpl;
import com.sopromadze.blogapi.service.impl.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @BeforeEach
    void initTest() {
        user =  new User();
        user.setPassword("1234jc");
        user.setEmail("hola@gmail.com");
        user.setLastName("hola");
        user.setFirstName("hola");
        user.setUsername("user");
        user.setId(1L);

        //userDetails =

    }

    @Test
    void loadUserByUsername_success(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //assertEquals(user, );
    }
}
