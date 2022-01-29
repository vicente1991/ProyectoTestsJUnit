package com.sopromadze.blogapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


public interface CustomUserDetailsService {

	UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException;

	UserDetails loadUserById(Long id);

}