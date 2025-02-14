package com.example.loanmanagement.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndStatus(Long customerId, LoanStatus status);
}
