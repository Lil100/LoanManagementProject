package com.example.loanmanagement.loanstatistics;

import com.example.loanmanagement.loan.LoanEntity;
import com.example.loanmanagement.loan.LoanRepository;
import com.example.loanmanagement.repaymentschedule.LoanRepaymentScheduleEntity;
import com.example.loanmanagement.repaymentschedule.LoanRepaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanStatisticsService {

    private final LoanRepository loanRepository;
    private final LoanRepaymentScheduleRepository repaymentScheduleRepository;

    /**
     * Get total amount of loans disbursed.
     */
    public BigDecimal getTotalLoansDisbursed() {
        return loanRepository.findAll().stream()
                .map(LoanEntity::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total amount repaid across all loans.
     */
    public BigDecimal getTotalLoansRepaid() {
        return repaymentScheduleRepository.findAll().stream()
                .filter(schedule -> "PAID".equals(schedule.getStatus()))
                .map(LoanRepaymentScheduleEntity::getPrincipalComponent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total outstanding balance (unpaid amount).
     */
    public BigDecimal getTotalOutstandingBalance() {
        return repaymentScheduleRepository.findAll().stream()
                .filter(schedule -> "PENDING".equals(schedule.getStatus()))
                .map(LoanRepaymentScheduleEntity::getRemainingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get percentage of total repayment completion.
     */
    public BigDecimal getRepaymentCompletionRate() {
        BigDecimal totalDisbursed = getTotalLoansDisbursed();
        BigDecimal totalRepaid = getTotalLoansRepaid();

        if (totalDisbursed.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalRepaid.multiply(BigDecimal.valueOf(100)).divide(totalDisbursed, 2, BigDecimal.ROUND_HALF_UP);
    }
}
