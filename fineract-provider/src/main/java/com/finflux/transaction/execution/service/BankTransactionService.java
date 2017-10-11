package com.finflux.transaction.execution.service;

import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.BankTransactionEntityType;
import com.finflux.transaction.execution.data.BankTransactionRequest;
import com.finflux.transaction.execution.provider.BankTransferService;

/**
 * Created by dhirendra on 23/11/16.
 */
public interface BankTransactionService {

        boolean validateAccount(Long beneficiary);

        BankTransactionDetail transactionEntry(Long externalPaymentServiceId, BankTransactionRequest transactionRequest );

//        AccountTransactionDetail initiateTransaction(Long entityId, Integer entityTypeId, Long entityTxnId);

        List<BankTransactionDetail> getAllTransaction(BankTransactionEntityType entityType, Long entityId);

        BankTransactionDetail getTransactionDetail(Long transactionId);

        BankTransferService getBankTransferService(Long externalServiceId);

        List<EnumOptionData> geetSupportedTransfers(Long transactionId);
}
