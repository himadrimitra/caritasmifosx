package com.finflux.portfolio.investmenttracker.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.exception.InsufficientAccountBalanceException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountStateTransitionException;
import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccount;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountStatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class InvestmentAccountDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public InvestmentAccountDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }
    
    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long officeId = this.fromApiJsonHelper.extractLongNamed(
                InvestmentAccountApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.officeIdParamName)
                .value(officeId).notNull().longGreaterThanZero();
        
        final Long partnerId = this.fromApiJsonHelper.extractLongNamed(
                InvestmentAccountApiConstants.partnerIdParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.partnerIdParamName)
                .value(partnerId).notNull().longGreaterThanZero();
        
        final Long investmetProductId = this.fromApiJsonHelper.extractLongNamed(
                InvestmentAccountApiConstants.investmetProductIdParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmetProductIdParamName)
                .value(investmetProductId).notNull().longGreaterThanZero();
        
        final Integer status = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(InvestmentAccountApiConstants.statusParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.statusParamName).value(status).notNull().integerSameAsNumber(InvestmentAccountStatus.PENDING_APPROVAL.getValue());
        
        final String externalId = this.fromApiJsonHelper.extractStringNamed(InvestmentAccountApiConstants.externalIdParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.externalIdParamName).value(externalId).ignoreIfNull().notExceedingLengthOf(100);

        final String currencyCode = this.fromApiJsonHelper.extractStringNamed(InvestmentAccountApiConstants.currencyCodeParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.currencyCodeParamName).value(currencyCode).notBlank();

        final Integer digitsAfterDecimal = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                InvestmentAccountApiConstants.digitsAfterDecimalParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.digitsAfterDecimalParamName).value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.inMultiplesOfParamName, element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed(InvestmentAccountApiConstants.inMultiplesOfParamName,
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.inMultiplesOfParamName).value(inMultiplesOf).ignoreIfNull().integerZeroOrGreater();
        }


        final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.submittedOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();
        
        if(this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.approvedOnDateParamName, element)){
            final LocalDate approvedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.approvedOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.approvedOnDateParamName).value(approvedOnDate).notNull();     
        }
        
        if(this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.activatedOnDateParamName, element)){
            final LocalDate activatedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.activatedOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.activatedOnDateParamName).value(activatedOnDate).notNull();         
        }
        
        final LocalDate investmentOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.investmentOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentOnDateParamName).value(investmentOnDate).notNull();
        
        final BigDecimal investmentAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(InvestmentAccountApiConstants.investmentAmountParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentAmountParamName).value(investmentAmount).notNull().positiveAmount();
    
        final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                InvestmentAccountApiConstants.interestRateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.interestRateParamName)
                .value(interestRate).positiveAmount();
        
        Integer interestRateType = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentAccountApiConstants.interestRateTypeParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.interestRateTypeParamName).value(interestRateType)
                .notNull().inMinMaxRange(2, 3);
        
        Integer investmentTerm = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentAccountApiConstants.investmentTermParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentTermParamName).value(investmentTerm)
                .notNull().integerGreaterThanZero();
        
        Integer investmentTermType = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentAccountApiConstants.investmentTermTypeParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentTermTypeParamName).value(investmentTermType)
                .notNull().inMinMaxRange(0,2);
        
        final LocalDate maturityOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.maturityOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.maturityOnDateParamName).value(maturityOnDate).notNull();
        
        final BigDecimal maturityAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(InvestmentAccountApiConstants.maturityAmountParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.maturityAmountParamName).value(maturityAmount).notNull().positiveAmount();
    
        boolean reinvestAfterMaturity = this.fromApiJsonHelper.extractBooleanNamed(
                InvestmentAccountApiConstants.reinvestAfterMaturityParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.reinvestAfterMaturityParamName).value(reinvestAfterMaturity)
                .notNull();
        
        final Long staffId = this.fromApiJsonHelper.extractLongNamed(
                InvestmentAccountApiConstants.staffIdParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.staffIdParamName)
                .value(staffId).ignoreIfNull().longGreaterThanZero();
        
        boolean trackSourceAccounts = this.fromApiJsonHelper.extractBooleanNamed(
                InvestmentAccountApiConstants.trackSourceAccountsParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.trackSourceAccountsParamName).value(trackSourceAccounts)
                .notNull();
        
        Locale locale =this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.localeParamName).value(locale).notNull();
        
        if(trackSourceAccounts){
            final JsonArray savingsAccountActions = this.fromApiJsonHelper.extractJsonArrayNamed(InvestmentAccountApiConstants.savingsAccountsParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.savingsAccountsParamName).value(savingsAccountActions).jsonArrayNotEmpty();
            
            BigDecimal sumOfInvestmentAmount = new BigDecimal(0);
            //investment account and savings account linkages validation
            for (JsonElement savingsAccountElement : savingsAccountActions) {             
                 final Long savingsAccountId = this.fromApiJsonHelper.extractLongNamed(
                        InvestmentAccountApiConstants.savingsAccountIdParamName, savingsAccountElement);
                 baseDataValidator.reset().parameter(InvestmentAccountApiConstants.savingsAccountIdParamName)
                        .value(savingsAccountId).notNull().longGreaterThanZero();
                 
                 final BigDecimal individualInvestmentAmount = this.fromApiJsonHelper.extractBigDecimalNamed(InvestmentAccountApiConstants.individualInvestmentAmountParamName, savingsAccountElement, locale);
                 baseDataValidator.reset().parameter(InvestmentAccountApiConstants.individualInvestmentAmountParamName).value(individualInvestmentAmount).notNull().positiveAmount();
             
                 sumOfInvestmentAmount = sumOfInvestmentAmount.add(individualInvestmentAmount);
            }
            if(sumOfInvestmentAmount.compareTo(investmentAmount) != 0){
                baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentAmountParamName)
                .failWithCode("parameter.should.be.equal.to.sum.of.savingsaccount.investmentamount", "Investment Amount Parameter should be equal to sum of savings account Investment Amount");
            }
           
        }

        
        final JsonArray chargesActions = this.fromApiJsonHelper.extractJsonArrayNamed(InvestmentAccountApiConstants.chargesParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.chargesParamName).value(chargesActions).ignoreIfNull();
        
        for (JsonElement chargeElement : chargesActions) {             
            Long chargeId = this.fromApiJsonHelper.extractLongNamed(
                    InvestmentAccountApiConstants.chargeIdParamName, chargeElement);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.chargeIdParamName)
                    .value(chargeId).notNull().longGreaterThanZero();
            
            boolean isPentality = this.fromApiJsonHelper.extractBooleanNamed(
                    InvestmentAccountApiConstants.isPentalityParamName, chargeElement);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.isPentalityParamName).value(isPentality)
                    .notNull();
            
            boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(
                    InvestmentAccountApiConstants.isActiveParamName, chargeElement);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.isActiveParamName).value(isActive)
                    .notNull();
            
            final LocalDate inactivationDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.inactivationDateParamName, chargeElement);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.inactivationDateParamName).value(inactivationDate).ignoreIfNull();
           
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
       
    }
    
    public void validateForInvestmentAccountToActivate(InvestmentAccount investmentAccount) {
        if(InvestmentAccountStatus.APPROVED.getValue().compareTo(investmentAccount.getStatus()) != 0){
            String defaultErrorMessage = "Investment Account should be in Approve status to Activate";
            String action = InvestmentAccountStatus.ACTIVE.name();
            throw new InvestmentAccountStateTransitionException(action,defaultErrorMessage,investmentAccount.getAccountNumber());
        }
    }
    
    public void validateForInvestmentAccountToApprove(InvestmentAccount investmentAccount) {
        if(InvestmentAccountStatus.PENDING_APPROVAL.getValue().compareTo(investmentAccount.getStatus()) != 0){
            String defaultErrorMessage = "Investment Account should be in PENDING_APPROVAL status to Approve";
            String action = InvestmentAccountStatus.APPROVED.name();
            throw new InvestmentAccountStateTransitionException(action,defaultErrorMessage,investmentAccount.getAccountNumber());
        }
    }
    
    public void validateForInvestmentAccountToReject(InvestmentAccount investmentAccount) {
        if(InvestmentAccountStatus.PENDING_APPROVAL.getValue().compareTo(investmentAccount.getStatus()) != 0){
            String defaultErrorMessage = "Investment Account should be in PENDING_APPROVAL status to Reject";
            String action = InvestmentAccountStatus.REJECTED.name();
            throw new InvestmentAccountStateTransitionException(action,defaultErrorMessage,investmentAccount.getAccountNumber());
        }
    }
    
    public void validateForInvestmentAccountToUndoApproval(InvestmentAccount investmentAccount) {
        if(InvestmentAccountStatus.APPROVED.getValue().compareTo(investmentAccount.getStatus()) != 0){
            String defaultErrorMessage = "Investment Account should be in APPROVED status to UndoApproval";
            String action = "UndoApproval";
            throw new InvestmentAccountStateTransitionException(action,defaultErrorMessage,investmentAccount.getAccountNumber());
        }
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
    
    public void validateSavingsAccountBalanceForInvestment(final SavingsAccount savingAccount, final BigDecimal savingsAccountInvestmentAmount){
        BigDecimal savingsAccountBalance = savingAccount.getWithdrawableBalance();
        if(savingsAccountBalance.compareTo(savingsAccountInvestmentAmount) < 0){
            throw new InsufficientAccountBalanceException(savingAccount.getAccountNumber(),savingAccount.getAccountNumber());
        }
    }
}
