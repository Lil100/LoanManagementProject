package com.example.loanmanagement.repaymentschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/repayment-schedule")
@RequiredArgsConstructor
public class LoanRepaymentScheduleController {

    private final LoanRepaymentScheduleService repaymentScheduleService;

    /**
     * Retrieve existing repayment schedule for a loan or generate a new one if none exists.
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<?> getOrGenerateRepaymentSchedule(@PathVariable Long loanId) {
        try {
            List<LoanRepaymentScheduleEntity> schedule = repaymentScheduleService.getOrGenerateRepaymentSchedule(loanId);
            return ResponseEntity.ok(schedule);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Generate a new repayment schedule for a loan if it doesn't already exist.
     */
    @PostMapping("/{loanId}")
    public ResponseEntity<?> generateRepaymentSchedule(@PathVariable Long loanId) {
        try {
            List<LoanRepaymentScheduleEntity> newSchedule = repaymentScheduleService.generateRepaymentSchedule(loanId);
            return ResponseEntity.status(201).body(newSchedule);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // Conflict: Schedule already exists
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Not Found: Loan doesn't exist
        }
    }


    /**
     * Get the outstanding balance for a specific loan.
     */
    @GetMapping("/{loanId}/outstanding-balance")
    public ResponseEntity<BigDecimal> getOutstandingBalance(@PathVariable Long loanId) {
        BigDecimal outstandingBalance = repaymentScheduleService.getOutstandingBalance(loanId);
        return ResponseEntity.ok(outstandingBalance);
    }

    /**
     * Get the total remaining balance across all loans.
     */
    @GetMapping("/total-remaining-balance")
    public ResponseEntity<BigDecimal> getTotalRemainingBalance() {
        BigDecimal totalRemainingBalance = repaymentScheduleService.getTotalRemainingBalance();
        return ResponseEntity.ok(totalRemainingBalance);
    }


    /**
     * Update the status of a repayment schedule entry.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRepaymentScheduleStatus(@PathVariable Long id, @RequestBody LoanRepaymentScheduleEntity repaymentSchedule) {
        try {
            LoanRepaymentScheduleEntity updatedSchedule = repaymentScheduleService.updateRepaymentScheduleStatus(id, repaymentSchedule);
            return ResponseEntity.ok(updatedSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Not Found: Schedule entry doesn't exist
        }
    }

    /**
     * Delete a repayment schedule entry by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRepaymentSchedule(@PathVariable Long id) {
        try {
            repaymentScheduleService.deleteRepaymentSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Not Found
        }
    }
}
