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

    @GetMapping("/summary")
    public ResponseEntity<Map<String, BigDecimal>> getLoanStatistics() {
        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put("totalLoansDisbursed", loanStatisticsService.getTotalLoansDisbursed());
        stats.put("totalLoansRepaid", loanStatisticsService.getTotalLoansRepaid());
        stats.put("totalOutstandingBalance", loanStatisticsService.getTotalOutstandingBalance());
        stats.put("repaymentCompletionRate", loanStatisticsService.getRepaymentCompletionRate());

        return ResponseEntity.ok(stats);
    }
}
