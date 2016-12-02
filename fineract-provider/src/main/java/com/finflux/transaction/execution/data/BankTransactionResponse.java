package com.finflux.transaction.execution.data;


/**
 * Created by dhirendra on 25/11/16.
 */
public class BankTransactionResponse extends BasicHttpResponse {

        private String transactionId;
        private String referenceNumber;
        private TransactionStatus transactionStatus;

        public BankTransactionResponse(BasicHttpResponse httpResponse, String transactionId,
									   String referenceNumber, TransactionStatus transactionStatus) {
                super(httpResponse);
                this.transactionId = transactionId;
                this.referenceNumber = referenceNumber;
                this.transactionStatus = transactionStatus;
        }

        public String getTransactionId() {
                return transactionId;
        }

        public void setTransactionId(String transactionId) {
                this.transactionId = transactionId;
        }

        public String getReferenceNumber() {
                return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
                this.referenceNumber = referenceNumber;
        }

        public TransactionStatus getTransactionStatus() {
                return transactionStatus;
        }

        public void setTransactionStatus(TransactionStatus transactionStatus) {
                this.transactionStatus = transactionStatus;
        }
}
