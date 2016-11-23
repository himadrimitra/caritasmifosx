/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.ChangedTransactionDetail;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bankstatement.domain.BankStatementDetails;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepository;

@Service
public class BankStatementTransactionWriteServiceImpl implements BankStatementTransactionWriteService, BusinessEventListner{
	
    private final BusinessEventNotifierService businessEventNotifierService;
    private final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService;
    private final BankStatementDetailsRepository bankStatementDetailsRepository;
    
    @Autowired
    public BankStatementTransactionWriteServiceImpl(final BusinessEventNotifierService businessEventNotifierService,
    		final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService,
    		final BankStatementDetailsRepository bankStatementDetailsRepository) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.bankStatementDetailsReadPlatformService = bankStatementDetailsReadPlatformService;
        this.bankStatementDetailsRepository = bankStatementDetailsRepository;
        
    }
    
    @PostConstruct
    public void registerForNotification() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADD_CHARGE,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPLY_OVERDUE_CHARGE,
                new BankStatementDetailEventListner());        
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CLOSE,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_INTEREST_RECALCULATION,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UNDO_WRITTEN_OFF,
                new BankStatementDetailEventListner());        
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WRITTEN_OFF,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADD_SUBSIDY,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REVOKE_SUBSIDY,
                new BankStatementDetailEventListner());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WAIVE_INTEREST,
                new BankStatementDetailEventListner());
    }

	@Override
	public void updateLoanTransactionOnReversal(
			ChangedTransactionDetail changedTransactionDetail) {
		List<BankStatementDetails> bankStatementDetailList = new ArrayList<>();
		for (Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
			BankStatementDetails bankStatementDetails = this.bankStatementDetailsRepository.getBankStatementDetailsByLoanTransction(mapEntry.getKey());
			if(bankStatementDetails != null){
				bankStatementDetails.setLoanTransaction(mapEntry.getValue());
				bankStatementDetailList.add(bankStatementDetails);
			}
        }
		this.bankStatementDetailsRepository.save(bankStatementDetailList);
		
	}
	
	private class BankStatementDetailEventListner  implements BusinessEventListner {

		@Override
		public void businessEventToBeExecuted(
				Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void businessEventWasExecuted(
				Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			ChangedTransactionDetail changedTransactionDetail = (ChangedTransactionDetail) businessEventEntity.get(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL);
					if(changedTransactionDetail != null){
						updateLoanTransactionOnReversal(changedTransactionDetail);					
					}
		}
		
	}

	@Override
	public void businessEventToBeExecuted(
			Map<BUSINESS_ENTITY, Object> businessEventEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void businessEventWasExecuted(
			Map<BUSINESS_ENTITY, Object> businessEventEntity) {
		// TODO Auto-generated method stub
		
	}
}
