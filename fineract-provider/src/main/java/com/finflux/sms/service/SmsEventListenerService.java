package com.finflux.sms.service;

import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;

public interface SmsEventListenerService {

    public void sendMessgeProcessForLoanTransaction(final LoanTransaction loanTransaction, String messageTemplate);
}