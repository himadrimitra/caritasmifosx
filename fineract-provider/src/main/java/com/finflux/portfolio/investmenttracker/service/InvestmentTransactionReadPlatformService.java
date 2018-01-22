package com.finflux.portfolio.investmenttracker.service;

import java.util.List;

import com.finflux.portfolio.investmenttracker.data.InvestmentSavingsTransactionData;
import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionData;

public interface InvestmentTransactionReadPlatformService {

    List<InvestmentTransactionData> findByAccountId(Long accountId);
    
    List<InvestmentSavingsTransactionData> findByInvestmentIdAndSavingsId(Long accountId, Long savingsId);
    
    InvestmentSavingsTransactionData findBySavingsTransactionId(Long savingTransactionId);
}
