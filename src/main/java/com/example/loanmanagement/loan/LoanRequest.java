package com.example.loanmanagement.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanRequest {
    private Long customerId;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int repaymentPeriodInMonths;
    private String repaymentFrequency;
}
