package com.sopromadze.blogapi.config;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@TestConfiguration
public class SpringSecurityTestWebConfig {

    @Bean("customUserDetailsServiceImpl")
    @Primary
    public UserDetailsService userDetailsService() {

        Role rol1 = new Role();
        rol1.setName(RoleName.ROLE_USER);

        Role rol2 = new Role();
        rol2.setName(RoleName.ROLE_ADMIN);

        List<Role> rol = Arrays.asList(rol1);

        List<Role> ambosRoles = Arrays.asList(rol1,rol2);

        User admin = new User();
        admin.setPassword("admin");
        admin.setUsername("admin");
        admin.setRoles(ambosRoles);



        User user = new User();
        user.setPassword("user");
        user.setUsername("user");
        user.setRoles(rol);



        return new InMemoryUserDetailsManager((UserDetails) List.of(admin, user));

    }


}