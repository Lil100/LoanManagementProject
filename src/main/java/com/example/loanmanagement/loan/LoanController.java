package com.example.loanmanagement.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // ✅ Apply for a Loan
    @PostMapping("/apply")
    public ResponseEntity<LoanEntity> applyForLoan(@RequestBody LoanRequest request) {
        LoanEntity loan = loanService.applyForLoan(
                request.getCustomerId(),
                request.getPrincipalAmount(),
                request.getInterestRate(),
                request.getRepaymentPeriodInMonths(),
                request.getRepaymentFrequency()
        );
        return ResponseEntity.ok(loan);
    }

    // ✅ Get Loan by ID
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanEntity> getLoanById(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    // ✅ Get all Loans for a specific Customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanEntity>> getLoansByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomer(customerId));
    }

    // ✅ Update Loan Status (Approve/Reject)
    @PatchMapping("/{loanId}/status")
    public ResponseEntity<LoanEntity> updateLoanStatus(@PathVariable Long loanId, @RequestParam LoanStatus status) {
        return ResponseEntity.ok(loanService.updateLoanStatus(loanId, status));
    }


}
