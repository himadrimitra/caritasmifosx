package com.finflux.portfolio.investmenttracker.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.exception.InsufficientAccountBalanceException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.Exception.FutureDateTransactionException;
import com.finflux.portfolio.investmenttracker.Exception.InvalidDateException;
import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountAmountException;
import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountStateTransitionException;
import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccount;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsLinkages;
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
        
        LocalDate today =DateUtils.getLocalDateOfTenant(); 
        
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
        if(submittedOnDate != null && submittedOnDate.isAfter(today)){
        	throw new FutureDateTransactionException("submitted");
        }
                
        final LocalDate investmentOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.investmentOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentOnDateParamName).value(investmentOnDate).notNull();
        if(investmentOnDate.isBefore(submittedOnDate)){
        	throw new InvalidDateException("investment.on", "submitted");
        }
        
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
        
        if(!maturityOnDate.isAfter(investmentOnDate)){
        	throw new InvalidDateException("maturity", "investment");
        }
        
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
            
            BigDecimal sumOfInvestmentAmount = BigDecimal.ZERO;
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
            if(!MathUtility.isEqual(sumOfInvestmentAmount, investmentAmount)){
                baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentAmountParamName)
                .failWithCode("parameter.should.be.equal.to.sum.of.savingsaccount.investmentamount", "Investment Amount Parameter should be equal to sum of savings account Investment Amount");
            }
           
        }

        
        validateCharges(baseDataValidator, element);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
       
    }

    private void validateCharges(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        final JsonArray chargesActions = this.fromApiJsonHelper.extractJsonArrayNamed(InvestmentAccountApiConstants.chargesParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.chargesParamName).value(chargesActions).ignoreIfNull();
        if(chargesActions != null && chargesActions.size()>0){
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
        }
        
    }
    
    public void validateForInvestmentAccountToActivate(InvestmentAccount investmentAccount, final String json) {
    	if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_ACTIVATE_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final LocalDate activatedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.activatedOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.activatedOnDateParamName).value(activatedOnDate).notNull();
        
        for (InvestmentAccountSavingsLinkages investmentSavingAccount : investmentAccount.getInvestmentAccountSavingsLinkages()) {
        	baseDataValidator.reset().parameter(InvestmentAccountApiConstants.activatedOnDateParamName).value(activatedOnDate).notNull();
		}
        
        investmentAccount.setActivatedOnDate(activatedOnDate.toDate());
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        validateForAcivate(investmentAccount);
    }
    
    public void validateForInvestmentAccountToApprove(InvestmentAccount investmentAccount,final String json) {
    	if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_APPROVE_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate approvedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.approvedOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.approvedOnDateParamName).value(approvedOnDate).notNull();
        investmentAccount.setApprovedOnDate(approvedOnDate.toDate());
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        validateForApprove(investmentAccount);
    }
    
    public void validateForInvestmentAccountToClose(InvestmentAccount investmentAccount, final String json) {
    	if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_CLOSE_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final LocalDate closedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.closedOnDateParamName, element);
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.closedOnDateParamName).value(closedOnDate).notNull();
        
        investmentAccount.setCloseOnDate(closedOnDate.toDate());
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        validateForClose(investmentAccount);
    }
    
    
    public static void validateForApprove(InvestmentAccount investmentAccount){
    	if(!InvestmentAccountStatus.PENDING_APPROVAL.getValue().equals(investmentAccount.getStatus())){
            throw new InvestmentAccountStateTransitionException("pending.approve","pending approval");
        }
    	LocalDate today = DateUtils.getLocalDateOfTenant();
    	if(investmentAccount.getApprovedOnDate().isAfter(today)){
    		throw new FutureDateTransactionException("approve");
    	}
    	if(investmentAccount.getApprovedOnDate().isBefore(investmentAccount.getSubmittedOnDate())){
    		throw new InvalidDateException("approve", "submitted");
    	}

    	if(investmentAccount.getApprovedOnDate().isAfter(investmentAccount.getInvestmentOnDate())){
    		throw new InvalidDateException("investment", "approve");
    	}
    	for (InvestmentAccountSavingsLinkages investmentSavingAccount : investmentAccount.getInvestmentAccountSavingsLinkages()) {
        	if(investmentAccount.getApprovedOnDate().isBefore(investmentSavingAccount.getSavingsAccount().getActivationLocalDate())){
        		throw new InvalidDateException("approve", "savings.activated");
        	}
		}
    }
    
    public static void validateForClose(InvestmentAccount investmentAccount){
    	if(!InvestmentAccountStatus.MATURED.getValue().equals(investmentAccount.getStatus())){
            throw new InvestmentAccountStateTransitionException("matured","matured");
        }
    	LocalDate today = DateUtils.getLocalDateOfTenant();
    	if(investmentAccount.getCloseOnDate().isAfter(today)){
    		throw new FutureDateTransactionException("closed");
    	}
    	if(investmentAccount.getCloseOnDate().isBefore(investmentAccount.getActivatedOnDate())){
    		throw new InvalidDateException("closed", "activation");
    	}
    	
    }
    
    public static void validateForAcivate(InvestmentAccount investmentAccount){
    	if(!InvestmentAccountStatus.APPROVED.getValue().equals(investmentAccount.getStatus())){
            throw new InvestmentAccountStateTransitionException("approved","approved");            
        }
    	LocalDate today = DateUtils.getLocalDateOfTenant();
    	if(investmentAccount.getActivatedOnDate().isAfter(today)){
    		throw new FutureDateTransactionException("activation");
    	}
    	if(investmentAccount.getActivatedOnDate().isBefore(investmentAccount.getApprovedOnDate())){
    		throw new InvalidDateException("activation", "approved");
    	}
    	if(investmentAccount.getActivatedOnDate().isAfter(investmentAccount.getInvestmentOnDate())){
    		throw new InvalidDateException("investment", "activation");
    	}
    	for (InvestmentAccountSavingsLinkages investmentSavingAccount : investmentAccount.getInvestmentAccountSavingsLinkages()) {
    		if(investmentAccount.getActivatedOnDate().isBefore(investmentSavingAccount.getSavingsAccount().getActivationLocalDate())){
        		throw new InvalidDateException("activation", "savings.activated");
        	}
		}
    }
    
    public void validateForInvestmentAccountToReject(InvestmentAccount investmentAccount,final String json) {
        if(!InvestmentAccountStatus.PENDING_APPROVAL.getValue().equals(investmentAccount.getStatus())){
        	throw new InvestmentAccountStateTransitionException("pending.approve","pending approval");
        }
    }
    
    public void validateForInvestmentAccountToUndoApproval(InvestmentAccount investmentAccount) {
        if(!InvestmentAccountStatus.APPROVED.getValue().equals(investmentAccount.getStatus())){
            throw new InvestmentAccountStateTransitionException("approved","approved");
        }
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
    
    public void validateSavingsAccountBalanceForInvestment(final SavingsAccount savingAccount, final BigDecimal savingsAccountInvestmentAmount, LocalDate date){
        BigDecimal savingsAccountBalance = savingAccount.getWithdrawalBalanceAsOfDate(date);
        if(MathUtility.isLesser(savingsAccountBalance, savingsAccountInvestmentAmount)){
            throw new InsufficientAccountBalanceException(savingAccount.getAccountNumber(),savingAccount.getAccountNumber());
        }
    }
    
    
    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors);
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_UPADTE_DATA_PARAMETERS);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.officeIdParamName, element)) {
            final Long officeId = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.officeIdParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.officeIdParamName).value(officeId).notNull()
                    .longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.partnerIdParamName, element)) {
            final Long partnerId = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.partnerIdParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.partnerIdParamName).value(partnerId).notNull()
                    .longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.investmetProductIdParamName, element)) {
            final Long investmetProductId = this.fromApiJsonHelper
                    .extractLongNamed(InvestmentAccountApiConstants.investmetProductIdParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmetProductIdParamName).value(investmetProductId)
                    .notNull().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.investmetProductIdParamName, element)) {
            final Integer status = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(InvestmentAccountApiConstants.statusParamName,
                    element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.statusParamName).value(status).notNull()
                    .integerSameAsNumber(InvestmentAccountStatus.PENDING_APPROVAL.getValue());
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(InvestmentAccountApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.currencyCodeParamName, element)) {
            final String currencyCode = this.fromApiJsonHelper.extractStringNamed(InvestmentAccountApiConstants.currencyCodeParamName,
                    element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.currencyCodeParamName).value(currencyCode).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.digitsAfterDecimalParamName, element)) {
            final Integer digitsAfterDecimal = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(InvestmentAccountApiConstants.digitsAfterDecimalParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.digitsAfterDecimalParamName).value(digitsAfterDecimal)
                    .notNull().inMinMaxRange(0, 6);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.inMultiplesOfParamName, element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed(InvestmentAccountApiConstants.inMultiplesOfParamName,
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.inMultiplesOfParamName).value(inMultiplesOf).ignoreIfNull()
                    .integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper
                    .extractLocalDateNamed(InvestmentAccountApiConstants.submittedOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.approvedOnDateParamName, element)) {
            final LocalDate approvedOnDate = this.fromApiJsonHelper
                    .extractLocalDateNamed(InvestmentAccountApiConstants.approvedOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.approvedOnDateParamName).value(approvedOnDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.activatedOnDateParamName, element)) {
            final LocalDate activatedOnDate = this.fromApiJsonHelper
                    .extractLocalDateNamed(InvestmentAccountApiConstants.activatedOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.activatedOnDateParamName).value(activatedOnDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.investmentOnDateParamName, element)) {
            final LocalDate investmentOnDate = this.fromApiJsonHelper
                    .extractLocalDateNamed(InvestmentAccountApiConstants.investmentOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentOnDateParamName).value(investmentOnDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.investmentAmountParamName, element)) {
            BigDecimal investmentAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(InvestmentAccountApiConstants.investmentAmountParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentAmountParamName).value(investmentAmount).notNull()
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.interestRateParamName, element)) {
            final BigDecimal interestRate = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(InvestmentAccountApiConstants.interestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.interestRateParamName).value(interestRate).positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.interestRateTypeParamName, element)) {
            Integer interestRateType = this.fromApiJsonHelper
                    .extractIntegerWithLocaleNamed(InvestmentAccountApiConstants.interestRateTypeParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.interestRateTypeParamName).value(interestRateType).notNull()
                    .inMinMaxRange(2, 3);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.investmentTermParamName, element)) {
            Integer investmentTerm = this.fromApiJsonHelper
                    .extractIntegerWithLocaleNamed(InvestmentAccountApiConstants.investmentTermParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentTermParamName).value(investmentTerm).notNull()
                    .integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.investmentTermTypeParamName, element)) {
            Integer investmentTermType = this.fromApiJsonHelper
                    .extractIntegerWithLocaleNamed(InvestmentAccountApiConstants.investmentTermTypeParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.investmentTermTypeParamName).value(investmentTermType)
                    .notNull().inMinMaxRange(0, 2);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.maturityOnDateParamName, element)) {
            final LocalDate maturityOnDate = this.fromApiJsonHelper
                    .extractLocalDateNamed(InvestmentAccountApiConstants.maturityOnDateParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.maturityOnDateParamName).value(maturityOnDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.maturityAmountParamName, element)) {
            final BigDecimal maturityAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(InvestmentAccountApiConstants.maturityAmountParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.maturityAmountParamName).value(maturityAmount).notNull()
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.reinvestAfterMaturityParamName, element)) {
            boolean reinvestAfterMaturity = this.fromApiJsonHelper
                    .extractBooleanNamed(InvestmentAccountApiConstants.reinvestAfterMaturityParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.reinvestAfterMaturityParamName).value(reinvestAfterMaturity)
                    .notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.staffIdParamName, element)) {
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.staffIdParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.staffIdParamName).value(staffId).ignoreIfNull()
                    .longGreaterThanZero();
        }

        boolean trackSourceAccounts = false;
        if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.trackSourceAccountsParamName, element)) {
            trackSourceAccounts = this.fromApiJsonHelper.extractBooleanNamed(InvestmentAccountApiConstants.trackSourceAccountsParamName,
                    element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.trackSourceAccountsParamName).value(trackSourceAccounts)
                    .notNull();
        }

        Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        baseDataValidator.reset().parameter(InvestmentAccountApiConstants.localeParamName).value(locale).notNull();

        if (trackSourceAccounts) {
            final JsonArray savingsAccountActions = this.fromApiJsonHelper
                    .extractJsonArrayNamed(InvestmentAccountApiConstants.savingsAccountsParamName, element);
            baseDataValidator.reset().parameter(InvestmentAccountApiConstants.savingsAccountsParamName).value(savingsAccountActions)
                    .jsonArrayNotEmpty();

            // investment account and savings account linkages validation
            for (JsonElement savingsAccountElement : savingsAccountActions) {
                if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.idParamName, element)) {
                    final Long id = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.idParamName,
                            savingsAccountElement);
                    baseDataValidator.reset().parameter(InvestmentAccountApiConstants.idParamName).value(id).notNull()
                            .longGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.savingsAccountIdParamName, element)) {
                    final Long savingsAccountId = this.fromApiJsonHelper
                            .extractLongNamed(InvestmentAccountApiConstants.savingsAccountIdParamName, savingsAccountElement);
                    baseDataValidator.reset().parameter(InvestmentAccountApiConstants.savingsAccountIdParamName).value(savingsAccountId)
                            .notNull().longGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.individualInvestmentAmountParamName, element)) {
                    final BigDecimal individualInvestmentAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                            InvestmentAccountApiConstants.individualInvestmentAmountParamName, savingsAccountElement, locale);
                    baseDataValidator.reset().parameter(InvestmentAccountApiConstants.individualInvestmentAmountParamName)
                            .value(individualInvestmentAmount).notNull().positiveAmount();
                }

            }
        }
        
        validateCharges(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateForSavingsAccountlinkages(final Set<InvestmentAccountSavingsLinkages> savingsAccountlinkages,
            final BigDecimal investmentAmount) {
        BigDecimal sumOfInvestmentAmount = BigDecimal.ZERO;
        for (InvestmentAccountSavingsLinkages accountLink : savingsAccountlinkages) {
            sumOfInvestmentAmount = sumOfInvestmentAmount.add(accountLink.getInvestmentAmount());
        }
        if (!MathUtility.isEqual(sumOfInvestmentAmount, investmentAmount)) {
            throw new InvestmentAccountAmountException();
        }
    }
    
}
