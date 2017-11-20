package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountDataValidator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component
public  class InvestmentAccountDataAssembler {

    private final ChargeRepositoryWrapper chargeRepository;
    private final OfficeRepositoryWrapper officeReposiotory;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final InvestmentProductRepositoryWrapper investmentProductRepository;
    private final SavingsAccountRepositoryWrapper savingsAccountRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final InvestmentAccountDataValidator investmentAccountDataValidator;

    @Autowired
    public InvestmentAccountDataAssembler(final ChargeRepositoryWrapper chargeRepository,
            final OfficeRepositoryWrapper officeReposiotory,
            final CodeValueRepositoryWrapper codeValueRepository,
            final InvestmentProductRepositoryWrapper investmentProductRepository,
            final SavingsAccountRepositoryWrapper savingsAccountRepository,
            final FromJsonHelper fromApiJsonHelper,
            final StaffRepositoryWrapper staffRepositoryWrapper,
            final InvestmentAccountDataValidator investmentAccountDataValidator) {
        this.chargeRepository = chargeRepository;
        this.officeReposiotory = officeReposiotory;
        this.codeValueRepository = codeValueRepository;
        this.investmentProductRepository = investmentProductRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.investmentAccountDataValidator = investmentAccountDataValidator;
    }
    
    public InvestmentAccount createAssemble(final JsonCommand command, final AppUser appUser) {
        
        final Long officeId =  command.longValueOfParameterNamed(InvestmentAccountApiConstants.officeIdParamName);
        final Office office = this.officeReposiotory.findOneWithNotFoundDetection(officeId);
        
        final Long partnerId =  command.longValueOfParameterNamed(InvestmentAccountApiConstants.partnerIdParamName);
        final CodeValue partner = this.codeValueRepository.findOneWithNotFoundDetection(partnerId);
    
        final Long investmetProductId =  command.longValueOfParameterNamed(InvestmentAccountApiConstants.investmetProductIdParamName);
        final InvestmentProduct investmentProduct = this.investmentProductRepository.findOneWithNotFoundDetection(investmetProductId);
        
        final Integer status = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.statusParamName);
        
        final String externalId = command.stringValueOfParameterNamed(InvestmentAccountApiConstants.externalIdParamName);
    
