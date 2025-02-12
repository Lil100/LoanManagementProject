package com.example.loanmanagement.user;

import com.example.loanmanagement.CustomerUserDetailService;
import com.example.loanmanagement.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerUserDetailService customerUserDetailService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository customerRepository, JwtUtil jwtUtil,
                       CustomerUserDetailService customerUserDetailService,
                       AuthenticationManager authenticationManager) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.customerUserDetailService = customerUserDetailService;
        this.authenticationManager = authenticationManager;
    }

    // ✅ Sign Up (Register Customer)
    public void signUp(UserEntity customerEntity) {
        Optional<UserEntity> existingCustomer = customerRepository.findByEmail(customerEntity.getEmail());
        if (existingCustomer.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Hash password before saving
        String encodedPassword = passwordEncoder.encode(customerEntity.getPassword());
        System.out.println("Encoded Password: " + encodedPassword); // 🔍 Debugging
        customerEntity.setPassword(encodedPassword);

        customerRepository.save(customerEntity);
    }


    // ✅ Sign In (Authenticate & Generate Token)
    public String signIn(UserEntity customerEntity) {
        UserEntity customer = customerRepository.findByEmail(customerEntity.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User found: " + customer.getEmail()); // Debug log

        // Validate password
        if (!passwordEncoder.matches(customerEntity.getPassword(), customer.getPassword())) {
            System.out.println("Password mismatch for user: " + customer.getEmail()); // Debug log
            throw new BadCredentialsException("Invalid username or password");
        }

        try {
            // Authenticate user using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customer.getEmail(), customerEntity.getPassword()));

            // Load user details and generate a token
            UserDetails userDetails = customerUserDetailService.loadUserByUsername(customer.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            System.out.println("Authentication successful. Token generated."); // Debug log
            return token;
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage()); // Debug log
            throw new RuntimeException("Invalid username or password");
        }
    }
}
