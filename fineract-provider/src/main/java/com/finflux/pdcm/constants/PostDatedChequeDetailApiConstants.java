package com.finflux.pdcm.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PostDatedChequeDetailApiConstants {

    public static final String RESOURCE_NAME = "pdc";

    public static final String ENTITY_PDC = "PDC";
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_PRESENT = "PRESENT";
    public static final String ACTION_BOUNCED = "BOUNCED";
    public static final String ACTION_CLEAR = "CLEAR";
    public static final String ACTION_CANCEL = "CANCEL";
    public static final String ACTION_RETURN = "RETURN";
    public static final String ACTION_UNDO = "UNDO";

    public static final String resourceIds = "resourceIds";

    /**
     * Template
     */

    /**
     * Cheque Type
     */
    public static final String CHEQUE_TYPE_REPAYMENT_PDC = "Repayment PDC";
    public static final String CHEQUE_TYPE_SECURITY_PDC = "Security PDC";

    /**
     * Cheque Status
     */
    public static final String CHEQUE_STATUS_PENDING = "Pending";
    public static final String CHEQUE_STATUS_PRESENTED = "Presented";
    public static final String CHEQUE_STATUS_CHEQUE_BOUNCED = "Cheque bounced";
    public static final String CHEQUE_STATUS_CLEARED = "Cleared";
    public static final String CHEQUE_STATUS_CANCELLED = "Cancelled";
    public static final String CHEQUE_STATUS_RETURNED = "Returned";

    /**
     * Parameters
     */

    public static final String entityIdParamName = "entityId";

    public static final String pdcDetailsParamName = "pdcDetails";

    public static final String chequeTypeParamName = "chequeType";
    public static final String bankNameParamName = "bankName";
    public static final String branchNameParamName = "branchName";
    public static final String accountNumberParamName = "accountNumber";
    public static final String ifscCodeParamName = "ifscCode";
    public static final String amountsParamName = "amounts";
    public static final String amountParamName = "amount";
    public final static String chequeNumbersParamName = "chequeNumbers";
    public final static String chequeNumberParamName = "chequeNumber";
    public final static String chequeNumberPattern = "[0-9]+";
    public final static String chequeDatesParamName = "chequeDates";
    public final static String chequeDateParamName = "chequeDate";
    public final static String chequeAmountParamName = "chequeAmount";
    public final static String paymentTypeParamName = "paymentType";
    public final static String numberOfPDCParamName = "numberOfPDC";
    public final static String isIncrementChequeNumberParamName = "isIncrementChequeNumber";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Search Parameters
     */
    public static final String officeIdNameParamName = "officeId";
    public static final String chequeStatusParamName = "chequeStatus";
    public static final String fromDateParamName = "fromDate";
    public static final String toDateParamName = "toDate";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(pdcDetailsParamName, localeParamName,
            dateFormatParamName));

    public static final Set<String> CREATE_EACH_OBJECT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(chequeTypeParamName,
            bankNameParamName, branchNameParamName, accountNumberParamName, ifscCodeParamName, amountsParamName, chequeNumbersParamName,
            chequeDatesParamName, paymentTypeParamName, numberOfPDCParamName, isIncrementChequeNumberParamName));

    public static final Set<String> UPDATE_EACH_OBJECT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(bankNameParamName,
            branchNameParamName, ifscCodeParamName, chequeNumberParamName, chequeDateParamName, chequeAmountParamName, localeParamName,
            dateFormatParamName));

    public static final String ERROR_CODE_CHEQUE_NUMBERS_AND_DATES_ARRAY_SIZE_NOT_EQUAL = "chequeNumbers.and.chequeDates.both.size.not.equal";
    public static final String ERROR_CODE_CHEQUE_DATES_AND_AMOUNTS_ARRAY_SIZE_NOT_EQUAL = "chequeDates.and.chequeAmounts.both.size.not.equal";
    public static final String ERROR_CODE_INVALID_CHEQUE_DATE = "chequeDates.are.in.valid.formate";
    public static final String ERROR_CODE_INVALID_AMOUNT = "chequeAmounts.are.in.valid.formate";

    /**
     * API Param and Path
     */
    public static final String loanIdParam = "loanId";
    public static final String entityTypeParam = "entityType";
    public static final String entityIdParam = "entityId";
    public static final String pdcIdParam = "pdcId";

    public static final String parentPathUrl = "pdcm";
    public static final String templatePath = "{entityType}/{entityId}/template";
    public static final String createPathUrl = "{entityType}/{entityId}";
    public static final String retrieveAllPathUrl = "{entityType}/{entityId}";
    public static final String retrieveOnePathUrl = "{pdcId}";
    public static final String updatePathUrl = "{pdcId}";

    public static final String deleteCommandParam = "delete";

    public static final String commandParam = "command";

    public static final String searchTemplatePath = "search/template";

    public static final String searchPDCUrl = "search";

    public static final Set<String> SEARCH_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(officeIdNameParamName,
            chequeTypeParamName, chequeStatusParamName, fromDateParamName, toDateParamName, localeParamName, dateFormatParamName));

    public static final String presentCommandParam = "present";
    public static final String bouncedCommandParam = "bounced";
    public static final String clearCommandParam = "clear";
    public static final String cancelCommandParam = "cancel";
    public static final String returnCommandParam = "return";
    public static final String undoCommandParam = "undo";

    public static final String dateParamName = "date";
    public static final String descriptionParamName = "description";
    public static final String pdcChequeDetailsParamName = "pdcChequeDetails";

    public static final Set<String> PRESENTED_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(dateParamName, descriptionParamName,
            pdcChequeDetailsParamName, localeParamName, dateFormatParamName));

    public static final String transactionIds = "transactionIds";
    public static final String statusIds = "statusIds";

    public static final String errors = "errors";

}