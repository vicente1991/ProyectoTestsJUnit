package com.sopromadze.blogapi.config;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.*;

@TestConfiguration
public class SpringSecurityTestWebConfig {

    @Bean("customUserDetailsService")
    @Primary
    public UserDetailsService userDetailsService() {

        Role rol1 = new Role();
        rol1.setName(RoleName.ROLE_USER);

        Role rol2 = new Role();
        rol2.setName(RoleName.ROLE_ADMIN);

        List<Role> rolUs = Arrays.asList(rol1);

        List<Role> rolAd = Arrays.asList(rol2);

        List<Role> rolAmbos = Arrays.asList(rol1,rol2);

        User admin = new User();
        admin.setPassword("admin");
        admin.setUsername("admin");
        admin.setRoles(rolAd);


        UserPrincipal adminP = UserPrincipal.create(admin);


        User user = new User();
        user.setPassword("user");
        user.setUsername("user");
        user.setRoles(rolUs);

        UserPrincipal userP = UserPrincipal.create(user);




        return new InMemoryUserDetailsManager(List.of(adminP, userP));



    }


}
