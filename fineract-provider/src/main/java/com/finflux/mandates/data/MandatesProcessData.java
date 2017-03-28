package com.finflux.mandates.data;

import org.apache.fineract.organisation.office.data.OfficeData;

import java.util.Collection;
import java.util.Date;

public class MandatesProcessData {

        private final Long id;
        private final Date requestDate;
        private final String mandateProcessType;
        private final String mandateProcessStatus;
        private final Long officeId;
        private final String officeName;
        private final Boolean includeChildOffices;
        private final Boolean includeMandateScans;
        private final Date paymentDueStartDate;
        private final Date paymentDueEndDate;
        private final String includeFailedTransactions;
        private final Long documentId;
        private final String documentName;
        private final String failureReasonCode;
        private final String failureReasonDesc;
        private final Integer totalRecords;
        private final Integer successRecords;
        private final Integer failedRecords;
        private final Integer unprocessedRecords;

        private final Collection<OfficeData> officeOptions;
        private final Collection<MandateTransactionsData> failedTransactionOptions;

        private MandatesProcessData(final Long id,
                final Date requestDate,
                final String mandateProcessType,
                final String mandateProcessStatus,
                final Long officeId,
                final String officeName,
                final Boolean includeChildOffices,
                final Boolean includeMandateScans,
                final Date paymentDueStartDate,
                final Date paymentDueEndDate,
                final String includeFailedTransactions,
                final Long documentId,
                final String documentName,
                final String failureReasonCode,
                final String failureReasonDesc,
                final Integer totalRecords,
                final Integer successRecords,
                final Integer failedRecords,
                final Integer unprocessedRecords,
                final Collection<OfficeData> officeOptions,
                final Collection<MandateTransactionsData> failedTransactionOptions){

                this.id = id;
                this.requestDate = requestDate;
                this.mandateProcessType = mandateProcessType;
                this.mandateProcessStatus = mandateProcessStatus;
                this.officeId = officeId;
                this.officeName = officeName;
                this.includeChildOffices = includeChildOffices;
                this.includeMandateScans = includeMandateScans;
                this.paymentDueStartDate = paymentDueStartDate;
                this.paymentDueEndDate = paymentDueEndDate;
                this.includeFailedTransactions = includeFailedTransactions;
                this.documentId = documentId;
                this.documentName = documentName;
                this.failureReasonCode = failureReasonCode;
                this.failureReasonDesc = failureReasonDesc;
                this.officeOptions = officeOptions;
                this.failedTransactionOptions = failedTransactionOptions;
                this.totalRecords = totalRecords;
                this.successRecords = successRecords;
                this.failedRecords = failedRecords;
                this.unprocessedRecords = unprocessedRecords;

        }

        public static MandatesProcessData mandateDownloadTemplate(final Collection<OfficeData> officeOptions) {
                final Long id = null;
                final Date requestDate = null;
                final String mandateProcessType = null;
                final String mandateProcessStatus = null;
                final Long officeId = null;
                final String officeName = null;
                final Boolean includeChildOffices = null;
                final Boolean includeMandateScans = null;
                final Date paymentDueStartDate = null;
                final Date paymentDueEndDate = null;
                final String includeFailedTransactions = null;
                final Long documentId = null;
                final String documentName = null;
                final String failureReasonCode = null;
                final String failureReasonDesc = null;
                final Integer totalRecords = null;
                final Integer successRecords = null;
                final Integer failedRecords = null;
                final Integer unprocessedRecords = null;
                final Collection<MandateTransactionsData> failedTransactionOptions = null;

                return new MandatesProcessData(id, requestDate, mandateProcessType, mandateProcessStatus, officeId, officeName,
                        includeChildOffices, includeMandateScans, paymentDueStartDate, paymentDueEndDate, includeFailedTransactions,
                        documentId, documentName, failureReasonCode, failureReasonDesc, totalRecords, successRecords, failedRecords,
                        unprocessedRecords, officeOptions, failedTransactionOptions);
        }

        public static MandatesProcessData transactionsDownloadTemplate(final Collection<OfficeData> officeOptions,
                final Collection<MandateTransactionsData> failedTransactionOptions) {
                final Long id = null;
                final Date requestDate = null;
                final String mandateProcessType = null;
                final String mandateProcessStatus = null;
                final Long officeId = null;
                final String officeName = null;
                final Boolean includeChildOffices = null;
                final Boolean includeMandateScans = null;
                final Date paymentDueStartDate = null;
                final Date paymentDueEndDate = null;
                final String includeFailedTransactions = null;
                final Long documentId = null;
                final String documentName = null;
                final Integer totalRecords = null;
                final Integer successRecords = null;
                final Integer failedRecords = null;
                final Integer unprocessedRecords = null;
                final String failureReasonCode = null;
                final String failureReasonDesc = null;

                return new MandatesProcessData(id, requestDate, mandateProcessType, mandateProcessStatus, officeId, officeName,
                        includeChildOffices, includeMandateScans, paymentDueStartDate, paymentDueEndDate, includeFailedTransactions,
                        documentId, documentName, failureReasonCode, failureReasonDesc, totalRecords, successRecords, failedRecords,
                        unprocessedRecords, officeOptions, failedTransactionOptions);
        }

        public static MandatesProcessData from(final Long id, final Date requestDate, final String mandateProcessType,
                final String mandateProcessStatus, final Long officeId, final String officeName, final Boolean includeChildOffices,
                final Boolean includeMandateScans, final Date paymentDueStartDate, final Date paymentDueEndDate,
                final String includeFailedTransactions, final Long documentId, final String documentName, final String failureReasonCode,
                final String failureReasonDesc, final Integer totalRecords, final Integer successRecords, final Integer failedRecords,
                final Integer unprocessedRecords) {

                final Collection<OfficeData> officeOptions = null;
                final Collection<MandateTransactionsData> failedTransactionOptions = null;
                return new MandatesProcessData(id, requestDate, mandateProcessType, mandateProcessStatus, officeId, officeName,
                        includeChildOffices, includeMandateScans, paymentDueStartDate, paymentDueEndDate, includeFailedTransactions,
                        documentId, documentName, failureReasonCode, failureReasonDesc, totalRecords, successRecords, failedRecords,
                        unprocessedRecords, officeOptions, failedTransactionOptions);
        }

        public Long getId() {
                return id;
        }

        public Date getRequestDate() {
                return requestDate;
        }

        public String getMandateProcessType() {
                return mandateProcessType;
        }

        public String getMandateProcessStatus() {
                return mandateProcessStatus;
        }

        public Long getOfficeId() {
                return officeId;
        }

        public String getOfficeName() {
                return officeName;
        }

        public Boolean includeChildOffices() {
                return includeChildOffices;
        }

        public Boolean includeMandateScans() {
                return includeMandateScans;
        }

        public Date getPaymentDueStartDate() {
                return paymentDueStartDate;
        }

        public Date getPaymentDueEndDate() {
                return paymentDueEndDate;
        }

        public String getIncludeFailedTransactions() {
                return includeFailedTransactions;
        }

        public Long getDocumentId() {
                return documentId;
        }

        public String getDocumentName() {
                return documentName;
        }

        public String getFailureReasonCode() {
                return failureReasonCode;
        }

        public String getFailureReasonDesc() {
                return failureReasonDesc;
        }

}
