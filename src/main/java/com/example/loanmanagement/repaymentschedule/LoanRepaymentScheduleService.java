package com.example.loanmanagement.repaymentschedule;

import com.example.loanmanagement.loan.LoanEntity;
import com.example.loanmanagement.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanRepaymentScheduleService {

    private final LoanRepaymentScheduleRepository loanRepaymentScheduleRepository;
    private final LoanRepository loanRepository;

//    to check if the schedule exists or not
    public List<LoanRepaymentScheduleEntity> getOrGenerateRepaymentSchedule(Long loanId) {
        List<LoanRepaymentScheduleEntity> existingSchedule = loanRepaymentScheduleRepository.findByLoanId(loanId);

        // If schedule exists, return it
        if (!existingSchedule.isEmpty()) {
            return existingSchedule;
        }

        // If schedule does not exist, generate and return a new one
        return generateRepaymentSchedule(loanId);
    }

    /**
     * Generates a new repayment schedule for a loan.
     */
    public List<LoanRepaymentScheduleEntity> generateRepaymentSchedule(Long loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal monthlyInterestRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        int repaymentMonths = loan.getRepaymentPeriodInMonths();

        // EMI Formula: EMI = (P × r × (1 + r)^n) / ((1 + r)^n - 1)
        BigDecimal emi = principal.multiply(monthlyInterestRate)
                .multiply((BigDecimal.ONE.add(monthlyInterestRate)).pow(repaymentMonths))
                .divide((BigDecimal.ONE.add(monthlyInterestRate)).pow(repaymentMonths).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        List<LoanRepaymentScheduleEntity> scheduleList = new ArrayList<>();
        LocalDate dueDate = loan.getStartDate();
        BigDecimal remainingBalance = principal;

        for (int i = 1; i <= repaymentMonths; i++) {
            BigDecimal interestComponent = remainingBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalComponent = emi.subtract(interestComponent).setScale(2, RoundingMode.HALF_UP);
            remainingBalance = remainingBalance.subtract(principalComponent);

            LoanRepaymentScheduleEntity schedule = LoanRepaymentScheduleEntity.builder()
                    .loan(loan)
                    .dueDate(dueDate)
                    .installmentAmount(emi)
                    .principalComponent(principalComponent)
                    .interestComponent(interestComponent)
                    .remainingBalance(remainingBalance)
                    .status("PENDING")
                    .build();

            scheduleList.add(schedule);
            dueDate = dueDate.plusMonths(1);
        }

        return loanRepaymentScheduleRepository.saveAll(scheduleList);
    }

    /**
     * Fetch repayment schedule for a loan by loanId.
     */
    public List<LoanRepaymentScheduleEntity> getScheduleByLoan(Long loanId) {
        return loanRepaymentScheduleRepository.findByLoanId(loanId);
    }

    /**
     * Create a new repayment schedule entry.
     */
    public LoanRepaymentScheduleEntity createRepaymentSchedule(LoanRepaymentScheduleEntity repaymentSchedule) {
        return loanRepaymentScheduleRepository.save(repaymentSchedule);
    }

    /**
     * Update the status of an existing repayment schedule entry.
     */
    public LoanRepaymentScheduleEntity updateRepaymentScheduleStatus(Long id, LoanRepaymentScheduleEntity repaymentSchedule) {
        Optional<LoanRepaymentScheduleEntity> existingSchedule = loanRepaymentScheduleRepository.findById(id);
        if (existingSchedule.isPresent()) {
            LoanRepaymentScheduleEntity schedule = existingSchedule.get();
            schedule.setStatus(repaymentSchedule.getStatus());  // Update status (e.g., PENDING, PAID)
            return loanRepaymentScheduleRepository.save(schedule);
        }
        throw new IllegalArgumentException("Repayment schedule not found for id: " + id);
    }

    /**
     * Delete a repayment schedule entry by ID.
     */
    public void deleteRepaymentSchedule(Long id) {
        loanRepaymentScheduleRepository.deleteById(id);
    }
}
