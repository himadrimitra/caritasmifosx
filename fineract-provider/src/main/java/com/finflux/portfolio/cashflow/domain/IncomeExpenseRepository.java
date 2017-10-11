package com.finflux.portfolio.cashflow.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncomeExpenseRepository extends JpaRepository<IncomeExpense, Long>, JpaSpecificationExecutor<IncomeExpense> {

}