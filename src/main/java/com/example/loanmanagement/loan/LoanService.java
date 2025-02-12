package com.example.loanmanagement.loan;

import com.example.loanmanagement.customer.CustomerEntity;
import com.example.loanmanagement.customer.CustomerRepository;
import com.example.loanmanagement.user.UserEntity;
import com.example.loanmanagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;

    public LoanEntity applyForLoan(Long customerId, BigDecimal principalAmount, BigDecimal interestRate,
                                   int repaymentPeriodInMonths, String repaymentFrequency) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LoanEntity loan = LoanEntity.builder()
                .customer(customer)
                .principalAmount(principalAmount)
                .interestRate(interestRate)
                .repaymentPeriodInMonths(repaymentPeriodInMonths)
                .repaymentFrequency(repaymentFrequency)
                .startDate(LocalDate.now())
                .status("PENDING")
                .build();

        return loanRepository.save(loan);
    }

    public List<LoanEntity> getLoansByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public LoanEntity getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }
}
