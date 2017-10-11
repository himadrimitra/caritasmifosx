package com.finflux.transaction.execution.service;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.exception.BankAccountDetailNotFoundException;
import com.finflux.portfolio.bank.service.BankAccountDetailsReadService;
import com.finflux.transaction.execution.data.BankTransactionEntityType;
import com.finflux.transaction.execution.data.BankTransactionRequest;

@Component
public class LoanBankTransactionHandler {

    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final LoanTransactionRepositoryWrapper loanTransactionRepositoryWrapper;
    private final BankTransactionService bankTransactionService;
    private final BankAccountDetailsReadService bankAccountDetailsReadService;

    @Autowired
    public LoanBankTransactionHandler(final LoanRepositoryWrapper loanRepositoryWrapper,
									  final LoanTransactionRepositoryWrapper loanTransactionRepositoryWrapper,
									  final BankTransactionService bankTransactionService,
									  final BankAccountDetailsReadService bankAccountDetailsReadService){
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.loanTransactionRepositoryWrapper = loanTransactionRepositoryWrapper;
        this.bankTransactionService = bankTransactionService;
        this.bankAccountDetailsReadService = bankAccountDetailsReadService;
    }

    public void createBankTransferByEntity(final Long entityId, final Long entityTxnId) {
        Loan loan = loanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
        LoanTransaction loanTransaction = loanTransactionRepositoryWrapper.findOneWithNotFoundDetection(entityTxnId);
        createBankTransfer(loan, loanTransaction);
    }

    public void createBankTransfer(final Loan loan, final LoanTransaction loanTransaction) {
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
                String reason = "LT"+entityTransactionId;
                BankTransactionRequest bankTransactionRequest = new BankTransactionRequest(loan.getClient(),fromAccount, toAccount,
                        BankTransactionEntityType.LOANS.getValue(), entityId, entityTransactionId, amount,reason);
                this.bankTransactionService.transactionEntry(externalServiceId, bankTransactionRequest);
            }
        }

    }

}
