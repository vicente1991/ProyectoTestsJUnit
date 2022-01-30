package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    static User user;
    static UserPrincipal userPrincipal;


    @BeforeEach
    void initData (){

        user = new User ("Inmaculada", "Domínguez", "inmadv", "inma.dvgs@gmail.com", "12345");
        user.setId(1L);

        userPrincipal = UserPrincipal.builder()
                                     .id(2L)
                                     .firstName("Auxiliadora")
                                     .username("auxivm")
                                     .build();

    }

    @Test
    void findByUsername_Success() {
        assertThat(userRepository.findByUsername("inmadv").isPresent());
    }

    @Test
    void findByUsername_Fail() {
        assertThat(userRepository.findByUsername("holaquetal").isEmpty());
    }

    @Test
    void findByEmail_Success() {
        assertThat(userRepository.findByEmail("inma.dvgs@gmail.com").isPresent());
    }

    @Test
    void findByEmail_Fail() {
        assertThat(userRepository.findByEmail("inmadvgs@gmaill.com").isEmpty());
    }

    @Test
    void existsByUsername_Success() {
       assertThat(userRepository.existsByUsername("inmadv"));
    }

    @Test
    void existsByEmail_Success() {
        assertThat(userRepository.existsByEmail("inma.dvgs@gmail.com"));
    }

    @Test
    void findByUsernameOrEmail_Success() {
        assertThat(userRepository.findByUsernameOrEmail("inmadv", "inma.dvgs@gmail.com").isPresent());
    }

    @Test
    void getUser_Success() {
        assertNotNull(userRepository.getUser(userPrincipal));
    }

    @Test
    void getUserByName_Fail() {
        assertThrows(ResourceNotFoundException.class,
                () -> userRepository.getUserByName("pepeRana"),
                "No existe un usuario con ese nickname");
    }
}