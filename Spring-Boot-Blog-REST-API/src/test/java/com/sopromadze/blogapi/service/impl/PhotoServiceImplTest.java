package com.sopromadze.blogapi.service;


import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PhotoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
public class PhotoServiceTest {

    @Mock
    PhotoRepository photoRepository;

    @Mock
    AlbumRepository albumRepository;

    @InjectMocks
    PhotoServiceImpl photoService;


    Pageable pageable;
    Page<Photo> pageResultPhoto;
    Photo photo;
    PhotoRequest photoRequest;
    PagedResponse<PhotoResponse> result;
    PhotoResponse photoResponse;
    Album album;
    User user;
    Role rolAdmin;
    Role rolUser;
    ApiResponse apiResponse;

    @BeforeEach
    void initTest() {

        rolAdmin = new Role();
        rolAdmin.setName(RoleName.ROLE_ADMIN);

        rolUser = new Role();
        rolUser.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rolAdmin);
        user = new User();
        user.setUsername("user");
        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword("1234jc");
        user.setId(3L);
        user.setRoles(roles);

        album = new Album();
        album.setTitle("Titulo");
        album.setUser(user);
        album.setId(4L);


        photo = new Photo();
        photo.setTitle("Titulo guapo");
        photo.setId(1L);
        photo.setAlbum(album);
        photo.setThumbnailUrl("algo");

        photoRequest= new PhotoRequest();
        photoRequest.setTitle("Titulo guapo cambiado");
        photoRequest.setAlbumId(4L);
        photoRequest.setThumbnailUrl("algo");



        pageResultPhoto = new PageImpl<>(Arrays.asList(photo));
        pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");


        photoResponse = new PhotoResponse(1L,"Titulo guapo",null,"algo",4L);
        List<PhotoResponse> photoResponseList = Arrays.asList(photoResponse);

        result = new PagedResponse<>();

