package com.finflux.portfolio.client.cashflow.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientIncomeExpenseRepository extends JpaRepository<ClientIncomeExpense, Long>,
        JpaSpecificationExecutor<ClientIncomeExpense> {
}