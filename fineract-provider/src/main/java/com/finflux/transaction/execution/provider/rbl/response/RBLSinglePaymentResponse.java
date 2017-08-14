package com.finflux.transaction.execution.provider.rbl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by dhirendra on 25/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RBLSinglePaymentResponse {

    @JsonProperty("Header")
    private Header header;

    @JsonProperty("Body")
    private Body body;

    @JsonProperty("Signature")
    private Signature signature;

    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }

    public Signature getSignature() {
        return signature;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {

        @JsonProperty("TranID")
        private String transactionId;

        @JsonProperty("Corp_ID")
        private String corpId;

        @JsonProperty("Maker_ID")
        private String makerId;

        @JsonProperty("Checker_ID")
        private String checkerId;

        @JsonProperty("Approver_ID")
        private String approverId;

        @JsonProperty("Status")
        private String status;

        @JsonProperty("Resp_cde")
        @JsonDeserialize(using = EmptyToNullObject.class)
        private String responseCode;

        @JsonProperty("Error_Cde")
        @JsonDeserialize(using = EmptyToNullObject.class)
        private String errorCode;

        @JsonProperty("Error_Desc")
        @JsonDeserialize(using = EmptyToNullObject.class)
        private String errorDescription;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getCorpId() {
            return corpId;
        }

        public void setCorpId(String corpId) {
            this.corpId = corpId;
        }

        public String getMakerId() {
            return makerId;
        }

        public void setMakerId(String makerId) {
            this.makerId = makerId;
        }

        public String getCheckerId() {
            return checkerId;
        }

        public void setCheckerId(String checkerId) {
            this.checkerId = checkerId;
        }

        public String getApproverId() {
            return approverId;
        }

        public void setApproverId(String approverId) {
            this.approverId = approverId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JsonProperty("RefNo")
        private String referenceNumber;

        @JsonProperty("UTRNo")
        private String utrNumber;

        @JsonProperty("PONum")
        private String poNumber;

        @JsonProperty("Amount")
        private String amount;

        @JsonProperty("BenIFSC")
        private String beneficiaryIFSC;

        @JsonProperty("Ben_Acct_No")
        private String beneficiaryAccountNumber;

        @JsonProperty("Txn_Time")
        private String txnTime;

        @JsonProperty("TXNSTATUS")
        private String transactionStatus;
        
        @JsonProperty("STATUSDESC")
        private String statusDescription;

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBeneficiaryIFSC() {
            return beneficiaryIFSC;
        }

        public void setBeneficiaryIFSC(String beneficiaryIFSC) {
            this.beneficiaryIFSC = beneficiaryIFSC;
        }

        public String getBeneficiaryAccountNumber() {
            return beneficiaryAccountNumber;
        }

        public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
            this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        }

        public String getTxnTime() {
            return txnTime;
        }

        public void setTxnTime(String txnTime) {
            this.txnTime = txnTime;
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

        public String getTransactionStatus() {
            return this.transactionStatus;
        }

        public void setTransactionStatus(String transactionStatus) {
            this.transactionStatus = transactionStatus;
        }

        public String getStatusDescription() {
            return this.statusDescription;
        }

        public void setStatusDescription(String statusDescription) {
            this.statusDescription = statusDescription;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Signature {

        @JsonProperty("Signature")
        private String signature;

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }
}
