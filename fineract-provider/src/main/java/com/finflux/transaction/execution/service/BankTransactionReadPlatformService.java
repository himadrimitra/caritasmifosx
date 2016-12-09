package com.finflux.transaction.execution.service;

import java.util.List;

import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.BankTransactionEntityType;
import com.finflux.transaction.execution.data.TransactionStatus;

/**
 * Created by dhirendra on 15/09/16.
 */
public interface BankTransactionReadPlatformService {

    BankTransactionDetail getAccountTransactionDetails(Long transactionId);

    List<BankTransactionDetail> getAccountTransactionsByEntity(BankTransactionEntityType entityType, Long entityId);

    List<BankTransactionDetail> getAccountTransactionsByStatus(TransactionStatus status);
}
