package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostTest {

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

    @Test
    void whengetAllPost_Success(){


        Post p= new Post();
        p.setTitle("Nuevo Post");
        p.setId(1L);

        Page<Post> res= new PageImpl<>(Arrays.asList(p));

        PagedResponse<Post> pagedResponse = new PagedResponse<>();

        pagedResponse.setContent(res.getContent());
        pagedResponse.setTotalPages(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);
        pagedResponse.setSize(1);

        Pageable pageable = PageRequest.of(1, 10);
        when(postRepository.findAll(any(Pageable.class))).thenReturn(res);

        assertEquals(pagedResponse, postService.getAllPosts(1, 1));
    }

  /*  @Test
    void whenGetPostCreated_Success(){

        Post p= new Post();
        p.setTitle("Nuevo Post");
        p.setId(1L);
        p.setCreatedBy(1L);

        User u=new User();
        u.setUsername("Vicente");
        u.setId(1L);
        u.setPosts(List.of(p));

        Page<Post> res= new PageImpl<>(Arrays.asList(p));
        List<Post> content = res.getNumberOfElements() == 0 ? Collections.emptyList() : res.getContent();

        Pageable pageable= PageRequest.of(1,1);
        System.out.println(res.getNumberOfElements());
        System.out.println(res.getContent());

        PagedResponse<Post> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(content);
        pagedResponse.setTotalPages(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);
        pagedResponse.setSize(1);

        lenient().when(userRepository.getUserByName("Vicente")).thenReturn(u);
        lenient().when(postRepository.findByCreatedBy(1L,pageable)).thenReturn(res);
        System.out.println(postRepository.findByCreatedBy(1L,pageable).getNumberOfElements());
        assertEquals(pagedResponse,postService.getPostsByCreatedBy("Vicente",1,1));
    }*/

}
