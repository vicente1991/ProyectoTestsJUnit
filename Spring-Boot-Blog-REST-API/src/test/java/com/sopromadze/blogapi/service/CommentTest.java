package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void getAllComments_Success(){

        Comment c= new Comment();
        c.setName("Vicente");
        c.setBody("Nuevo comentario");

        Post p= new Post();
        p.setTitle("Nuevo Post");
        p.setId(1L);

        Page<Comment> res= new PageImpl<>(Arrays.asList(c));

        PagedResponse<Comment> pagedResponse = new PagedResponse<>();

        pagedResponse.setContent(res.getContent());
        pagedResponse.setTotalPages(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);
        pagedResponse.setSize(1);

        Pageable pageable = PageRequest.of(1, 10);
        when(commentRepository.findByPostId(any(Long.class), any(Pageable.class))).thenReturn(res);

        assertEquals(pagedResponse, commentService.getAllComments(1L, 1, 10));
    }

    @Test
    void getOneComments_Success(){

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

        Comment c= new Comment();
        c.setName("Comentario 1");
        c.setId(1L);
        c.setPost(p);
        c.setEmail("Vicente@mail.com");
        c.setBody("Hola que tal");
        c.setUser(u);

        lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.of(p));
        lenient().when(commentRepository.findById(c.getId())).thenReturn(java.util.Optional.of(c));
        assertEquals(c,commentService.getComment(p.getId(),c.getId()));

    }

    @Test
    public void whennewComment_Success(){

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

        lenient().when(userRepository.getUser(up)).thenReturn(u);

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
    void deleteComment_Success(){

       Role rol = new Role();
       rol.setName(RoleName.ROLE_ADMIN);
        List<Role> roles= Arrays.asList(rol);
        User u= new User();
        u.setId(1L);
        u.setFirstName("Vicente");
        u.setLastName("Rufo Bru");
        u.setRoles(roles);
        u.setEmail("Vicente@mail.com");
        u.setUsername("Vicent");


        UserPrincipal up= UserPrincipal.builder()
                .id(2L)
                .email("blabla@mail.com")
                .authorities( u.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .firstName("bla")
                .lastName("blabla")
                .build();

        Post p= new Post();
        p.setTitle("Post General");
        p.setId(1L);
        p.setBody("Explicando cosas");
        p.setUser(u);

        lenient().when(userRepository.getUser(up)).thenReturn(u);

        CommentRequest com = new CommentRequest();
        com.setBody("nuevo mensaje de texto");

        Comment c= new Comment();
        c.setName("Comentario 1");
        c.setId(1L);
        c.setPost(p);
        c.setEmail("Vicente@mail.com");
        c.setBody("Hola que tal");
        c.setUser(u);

        lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.of(p));
        lenient().when(commentRepository.findById(c.getId())).thenReturn(java.util.Optional.of(c));
        doNothing().when(commentRepository).deleteById(c.getId());
        assertEquals(true,commentService.deleteComment(p.getId(),c.getId(),up).getSuccess());

    }

    @Test
    void whenCommentUpdate_Success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        List<Role> roles= Arrays.asList(rol);

        User u= new User();
        u.setId(1L);
        u.setRoles(roles);
        u.setFirstName("Vicente");
        u.setLastName("Rufo Bru");
        u.setEmail("Vicente@mail.com");
        u.setUsername("Vicent");


        UserPrincipal up= UserPrincipal.builder()
                .id(2L)
                .email("blabla@mail.com")
                .authorities( u.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .firstName("bla")
                .lastName("blabla")
                .build();

        Post p= new Post();
        p.setTitle("Post General");
        p.setId(1L);
        p.setBody("Explicando cosas");
        p.setUser(u);

        Comment c= new Comment();
        c.setName("Comentario 1");
        c.setId(1L);
        c.setPost(p);
        c.setEmail("Vicente@mail.com");
        c.setBody("Hola que tal");
        c.setUser(u);

        CommentRequest com = new CommentRequest();
        com.setBody("mensaje rectificado de texto");

        lenient().when(postRepository.findById(p.getId())).thenReturn(java.util.Optional.of(p));
        lenient().when(commentRepository.findById(c.getId())).thenReturn(java.util.Optional.of(c));
        lenient().when(commentRepository.save(c)).thenReturn(c);
        Comment c2= commentRepository.save(c);
        assertEquals(c,c2);
    }
    @Test
    void deleteComment_throwResourceNotFoundException(){

        Post p = new Post();
        p.setId(1L);
        p.setTitle("Post Nuevo");

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        User u= new User();
        u.setId(1L);
        u.setRoles(roles);
        u.setFirstName("Vicente");
        u.setLastName("Rufo Bru");
        u.setEmail("Vicente@mail.com");
        u.setUsername("Vicent");

        Comment c= new Comment();
        c.setName("Comentario 1");
        c.setId(1L);
        c.setPost(p);
        c.setEmail("Vicente@mail.com");
        c.setBody("Hola que tal");
        c.setUser(u);


        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(2L)
                .authorities(u.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("a", "x", 1L);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("You don't have permission to delete this comment");

        lenient().when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(p));
        lenient().when(commentRepository.findById(1L)).thenReturn(java.util.Optional.of(c));
        //lenient().when(commentRepository.deleteById(c.getId()));
        assertThrows(resourceNotFoundException.getClass(), ()->commentRepository.deleteById(0L));

    }


}
