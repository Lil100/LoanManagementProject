package com.example.loanmanagement.loan;

import com.example.loanmanagement.customer.CustomerEntity;
import com.example.loanmanagement.customer.CustomerRepository;
import com.example.loanmanagement.repaymentschedule.LoanRepaymentScheduleEntity;
import com.example.loanmanagement.repaymentschedule.LoanRepaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanRepaymentScheduleService repaymentScheduleService;

    // ✅ Apply for a Loan
    public LoanEntity applyForLoan(Long customerId, BigDecimal principalAmount, BigDecimal interestRate,
                                   int repaymentPeriodInMonths, String repaymentFrequency) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        // Ensure the customer does not have an active loan
        if (hasActiveLoan(customerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer already has an active loan");
        }

        LoanEntity loan = LoanEntity.builder()
                .customer(customer)
                .principalAmount(principalAmount)
                .interestRate(interestRate)
                .repaymentPeriodInMonths(repaymentPeriodInMonths)
                .repaymentFrequency(repaymentFrequency)
                .startDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .build();

        return loanRepository.save(loan);
    }

    // ✅ Check if Customer has an Active Loan
    public boolean hasActiveLoan(Long customerId) {
        return loanRepository.existsByCustomerIdAndStatus(customerId, LoanStatus.PENDING);
    }

    // ✅ Approve or Reject Loan
    public LoanEntity updateLoanStatus(Long loanId, LoanStatus status) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        // Only allow status changes if the loan is still pending
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending loans can be updated");
        }

        loan.setStatus(status);

        if (status == LoanStatus.APPROVED) {
            repaymentScheduleService.generateRepaymentSchedule(loan.getId());
        }

        return loanRepository.save(loan);
    }

    // ✅ Get Loans for a Specific Customer
    public List<LoanEntity> getLoansByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    // ✅ Get Loan by ID
    public LoanEntity getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }


}
