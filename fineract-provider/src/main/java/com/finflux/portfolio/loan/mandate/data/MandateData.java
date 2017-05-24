package com.finflux.portfolio.loan.mandate.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.loan.mandate.domain.AccountTypeEnum;
import com.finflux.portfolio.loan.mandate.domain.DebitFrequencyEnum;
import com.finflux.portfolio.loan.mandate.domain.DebitTypeEnum;

public class MandateData {

        private final Long id;
        private final Long loanId;
        private final String loanAccountNo;
        private final EnumOptionData mandateStatus;
        private final Date requestDate;
        private final String umrn;
        private final String bankAccountHolderName;
        private final String bankName;
        private final String branchName;
        private final String bankAccountNumber;
        private final String micr;
        private final String ifsc;
        private final EnumOptionData accountType;
        private final Date periodFromDate;
        private final Date periodToDate;
        private final Boolean periodUntilCancelled;
        private final EnumOptionData debitType;
        private final BigDecimal amount;
        private final EnumOptionData debitFrequency;
        private final Long scannedDocumentId;
        private final String scannedDocumentName;
        private final String returnReason;
        private final Date returnProcessDate;
        private final String returnProcessReferenceId;

        private final String applicantName ;
        private final String applicantMobileNo ;
        private final String applicantEmailId ;
        
        private Collection<EnumOptionData> accountTypeOptions;
        private Collection<EnumOptionData> debitTypeOptions;
        private Collection<EnumOptionData> debitFrequencyOptions;
        private Collection<EnumOptionData> scannedDocumentOptions;

        private final BankAccountDetailData bankAccountDetails ;
        
        private MandateData(final Long id,
                final Long loanId,
                final String loanAccountNo,
                final String applicantName,
                final String applicantMobileNo,
                final String applicantEmailId,
                final EnumOptionData mandateStatus,
                final Date requestDate,
                final String umrn,
                final String bankAccountHolderName,
                final String bankName,
                final String branchName,
                final String bankAccountNumber,
                final String micr,
                final String ifsc,
                final EnumOptionData accountType,
                final Date periodFromDate,
                final Date periodToDate,
                final Boolean periodUntilCancelled,
                final EnumOptionData debitType,
                final BigDecimal amount,
                final EnumOptionData debitFrequency,
                final Long scannedDocumentId,
                final String scannedDocumentName,
                final String returnReason,
                final Date returnProcessDate,
                final String returnProcessReferenceId,
                final Collection<EnumOptionData> accountTypeOptions,
                final Collection<EnumOptionData> debitTypeOptions,
                final Collection<EnumOptionData> debitFrequencyOptions,
                final Collection<EnumOptionData> scannedDocumentOptions,
                final BankAccountDetailData bankAccountDetails){

                this.id = id;
                this.loanId = loanId;
                this.loanAccountNo = loanAccountNo;
                this.applicantName = applicantName ;
                this.applicantMobileNo = applicantMobileNo ;
                this.applicantEmailId = applicantEmailId ;
                this.mandateStatus = mandateStatus;
                this.requestDate = requestDate;
                this.umrn = umrn;
                this.bankAccountHolderName = bankAccountHolderName;
                this.bankName = bankName;
                this.branchName = branchName;
                this.bankAccountNumber = bankAccountNumber;
                this.micr = micr;
                this.ifsc = ifsc;
                this.accountType = accountType;
                this.periodFromDate = periodFromDate;
                this.periodToDate = periodToDate;
                this.periodUntilCancelled = periodUntilCancelled;
                this.debitType = debitType;
                this.amount = amount;
                this.debitFrequency = debitFrequency;
                this.scannedDocumentId = scannedDocumentId;
                this.scannedDocumentName = scannedDocumentName;
                this.returnReason = returnReason;
                this.returnProcessDate = returnProcessDate;
                this.returnProcessReferenceId = returnProcessReferenceId;
                this.accountTypeOptions = accountTypeOptions;
                this.debitTypeOptions = debitTypeOptions;
                this.debitFrequencyOptions = debitFrequencyOptions;
                this.scannedDocumentOptions = scannedDocumentOptions;
                this.bankAccountDetails = bankAccountDetails ;
        }

