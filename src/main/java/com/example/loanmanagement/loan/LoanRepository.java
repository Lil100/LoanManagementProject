package com.example.loanmanagement.loan;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

    public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
        List<LoanEntity> findByCustomerId(Long customerId);  // Get all loans for a customer
    }


