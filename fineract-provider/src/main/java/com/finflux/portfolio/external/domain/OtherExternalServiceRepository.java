package com.finflux.portfolio.external.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.finflux.portfolio.bank.domain.BankAccountDetails;

public interface OtherExternalServiceRepository extends JpaRepository<OtherExternalService, Long>, JpaSpecificationExecutor<OtherExternalService> {

}
