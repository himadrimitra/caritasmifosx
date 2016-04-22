package com.finflux.risk.existingloans.service;

import java.util.Collection;


import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;


public interface ExistingLoanStatusDropDownReadPlatformService {
    Collection<LoanStatusEnumData> statusTypeOptions();


}
