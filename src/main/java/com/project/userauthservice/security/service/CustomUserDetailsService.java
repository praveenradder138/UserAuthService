package com.project.userauthservice.security.service;

import com.project.userauthservice.models.User;
import com.project.userauthservice.repositories.UserRepository;
import com.project.userauthservice.security.models.CustomerUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional=userRepository.findByEmail(email);
        if(userOptional==null){
            throw new UsernameNotFoundException("User with email "+email+ " doesnt exist");
        }
        User user=userOptional.get();
        return new CustomerUserDetails(user);
    }
}
