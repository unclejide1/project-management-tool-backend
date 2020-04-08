package com.jide.ppmtool.services;

import com.jide.ppmtool.model.User;
import com.jide.ppmtool.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByUsername(username);
        if(foundUser == null){
            throw new UsernameNotFoundException("User not Found");
        }
        return foundUser;
    }

    @Transactional
    public User loadUserById(Long id){
        User loadedUser = userRepository.getById(id);
        if(loadedUser == null){
            throw new UsernameNotFoundException("User not Found");
        }
        return loadedUser;

    }
}
