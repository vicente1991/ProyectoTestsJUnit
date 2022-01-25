package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
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
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class CommentTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CommentService commentService;



    @Test
    public void whennewComment_Succespublic(){

        User u= new User();
        u.setFirstName("Vicente");
        u.setLastName("Rufo Bru");
        u.setEmail("Vicente@mail.com");
        u.setUsername("Vicent");

        Post p= new Post();
        p.setTitle("Post General");
        p.setId(1L);
        p.setBody("Explicando cosas");
        p.setUser(u);

        lenient().when(userRepository.findByUsername("Vicent")).thenReturn(java.util.Optional.of((u)));
        Map<String,Integer> nuevo= Map.of("1",2);

        Comment c= new Comment();
        c.setName("Comentario 1");
        c.setEmail("Vicente@mail.com");
        c.setBody("Hola que tal");
        c.setUser(u);

        lenient().when(commentRepository.save(c)).thenReturn(c);
        assertEquals(c,commentService.addComment(c,nuevo));
    }


}
