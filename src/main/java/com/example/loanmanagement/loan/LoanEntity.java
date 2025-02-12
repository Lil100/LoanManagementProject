package com.example.loanmanagement.loan;

import com.example.loanmanagement.customer.CustomerEntity;
import com.example.loanmanagement.user.UserEntity;
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
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) // Foreign key
    private CustomerEntity customer;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private int repaymentPeriodInMonths; // Number of months

    @Column(nullable = false)
    private String repaymentFrequency; // e.g., Monthly, Weekly, Daily

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING"; // Default status: PENDING

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @PrePersist
    public void calculateEndDate() {
        this.endDate = this.startDate.plusMonths(this.repaymentPeriodInMonths);
    }
}
