package com.example.loanmanagement.repaymentschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repayment-schedule")
@RequiredArgsConstructor
public class LoanRepaymentScheduleController {

    private final LoanRepaymentScheduleService repaymentScheduleService;

    /**
     * Get the repayment schedule for a specific loan or generate a new one if it does not exist.
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<List<LoanRepaymentScheduleEntity>> getOrGenerateRepaymentSchedule(@PathVariable Long loanId) {
        List<LoanRepaymentScheduleEntity> existingSchedule = repaymentScheduleService.getScheduleByLoan(loanId);

        // If schedule exists, return it
        if (!existingSchedule.isEmpty()) {
            return ResponseEntity.ok(existingSchedule);
        }

        // If schedule does not exist, generate and return a new one
        List<LoanRepaymentScheduleEntity> newSchedule = repaymentScheduleService.generateRepaymentSchedule(loanId);
        return ResponseEntity.ok(newSchedule);
    }



    /**
     * Create a new repayment schedule entry.
     */
    @PostMapping
    public ResponseEntity<LoanRepaymentScheduleEntity> createRepaymentSchedule(@RequestBody LoanRepaymentScheduleEntity repaymentSchedule) {
        LoanRepaymentScheduleEntity createdSchedule = repaymentScheduleService.createRepaymentSchedule(repaymentSchedule);
        return ResponseEntity.status(201).body(createdSchedule);
    }

    /**
     * Update the status of a repayment schedule entry (e.g., mark it as PAID).
     */
    @PutMapping("/{id}")
    public ResponseEntity<LoanRepaymentScheduleEntity> updateRepaymentScheduleStatus(@PathVariable Long id, @RequestBody LoanRepaymentScheduleEntity repaymentSchedule) {
        LoanRepaymentScheduleEntity updatedSchedule = repaymentScheduleService.updateRepaymentScheduleStatus(id, repaymentSchedule);
        return ResponseEntity.ok(updatedSchedule);
    }

    /**
     * Delete a repayment schedule entry by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepaymentSchedule(@PathVariable Long id) {
        repaymentScheduleService.deleteRepaymentSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
