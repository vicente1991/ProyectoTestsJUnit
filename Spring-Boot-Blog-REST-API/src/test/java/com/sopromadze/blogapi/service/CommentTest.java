package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CommentServiceImpl commentService;



    @Test
    public void whennewComment_Succes(){

        User u= new User();
        u.setFirstName("Vicente");
        u.setLastName("Rufo Bru");
        u.setEmail("Vicente@mail.com");
        u.setUsername("Vicent");

        UserPrincipal up= UserPrincipal.builder()
                .id(2L)
                .email("blabla@mail.com")
                .firstName("bla")
                .lastName("blabla")
                .build();

        Post p= new Post();
        p.setTitle("Post General");
        p.setId(1L);
        p.setBody("Explicando cosas");
        p.setUser(u);

        CommentRequest com = new CommentRequest();
                com.setBody("nuevo mensaje de texto");

        when(userRepository.getUser(up)).thenReturn(u);

        Comment c= new Comment();
        c.setName("Comentario 1");
        c.setId(1L);
        c.setEmail("Vicente@mail.com");
        c.setBody("Hola que tal");
        c.setUser(u);

        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(p));
        when(commentRepository.save(any(Comment.class))).thenReturn(c);
        assertEquals(c,commentService.addComment(com,1L,up));
    }

    @Test
    void deleteComment_Succes(){


    }


}
