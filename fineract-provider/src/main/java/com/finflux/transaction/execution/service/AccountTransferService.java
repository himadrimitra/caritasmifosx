package com.finflux.transaction.execution.service;

import com.finflux.transaction.execution.data.AccountTransactionDetail;
import com.finflux.transaction.execution.data.AccountTransactionRequest;

import java.util.List;

/**
 * Created by dhirendra on 23/11/16.
 */
public interface AccountTransferService {

        boolean validateAccount(Long beneficiary);

        AccountTransactionDetail transactionEntry(Long externalPaymentServiceId, AccountTransactionRequest transactionRequest );

        AccountTransactionDetail initiateTransaction(Long entityId, Integer entityTypeId, Long entityTxnId);

        AccountTransactionDetail refreshStatus(Long transactionId);

        List<AccountTransactionDetail> getAllTransaction(Long entityId, Integer entityTypeId);

        AccountTransactionDetail getTransactionDetail(Long entityId, Integer entityTypeId, Long entityTxnId);

}
