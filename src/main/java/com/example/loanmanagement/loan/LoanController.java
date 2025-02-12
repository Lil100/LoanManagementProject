package com.example.loanmanagement.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanEntity> applyForLoan(@RequestBody Map<String, Object> request) {
        Long customerId = Long.valueOf(request.get("customerId").toString());
        BigDecimal principalAmount = new BigDecimal(request.get("principalAmount").toString());
        BigDecimal interestRate = new BigDecimal(request.get("interestRate").toString());
        int repaymentPeriod = Integer.parseInt(request.get("repaymentPeriodInMonths").toString());
        String repaymentFrequency = request.get("repaymentFrequency").toString();

        LoanEntity loan = loanService.applyForLoan(customerId, principalAmount, interestRate, repaymentPeriod, repaymentFrequency);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanEntity> getLoanById(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanEntity>> getLoansByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomer(customerId));
    }
}
