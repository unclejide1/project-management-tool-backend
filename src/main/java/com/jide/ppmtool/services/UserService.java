package com.jide.ppmtool.services;

import com.jide.ppmtool.exceptions.UsernameAlreadyExistsException;
import com.jide.ppmtool.model.User;
import com.jide.ppmtool.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User createUser(User newUser){
        try{
            //1st encrypt the password with bcrypt
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

            //ensure that the username is unique
            newUser.setUsername(newUser.getUsername());
            newUser.setConfirmPassword("");
            //ensure that the passwords match
            //don't persist the confirm password

            return userRepository.save(newUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
           throw new UsernameAlreadyExistsException("username '" + newUser.getUsername() +  e.getMessage() + "' already exists");
        }
    }

}
