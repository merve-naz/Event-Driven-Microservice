package com.microservices.demo.elastic.query.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class TwitterQueryUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      //Spring'in kullanacağı UserDetails nesnesini oluşturuyo
        TwitterQueryUser user = TwitterQueryUser.builder()
                .username(username)
                .build();
        return user;
    }
}
