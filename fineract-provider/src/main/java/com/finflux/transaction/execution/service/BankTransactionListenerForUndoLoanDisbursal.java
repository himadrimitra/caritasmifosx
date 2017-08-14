/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.transaction.execution.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.data.TransactionStatus;

@Service
public class BankTransactionListenerForUndoLoanDisbursal implements BusinessEventListner {

    private final BankTransactionLoanActionsValidationService bankTransactionLoanActionsValidationService;

    @Autowired
    public BankTransactionListenerForUndoLoanDisbursal(
            final BankTransactionLoanActionsValidationService bankTransactionLoanActionsValidationService) {
        this.bankTransactionLoanActionsValidationService = bankTransactionLoanActionsValidationService;
    }

    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
        if (loanEntity != null) {
            final Loan loan = (Loan) loanEntity;
            List<Integer> statusList = new ArrayList<>(Arrays.asList(TransactionStatus.DRAFTED.getValue(),
                    TransactionStatus.SUBMITTED.getValue(), TransactionStatus.INITIATED.getValue(), TransactionStatus.PENDING.getValue(),
                    TransactionStatus.SUCCESS.getValue(), TransactionStatus.FAILED.getValue(), TransactionStatus.ERROR.getValue()));
            Boolean isSubmitBankTransaction = false;
            this.bankTransactionLoanActionsValidationService.validateForInactiveBankTransactions(loan.getId(), statusList, isSubmitBankTransaction);
        }

    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

}
