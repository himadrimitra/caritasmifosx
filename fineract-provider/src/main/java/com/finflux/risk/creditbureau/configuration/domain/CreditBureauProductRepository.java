package com.finflux.risk.creditbureau.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditBureauProductRepository extends JpaRepository<CreditBureauProduct, Long>,
        JpaSpecificationExecutor<CreditBureauProduct> {
}