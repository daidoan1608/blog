package com.example.blog.service.Impl;

import com.example.blog.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserServiceImpl userServiceImpl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userServiceImpl.findByUsername(username);
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new User(
                userDto.getUsername(),
                userDto.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userDto.getRole().name()))
        );
    }
}
