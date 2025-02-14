package com.example.loanmanagement.loanstatistics;

import com.example.loanmanagement.loan.LoanRepository;
import com.example.loanmanagement.repaymentschedule.LoanRepaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanStatisticsService {

    private final LoanRepository loanRepository;
    private final LoanRepaymentScheduleRepository loanRepaymentScheduleRepository;

    /**
     * Get total loans disbursed (sum of principal amounts for all loans)
     */
    public BigDecimal getTotalLoansDisbursed() {
        return loanRepository.findAll().stream()
                .map(loan -> loan.getPrincipalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total loans repaid (sum of all repaid amounts)
     */
    public BigDecimal getTotalLoansRepaid() {
        return loanRepaymentScheduleRepository.findAll().stream()
                .map(schedule -> schedule.getInstallmentAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total outstanding balance (sum of remaining balance of all loans)
     */
    public BigDecimal getTotalOutstandingBalance() {
        return loanRepaymentScheduleRepository.findAll().stream()
                .map(schedule -> schedule.getRemainingBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate the repayment completion rate
     */
    public BigDecimal getRepaymentCompletionRate() {
        BigDecimal totalLoansRepaid = getTotalLoansRepaid();
        BigDecimal totalLoansDisbursed = getTotalLoansDisbursed();

        if (totalLoansDisbursed.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // Prevent division by zero if no loans have been disbursed
        }

        return totalLoansRepaid
                .divide(totalLoansDisbursed, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Get total loan taken by a specific loanId
     */
    public BigDecimal getTotalLoanTakenByLoanId(Long loanId) {
        return loanRepository.findById(loanId)
                .map(loan -> loan.getPrincipalAmount())
                .orElse(BigDecimal.ZERO);  // Return zero if loan is not found
    }

    /**
     * Get total amount repaid by a specific loanId
     */
    public BigDecimal getTotalAmountRepaidByLoanId(Long loanId) {
        return loanRepaymentScheduleRepository.findByLoanId(loanId).stream()
                .filter(schedule -> schedule.getStatus().equals("PAID"))
                .map(schedule -> schedule.getInstallmentAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get remaining balance for a specific loanId
     */
    public BigDecimal getRemainingBalanceByLoanId(Long loanId) {
        return loanRepaymentScheduleRepository.findByLoanId(loanId).stream()
                .map(schedule -> schedule.getRemainingBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get the loan status by loanId (assuming status could be 'PENDING', 'PAID', etc.)
     */
    public String getLoanStatusByLoanId(Long loanId) {
        return loanRepaymentScheduleRepository.findByLoanId(loanId).stream()
                .filter(schedule -> schedule.getStatus().equals("PENDING"))
                .findFirst()
                .map(schedule -> "PENDING")
                .orElse("PAID");
    }
}
