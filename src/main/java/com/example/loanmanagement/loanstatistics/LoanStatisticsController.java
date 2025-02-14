package com.example.loanmanagement.loanstatistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class LoanStatisticsController {

    private final LoanStatisticsService loanStatisticsService;

    // Get overall loan statistics (all customers)
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getLoanStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLoansDisbursed", loanStatisticsService.getTotalLoansDisbursed());
        stats.put("totalLoansRepaid", loanStatisticsService.getTotalLoansRepaid());
        stats.put("totalOutstandingBalance", loanStatisticsService.getTotalOutstandingBalance());
        stats.put("repaymentCompletionRate", loanStatisticsService.getRepaymentCompletionRate());

        return ResponseEntity.ok(stats);
    }

    // Get loan statistics by loan ID
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<Map<String, Object>> getLoanStatisticsByLoanId(@PathVariable Long loanId) {
        Map<String, Object> loanStats = new HashMap<>();
        loanStats.put("loanId", loanId);
        loanStats.put("totalLoanTaken", loanStatisticsService.getTotalLoanTakenByLoanId(loanId));
        loanStats.put("totalAmountRepaid", loanStatisticsService.getTotalAmountRepaidByLoanId(loanId));
        loanStats.put("remainingBalance", loanStatisticsService.getRemainingBalanceByLoanId(loanId));

        return ResponseEntity.ok(loanStats);
    }
}
