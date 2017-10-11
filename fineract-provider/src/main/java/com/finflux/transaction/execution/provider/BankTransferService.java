package com.finflux.transaction.execution.provider;

import java.math.BigDecimal;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.transaction.execution.data.BankTransactionResponse;
import com.finflux.transaction.execution.data.TransferType;

/**
 * Created by dhirendra on 23/08/16.
 */
public interface BankTransferService {

    BankTransactionResponse doTransaction(Long internalTxnId, String internalTxnReferenceId, BigDecimal amount, String reason, BankAccountDetailData debitAccount,
                                          BankAccountDetailData beneficiaryAccount, TransferType transferType, String debitParticulars, String debitremarks,
                                          String beneficiaryParticulars, String beneficiaryRemarks, Long makerId, Long checkerId,
                                          Long approverId);

    BankTransactionResponse getTransactionStatus(Long internalTxnId, String internalTxnReferenceId, String referenceNumber, Long makerId, Long checkerId,
                                                 Long approverId);

    void getStatus(String externalTxnId);

}
