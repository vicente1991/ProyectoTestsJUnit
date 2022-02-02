package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    PostServiceImpl postService;


        private Post p;
        private Page<Post> res;
        private PagedResponse<Post> pagedResponse;
        private User u;
        private UserPrincipal up;
        private PostRequest postRequest;
        private PostResponse postResponse;
        private Tag tag;
        private Category category;
        private List<Post> content;
        private Pageable pageable;
        private ApiResponse apiResponse;

        @BeforeEach
        void init(){


            category= new Category();
            category.setId(1L);
            category.setName("Nueva");
            category.setPosts(content);
            category.setCreatedAt(Instant.now());
            category.setUpdatedAt(Instant.now());

            Role rol = new Role();
            rol.setName(RoleName.ROLE_USER);

            List<Role> roles = Arrays.asList(rol);

            u=new User();
            u.setUsername("Vicente");
            u.setId(1L);
            u.setRoles(roles);

            up = UserPrincipal.builder()
                    .id(2L)
                    .authorities(u.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                    .build();


            tag= new Tag();
            tag.setId(1L);
            tag.setPosts(content);
            tag.setName("TagTag");
            tag.setCreatedAt(Instant.now());
            tag.setUpdatedAt(Instant.now());

            res= new PageImpl<>(Arrays.asList(p));
            content = res.getNumberOfElements() == 0 ? Collections.emptyList() : res.getContent();
            pagedResponse = new PagedResponse<>();

            pagedResponse.setContent(content);
            pagedResponse.setTotalPages(1);
            pagedResponse.setTotalElements(1);
            pagedResponse.setPage(0);
            pagedResponse.setLast(true);
            pagedResponse.setSize(1);

            postRequest= new PostRequest();
            postRequest.setBody("bla");
            postRequest.setCategoryId(1L);
            postRequest.setTitle("PostR");

            p = new Post();
            p.setTitle(postRequest.getTitle());
            p.setId(1L);
            p.setTags(Collections.emptyList());
            p.setBody(postRequest.getBody());
            p.setCreatedBy(1L);
            p.setCategory(category);
            p.setUser(u);
            p.setCreatedAt(Instant.now());
            p.setUpdatedAt(Instant.now());

            postResponse= new PostResponse();
            postResponse.setTitle(postRequest.getTitle());
            postResponse.setTags(postRequest.getTags());
            postResponse.setCategory(p.getCategory().getName());
            postResponse.setBody(postRequest.getBody());

            apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("You don't have permission to delete this tag");

            pageable= PageRequest.of(1,1);
        }


        @Test
        void whengetAllPost_Success(){

            when(postRepository.findAll(any(Pageable.class))).thenReturn(res);
            assertEquals(pagedResponse, postService.getAllPosts(1, 1));
        }


        @Test
        void whenGetPostCreated_Success(){

            lenient().when(userRepository.getUserByName(u.getUsername())).thenReturn(u);
            lenient().when(postRepository.findByCreatedBy(Mockito.any(),Mockito.any())).thenReturn(res);
            assertEquals(pagedResponse,postService.getPostsByCreatedBy("Vicente",1,1));
        }

        @Test
        void whenGetPostByCategory(){
            lenient().when(categoryRepository.findById(category.getId())).thenReturn(java.util.Optional.ofNullable(category));
            lenient().when(postRepository.findByCategoryId(Mockito.any(),Mockito.any())).thenReturn(res);
            assertEquals(pagedResponse,postService.getPostsByCategoryId(category.getId(),1,1));
        }

        @Test
        void whenGetPostByTag(){
            lenient().when(tagRepository.findById(tag.getId())).thenReturn(java.util.Optional.ofNullable(tag));
            lenient().when(postRepository.findByTagsIn(any(),any(Pageable.class))).thenReturn(res);
            assertEquals(pagedResponse,postService.getPostsByTagId(tag.getId(),0,1));

        }

        @Test
        void whenUpdatePost_ResourceNotFoundExceptionForPost(){
            lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(p));
            assertThrows(ResourceNotFoundException.class,()->postService.updatePost(p.getId(),postRequest,up));
        }

        @Test
        void whenUpdatePost_ResourceNotFoundExceptionForCategory(){
            lenient().when(categoryRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(category));
            assertThrows(ResourceNotFoundException.class,()->postService.updatePost(p.getId(),postRequest,up));
        }

        @Test
        void whenUpdatePost(){
            lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(p));
            lenient().when(categoryRepository.findById(category.getId())).thenReturn(java.util.Optional.ofNullable(category));
            lenient().when(postRepository.save(p)).thenReturn(p);
            assertThrows(UnauthorizedException.class,()->postService.updatePost(p.getId(),postRequest,up));
        }

        @Test
        void whenDeletePost_ResourceNotFoundExceptionForPost(){
            lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(p));
            assertThrows(ResourceNotFoundException.class,()->postService.deletePost(22L,up));
        }

        @Test
        void whenDeletePost(){
            lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(p));
            assertThrows(UnauthorizedException.class,()->postService.deletePost(p.getId(),up));
        }


        @Test
        void whenAddPost(){
            lenient().when(userRepository.findById(up.getId())).thenReturn(java.util.Optional.ofNullable(u));
            lenient().when(categoryRepository.findById(postRequest.getCategoryId())).thenReturn(java.util.Optional.ofNullable(category));
            lenient().when(tagRepository.findByName(tag.getName())).thenReturn(tag);
            lenient().when(tagRepository.save(tag)).thenReturn(tag);
            lenient().when(postRepository.save(any())).thenReturn(p);
            assertEquals(postResponse,postService.addPost(postRequest,up));
        }

        @Test
        void whenAddPost_ResourceNotFoundExceptionForUser(){
            lenient().when(userRepository.findById(u.getId())).thenReturn(java.util.Optional.ofNullable(u));
            assertThrows(ResourceNotFoundException.class,()->postService.addPost(postRequest,up));
        }

        @Test
        void whenAddPost_ResourceNotFoundExceptionForCategory(){
            lenient().when(categoryRepository.findById(postRequest.getCategoryId())).thenReturn(java.util.Optional.ofNullable(category));
            assertThrows(ResourceNotFoundException.class,()->postService.addPost(postRequest,up));
        }

        @Test
        void whenGetPost(){

            lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(p));
            assertEquals(postService.getPost(p.getId()), p);
        }

        @Test
        void whengetPost_ResourceNotFoundException(){

            lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.ofNullable(p));
            assertThrows(ResourceNotFoundException.class,()->postService.deletePost(22L,up));
        }

    }
    
