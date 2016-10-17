package com.finflux.risk.existingloans.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExistingLoanApiConstants {

    public static final String EXISTINGLOAN_RESOURCE_NAME = "existingLoan";

    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    //
    public static final String existingLoanSource = "ExistingLoanSource";
    public static final String bureauOption = "BureauOption";
    public static final String lenderOption = "LenderOption";
    public static final String loanType = "LoanType";
    public static final String externalLoanPurpose = "externalLoanPurpose";

    // request parameters
    public static final String idParamName = "id";
    public static final String clientIdParamName = "clientId";
    public static final String loanApplicationIdParamName = "loanApplicationId";
    public static final String loanIdParamName = "loanId";
    public static final String sourceIdParamName = "sourceId";
    public static final String loanCreditBureauEnquiryIdParamName = "loanCreditBureauEnquiryId";
    public static final String creditBureauProductIdParamName = "creditBureauProductId";
    public static final String lenderIdParamName = "lenderId";
    public static final String lenderNameParamName = "lenderName";
    public static final String loanTypeIdParamName = "loanTypeId";
    public static final String amountBorrowedParamName = "amountBorrowed";
    public static final String currentOutstandingIdParamName = "currentOutstanding";
    public static final String amtOverdueParamName = "amtOverdue";
    public static final String writtenoffamountParamName = "writtenOffAmount";
    public static final String loanTenureParamName = "loanTenure";
    public static final String loanTenurePeriodTypeParamName = "loanTenurePeriodType";
    public static final String repaymentFrequencyParamName = "repaymentFrequency";
    public static final String loanStatusOptionsParamName = "loanStatusOptions";
    public static final String repaymentFrequencyMultipleOfParamName = "repaymentFrequencyMultipleOf";
    public static final String installmentAmountParamName = "installmentAmount";
    public static final String externalLoanPurposeIdParamName = "externalLoanPurposeId";
    public static final String loanStatusIdParamName = "loanStatusId";
    public static final String disbursedDateParamName = "disbursedDate";
    public static final String maturityDateParamName = "maturityDate";
    public static final String gt0dpd3mthsParamName = "gt0dpd3mths";
    public static final String dpd30mths12ParamName = "dpd30mths12";
    public static final String dpd30mths24ParamName = "dpd30mths24";
    public static final String dpd60mths24ParamName = "dpd60mths24";
    public static final String remarkParamName = "remark";
    public static final String archiveParamName = "archive";
    public static final String createdbyIdParamName = "createdById";
    public static final String lastmodifiedbyIdParamName = "lastmodifiedById";
    public static final String createdDateIdParamName = "createdDate";
    public static final String lastmodifiedDate = "lastmodifiedDate";

    public static final Set<String> EXISTING_LOAN_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, clientIdParamName, loanApplicationIdParamName, loanIdParamName, sourceIdParamName,
            loanCreditBureauEnquiryIdParamName, creditBureauProductIdParamName, lenderIdParamName, lenderNameParamName, loanTypeIdParamName,
            amountBorrowedParamName, currentOutstandingIdParamName, writtenoffamountParamName, amtOverdueParamName, loanTenureParamName,
            loanTenurePeriodTypeParamName, repaymentFrequencyParamName, repaymentFrequencyMultipleOfParamName, installmentAmountParamName,
            externalLoanPurposeIdParamName, loanStatusIdParamName, disbursedDateParamName, maturityDateParamName, gt0dpd3mthsParamName,
            dpd30mths12ParamName, dpd30mths24ParamName, dpd60mths24ParamName, remarkParamName, archiveParamName));

    public static final Set<String> EXISTING_LOAN_UPDATE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, clientIdParamName, loanApplicationIdParamName, loanIdParamName, sourceIdParamName,
            loanCreditBureauEnquiryIdParamName, creditBureauProductIdParamName, lenderIdParamName, lenderNameParamName, loanTypeIdParamName,
            amountBorrowedParamName, currentOutstandingIdParamName, amtOverdueParamName, writtenoffamountParamName, loanTenureParamName,
            loanTenurePeriodTypeParamName, repaymentFrequencyParamName, repaymentFrequencyMultipleOfParamName, installmentAmountParamName,
            externalLoanPurposeIdParamName, loanStatusIdParamName, disbursedDateParamName, maturityDateParamName, gt0dpd3mthsParamName,
            dpd30mths12ParamName, dpd30mths24ParamName, dpd60mths24ParamName, remarkParamName, archiveParamName));

    public static final Set<String> EXISTING_LOAN_RESPONSE_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, clientIdParamName, loanApplicationIdParamName, loanIdParamName,
                    sourceIdParamName, loanCreditBureauEnquiryIdParamName, creditBureauProductIdParamName, lenderIdParamName, lenderNameParamName,
                    loanTypeIdParamName, amountBorrowedParamName, currentOutstandingIdParamName, amtOverdueParamName,
                    writtenoffamountParamName, loanTenureParamName, loanTenurePeriodTypeParamName, repaymentFrequencyParamName,
                    repaymentFrequencyMultipleOfParamName, installmentAmountParamName, externalLoanPurposeIdParamName,
                    loanStatusIdParamName, disbursedDateParamName, maturityDateParamName, gt0dpd3mthsParamName, dpd30mths12ParamName,
                    dpd30mths24ParamName, dpd60mths24ParamName, remarkParamName, archiveParamName, loanStatusOptionsParamName));

    public static final String existingLoansParamName = "existingLoans";

}