        public static MandateData createTemplate(final Collection<EnumOptionData> scannedDocumentOptions, final LoanAccountData loan,
                final BankAccountDetailData bankAccountDetails){
                final Long id = null;
                final Long loanId = null;
                final String loanAccountNo = null;
                final String applicantName = null;
                final String applicantMobileNo = null;
                final String applicantEmailId = null;
                final EnumOptionData mandateStatus = null;
                final Date requestDate = null;
                final String umrn = null;
                final String bankAccountHolderName = null;
                final String bankName = null;
                final String branchName = null;
                final String bankAccountNumber = null;
                final String micr = null;
                final String ifsc = null;
                final EnumOptionData accountType = AccountTypeEnum.enumOptionDataFrom(AccountTypeEnum.SB.getValue());
                final Date periodFromDate = null;
                final Date periodToDate = null;
                final Boolean periodUntilCancelled = true;
                final EnumOptionData debitType = DebitTypeEnum.enumOptionDataFrom(DebitTypeEnum.MAXIMUM_AMOUNT.getValue());
                final BigDecimal amount = loan.getCalculatedEmiAmount();
                final EnumOptionData debitFrequency = DebitFrequencyEnum.enumOptionDataFrom(DebitFrequencyEnum.AS_AND_WHEN_PRESENTED.getValue());
                final Long scannedDocumentId = null;
                final String scannedDocumentName = null;
                final String returnReason = null;
                final Date returnProcessDate = null;
                final String returnProcessReferenceId = null;
                final Collection<EnumOptionData> accountTypeOptions = AccountTypeEnum.getAccountTypeOptionData();
                final Collection<EnumOptionData> debitTypeOptions = DebitTypeEnum.getDebitTypeOptionData();
                final Collection<EnumOptionData> debitFrequencyOptions = DebitFrequencyEnum.getDebitFrequencyOptionData();

                return new MandateData(id, loanId, loanAccountNo, applicantName, applicantMobileNo, applicantEmailId, mandateStatus, requestDate, umrn, bankAccountHolderName, bankName,
                        branchName, bankAccountNumber, micr, ifsc, accountType, periodFromDate, periodToDate, periodUntilCancelled,
                        debitType, amount, debitFrequency, scannedDocumentId, scannedDocumentName, returnReason, returnProcessDate,
                        returnProcessReferenceId, accountTypeOptions, debitTypeOptions, debitFrequencyOptions, scannedDocumentOptions, bankAccountDetails);
        }

        public static MandateData createTemplateFrom(final MandateData data,
                        final Collection<EnumOptionData> scannedDocumentOptions) {
                final Long id = null;
                final Long loanId = null;
                final String loanAccountNo = null;
                final String applicantName = null;
                final String applicantMobileNo = null;
                final String applicantEmailId = null;
                final EnumOptionData mandateStatus = null;
                final Date requestDate = null;
                final Long scannedDocumentId = null;
                final String scannedDocumentName = null;
                final String returnReason = null;
                final Date returnProcessDate = null;
                final String returnProcessReferenceId = null;
                final Collection<EnumOptionData> accountTypeOptions = AccountTypeEnum.getAccountTypeOptionData();
                final Collection<EnumOptionData> debitTypeOptions = DebitTypeEnum.getDebitTypeOptionData();
                final Collection<EnumOptionData> debitFrequencyOptions = DebitFrequencyEnum.getDebitFrequencyOptionData();
                final BankAccountDetailData bankAccountDetailsData = null ;
                return new MandateData(id, loanId, loanAccountNo, applicantName, applicantMobileNo, applicantEmailId, mandateStatus, requestDate, data.umrn, data.bankAccountHolderName,
                        data.bankName, data.branchName, data.bankAccountNumber, data.micr, data.ifsc, data.accountType,
                        data.periodFromDate, data.periodToDate, data.periodUntilCancelled, data.debitType, data.amount,
                        data.debitFrequency, scannedDocumentId, scannedDocumentName, returnReason, returnProcessDate,
                        returnProcessReferenceId, accountTypeOptions, debitTypeOptions, debitFrequencyOptions, scannedDocumentOptions, bankAccountDetailsData);
        }

