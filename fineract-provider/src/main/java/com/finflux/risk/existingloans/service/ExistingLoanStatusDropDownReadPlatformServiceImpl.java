package com.finflux.risk.existingloans.service;

import java.util.List;

import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.springframework.stereotype.Service;

@Service
public class ExistingLoanStatusDropDownReadPlatformServiceImpl implements ExistingLoanStatusDropDownReadPlatformService{

    @Override
    public List<LoanStatusEnumData> statusTypeOptions() {
        // TODO Auto-generated method stub
        return LoanEnumerations.statusType(LoanStatus.values());
    }

}
