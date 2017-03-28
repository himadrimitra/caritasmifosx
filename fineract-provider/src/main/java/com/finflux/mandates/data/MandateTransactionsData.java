package com.finflux.mandates.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.math.BigDecimal;
import java.util.Date;

public class MandateTransactionsData {

        private final Long id;
        private final Long mandateId;
        private final Long loanId;
        private final BigDecimal paymentDueAmount;
        private final Date paymentDueDate;
        private final Date requestDate;
        private final String status;
        private final Date returnProcessDate;
        private final String returnProcessReferenceId;
        private final String returnReason;
        private final String umrn;
        private final String bankAccountHolderName;
        private final String bankAccountNumber;
        private final String bankName;
        private final String branchName;
        private final String micr;
        private final String ifsc;
        private final EnumOptionData accountType;
        private final String loanAccountNo;

        public MandateTransactionsData(final Long id, final Long mandateId, final Long loanId, final BigDecimal paymentDueAmount,
                final Date paymentDueDate, final Date requestDate, final String status, final Date returnProcessDate,
                final String returnProcessReferenceId, final String returnReason, final String umrn, final String bankAccountHolderName,
                final String bankAccountNumber, final String bankName, final String branchName, final String micr, final String ifsc,
                final EnumOptionData accountType, final String loanAccountNo){

                this.id = id;
                this.mandateId = mandateId;
                this.loanId = loanId;
                this.paymentDueAmount = paymentDueAmount;
                this.paymentDueDate = paymentDueDate;
                this.requestDate = requestDate;
                this.status = status;
                this.returnProcessDate = returnProcessDate;
                this.returnProcessReferenceId = returnProcessReferenceId;
                this.returnReason = returnReason;
                this.umrn = umrn;
                this.bankAccountHolderName = bankAccountHolderName;
                this.bankAccountNumber = bankAccountNumber;
                this.bankName = bankName;
                this.branchName = branchName;
                this.micr = micr;
                this.ifsc = ifsc;
                this.accountType = accountType;
                this.loanAccountNo = loanAccountNo;

        }

        public Long getId() {
                return id;
        }

        public Long getMandateId() {
                return mandateId;
        }

        public Long getLoanId() {
                return loanId;
        }

        public BigDecimal getPaymentDueAmount() {
                return paymentDueAmount;
        }

        public Date getPaymentDueDate() {
                return paymentDueDate;
        }

        public Date getRequestDate() {
                return requestDate;
        }

        public String getStatus() {
                return status;
        }

        public Date getReturnProcessDate() {
                return returnProcessDate;
        }

        public String getReturnProcessReferenceId() {
                return returnProcessReferenceId;
        }

        public String getReturnReason() {
                return returnReason;
        }

        public String getUmrn() {
                return umrn;
        }

        public String getBankAccountHolderName() {
                return bankAccountHolderName;
        }

        public String getBankAccountNumber() {
                return bankAccountNumber;
        }

        public String getBankName() {
                return bankName;
        }

        public String getBranchName() {
                return branchName;
        }

        public String getMicr() {
                return micr;
        }

        public String getIfsc() {
                return ifsc;
        }

        public EnumOptionData getAccountType() {
                return accountType;
        }

        public String getLoanAccountNo() {
                return loanAccountNo;
        }
}
