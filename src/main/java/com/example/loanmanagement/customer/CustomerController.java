package com.example.loanmanagement.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService userService) {
        this.customerService = userService;
    }


    @PostMapping("/customers")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerEntity userEntity) {
        customerService.registerUserEntity(userEntity);
        return ResponseEntity.ok("Customer created successfully");
    }


    @GetMapping("/customers")
    public ResponseEntity<List<CustomerEntity>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    @GetMapping("/customers/{id}")
    public ResponseEntity<Optional<CustomerEntity>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }


    @PutMapping("/customers/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable Long id, @RequestBody CustomerEntity userEntity) {
        customerService.updateCustomer(id, userEntity);
        return ResponseEntity.ok("Customer updated successfully");
    }


    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }
}
