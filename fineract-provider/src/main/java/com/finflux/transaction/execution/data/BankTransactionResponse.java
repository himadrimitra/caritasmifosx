package com.finflux.transaction.execution.data;


import java.util.List;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.joda.time.DateTime;

/**
 * Created by dhirendra on 25/11/16.
 */
public class BankTransactionResponse extends BasicHttpResponse {

        private String transactionId;
        private String referenceNumber;
        private String utrNumber;
        private String poNumber;
        private TransactionStatus transactionStatus;
        private DateTime transactionTime;
        private List<ApiParameterError> validationErrors = null;

        public BankTransactionResponse(BasicHttpResponse httpResponse, String transactionId,
									   String referenceNumber, TransactionStatus transactionStatus, String utrNumber,
                                       String poNumber,DateTime transactionTime) {
                super(httpResponse);
                this.transactionId = transactionId;
                this.referenceNumber = referenceNumber;
                this.transactionStatus = transactionStatus;
                this.utrNumber = utrNumber;
                this.poNumber = poNumber;
                this.transactionTime = transactionTime;
        }
        
        public BankTransactionResponse(BasicHttpResponse httpResponse, String transactionId, TransactionStatus transactionStatus, List<ApiParameterError> validationErrors) {
            super(httpResponse);
            this.transactionId = transactionId;
            this.transactionStatus = transactionStatus;
            this.validationErrors = validationErrors;
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

        public String getUtrNumber() {
                return utrNumber;
        }

        public void setUtrNumber(String utrNumber) {
                this.utrNumber = utrNumber;
        }

        public String getPoNumber() {
                return poNumber;
        }

        public void setPoNumber(String poNumber) {
                this.poNumber = poNumber;
        }

        public DateTime getTransactionTime() {
                return transactionTime;
        }

        public List<ApiParameterError> getValidationErrors() {
            return this.validationErrors;
        }
}
