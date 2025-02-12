package com.example.loanmanagement.repaymentschedule;

import com.example.loanmanagement.repaymentschedule.LoanRepaymentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepaymentScheduleRepository extends JpaRepository<LoanRepaymentScheduleEntity, Long> {
    List<LoanRepaymentScheduleEntity> findByLoanId(Long loanId);
}
