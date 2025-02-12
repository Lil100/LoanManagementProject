package com.example.loanmanagement.repaymentschedule;

import com.example.loanmanagement.loan.LoanEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRepaymentScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false) // Links to LoanEntity
    private LoanEntity loan;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private BigDecimal installmentAmount;

    @Column(nullable = false)
    private BigDecimal principalComponent;

    @Column(nullable = false)
    private BigDecimal interestComponent;

    @Column(nullable = false)
    private BigDecimal remainingBalance;

    @Column(nullable = false)
    private String status; // PENDING, PAID, OVERDUE
}
