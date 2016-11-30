package com.finflux.transaction.execution.service.provider;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.transaction.execution.data.AccountTransactionResponse;
import com.finflux.transaction.execution.data.TransferType;

/**
 * Created by dhirendra on 23/08/16.
 */
public interface BankTransferService {

    AccountTransactionResponse doTransaction(String internalTxnId, Double amount, String reason, BankAccountDetailData debitAccount,
            BankAccountDetailData beneficiaryAccount, TransferType transferType, String debitParticulars, String debitremarks,
            String beneficiaryParticulars, String beneficiaryRemarks);

    AccountTransactionResponse getTransactionStatus(String internalTxnId, String referenceNumber, String makerId, String checkerId,
            String approverId);

    void getStatus(String externalTxnId);

}
