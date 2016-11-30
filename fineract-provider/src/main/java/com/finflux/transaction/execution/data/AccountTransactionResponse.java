package com.finflux.transaction.execution.data;


/**
 * Created by dhirendra on 25/11/16.
 */
public class AccountTransactionResponse extends BasicHttpResponse {

        private String transactionId;
        private String referenceNumber;
        private TransactionStatus transactionStatus;

        public AccountTransactionResponse(BasicHttpResponse httpResponse, String transactionId,
                String referenceNumber, TransactionStatus transactionStatus) {
                super(httpResponse);
                this.transactionId = transactionId;
                this.referenceNumber = referenceNumber;
                this.transactionStatus = transactionStatus;
        }
}
