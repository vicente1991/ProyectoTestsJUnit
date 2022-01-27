package com.sopromadze.blogapi.service.impl;


import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
public class CategoryTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    void getAllCategories_success(){


        User user = new User();
        user.setUsername("user");
        user.setId(1L);

        List<Post> postList = new ArrayList<>();

        Category category = new Category();
        category.setId(1L);
        category.setPosts(postList);
        category.setName("Categoria");

        Page<Category> pageResult = new PageImpl<>(Arrays.asList(category));

        PagedResponse<Category> result = new PagedResponse<>();
        result.setContent(pageResult.getContent());
        result.setTotalPages(1);
        result.setTotalElements(1);
        result.setLast(true);
        result.setSize(1);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        when(categoryRepository.findAll(pageable)).thenReturn(pageResult);

        assertEquals(result,categoryService.getAllCategories(1,1));
    }





    @Test
    void getCategory_success(){

        List<Post> postList = new ArrayList<>();

        Category category = new Category();
        category.setId(1L);
        category.setPosts(postList);
        category.setName("Categoria");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        assertEquals(category,categoryService.getCategory(1L));
    }
    @Test
    void getCategory_throwResourceNotFoundException(){
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->categoryService.getCategory(any(Long.class)));
    }


    @Test
    void addCategory_success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();


        List<Post> postList = new ArrayList<>();

        Category category = new Category();
        category.setId(1L);
        category.setPosts(postList);
        category.setName("Categoria");

        when(categoryRepository.save(category)).thenReturn(category);
        assertEquals(category,categoryService.addCategory(category, userPrincipal));
    }


    @Test
    void updateCategory_success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        List<Post> postList = new ArrayList<>();



        Category newCategory = new Category();
        newCategory.setId(1L);
        newCategory.setPosts(postList);
        newCategory.setName("Categoria nueva");
        //newCategory.setCreatedBy(10L);

        Category category = new Category();
        category.setId(4L);
        category.setPosts(postList);
        category.setName("Categoria");
        category.setCreatedBy(3L);



        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        category.setName(newCategory.getName());

        when(categoryRepository.save(category)).thenReturn(category);

        assertEquals(category,categoryService.updateCategory(4L,newCategory,userPrincipal));
    }

    @Test
    void updateCategory_throwResourceNotFoundException(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->categoryService.updateCategory(null,any(Category.class),userPrincipal));
    }
    @Test
    void updateCategory_throwUnauthorizedException(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        List<Post> postList = new ArrayList<>();

        Category newCategory = new Category();
        newCategory.setId(1L);
        newCategory.setPosts(postList);
        newCategory.setName("Categoria nueva");

        Category category = new Category();
        category.setId(4L);
        category.setPosts(postList);
        category.setName("Categoria");
        category.setCreatedBy(10L);



        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        category.setName(newCategory.getName());

        when(categoryRepository.save(category)).thenReturn(category);

        assertThrows(UnauthorizedException.class,()->categoryService.updateCategory(4L,newCategory,userPrincipal));
    }

    @Test
    void deleteCategory_success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        List<Post> postList = new ArrayList<>();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("You successfully deleted category");

        Category category = new Category();
        category.setId(4L);
        category.setPosts(postList);
        category.setName("Categoria");
        category.setCreatedBy(3L);

        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);
        categoryService.deleteCategory(4L,userPrincipal);
        assertEquals(apiResponse,categoryService.deleteCategory(category.getId(),userPrincipal));

    }

    @Test
    void deleteCategory_throwResourceNotFoundException(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->categoryService.deleteCategory(any(Long.class),userPrincipal));
    }

    @Test
    void deleteCategory_throwUnauthorizedException(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();
        List<Post> postList = new ArrayList<>();

        Category newCategory = new Category();
        newCategory.setId(1L);
        newCategory.setPosts(postList);
        newCategory.setName("Categoria nueva");

        Category category = new Category();
        category.setId(4L);
        category.setPosts(postList);
        category.setName("Categoria");
        category.setCreatedBy(10L);



        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        category.setName(newCategory.getName());

        when(categoryRepository.save(category)).thenReturn(category);

        assertThrows(UnauthorizedException.class,()->categoryService.deleteCategory(4L,userPrincipal));
    }
}