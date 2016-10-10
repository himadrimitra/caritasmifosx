package com.finflux.risk.existingloans.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;
import com.finflux.risk.existingloans.data.ExistingLoanDataValidator;
import com.finflux.risk.existingloans.domain.ExistingLoan;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ExistingLoanAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ExistingLoanDataValidator existingLoanDataValidator;

    @Autowired
    public ExistingLoanAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
            final ExistingLoanDataValidator existingLoanDataValidator) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.existingLoanDataValidator = existingLoanDataValidator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ExistingLoan> assembleForSave(final Client client, final JsonCommand command) {

        final List<ExistingLoan> existingLoans = new ArrayList();
        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        if (parentElement.isJsonObject() && !command.parameterExists(ExistingLoanApiConstants.existingLoansParamName)) {
            final ExistingLoan existingLoan = assembleCreateFormEachObject(client, parentElement.getAsJsonObject());
            existingLoans.add(existingLoan);
        } else if (command.parameterExists(ExistingLoanApiConstants.existingLoansParamName)) {
            final JsonArray array = parentElementObj.get(ExistingLoanApiConstants.existingLoansParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    final ExistingLoan existingLoan = assembleCreateFormEachObject(client, element);
                    existingLoans.add(existingLoan);
                }
            }
        }
        return existingLoans;
    }

    private ExistingLoan assembleCreateFormEachObject(final Client client, final JsonObject element) {

        final Long loanApplicationId = this.fromApiJsonHelper.extractLongNamed("loanApplicationId", element);

        final Long loanId = this.fromApiJsonHelper.extractLongNamed("loanId", element);

        CodeValue sourceCvId = null;
        final Long sourcecvId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.sourceCvIdParamName, element);
        if (sourcecvId != null) {
            sourceCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.Source_Cv_Option,
                    sourcecvId);
        }

        CodeValue bureauCvId = null;
        final Long bureaucvId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanEnquiryIdParamName, element);
        if (bureaucvId != null) {
            bureauCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.Bureau_Cv_Option,
                    bureaucvId);
        }

        final Long bureauEnqRefId = this.fromApiJsonHelper.extractLongNamed("bureauEnqRefId", element);

        CodeValue lenderCvId = null;
        final Long lendercvId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.lenderCvIdParamName, element);
        if (lendercvId != null) {
            lenderCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.Lender_Cv_Option,
                    lendercvId);
        }

        final String lenderNotListed = this.fromApiJsonHelper.extractStringNamed("lenderNotListed", element);

        CodeValue loanType = null;
        final Long loanTypeId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanTypeCvIdParamName, element);
        if (loanTypeId != null) {
            loanType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.LoanType_Cv_Option,
                    loanTypeId);
        }

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        final BigDecimal amountBorrowed = this.fromApiJsonHelper.extractBigDecimalNamed(ExistingLoanApiConstants.amountBorrowedParamName,
                element, locale);
        final BigDecimal currentOutstanding = this.fromApiJsonHelper.extractBigDecimalNamed(
                ExistingLoanApiConstants.currentOutstandingIdParamName, element, locale);
        final BigDecimal amtOverdue = this.fromApiJsonHelper.extractBigDecimalNamed(ExistingLoanApiConstants.amtOverdueParamName, element,
                locale);
        final BigDecimal writtenoffamount = this.fromApiJsonHelper.extractBigDecimalNamed(
                ExistingLoanApiConstants.writtenoffamountParamName, element, locale);
        // to be discuss
        final Integer loanTenure = this.fromApiJsonHelper.extractIntegerNamed("loanTenure", element, locale);
        final Integer loanTenurePeriodType = this.fromApiJsonHelper.extractIntegerNamed("loanTenurePeriodType", element, locale);
        final Integer repaymentFrequency = this.fromApiJsonHelper.extractIntegerNamed("repaymentFrequency", element, locale);
        final Integer repaymentFrequencyMultipleOf = this.fromApiJsonHelper.extractIntegerNamed("repaymentFrequencyMultipleOf", element,
                locale);
        //
        final BigDecimal installmentAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                ExistingLoanApiConstants.installmentAmountParamName, element, locale);

        CodeValue externalLoanPurpose = null;
        final Long externalPurposecvId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName,
                element);
        if (externalPurposecvId != null) {
            externalLoanPurpose = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                    ExistingLoanApiConstants.ExternalLoan_Purpose_Option, externalPurposecvId);
        }
        final Integer loanStatus = this.fromApiJsonHelper.extractIntegerNamed("loanStatusId", element, locale);
        final LocalDate disbursedDate = this.fromApiJsonHelper.extractLocalDateNamed("disbursedDate", element);
        final LocalDate maturityDate = this.fromApiJsonHelper.extractLocalDateNamed("maturityDate", element);
        this.existingLoanDataValidator.validateMaturityOnDate(disbursedDate, maturityDate);
        final Integer gt0dpd3mths = this.fromApiJsonHelper.extractIntegerNamed("gt0dpd3mths", element, locale);
        final Integer dpd30mths12 = this.fromApiJsonHelper.extractIntegerNamed("dpd30mths12", element, locale);
        final Integer dpd30mths24 = this.fromApiJsonHelper.extractIntegerNamed("dpd30mths24", element, locale);
        final Integer dpd60mths24 = this.fromApiJsonHelper.extractIntegerNamed("dpd60mths24", element, locale);

        final String remark = this.fromApiJsonHelper.extractStringNamed("remark", element);
        final Integer archive = this.fromApiJsonHelper.extractIntegerNamed("archive", element, locale);

        return ExistingLoan.saveExistingLoan(client, loanApplicationId, loanId, sourceCvId, null, bureauEnqRefId, lenderCvId,
                lenderNotListed, loanType, amountBorrowed, currentOutstanding, amtOverdue, writtenoffamount, loanTenure,
                loanTenurePeriodType, repaymentFrequency, repaymentFrequencyMultipleOf, installmentAmount, externalLoanPurpose, loanStatus,
                disbursedDate, maturityDate, gt0dpd3mths, dpd30mths12, dpd30mths24, dpd60mths24, remark, archive);
    }
}