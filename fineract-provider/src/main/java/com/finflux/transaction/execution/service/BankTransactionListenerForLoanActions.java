package com.finflux.transaction.execution.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.bank.service.BankAccountDetailsReadService;

@Component
public class BankTransactionListenerForLoanActions implements BusinessEventListner {

    private final BusinessEventNotifierService businessEventNotifierService;
    private final BankAccountDetailsReadService bankAccountDetailsReadService;
    private final BankTransactionService bankTransactionService;
    private final LoanBankTransactionHandler loanBankTransactionHandler;

    @Autowired
    public BankTransactionListenerForLoanActions(final BusinessEventNotifierService businessEventNotifierService,
                                                 final BankAccountDetailsReadService bankAccountDetailsReadService,
                                                 final BankTransactionService bankTransactionService,
                                                 final LoanBankTransactionHandler loanBankTransactionHandler) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.bankAccountDetailsReadService = bankAccountDetailsReadService;
        this.bankTransactionService = bankTransactionService;
        this.loanBankTransactionHandler = loanBankTransactionHandler;
    }

    @PostConstruct
    public void registerForNotification() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL, this);
    }

    @Override
    public void businessEventToBeExecuted(@SuppressWarnings("unused") Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
        Object loanTransactionEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN_TRANSACTION);
        if (loanTransactionEntity != null) {
            Loan loan = (Loan) loanEntity;
            LoanTransaction loanTransaction = (LoanTransaction) loanTransactionEntity;
            loanBankTransactionHandler.createBankTransfer(loan,loanTransaction);
        }

    }
}
