package com.finflux.reconcilation.bank.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankRepository extends JpaRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {

}
