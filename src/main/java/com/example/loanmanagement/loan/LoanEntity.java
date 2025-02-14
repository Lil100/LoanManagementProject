package com.example.loanmanagement.loan;

import com.example.loanmanagement.customer.CustomerEntity;
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
@Table(name = "loans", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id"}, name = "unique_active_loan_per_customer")
})
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private int repaymentPeriodInMonths;

    @Column(nullable = false)
    private String repaymentFrequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @PrePersist
    public void calculateEndDate() {
        this.endDate = this.startDate.plusMonths(this.repaymentPeriodInMonths);
    }

    /**
     * Approves the loan and sets the status to APPROVED.
     */
    public void approveLoan() {
        if (this.status == LoanStatus.PENDING) {
            this.status = LoanStatus.APPROVED;
        } else {
            throw new IllegalStateException("Loan must be in PENDING state to approve.");
        }
    }

    /**
     * Rejects the loan and sets the status to REJECTED.
     */
    public void rejectLoan() {
        if (this.status == LoanStatus.PENDING) {
            this.status = LoanStatus.REJECTED;
        } else {
            throw new IllegalStateException("Loan must be in PENDING state to reject.");
        }
    }
}
