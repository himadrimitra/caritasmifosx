package com.finflux.portfolio.loan.mandate.domain;

import com.finflux.portfolio.loan.mandate.api.MandateApiConstants;
import com.finflux.portfolio.loanemipacks.api.LoanEMIPacksApiConstants;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.tomcat.jni.Local;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Component
@Table(name = "f_loan_mandates")

public class Mandate extends AbstractPersistable<Long>{

        @Column(name ="loan_id", nullable = false)
        private Long loanId;

        @Column(name ="mandate_status_enum", nullable = false)
        private Integer mandateStatusEnum;

        @Column(name ="request_date", nullable = false)
        @Temporal(TemporalType.DATE)
        private Date requestDate;

        @Column(name ="umrn", nullable = true)
        private String umrn;

        @Column(name ="bank_account_holder_name", nullable = false)
        private String bankAccountHolderName;

        @Column(name ="bank_name", nullable = false)
        private String bankName;

        @Column(name ="branch_name", nullable = false)
        private String branchName;

        @Column(name ="bank_account_number", nullable = false)
        private String bankAccountNumber;

        @Column(name ="micr", nullable = true)
        private String micr;

        @Column(name ="ifsc", nullable = true)
        private String ifsc;

        @Column(name ="account_type_enum", nullable = false)
        private Integer accountTypeEnum;

        @Column(name ="period_from_date", nullable = false)
        @Temporal(TemporalType.DATE)
        private Date periodFromDate;

        @Column(name ="period_to_date", nullable = true)
        @Temporal(TemporalType.DATE)
        private Date periodToDate;

        @Column(name ="period_until_cancelled", nullable = true)
        private Boolean periodUntilCancelled;

        @Column(name ="debit_type_enum", nullable = false)
        private Integer debitTypeEnum;

        @Column(name ="amount", nullable = false)
        private BigDecimal amount;

        @Column(name ="debit_frequency_enum", nullable = false)
        private Integer debitFrequencyEnum;

        @Column(name ="scanned_document_id", nullable = false)
        private Long scannedDocumentId;

        @Column(name ="return_reason", nullable = true)
        private String returnReason;

        @Column(name ="return_process_date", nullable = true)
        @Temporal(TemporalType.DATE)
        private Date returnProcessDate;

        @Column(name ="return_process_reference_id", nullable = true)
        private String returnProcessReferenceId;

        protected Mandate(){};

        public Integer getMandateStatusEnum() {
                return mandateStatusEnum;
        }

