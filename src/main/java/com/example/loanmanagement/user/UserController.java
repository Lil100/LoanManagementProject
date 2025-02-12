package com.example.loanmanagement.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService customerService;

    public UserController(UserService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserEntity customerEntity) {
        try {
            customerService.signUp(customerEntity);
            return ResponseEntity.ok("User signed up successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body("User already exists");
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody UserEntity customerEntity) {
        try {
            String token = customerService.signIn(customerEntity);
            return ResponseEntity.ok("User successfully signed in! Token: " + token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

}
