package com.finflux.mandates.data;

import java.math.BigDecimal;
import java.util.Date;

public class ProcessResponseData {
        private String reference;
        private String status;
        private String failureReason;
        private String UMRN;
        private int rowId;
        private String processStatus;
        private String processDesc;
        private BigDecimal amount;
        private Date transactionDate;

        public String getReference() {
                return reference;
        }

        public String getStatus() {
                return status;
        }

        public String getFailureReason() {
                return failureReason;
        }

        public String getUMRN() {
                return UMRN;
        }

        public int getRowId() {
                return rowId;
        }

        public String getProcessStatus() {
                return processStatus;
        }

        public String getProcessDesc() {
                return processDesc;
        }

        public BigDecimal getAmount() {
                return amount;
        }

        public Date getTransactionDate() {
                return transactionDate;
        }

        public void setProcessStatus(String status, String desc){
                this.processStatus = status;
                this.processDesc = desc;
        }

        public void setReference(String reference) {
                this.reference = reference;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public void setFailureReason(String failureReason) {
                this.failureReason = failureReason;
        }

        public void setUMRN(String UMRN) {
                this.UMRN = UMRN;
        }

        public void setRowId(int rowId) {
                this.rowId = rowId;
        }

        public void setAmount(BigDecimal amount) {
                this.amount = amount;
        }

        public void setTransactionDate(Date transactionDate) {
                this.transactionDate = transactionDate;
        }
}