        public Map<String,Object> edit(final JsonCommand command) {

                Map<String,Object> changes = new HashMap<>();

                if (command.isChangeInLocalDateParameterNamed(MandateApiConstants.requestDate, LocalDate.fromDateFields(this.requestDate))) {
                        this.requestDate = command.localDateValueOfParameterNamed(MandateApiConstants.requestDate).toDate();
                        changes.put(MandateApiConstants.requestDate, this.requestDate);
                }

                if (command.isChangeInStringParameterNamed(MandateApiConstants.bankAccountHolderName, this.bankAccountHolderName)) {
                        this.bankAccountHolderName = command.stringValueOfParameterNamed(MandateApiConstants.bankAccountHolderName);
                        changes.put(MandateApiConstants.bankAccountHolderName, this.bankAccountHolderName);
                }

                if (command.isChangeInStringParameterNamed(MandateApiConstants.bankName, this.bankName)) {
                        this.bankName = command.stringValueOfParameterNamed(MandateApiConstants.bankName);
                        changes.put(MandateApiConstants.bankName, this.bankName);
                }

                if (command.isChangeInStringParameterNamed(MandateApiConstants.branchName, this.branchName)) {
                        this.branchName = command.stringValueOfParameterNamed(MandateApiConstants.branchName);
                        changes.put(MandateApiConstants.branchName, this.branchName);
                }

                if (command.isChangeInStringParameterNamed(MandateApiConstants.bankAccountNumber, this.bankAccountNumber)) {
                        this.bankAccountNumber = command.stringValueOfParameterNamed(MandateApiConstants.bankAccountNumber);
                        changes.put(MandateApiConstants.bankAccountNumber, this.bankAccountNumber);
                }

                if (command.isChangeInStringParameterNamed(MandateApiConstants.micr, this.micr)) {
                        this.micr = command.stringValueOfParameterNamed(MandateApiConstants.micr);
                        changes.put(MandateApiConstants.micr, this.micr);
                }

                if (command.isChangeInStringParameterNamed(MandateApiConstants.ifsc, this.ifsc)) {
                        this.ifsc = command.stringValueOfParameterNamed(MandateApiConstants.ifsc);
                        changes.put(MandateApiConstants.ifsc, this.ifsc);
                }

                if (command.isChangeInIntegerSansLocaleParameterNamed(MandateApiConstants.accountType, this.accountTypeEnum)) {
                        this.accountTypeEnum = command.integerValueSansLocaleOfParameterNamed(MandateApiConstants.accountType);
                        changes.put(MandateApiConstants.accountType, AccountTypeEnum.enumOptionDataFrom(this.accountTypeEnum));
                }

                if (command.isChangeInLocalDateParameterNamed(MandateApiConstants.periodFromDate, LocalDate.fromDateFields(this.periodFromDate))) {
                        this.periodFromDate = command.localDateValueOfParameterNamed(MandateApiConstants.periodFromDate).toDate();
                        changes.put(MandateApiConstants.periodFromDate, this.periodFromDate);
                }

                LocalDate periodToDate = command.localDateValueOfParameterNamed(MandateApiConstants.periodToDate);
                if(periodToDate == null && this.periodToDate != null){
                        this.periodToDate = null;
                        changes.put(MandateApiConstants.periodToDate, this.periodToDate);
                } else if(periodToDate != null && this.periodToDate == null){
                        this.periodToDate = periodToDate.toDate();
                        this.periodUntilCancelled = false;
                        changes.put(MandateApiConstants.periodToDate, this.periodToDate);
                }else if(periodToDate != null && this.periodToDate !=null){
                        if (command.isChangeInLocalDateParameterNamed(MandateApiConstants.periodToDate, LocalDate.fromDateFields(this.periodToDate))) {
                                this.periodToDate = periodToDate.toDate();
                                this.periodUntilCancelled = false;
                                changes.put(MandateApiConstants.periodToDate, this.periodToDate);
                        }
                }

                if (command.isChangeInBooleanParameterNamed(MandateApiConstants.periodUntilCancelled, this.periodUntilCancelled)) {
                        this.periodUntilCancelled = command.booleanObjectValueOfParameterNamed(MandateApiConstants.periodUntilCancelled);
                        if(this.periodUntilCancelled != null && this.periodUntilCancelled){
                                this.periodToDate = null;
                        }
                        changes.put(MandateApiConstants.periodUntilCancelled, this.periodUntilCancelled);
                }

                if (command.isChangeInIntegerSansLocaleParameterNamed(MandateApiConstants.debitType, this.debitTypeEnum)) {
                        this.debitTypeEnum = command.integerValueSansLocaleOfParameterNamed(MandateApiConstants.debitType);
                        changes.put(MandateApiConstants.debitType, DebitTypeEnum.enumOptionDataFrom(this.debitTypeEnum));
                }

                if (command.isChangeInIntegerSansLocaleParameterNamed(MandateApiConstants.debitFrequency, this.debitFrequencyEnum)) {
                        this.debitFrequencyEnum = command.integerValueSansLocaleOfParameterNamed(MandateApiConstants.debitFrequency);
                        changes.put(MandateApiConstants.debitFrequency, DebitFrequencyEnum.enumOptionDataFrom(this.debitFrequencyEnum));
                }

                if (command.isChangeInBigDecimalParameterNamed(MandateApiConstants.amount, this.amount)) {
                        this.amount = command.bigDecimalValueOfParameterNamed(MandateApiConstants.amount);
                        changes.put(MandateApiConstants.amount, this.amount);
                }

                if (command.isChangeInLongParameterNamed(MandateApiConstants.scannedDocumentId, this.scannedDocumentId)) {
                        this.scannedDocumentId = command.longValueOfParameterNamed(MandateApiConstants.scannedDocumentId);
                        changes.put(MandateApiConstants.scannedDocumentId, this.scannedDocumentId);
                }

                return changes;
        }

