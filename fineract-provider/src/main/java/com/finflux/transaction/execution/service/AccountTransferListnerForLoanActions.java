package com.finflux.transaction.execution.service;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.exception.BankAccountDetailNotFoundException;
import com.finflux.portfolio.bank.service.BankAccountDetailsReadService;
import com.finflux.transaction.execution.data.AccountTransactionRequest;
import com.finflux.transaction.execution.data.AccountTransferEntityType;

@Component
public class AccountTransferListnerForLoanActions implements BusinessEventListner {

    private final BusinessEventNotifierService businessEventNotifierService;
    private final BankAccountDetailsReadService bankAccountDetailsReadService;
    private final AccountTransferService accountTransferService;

    @Autowired
    public AccountTransferListnerForLoanActions(final BusinessEventNotifierService businessEventNotifierService,
            final BankAccountDetailsReadService bankAccountDetailsReadService, final AccountTransferService accountTransferService) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.bankAccountDetailsReadService = bankAccountDetailsReadService;
        this.accountTransferService = accountTransferService;
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
            createBankTransfer(loan, loanTransaction);
        }

    }

    private void createBankTransfer(final Loan loan, final LoanTransaction loanTransaction) {
        if (loanTransaction.getPaymentDetail() != null) {
            PaymentType paymentType = loanTransaction.getPaymentDetail().getPaymentType();
            if (paymentType.getExternalServiceId() != null) {
                BankAccountDetailData fromAccount = this.bankAccountDetailsReadService.retrieveOneBy(
                        BankAccountDetailEntityType.PAYMENTTYPES, paymentType.getId());
                if (fromAccount == null) { throw new BankAccountDetailNotFoundException(paymentType.getId(),
                        BankAccountDetailEntityType.PAYMENTTYPES.getValue()); }

                BankAccountDetailData toAccount = this.bankAccountDetailsReadService.retrieveOneBy(BankAccountDetailEntityType.CLIENTS,
                        loan.getClientId());
                if (toAccount == null) { throw new BankAccountDetailNotFoundException(loan.getClientId(),
                        BankAccountDetailEntityType.CLIENTS.getValue()); }
                Long entityId = loan.getId();
                Long entityTransactionId = loanTransaction.getId();
                Long externalServiceId = paymentType.getExternalServiceId();
                BigDecimal amount = loanTransaction.getAmount();
                AccountTransactionRequest accountTransactionRequest = new AccountTransactionRequest(fromAccount, toAccount,
                        AccountTransferEntityType.LOANS.getValue(), entityId, entityTransactionId, amount);
                this.accountTransferService.transactionEntry(externalServiceId, accountTransactionRequest);

            }
        }

    }

}
