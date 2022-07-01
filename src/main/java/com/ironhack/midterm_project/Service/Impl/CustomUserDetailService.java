package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Model.Users.User;
import com.ironhack.midterm_project.Repository.UserRepository;
import com.ironhack.midterm_project.Security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(s);
        if(!optionalUser.isPresent()){
            throw new UsernameNotFoundException("User not found");
        }
        CustomUserDetails customUserDetails =new CustomUserDetails(optionalUser.get());

        return  customUserDetails;
    }
}

