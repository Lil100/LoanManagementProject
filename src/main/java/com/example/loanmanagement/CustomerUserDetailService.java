package com.example.loanmanagement;

import com.example.loanmanagement.customer.CustomerEntity;
import com.example.loanmanagement.customer.CustomerRepository;
import com.example.loanmanagement.user.UserEntity;
import com.example.loanmanagement.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailService implements UserDetailsService {

    private final CustomerRepository userRepository;
    private final UserRepository customerRepository;

    public CustomerUserDetailService(CustomerRepository userRepository, UserRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First, check if the email belongs to an admin/staff user
        CustomerEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword()) // Ensure this is encoded
                    .roles("ADMIN") // Set the appropriate role
                    .build();
        }

        // If not found, check if the email belongs to a customer
        UserEntity customer = customerRepository.findByEmail(email).orElse(null);
        if (customer != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(customer.getEmail())
                    .password(customer.getPassword()) // Ensure this is encoded
                    .roles("CUSTOMER") // Set the appropriate role
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }


}
