package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TagServiceImplTest {


    @Mock
    private TagRepository tagRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    TagServiceImpl tagService;

    static Pageable pageable;
    static Tag tag;
    static User user, user2;
    static List<Role> roles, roles2;

    @BeforeEach
    void initData(){

        pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        tag = new Tag("Etiqueta 1");
        tag.setId(1L);

        user = new User ("Inmaculada", "Domínguez", "inmadv", "inma.dvgs@gmail.com", "12345");
        user.setId(2L);

        user2 = new User ("Javier", "Domínguez", "javidv", "javierdominguez2006@gmail.com", "54321");
        user2.setId(4L);

        roles = new ArrayList<Role>();
        roles.add(new Role(RoleName.ROLE_ADMIN));

        roles2 = new ArrayList<Role>();
        roles2.add(new Role(RoleName.ROLE_USER));

        user.setRoles(roles);
        user2.setRoles(roles2);

        tag.setCreatedBy(user.getId());

    }


    @Test
    void getAllTags_Success() {

        Page<Tag> tags = new PageImpl<Tag>(List.of(tag));

        when(tagRepository.findAll(pageable)).thenReturn(tags);

        List<Tag> tagList = tags.getContent();

        PagedResponse <Tag> pagedResponse = new PagedResponse<>(tagList, tags.getNumber(), tags.getSize(), tags.getTotalElements(), tags.getTotalPages(), tags.isLast());

        assertEquals(tagService.getAllTags(1, 1), pagedResponse);
    }

    @Test
    void getTag_throwResourceNotFoundException() {
        when(tagRepository.findById(3L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                        () -> tagService.getTag(3L),
                        "No existe una etiqueta con ese ID");
    }

    @Test
    void addTag() {
        when(tagRepository.save(tag)).thenReturn(tag);

        assertEquals(tagService.addTag(tag, UserPrincipal.create(user)), tag);
    }

    @Test
    void updateTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag newTag = new Tag("Más populares");

        tag.setName(newTag.getName());

        when(tagRepository.save(tag)).thenReturn(tag);

        assertEquals(tagService.updateTag(1L, newTag, UserPrincipal.create(user)), tag);
    }

    @Test
    void updateTag_throwUnauthorizedException(){

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag newTag = new Tag("Más populares");

        tag.setName(newTag.getName());


        when(tagRepository.save(tag)).thenReturn(tag);

        assertThrows(UnauthorizedException.class,
                () -> tagService.updateTag(1L, newTag, UserPrincipal.create(user2)), "No tiene permiso");

    }


    @Test
    void deleteTag() {

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted tag");

        assertEquals(apiResponse, tagService.deleteTag(1L, UserPrincipal.create(user)));
    }

    @Test
    void deleteTag_throwUnauthorizedException(){

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        assertThrows(UnauthorizedException.class,
                () -> tagService.deleteTag(1L, UserPrincipal.create(user2)), "No tiene permiso para borrar esto");


    }
}