        final String currencyCode = command.stringValueOfParameterNamed(InvestmentAccountApiConstants.currencyCodeParamName);
        final Integer digitsAfterDecimal = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.digitsAfterDecimalParamName);
        final Integer inMultiplesOf = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.inMultiplesOfParamName);
        final MonetaryCurrency currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal, inMultiplesOf);

        final Date submittedOnDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.submittedOnDateParamName).toDate();
        Date approvedOnDate = null; 
        if(command.parameterExists(InvestmentAccountApiConstants.approvedOnDateParamName)){
            approvedOnDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.approvedOnDateParamName).toDate();
        }
        
        Date activatedOnDate = null; 
        if(command.parameterExists(InvestmentAccountApiConstants.activatedOnDateParamName)){
            activatedOnDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.activatedOnDateParamName).toDate();
        }
        
        final Date investmentOnDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.investmentOnDateParamName).toDate();
        final BigDecimal investmentAmount = command.bigDecimalValueOfParameterNamed(InvestmentAccountApiConstants.investmentAmountParamName);
        final BigDecimal interestRate = command.bigDecimalValueOfParameterNamed(InvestmentAccountApiConstants.interestRateParamName);
        final Integer interestRateType = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.interestRateTypeParamName);
        final Integer investmentTerm = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.investmentTermParamName);
        final Integer investmentTermType = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.investmentTermTypeParamName);
        final Date maturityOnDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.maturityOnDateParamName).toDate();
        final BigDecimal maturityAmount = command.bigDecimalValueOfParameterNamed(InvestmentAccountApiConstants.maturityAmountParamName);
        boolean reinvestAfterMaturity = command.booleanPrimitiveValueOfParameterNamed(InvestmentAccountApiConstants.reinvestAfterMaturityParamName);
        boolean trackSourceAccounts = command.booleanPrimitiveValueOfParameterNamed(InvestmentAccountApiConstants.trackSourceAccountsParamName);
        final Long staffId =  command.longValueOfParameterNamed(InvestmentAccountApiConstants.staffIdParamName);
        Staff staff = null;
        if(staffId != null){
             staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(staffId);
        }
        
        InvestmentAccount investmentAccount = InvestmentAccount.create(null, externalId,  office,  partner, investmentProduct, status,  currency,  submittedOnDate, appUser,
                approvedOnDate, null,  activatedOnDate,  null, investmentOnDate, appUser, investmentAmount,interestRate, interestRateType, 
                investmentTerm, investmentTermType, maturityOnDate,  appUser,  maturityAmount, reinvestAfterMaturity,null,null,null,null,null,null,
                staff,trackSourceAccounts);
         if(trackSourceAccounts){
             investmentAccount = assembleInvestmentAccountSavingsLinkages(command,investmentAccount);
         }      
         investmentAccount =  assembleInvestmentAccountCharges(command,investmentAccount);
         
         return investmentAccount;
    }
    
    private InvestmentAccount assembleInvestmentAccountSavingsLinkages( JsonCommand command, InvestmentAccount investmentAccount){
     
        Locale locale =command.extractLocale();
        final JsonArray actions = command.arrayOfParameterNamed(InvestmentAccountApiConstants.savingsAccountsParamName);
        if (actions != null) {
            Set<InvestmentAccountSavingsLinkages> savingsAccountlinkages = new HashSet<>();
            for (int i = 0; i < actions.size(); i++) {
                final JsonObject actionElement = actions.get(i).getAsJsonObject();
                final Long savingsAccountId = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.savingsAccountIdParamName, actionElement);
                final SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsAccountId);
                final BigDecimal individualInvestmentAmount = this.fromApiJsonHelper.extractBigDecimalNamed(InvestmentAccountApiConstants.individualInvestmentAmountParamName, actionElement,locale);
                final Integer status = InvestmentAccountStatus.PENDING_APPROVAL.getValue();
                InvestmentAccountSavingsLinkages accountLink = new InvestmentAccountSavingsLinkages(investmentAccount, savingsAccount, individualInvestmentAmount,
                         status, null, null);
                savingsAccountlinkages.add(accountLink);
            }      
            investmentAccount.setInvestmentAccountSavingsLinkages(savingsAccountlinkages);
        }
        
        return investmentAccount;
    }
    
    private InvestmentAccount assembleInvestmentAccountCharges( JsonCommand command, InvestmentAccount investmentAccount){
        
        final JsonArray charges = command.arrayOfParameterNamed(InvestmentAccountApiConstants.chargesParamName);
        if (charges != null && charges.size() > 0) {
            Set<InvestmentAccountCharge> chargesSet = new HashSet<>();
            for (int i = 0; i < charges.size(); i++) {
                final JsonObject actionElement = charges.get(i).getAsJsonObject();
                final Long chargeId = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.chargeIdParamName, actionElement);
                final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                boolean isPenality = this.fromApiJsonHelper.extractBooleanNamed(InvestmentAccountApiConstants.isPentalityParamName, actionElement);
                boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(InvestmentAccountApiConstants.isActiveParamName, actionElement);
                Date inactivationDate = null; 
                if(this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.inactivationDateParamName, actionElement)){
                    inactivationDate = this.fromApiJsonHelper.extractLocalDateNamed(InvestmentAccountApiConstants.inactivationDateParamName, actionElement).toDate();
                    inactivationDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.inactivationDateParamName).toDate();
                }
                InvestmentAccountCharge investmentAccountCharge = new InvestmentAccountCharge(investmentAccount, charge, isPenality, isActive,inactivationDate);
                chargesSet.add(investmentAccountCharge);
            }      
            investmentAccount.setInvestmentAccountCharges(chargesSet);
        }
        
        return investmentAccount;
    }
    

}
