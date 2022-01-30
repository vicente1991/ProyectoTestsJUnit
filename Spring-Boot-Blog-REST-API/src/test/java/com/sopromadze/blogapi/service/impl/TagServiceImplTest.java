package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TagRepository;
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

import java.util.ArrayList;
import java.util.List;

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


    @BeforeEach
    void initData(){

        pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        tag = new Tag("Etiqueta 1");
        tag.setId(1L);


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
    void getTag_Exception() {
        when(tagRepository.findById(3L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                        () -> tagRepository.findById(3L),
                        "No existe una etiqueta con ese ID");
    }

    @Test
    void addTag() {
    }

    @Test
    void updateTag() {
    }

    @Test
    void deleteTag() {
    }
}