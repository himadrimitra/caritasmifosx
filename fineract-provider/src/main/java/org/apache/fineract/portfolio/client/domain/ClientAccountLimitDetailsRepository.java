package org.apache.fineract.portfolio.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientAccountLimitDetailsRepository extends JpaRepository<ClientAccountLimitDetails, Long>,
        JpaSpecificationExecutor<ClientAccountLimitDetails> {

}