        public static MandateData from(Long id, Long loanId, String loanAccountNo, 
                final String applicantName,
                final String applicantMobileNo,
                final String applicantEmailId,
                EnumOptionData mandateStatus, Date requestDate, String umrn,
                String bankAccountHolderName, String bankName, String branchName, String bankAccountNumber, String micr, String ifsc,
                EnumOptionData accountType, Date periodFromDate, Date periodToDate, Boolean periodUntilCancelled, EnumOptionData debitType,
                BigDecimal amount, EnumOptionData debitFrequency, Long scannedDocumentId, String scannedDocumentName, String returnReason,
                Date returnProcessDate, String returnProcessReferenceId) {

                final Collection<EnumOptionData> accountTypeOptions = null;
                final Collection<EnumOptionData> debitTypeOptions = null;
                final Collection<EnumOptionData> debitFrequencyOptions = null;
                final Collection<EnumOptionData> scannedDocumentOptions = null;
                final BankAccountDetailData bankAccountDetailsData = null ;
                return new MandateData(id, loanId, loanAccountNo, applicantName, applicantMobileNo, applicantEmailId, mandateStatus, requestDate, umrn, bankAccountHolderName, bankName,
                        branchName, bankAccountNumber, micr, ifsc, accountType, periodFromDate, periodToDate, periodUntilCancelled,
                        debitType, amount, debitFrequency, scannedDocumentId, scannedDocumentName, returnReason, returnProcessDate,
                        returnProcessReferenceId, accountTypeOptions, debitTypeOptions, debitFrequencyOptions, scannedDocumentOptions, bankAccountDetailsData);
        }

        public Long getId() {
                return id;
        }

        public Long getLoanId() {
                return loanId;
        }

        public String getLoanAccountNo() {
                return loanAccountNo;
        }

        public EnumOptionData getMandateStatus() {
                return mandateStatus;
        }

        public Date getRequestDate() {
                return requestDate;
        }

        public String getUmrn() {
                return umrn;
        }

        public String getBankAccountHolderName() {
                return bankAccountHolderName;
        }

        public String getBankName() {
                return bankName;
        }

        public String getBranchName() {
                return branchName;
        }

        public String getBankAccountNumber() {
                return bankAccountNumber;
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

        public Date getPeriodFromDate() {
                return periodFromDate;
        }

        public Date getPeriodToDate() {
                return periodToDate;
        }

        public Boolean getPeriodUntilCancelled() {
                return periodUntilCancelled;
        }

        public EnumOptionData getDebitType() {
                return debitType;
        }

        public BigDecimal getAmount() {
                return amount;
        }

        public EnumOptionData getDebitFrequency() {
                return debitFrequency;
        }

        public Long getScannedDocumentId() {
                return scannedDocumentId;
        }

        public String getReturnReason() {
                return returnReason;
        }

        public Date getReturnProcessDate() {
                return returnProcessDate;
        }

        public String getReturnProcessReferenceId() {
                return returnProcessReferenceId;
        }

        public String getApplicantName() {
            return this.applicantName;
        }
        
        public String getApplicantMobileNo() {
            return this.applicantMobileNo;
        }

        public String getApplicantEmailId() {
            return this.applicantEmailId;
        }

        public void setEnumOptions(Collection<EnumOptionData> accountTypeOptionData, Collection<EnumOptionData> debitTypeOptionData,
                Collection<EnumOptionData> debitFrequencyOptionData, Collection<EnumOptionData> documentEnumOptionData) {
                this.accountTypeOptions = accountTypeOptionData;
                this.debitTypeOptions = debitTypeOptionData;
                this.debitFrequencyOptions = debitFrequencyOptionData;
                this.scannedDocumentOptions = documentEnumOptionData;
        }
}
