package com.example.loanmanagement.repaymentschedule;

import com.example.loanmanagement.loan.LoanEntity;
import com.example.loanmanagement.loan.LoanRepository;
import com.example.loanmanagement.loan.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanRepaymentScheduleService {

    private final LoanRepaymentScheduleRepository loanRepaymentScheduleRepository;
    private final LoanRepository loanRepository;

    /**
     * Retrieve existing repayment schedule or generate a new one if none exists.
     * Ensures schedule is only generated if loan is approved.
     */
    public List<LoanRepaymentScheduleEntity> getOrGenerateRepaymentSchedule(Long loanId) {
        List<LoanRepaymentScheduleEntity> existingSchedule = loanRepaymentScheduleRepository.findByLoanId(loanId);

        // Only generate schedule if none exists and the loan is approved
        if (existingSchedule.isEmpty()) {
            return generateRepaymentSchedule(loanId);
        }
        return existingSchedule;
    }

    /**
     * Generates a repayment schedule for a loan only if the loan is APPROVED.
     */
    public List<LoanRepaymentScheduleEntity> generateRepaymentSchedule(Long loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));

        // Ensure the loan status is APPROVED before generating the repayment schedule
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Cannot generate repayment schedule: Loan ID " + loanId + " is not approved.");
        }

        // Check if a repayment schedule already exists
        if (!loanRepaymentScheduleRepository.findByLoanId(loanId).isEmpty()) {
            throw new IllegalStateException("A repayment schedule already exists for Loan ID: " + loanId);
        }

        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal interestRate = loan.getInterestRate();
        int repaymentMonths = loan.getRepaymentPeriodInMonths();

        BigDecimal monthlyInterestRate = interestRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal emi;

        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            emi = principal.divide(BigDecimal.valueOf(repaymentMonths), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal factor = BigDecimal.ONE.add(monthlyInterestRate).pow(repaymentMonths);
            emi = principal.multiply(monthlyInterestRate).multiply(factor)
                    .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        }

        List<LoanRepaymentScheduleEntity> scheduleList = new ArrayList<>();
        LocalDate dueDate = loan.getStartDate().plusMonths(1);
        BigDecimal remainingBalance = principal;

        for (int i = 1; i <= repaymentMonths; i++) {
            BigDecimal interestComponent = remainingBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalComponent = emi.subtract(interestComponent).setScale(2, RoundingMode.HALF_UP);
            remainingBalance = remainingBalance.subtract(principalComponent).max(BigDecimal.ZERO);

            LoanRepaymentScheduleEntity schedule = LoanRepaymentScheduleEntity.builder()
                    .loan(loan)
                    .dueDate(dueDate)
                    .installmentAmount(emi)
                    .principalComponent(principalComponent)
                    .interestComponent(interestComponent)
                    .remainingBalance(remainingBalance)
                    .status(LoanStatus.PENDING) // Start with PENDING status
                    .build();

            scheduleList.add(schedule);
            dueDate = dueDate.plusMonths(1);
        }

        return loanRepaymentScheduleRepository.saveAll(scheduleList);
    }


    /**
     * Calculates total outstanding balance for a given loan.
     */
    public BigDecimal getOutstandingBalance(Long loanId) {
        return loanRepaymentScheduleRepository.findByLoanId(loanId)
                .stream()
                .map(LoanRepaymentScheduleEntity::getRemainingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates total remaining balance for all loans.
     */
    public BigDecimal getTotalRemainingBalance() {
        return loanRepository.findAll().stream()
                .map(loan -> getOutstandingBalance(loan.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * Update the status of an existing repayment schedule entry.
     */
    public LoanRepaymentScheduleEntity updateRepaymentScheduleStatus(Long id, LoanRepaymentScheduleEntity repaymentSchedule) {
        return loanRepaymentScheduleRepository.findById(id)
                .map(existingSchedule -> {
                    existingSchedule.setStatus(repaymentSchedule.getStatus());
                    return loanRepaymentScheduleRepository.save(existingSchedule);
                })
                .orElseThrow(() -> new IllegalArgumentException("Repayment schedule not found for ID: " + id));
    }

    /**
     * Delete a repayment schedule entry by ID.
     */
    public void deleteRepaymentSchedule(Long id) {
        if (!loanRepaymentScheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Repayment schedule not found for ID: " + id);
        }
        loanRepaymentScheduleRepository.deleteById(id);
    }
}
