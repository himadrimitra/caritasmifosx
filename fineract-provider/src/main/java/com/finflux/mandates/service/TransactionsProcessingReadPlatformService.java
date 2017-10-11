package com.finflux.mandates.service;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.MandatesSummaryData;
import org.apache.fineract.infrastructure.core.service.Page;

import java.util.Collection;
import java.util.Date;

public interface TransactionsProcessingReadPlatformService {

        Collection<MandateTransactionsData> retrieveRecentFailedTransactions();

        Collection<MandateTransactionsData> retrieveRequestStatusTransactions(MandatesProcessData processData);

        MandateTransactionsData findOneByLoanAccountNoAndInprocessStatus(String reference);

        Collection<MandatesSummaryData> retrieveTransactionSummary(Long office, Boolean includeChild, Date fromDate, Date toDate);

        Page<MandateTransactionsData> retrieveAllTransactions(Long office, Boolean includeChild, Date fromDate, Date toDate, Integer offset, Integer limit);
}
