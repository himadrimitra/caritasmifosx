package com.finflux.portfolio.loan.mandate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MandateRepository extends JpaRepository<Mandate, Long>, JpaSpecificationExecutor<Mandate> {

        Mandate findOneByIdAndLoanId(Long id, Long loanId);

        @Query("select m from Mandate m, Loan l where m.mandateStatusEnum in (101, 201, 301) and m.loanId = l.id and l.accountNumber = :accountNumber")
        Mandate findOneByLoanAccountNoAndInprocessStatus(@Param("accountNumber") String accountNumber);

        @Query("select m from Mandate m, Loan l where m.mandateStatusEnum = 400 and m.loanId = l.id and l.accountNumber = :accountNumber")
        Mandate findOneByLoanAccountNoAndActiveStatus(@Param("accountNumber") String accountNumber);
}