        result.setTotalPages(1);
        result.setTotalElements(1);
        result.setLast(true);
        result.setSize(1);
        result.setContent(photoResponseList);

        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Photo deleted successfully");
    }


    @Test
    void getAllPhotos_success(){
        when(photoRepository.findAll(pageable)).thenReturn(pageResultPhoto);
        assertEquals(result, photoService.getAllPhotos(1,1));
    }

    @Test
    void getPhoto_success(){
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        assertEquals(photoResponse, photoService.getPhoto(1L));
    }

    @Test
    void getPhoto_throwResourceNotFoundException(){
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        assertThrows(ResourceNotFoundException.class,()-> photoService.getPhoto(2L));
    }

    @Test
    void updatePhoto_success(){
        PhotoResponse photoResponse2 = new PhotoResponse(1L,"Titulo guapo cambiado",null,"algo",4L);
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photo.setTitle(photoRequest.getTitle());
        photo.setThumbnailUrl(photoRequest.getThumbnailUrl());
        photo.setAlbum(album);

        when(photoRepository.save(photo)).thenReturn(photo);

        assertEquals(photoResponse2,photoService.updatePhoto(1L,photoRequest,userPrincipal));
    }


    @Test
    void updatePhoto_throwResourceNotFoundException1(){
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(404L)).thenReturn(Optional.of(album));

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photo.setTitle(photoRequest.getTitle());
        photo.setThumbnailUrl(photoRequest.getThumbnailUrl());
        photo.setAlbum(album);

        when(photoRepository.save(photo)).thenReturn(photo);

        assertThrows(ResourceNotFoundException.class,()->photoService.updatePhoto(1L,photoRequest,userPrincipal));
    }

    @Test
    void updatePhoto_throwResourceNotFoundException2(){
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        when(photoRepository.findById(404L)).thenReturn(Optional.of(photo));

        photo.setTitle(photoRequest.getTitle());
        photo.setThumbnailUrl(photoRequest.getThumbnailUrl());
        photo.setAlbum(album);

        when(photoRepository.save(photo)).thenReturn(photo);

        assertThrows(ResourceNotFoundException.class,()->photoService.updatePhoto(1L,photoRequest,userPrincipal));
    }

    @Test
    void updatePhoto_throwUnauthorizedException(){
        List<Role> roles = Arrays.asList(rolUser);
        User usernew = new User();
        usernew.setId(4L);
        usernew.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(usernew.getId())
                .authorities(usernew.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photo.setTitle(photoRequest.getTitle());
        photo.setThumbnailUrl(photoRequest.getThumbnailUrl());
        photo.setAlbum(album);

        when(photoRepository.save(photo)).thenReturn(photo);

        assertThrows(UnauthorizedException.class,()->photoService.updatePhoto(1L,photoRequest,userPrincipal));
    }

    @Test
    void addPhoto_success(){
        PhotoResponse photoResponse2 = new PhotoResponse(null,"Titulo guapo cambiado",null,"algo",4L);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));
        Photo newPhoto = new Photo();
        newPhoto.setAlbum(album);
        newPhoto.setTitle(photoRequest.getTitle());
        newPhoto.setThumbnailUrl(photoRequest.getThumbnailUrl());
        newPhoto.setUrl(photoRequest.getUrl());

        when(photoRepository.save(newPhoto)).thenReturn(newPhoto);

        assertEquals(photoResponse2, photoService.addPhoto(photoRequest,userPrincipal));
    }


    @Test
    void addPhoto_throwResourceNotFoundException(){
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(404L)).thenReturn(Optional.of(album));
        Photo newPhoto = new Photo();
        newPhoto.setAlbum(album);
        newPhoto.setTitle(photoRequest.getTitle());
        newPhoto.setThumbnailUrl(photoRequest.getThumbnailUrl());
        newPhoto.setUrl(photoRequest.getUrl());

        when(photoRepository.save(newPhoto)).thenReturn(newPhoto);

        assertThrows(ResourceNotFoundException.class,()->photoService.addPhoto(photoRequest,userPrincipal));
    }


    @Test
    void addPhoto_throwUnauthorizedException(){
        List<Role> roles = Arrays.asList(rolUser);
        User usernew = new User();
        usernew.setId(4L);
        usernew.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(usernew.getId())
                .authorities(usernew.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));
        Photo newPhoto = new Photo();
        newPhoto.setAlbum(album);
        newPhoto.setTitle(photoRequest.getTitle());
        newPhoto.setThumbnailUrl(photoRequest.getThumbnailUrl());
        newPhoto.setUrl(photoRequest.getUrl());

        when(photoRepository.save(newPhoto)).thenReturn(newPhoto);

        assertThrows(UnauthorizedException.class,()->photoService.addPhoto(photoRequest,userPrincipal));
    }




    @Test
    void deletePhoto_success(){
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        assertEquals(apiResponse, photoService.deletePhoto(photo.getId(),userPrincipal));

    }


    @Test
    void deletePhoto_throwResourceNotFoundException(){
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(photoRepository.findById(404L)).thenReturn(Optional.of(photo));
        assertThrows(ResourceNotFoundException.class,()->photoService.deletePhoto(photo.getId(),userPrincipal));
    }

    @Test
    void deletePhoto_throwUnauthorizedException(){
        List<Role> roles = Arrays.asList(rolUser);
        User usernew = new User();
        usernew.setId(4L);
        usernew.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(usernew.getId())
                .authorities(usernew.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        assertThrows(UnauthorizedException.class,()->photoService.deletePhoto(photo.getId(),userPrincipal));
    }

    @Test
    void getAllPhotosByAlbum_success(){

        when(photoRepository.findByAlbumId(album.getId(),pageable)).thenReturn(pageResultPhoto);

        List<PhotoResponse> photoResponses = new ArrayList<>(pageResultPhoto.getContent().size());

        for (Photo photo : pageResultPhoto.getContent()) {
            photoResponses.add(new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                    photo.getThumbnailUrl(), photo.getAlbum().getId()));
        }

        assertEquals(result, photoService.getAllPhotosByAlbum(album.getId(),1,1));
    }



}
