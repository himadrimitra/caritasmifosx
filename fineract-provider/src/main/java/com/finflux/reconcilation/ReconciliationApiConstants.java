/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReconciliationApiConstants {

    public static final String BANK_STATEMENT_RESOURCE_NAME = "BANKSTATEMENT";
    public static final String BANK_STATEMENT_DETAILS_RESOURCE_NAME = "BANKSTATEMENTDETAILS";
    public static final String idParamName = "id";
    public static final String nameParamName = "name";
    public static final String transactionIdParamName = "transactionId";
    public static final String transactionDateParamName = "transactionDate";
    public static final String descriptionParamName = "description";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String currencyCodeParamName = "currencyCode";
    public static final String amountParamName = "amount";
    public static final String mobileNumberParamName = "mobileNumber";
    public static final String loanAccountNumberParamName = "loanAccountNumber";
    public static final String clientAccountNumberParamName = "clientAccountNumber";
    public static final String groupccountNumberParamName = "groupccountNumber";
    public static final String bankStatementIdParamName = "bankStatementId";
    public static final String createdByParamName = "createdBy";
    public static final String createdDateParamName = "createdDate";
    public static final String updatedByParamName = "updatedBy";
    public static final String updatedDateParamName = "updatedDate";
    public static final String cifKeyParamName = "cifKeyDocumentId";
    public static final String originalStatementKeyParamName = "orgStatementKeyDocumentId";
    public static final String entityName = "bankstatement";
    public static final Long bankStatementFolder = 1l;
    public static final String xlsFileExtension = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String xlsxFileExtension = "application/vnd.ms-excel";
    public static final String RECONCILE_ACTION = "RECONCILE";
    public static final String MANUAL_RECONCILE_ACTION = "MANUALRECONCILE";
    public static final String UNDO_RECONCILE_ACTION = "UNDORECONCILE";
    public static final String bankIdParamName = "bankId";
    public static final String BANK_DEFAULT_GL_CODE = "000001";
    public static final String transactionDataParamName = "transactionData";
    public static final String loanTransactionIdParamName = "loanTransactionId";
    public static final String paymentDetailDataParamName = "paymentDetailData";
    public static final String externalIdParamName = "externalId";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String type = "type";
    public static final String transactionAmountParamName = "transactionAmount";
    public static final String dateParamName = "date";
    public static final String officeIdParamName = "officeId";
    public static final String officeNameParamName = "officeName";
    public static final String glCodeParamName = "glCode";
    public static final String bankNameParamName = "bankName";
    public static final String glAccountParamName = "glAccount";
    public static final String JOURNAL_COMMAND_PARAMETER = "journal";
    public static final String CLIENT_PAYMENT = "Client Payment";
    public static final String DISBURSAL = "Disbursal";
    public static final String FIRST_FILE = "file0";
    public static final String SECOND_FILE = "file1";
    public static final String bankParamName = "bank";
    public static final String CIF_FILE_SIZE = "cifFileSize";
    public static final String ORIGINAL_FILE_SIZE = "orgFileSize";
    public static final String FILE_NAME = "fileName";
    public static final String LOCATION = "location";
    public static final String TYPE = "type";
    public static final String SIZE = "size";
    public static final String bankTransctionIdParamName = "bankTransctionId";
    public static final String BANK_STATEMENT_DETAIL_LIST = "bankStatementDetailsList";
    public static final String ERROR_ROWS = "errorRows";
    public static final String GL_ACCOUNT_ID = "glAccountId";
    public static final String DEBIT = "DEBIT";
    public static final String CREDIT_ACCOUNT = "credits";
    public static final String DEBIT_ACCOUNT = "debits";
    public static final String RESOURCE = "resource";
    public static final String BANK_STATEMENT_DETAIL_ID = "bankStatmentDetailsId";
    public static final String JOURNAL_ENTRY_RESPONSE = "journalEntryResponse";
    public static final String optionsLengthParam = "optionsLength";
    public static final String loanTransactionDataParam = "loanTransactionData";
    public static final String loanTransactionOptionsParam = "loanTransactionOptions";
    public static final String receiptNumberParam = "receiptNumber";

    public static final String RECONCILED = "reconciled";
    public static final String JOURNAL_ENTRY = "journalentry";
    public static final String OTHER = "other";
    public static final String ERROR = "error";
    public static final String MISCELLANEOUS = "miscellaneous";
    public static final String GENERATE_TRANSACTIONS = "generatetransactions";
    public static final String MANUAL_RECONCILED = "manualreconciled";
    
    public static final String amount = "Amount";
    public static final String transDate = "Trans.Date";
    public static final String serial = "Serial";
    public static final String description = "Description";
    public static final int headerIndex = 3;
    public static final String portFolioReconciledInflowAmount = "portFolioReconciledInflowAmount";
    public static final String portFolioReconciledOutflowAmount = "portFolioReconciledOutflowAmount";
    public static final String portFolioUnReconciledInflowAmount = "portFolioUnReconciledInflowAmount";
    public static final String portFolioUnReconciledOutflowAmount = "portFolioUnReconciledOutflowAmount";

    public static final String nonPortFolioReconciledInflowAmount = "nonPortFolioReconciledInflowAmount";
    public static final String nonPortFolioReconciledOutflowAmount = "nonPortFolioReconciledOutflowAmount";
    public static final String nonPortFolioUnReconciledInflowAmount = "nonPortFolioUnReconciledInflowAmount";
    public static final String nonPortFolioUnReconciledOutflowAmount = "nonPortFolioUnReconciledOutflowAmount";

    public static final String miscellaneousReconciledInflowAmount = "miscellaneousReconciledInflowAmount";
    public static final String miscellaneousUnReconciledInflowAmount = "miscellaneousUnReconciledInflowAmount";
    public static final String miscellaneousReconciledOutflowAmount = "miscellaneousReconciledOutflowAmount";
    public static final String miscellaneousUnReconciledOutflowAmount = "miscellaneousUnReconciledOutflowAmount";
    
    public static final String accountingTypeParamName = "accountingType";

    public static final List<String> EXCEL_FILE = Arrays.asList(xlsFileExtension, xlsxFileExtension);

    public static final Set<String> BANK_STATEMENT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName, cifKeyParamName,
            originalStatementKeyParamName, bankIdParamName));

    public static final Set<String> BANK_STATEMENT_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            descriptionParamName, createdByParamName, createdDateParamName, updatedByParamName, updatedDateParamName, cifKeyParamName,
            originalStatementKeyParamName, bankIdParamName, glCodeParamName, bankNameParamName, portFolioReconciledInflowAmount,
            portFolioReconciledOutflowAmount, portFolioUnReconciledInflowAmount, portFolioUnReconciledOutflowAmount,
            nonPortFolioReconciledInflowAmount, nonPortFolioReconciledOutflowAmount, nonPortFolioUnReconciledInflowAmount,
            nonPortFolioUnReconciledOutflowAmount, miscellaneousReconciledInflowAmount, miscellaneousUnReconciledInflowAmount,
            miscellaneousReconciledOutflowAmount, miscellaneousUnReconciledOutflowAmount));

    public static final Set<String> BANK_STATEMENT_DETAILS_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,
            descriptionParamName, clientAccountNumberParamName, groupccountNumberParamName, loanAccountNumberParamName, amountParamName,
            mobileNumberParamName, transactionDateParamName, transactionIdParamName, bankStatementIdParamName, loanTransactionIdParamName,
            paymentDetailDataParamName, officeIdParamName, officeNameParamName, externalIdParamName, submittedOnDateParamName, type,
            transactionAmountParamName, dateParamName, bankIdParamName, optionsLengthParam, loanTransactionDataParam,
            loanTransactionOptionsParam, receiptNumberParam));

    public static final String[] HEADER_DATA = { "Transaction Id", "Transaction Date", "Description", "Amount", "Mobile Number",
            "Client Account No", "Loan Account No", "Group External ID", "Branch External ID", "GL Code", "Accounting Type",
            "Transaction Type" };
    
    public static final String[] SIMPLIFIED_HEADER_DATA = { "Trans.Date", "Serial", "Amount","Description" };
    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";
    public static final String DELETE_ACTION = "DELETE";
    public static final String BANK_RESOURCE_NAME = "BANK";
    public static final String supportSimplifiedStatement = "supportSimplifiedStatement";

    public static final Set<String> BANK_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            glAccountParamName, supportSimplifiedStatement));

    public static final Set<String> BANK_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            glAccountParamName, glCodeParamName, supportSimplifiedStatement));

    public static final String TRANSACTION_ID_CAN_NOT_BE_BLANK = "Transaction Id can not be blank.";
    public static final String TRANSACTION_TYPE_CAN_NOT_BE_BLANK = "Transaction type can not be blank.";
    public static final String AMOUNT_CAN_NOT_BE_BLANK = "Amount can not be blank.";
    public static final String AMOUNT_INVALID = "Amount is invalid.";
    public static final String TRANSACTION_DATE_CAN_NOT_BE_BLANK = "Transaction Date can not be blank.";
    public static final String INVALID_TRANSACTION_DATE = "Transaction Date format is invalid.";
    public static final String PORTFOLIO_TRANSACTIONS = "PORTFOLIOTRANSACTIONS";
    public static final String LOAN_ACCOUNT_NUMBER_CAN_NOT_BE_BLANK = "Loan Account Number can not be blank.";
}
