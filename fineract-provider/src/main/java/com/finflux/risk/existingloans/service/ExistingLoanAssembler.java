package com.finflux.risk.existingloans.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProductRepositoryWrapper;
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
    private final CreditBureauProductRepositoryWrapper creditBureauProductRepository;

    @Autowired
    public ExistingLoanAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
            final ExistingLoanDataValidator existingLoanDataValidator,
            final CreditBureauProductRepositoryWrapper creditBureauProductRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.existingLoanDataValidator = existingLoanDataValidator;
        this.creditBureauProductRepository = creditBureauProductRepository;
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

        final Long loanApplicationId = this.fromApiJsonHelper
                .extractLongNamed(ExistingLoanApiConstants.loanApplicationIdParamName, element);
        final Long loanId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanIdParamName, element);

        CodeValue source = null;
        final Long sourcecvId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.sourceIdParamName, element);
        if (sourcecvId != null) {
            source = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.existingLoanSource,
                    sourcecvId);
        }

        CreditBureauProduct creditBureauProduct = null;
        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.creditBureauProductIdParamName,
                element);
        if (creditBureauProductId != null) {
            creditBureauProduct = this.creditBureauProductRepository.findOneWithNotFoundDetection(creditBureauProductId);
        }

        final Long loanCreditBureauEnquiryId = this.fromApiJsonHelper.extractLongNamed(
                ExistingLoanApiConstants.loanCreditBureauEnquiryIdParamName, element);

        CodeValue lender = null;
        final Long lenderId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.lenderIdParamName, element);
        if (lenderId != null) {
            lender = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.lenderOption, lenderId);
        }

        final String lenderName = this.fromApiJsonHelper.extractStringNamed(ExistingLoanApiConstants.lenderNameParamName, element);

        CodeValue loanType = null;
        final Long loanTypeId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanTypeIdParamName, element);
        if (loanTypeId != null) {
            loanType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.loanType, loanTypeId);
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
        final Long externalPurposeId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.externalLoanPurposeIdParamName,
                element);
        if (externalPurposeId != null) {
            externalLoanPurpose = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                    ExistingLoanApiConstants.externalLoanPurpose, externalPurposeId);
        }
        final Integer loanStatus = this.fromApiJsonHelper.extractIntegerNamed("loanStatusId", element, locale);
        final LocalDate disbursedDate = this.fromApiJsonHelper.extractLocalDateNamed("disbursedDate", element);
        final LocalDate maturityDate = this.fromApiJsonHelper.extractLocalDateNamed("maturityDate", element);
        final LocalDate closedDate = null;
        this.existingLoanDataValidator.validateMaturityOnDate(disbursedDate, maturityDate);
        final Integer gt0dpd3mths = this.fromApiJsonHelper.extractIntegerNamed("gt0dpd3mths", element, locale);
        final Integer dpd30mths12 = this.fromApiJsonHelper.extractIntegerNamed("dpd30mths12", element, locale);
        final Integer dpd30mths24 = this.fromApiJsonHelper.extractIntegerNamed("dpd30mths24", element, locale);
        final Integer dpd60mths24 = this.fromApiJsonHelper.extractIntegerNamed("dpd60mths24", element, locale);

        final String remark = this.fromApiJsonHelper.extractStringNamed("remark", element);
        final Integer archive = this.fromApiJsonHelper.extractIntegerNamed("archive", element, locale);
        final String receivedLoanStatus = null ;
        return ExistingLoan.saveExistingLoan(client, loanApplicationId, loanId, source, creditBureauProduct, loanCreditBureauEnquiryId,
                lender, lenderName, loanType, amountBorrowed, currentOutstanding, amtOverdue, writtenoffamount, loanTenure,
                loanTenurePeriodType, repaymentFrequency, repaymentFrequencyMultipleOf, installmentAmount, externalLoanPurpose, loanStatus,
                receivedLoanStatus, disbursedDate, maturityDate, closedDate, gt0dpd3mths, dpd30mths12, dpd30mths24, dpd60mths24, remark, archive);
    }

    public Map<String, Object> assembleForUpdate(final ExistingLoan existingLoan, final JsonCommand command) {
        final Map<String, Object> changes = existingLoan.update(command);

        if (changes.containsKey(ExistingLoanApiConstants.sourceIdParamName)) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.sourceIdParamName);
            CodeValue source = null;
            if (newValue != null) {
                source = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.existingLoanSource,
                        newValue);
            }
            existingLoan.updateSourced(source);
        }

        if (changes.containsKey(ExistingLoanApiConstants.creditBureauProductIdParamName)) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.creditBureauProductIdParamName);
            CreditBureauProduct creditBureauProduct = null;
            if (newValue != null) {
                creditBureauProduct = this.creditBureauProductRepository.findOneWithNotFoundDetection(newValue);
            }
            existingLoan.updateCreditBureauProduct(creditBureauProduct);
        }

        if (changes.containsKey(ExistingLoanApiConstants.lenderIdParamName)) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.lenderIdParamName);
            CodeValue lender = null;
            if (newValue != null) {
                lender = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.lenderOption,
                        newValue);
            }
            existingLoan.updateLender(lender);
        }

        if (changes.containsKey(ExistingLoanApiConstants.loanTypeIdParamName)) {
            CodeValue loanType = null;
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.loanTypeIdParamName);
            if (newValue != null) {
                loanType = this.codeValueRepository
                        .findOneByCodeNameAndIdWithNotFoundDetection(ExistingLoanApiConstants.loanType, newValue);
            }
            existingLoan.updateloanType(loanType);
        }

        if (changes.containsKey(ExistingLoanApiConstants.externalLoanPurposeIdParamName)) {
            CodeValue externalLoanPurposeCvId = null;
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.externalLoanPurposeIdParamName);
            if (newValue != null) {
                externalLoanPurposeCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                        ExistingLoanApiConstants.externalLoanPurpose, newValue);
            }
            existingLoan.updateExternalLoanPurpose(externalLoanPurposeCvId);
        }

        return changes;
    }
}