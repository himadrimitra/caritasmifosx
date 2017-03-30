package com.finflux.mandates.service;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;

import java.util.Collection;
import java.util.Date;

public interface TransactionsProcessingWritePlatformService {

        int addTransactionsWithRequestStatus(MandatesProcessData processData);

        void updateTransactionAsFailed(Long transactionId, String failureReason, String processReferenceId);

        void updateTransactionAsSuccess(Long transactionId, Long repaymentTransactionId, String processReferenceId);

        void updateTransactionsStatusAsInProcess(Collection<MandateTransactionsData> transactionsToProcess);
}
