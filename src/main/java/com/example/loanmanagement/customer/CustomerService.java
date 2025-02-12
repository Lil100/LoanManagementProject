package com.example.loanmanagement.customer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository userRepository;

    // Only one repository is needed for the same entity
    public CustomerService(CustomerRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register a new user
    public void registerUserEntity(CustomerEntity userEntity) {
        userRepository.save(userEntity);
    }

    // Get all customers
    public List<CustomerEntity> getAllCustomers() {
        return userRepository.findAll();
    }

    // Get a customer by their ID
    public Optional<CustomerEntity> getCustomerById(Long id) {
        return userRepository.findById(id);
    }

    // Update an existing customer
    public void updateCustomer(Long id, CustomerEntity updatedCustomer) {
        Optional<CustomerEntity> existingCustomer = userRepository.findById(id);
        if (existingCustomer.isPresent()) {
            CustomerEntity userEntity = existingCustomer.get();
            userEntity.setEmail(updatedCustomer.getEmail());
            userEntity.setPhoneNumber(updatedCustomer.getPhoneNumber());
            userRepository.save(userEntity);
        }
    }

    // Delete a customer by ID
    public void deleteCustomer(Long id) {
        userRepository.deleteById(id);
    }
}
