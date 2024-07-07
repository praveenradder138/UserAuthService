package com.project.userauthservice.controllers;

import com.project.userauthservice.dtos.SignInRequestDto;
import com.project.userauthservice.dtos.SignUpRequestDto;
import com.project.userauthservice.dtos.UserDto;
import com.project.userauthservice.exceptions.*;
import com.project.userauthservice.models.User;
import com.project.userauthservice.services.UserService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/")
public class UserController {

    UserService userService;
    UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) throws ExistingUserException {
        User user=userService.signup(signUpRequestDto.getName(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
        return ResponseEntity.ok(UserDto.from(user));

    }

    @GetMapping("/verificationEmail")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam String email) throws MessagingException {
        userService.sendVerificationEmail(email);
        return ResponseEntity.ok("Verification email sent successfully.");
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<UserDto> verifyEmail(@RequestParam String token) throws EmailVerificationTokenException {
        User user=userService.verifyEmail(token);
        return ResponseEntity.ok(UserDto.from(user));
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody SignInRequestDto signInRequestDto) throws NotExistingUserException, IncorrectPasswordException, UnverifiedEmailException {
        String jwtToken=userService.signin(signInRequestDto.getEmail(), signInRequestDto.getPassword());
        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<String> validateToken() throws EmailVerificationTokenException {
        return ResponseEntity.ok("Token successfully validated. You are authenticated.");
    }


}