        public static Mandate withStatus(final JsonCommand command, final MandateStatusEnum status) {
                Mandate ret = new Mandate();

                ret.loanId = command.getLoanId();
                ret.mandateStatusEnum = status.getValue();
                ret.requestDate = command.localDateValueOfParameterNamed(MandateApiConstants.requestDate).toDate();
                ret.umrn = command.stringValueOfParameterNamed(MandateApiConstants.umrn);
                ret.bankAccountHolderName = command.stringValueOfParameterNamed(MandateApiConstants.bankAccountHolderName);
                ret.bankName = command.stringValueOfParameterNamed(MandateApiConstants.bankName);
                ret.branchName = command.stringValueOfParameterNamed(MandateApiConstants.branchName);
                ret.bankAccountNumber = command.stringValueOfParameterNamed(MandateApiConstants.bankAccountNumber);
                ret.micr = command.stringValueOfParameterNamed(MandateApiConstants.micr);
                ret.ifsc = command.stringValueOfParameterNamed(MandateApiConstants.ifsc);
                ret.accountTypeEnum = command.integerValueSansLocaleOfParameterNamed(MandateApiConstants.accountType);
                ret.periodFromDate = command.localDateValueOfParameterNamed(MandateApiConstants.periodFromDate).toDate();
                LocalDate periodToDate = command.localDateValueOfParameterNamed(MandateApiConstants.periodToDate);
                ret.periodToDate = periodToDate == null? null:periodToDate.toDate();
                ret.periodUntilCancelled = command.booleanObjectValueOfParameterNamed(MandateApiConstants.periodUntilCancelled);
                ret.debitTypeEnum = command.integerValueSansLocaleOfParameterNamed(MandateApiConstants.debitType);
                ret.debitFrequencyEnum = command.integerValueSansLocaleOfParameterNamed(MandateApiConstants.debitFrequency);
                ret.amount = command.bigDecimalValueOfParameterNamed(MandateApiConstants.amount);
                ret.scannedDocumentId = command.longValueOfParameterNamed(MandateApiConstants.scannedDocumentId);

                return ret;
        }

        public void setFailed(final String failReason, final Long requestId) {
                String failureReasonDesc = null;
                if(null == failReason){
                        failureReasonDesc = "";
                }else{
                        failureReasonDesc = failReason.replaceAll("'","#");
                }
                if(failureReasonDesc.length() > 99){
                        failureReasonDesc = failureReasonDesc.substring(0,99);
                }
                this.returnReason = failureReasonDesc;
                this.returnProcessReferenceId = requestId.toString();
                this.returnProcessDate = new Date();
                switch(this.mandateStatusEnum){
                        case 101:
                                this.mandateStatusEnum = 102;
                                break;
                        case 201:
                                this.mandateStatusEnum = 202;
                                break;
                        case 301:
                                this.mandateStatusEnum = 302;
                                break;
                }
        }

        public void setInactive() {
                this.mandateStatusEnum = 500;
        }

        public void setSuccess(final Long requestId, final String UMRN) {
                this.returnProcessReferenceId = requestId.toString();
                this.returnProcessDate = new Date();
                switch(this.mandateStatusEnum){
                        case 101:
                                this.mandateStatusEnum = 400;
                                this.umrn = UMRN;
                                break;
                        case 201:
                                this.mandateStatusEnum = 400;
                                break;
                        case 301:
                                this.mandateStatusEnum = 500;
                                break;
                }
        }
}
