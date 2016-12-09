package com.finflux.portfolio.bank.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankAccountDetailAssociationsRepository extends JpaRepository<BankAccountDetailAssociations, Long>,
        JpaSpecificationExecutor<BankAccountDetailAssociations> {

    BankAccountDetailAssociations findByEntityIdAndEntityTypeId(Long entityId, Integer entityTypeId);

}
