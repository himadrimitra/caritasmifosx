package com.finflux.portfolio.loan.mandate.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MandateApiConstants {
        public static final String RESOURCE_NAME = "mandate";

        public static final String id = "id";
        public static final String loanId = "loanId";
        public static final String loanAccountNo = "loanAccountNo";
        public static final String mandateStatus = "mandateStatus";
        public static final String requestDate = "requestDate";
        public static final String umrn = "umrn";
        public static final String bankAccountHolderName = "bankAccountHolderName";
        public static final String bankName = "bankName";
        public static final String branchName = "branchName";
        public static final String bankAccountNumber = "bankAccountNumber";
        public static final String micr = "micr";
        public static final String ifsc = "ifsc";
        public static final String accountType = "accountType";
        public static final String periodFromDate = "periodFromDate";
        public static final String periodToDate = "periodToDate";
        public static final String periodUntilCancelled = "periodUntilCancelled";
        public static final String debitType = "debitType";
        public static final String amount = "amount";
        public static final String debitFrequency = "debitFrequency";
        public static final String scannedDocumentId = "scannedDocumentId";
        public static final String scannedDocumentName = "scannedDocumentName";
        public static final String returnReason = "returnReason";
        public static final String returnProcessDate = "returnProcessDate";
        public static final String returnProcessReferenceId = "returnProcessReferenceId";

        public static final String accountTypeEnumOptions = "accountTypeOptions";
        public static final String debitTypeEnumOptions = "debitTypeOptions";
        public static final String debitFrequencyEnumOptions = "debitFrequencyOptions";
        public static final String scannedDocumentOptions = "scannedDocumentOptions";

        public static final Set<String> ALLOWED_REQUEST_PARAMS_CREATE_EDIT = new HashSet<>(Arrays.asList(requestDate, bankAccountHolderName,
                bankName, branchName, bankAccountNumber, micr, ifsc, accountType, periodFromDate, periodToDate, periodUntilCancelled,
                debitType, amount, debitFrequency, scannedDocumentId, "locale", "dateFormat"));

        public static final Set<String> ALLOWED_REQUEST_PARAMS_UPDATE_CANCEL = new HashSet<>(Arrays.asList(requestDate, umrn, bankAccountHolderName,
                bankName, branchName, bankAccountNumber, micr, ifsc, accountType, periodFromDate, periodToDate, periodUntilCancelled,
                debitType, amount, debitFrequency, scannedDocumentId, "locale", "dateFormat"));

        public static final Set<String> ALLOWED_RESPONSE_PARAMS = new HashSet<>(Arrays.asList(id, loanId, loanAccountNo, mandateStatus,
                requestDate, umrn, bankAccountHolderName, bankName, branchName, bankAccountNumber, micr, ifsc, accountType,
                periodFromDate, periodToDate, periodUntilCancelled, debitType, amount, debitFrequency, scannedDocumentId,
                scannedDocumentName, returnReason, returnProcessDate, returnProcessReferenceId,
                accountTypeEnumOptions, debitTypeEnumOptions, debitFrequencyEnumOptions, scannedDocumentOptions));

}
