/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.transaction.execution.service;

import java.util.List;

import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.data.BankTransactionEntityType;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepository;

@Service
public class BankTransactionLoanActionsValidationServiceImpl implements BankTransactionLoanActionsValidationService {

    private final BankAccountTransactionRepository bankTransactionRepository;

    @Autowired
    public BankTransactionLoanActionsValidationServiceImpl(final BankAccountTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Override
    public void validateForInactiveBankTransactions(Long loanId, List<Integer> statusList) {

        Integer entityType = BankTransactionEntityType.LOANS.getValue();
        Long activeTransactionCount = this.bankTransactionRepository.countByEntityTypeAndEntityIdAndStatusIsIn(entityType, loanId,
                statusList);
        if (activeTransactionCount > 0) {
            final String globalisationMessageCode = "all.transactions.are.not.in.reject.or.closed.status";
            final String defaultUserMessage = "Can not undo disbursal of loan, as transactions are found not in reject, retried or closed status";
            throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, loanId);

        }
    }

    private void throwGeneralPlatformDomainRuleException(final String globalisationMessageCode, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, defaultUserMessageArgs);
    }

}
