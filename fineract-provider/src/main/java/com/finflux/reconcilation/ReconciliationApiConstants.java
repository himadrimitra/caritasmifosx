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
    public static final String CLIENT_PAYMEMT = "Client Payment";
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

    public static final List<String> EXCEL_FILE = Arrays.asList(xlsFileExtension, xlsxFileExtension);

    public static final Set<String> BANK_STATEMENT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName, cifKeyParamName,
            originalStatementKeyParamName, bankIdParamName));

    public static final Set<String> BANK_STATEMENT_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            descriptionParamName, createdByParamName, createdDateParamName, updatedByParamName, updatedDateParamName, cifKeyParamName,
            originalStatementKeyParamName, bankIdParamName, glCodeParamName, bankNameParamName));

    public static final Set<String> BANK_STATEMENT_DETAILS_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,
            descriptionParamName, clientAccountNumberParamName, groupccountNumberParamName, loanAccountNumberParamName, amountParamName,
            mobileNumberParamName, transactionDateParamName, transactionIdParamName, bankStatementIdParamName, loanTransactionIdParamName,
            paymentDetailDataParamName, officeIdParamName, officeNameParamName, externalIdParamName, submittedOnDateParamName, type,
            transactionAmountParamName, dateParamName, bankIdParamName));

    public static final String[] HEADER_DATA = { "Transaction Id", "Transaction Date", "Description", "Amount", "Mobile Number",
            "Client Account No", "Loan Account No", "Group External ID", "Branch External ID", "GL Code", "Accounting Type",
            "Transaction Type" };

    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";
    public static final String DELETE_ACTION = "DELETE";
    public static final String BANK_RESOURCE_NAME = "BANK";

    public static final Set<String> BANK_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            glAccountParamName));

    public static final Set<String> BANK_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            glAccountParamName, glCodeParamName));

    public static final String TRANSACTION_ID_CAN_NOT_BE_BLANK = "Transaction Id can not be blank.";
    public static final String TRANSACTION_TYPE_CAN_NOT_BE_BLANK = "Transaction type can not be blank.";
    public static final String AMOUNT_CAN_NOT_BE_BLANK = "Amount can not be blank.";
    public static final String AMOUNT_INVALID = "Amount is invalid.";
    public static final String TRANSACTION_DATE_CAN_NOT_BE_BLANK = "Transaction Date can not be blank.";
    public static final String INVALID_TRANSACTION_DATE = "Transaction Date format is invalid.";

}
