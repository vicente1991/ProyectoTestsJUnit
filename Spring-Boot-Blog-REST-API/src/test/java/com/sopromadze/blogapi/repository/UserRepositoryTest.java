package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    static User user;
    static UserPrincipal userPrincipal;
    static List<Role> roles;


    @BeforeEach
    void initData (){

        user = new User ("Inmaculada", "Domínguez", "inmadv", "inma.dvgs@gmail.com", "12345");

        roles = new ArrayList<Role>();
        roles.add(new Role(RoleName.ROLE_ADMIN));

        user.setRoles(roles);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        testEntityManager.persist(user);


    }

    @Test
    void findByUsername_Success() {
        assertEquals(userRepository.findByUsername("inmadv"), Optional.of(user));
    }

    @Test
    void findByUsername_Fail() {
        assertThat(userRepository.findByUsername("holaquetal").isEmpty());
    }

    @Test
    void findByEmail_Success() {
        assertEquals(userRepository.findByEmail("inma.dvgs@gmail.com"), Optional.of(user));
    }

    @Test
    void findByEmail_Fail() {
        assertThat(userRepository.findByEmail("inmadvgs@gmaill.com").isEmpty());
    }

    @Test
    void existsByUsername_Success() {
       assertEquals(userRepository.existsByUsername("inmadv"), true);
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

        userPrincipal = UserPrincipal.create(user);

        assertEquals(userRepository.getUser(userPrincipal), user);
    }

    @Test
    void getUserByName_Fail() {
        assertThrows(ResourceNotFoundException.class,
                () -> userRepository.getUserByName("pepeRana"),
                "No existe un usuario con ese nickname");
    }
}