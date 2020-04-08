package com.jide.ppmtool.controller;

import com.jide.ppmtool.MapValidationErrorService;
import com.jide.ppmtool.model.User;
import com.jide.ppmtool.payload.JWTLoginResponse;
import com.jide.ppmtool.payload.LoginRequest;
import com.jide.ppmtool.security.JwtProvider;
import com.jide.ppmtool.services.UserService;
import com.jide.ppmtool.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jide.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private MapValidationErrorService mapValidationErrorService;

    private UserService userService;

    private UserValidator userValidator;

    private JwtProvider tokenProvider;

    private AuthenticationManager authenticationManager;

    @Autowired
    public UserController(MapValidationErrorService mapValidationErrorService, UserService userService,
                          UserValidator userValidator, JwtProvider tokenProvider,
                          AuthenticationManager authenticationManager) {
        this.mapValidationErrorService = mapValidationErrorService;
        this.userService = userService;
        this.userValidator = userValidator;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationError(result);
        if(errorMap != null){
            return errorMap;
        }

        Authentication authentication  = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = TOKEN_PREFIX + tokenProvider.generateToken(authentication);

        return  ResponseEntity.ok(new JWTLoginResponse(true, jwt));
    }


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody User newUser, BindingResult result){
        userValidator.validate(newUser, result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationError(result);
        if(errorMap != null){
            return errorMap;
        }


        User createdUser = userService.createUser(newUser);

        return  new ResponseEntity<>(createdUser, HttpStatus.CREATED);

    }
}
