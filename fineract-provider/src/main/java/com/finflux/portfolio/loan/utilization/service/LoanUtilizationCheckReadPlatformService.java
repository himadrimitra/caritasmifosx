package com.finflux.portfolio.loan.utilization.service;

import java.math.BigDecimal;
import java.util.Collection;

import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckData;
import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckTemplateData;

public interface LoanUtilizationCheckReadPlatformService {

    Collection<LoanUtilizationCheckTemplateData> retrieveGroupUtilizationchecksTemplate(final Long groupId);

    Collection<LoanUtilizationCheckTemplateData> retrieveCenterUtilizationchecksTemplate(final Long centerId);

    Collection<LoanUtilizationCheckData> retrieveAll(final Long loanId);

    LoanUtilizationCheckData retrieveOne(final Long loanId, final Long utilizationCheckId);

    Collection<LoanUtilizationCheckData> retrieveCenterLoanUtilizationchecks(final Long centerId);

    Collection<LoanUtilizationCheckData> retrieveGroupLoanUtilizationchecks(final Long groupId);
    
    BigDecimal retrieveUtilityAmountByLoanId(final Long loanId);
}