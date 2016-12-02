package com.finflux.transaction.execution.provider;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.transaction.execution.data.BankTransactionResponse;
import com.finflux.transaction.execution.data.TransferType;

import java.math.BigDecimal;

/**
 * Created by dhirendra on 23/08/16.
 */
public interface BankTransferService {

    BankTransactionResponse doTransaction(String internalTxnId, BigDecimal amount, String reason, BankAccountDetailData debitAccount,
                                          BankAccountDetailData beneficiaryAccount, TransferType transferType, String debitParticulars, String debitremarks,
                                          String beneficiaryParticulars, String beneficiaryRemarks);

    BankTransactionResponse getTransactionStatus(String internalTxnId, String referenceNumber, String makerId, String checkerId,
                                                 String approverId);

    void getStatus(String externalTxnId);

}
