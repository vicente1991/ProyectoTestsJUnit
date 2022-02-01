package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleTest {

    @Autowired
    private RoleRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void test_findByName() {

        Role rolUser = new Role();
        rolUser.setName(RoleName.ROLE_USER);
        testEntityManager.persist(rolUser);

        assertEquals(true, repository.findByName(rolUser.getName()).isPresent());

    }

}
