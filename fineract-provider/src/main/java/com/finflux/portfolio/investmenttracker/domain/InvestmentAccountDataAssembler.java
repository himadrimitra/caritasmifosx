package com.finflux.portfolio.investmenttracker.domain;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.isDeletedParamName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
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
    private final InvestmentAccountSavingsLinkagesRepositoryWrapper investmentAccountSavingsLinkagesRepositoryWrapper;
    
    @Autowired
    public InvestmentAccountDataAssembler(final ChargeRepositoryWrapper chargeRepository, final OfficeRepositoryWrapper officeReposiotory,
            final CodeValueRepositoryWrapper codeValueRepository, final InvestmentProductRepositoryWrapper investmentProductRepository,
            final SavingsAccountRepositoryWrapper savingsAccountRepository, final FromJsonHelper fromApiJsonHelper,
            final InvestmentAccountDataValidator investmentAccountDataValidator, final StaffRepositoryWrapper staffRepositoryWrapper,
            final InvestmentAccountSavingsLinkagesRepositoryWrapper investmentAccountSavingsLinkagesRepositoryWrapper) {
        this.chargeRepository = chargeRepository;
        this.officeReposiotory = officeReposiotory;
        this.codeValueRepository = codeValueRepository;
        this.investmentProductRepository = investmentProductRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.investmentAccountDataValidator = investmentAccountDataValidator;
        this.investmentAccountSavingsLinkagesRepositoryWrapper = investmentAccountSavingsLinkagesRepositoryWrapper;

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
        
        InvestmentAccount investmentAccount = InvestmentAccount.create(externalId, office,  partner,  investmentProduct, status, currency,  submittedOnDate,  appUser, approvedOnDate,
                null, activatedOnDate,  null,  investmentOnDate, appUser, investmentAmount, interestRate,interestRateType, investmentTerm, 
                investmentTermType, maturityOnDate, appUser,  maturityAmount,  reinvestAfterMaturity, staff,trackSourceAccounts);
         if(trackSourceAccounts){
             investmentAccount = assembleInvestmentAccountSavingsLinkages(command,investmentAccount);
         }      
         investmentAccount =  assembleInvestmentAccountCharges(command,investmentAccount, maturityAmount.subtract(investmentAmount));
         
         return investmentAccount;
    }
    
    public void updateAssemble(final JsonCommand command, InvestmentAccount accountForUpdate, final Map<String, Object> changes) {
        if (changes.containsKey(InvestmentAccountApiConstants.officeIdParamName)) {
            final Long officeId = command.longValueOfParameterNamed(InvestmentAccountApiConstants.officeIdParamName);
            final Office office = this.officeReposiotory.findOneWithNotFoundDetection(officeId);
            accountForUpdate.updateOffice(office);
        }
        if (changes.containsKey(InvestmentAccountApiConstants.partnerIdParamName)) {
            final Long partnerId = command.longValueOfParameterNamed(InvestmentAccountApiConstants.partnerIdParamName);
            final CodeValue partner = this.codeValueRepository.findOneWithNotFoundDetection(partnerId);
            accountForUpdate.updatePrtner(partner);
        }
        if (changes.containsKey(InvestmentAccountApiConstants.investmetProductIdParamName)) {
            final Long productid = command.longValueOfParameterNamed(InvestmentAccountApiConstants.investmetProductIdParamName);
            final InvestmentProduct investmentProduct = this.investmentProductRepository.findOneWithNotFoundDetection(productid);
            accountForUpdate.updateInvestmentProduct(investmentProduct);
        }
        if (changes.containsKey(InvestmentAccountApiConstants.staffIdParamName)) {
            final Long staffId = command.longValueOfParameterNamed(InvestmentAccountApiConstants.staffIdParamName);
            final Staff staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(staffId);
            accountForUpdate.updateStaff(staff);
        }
        
        if (changes.containsKey(InvestmentAccountApiConstants.savingsAccountsParamName)) {
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = assembleInvestmentAccountSavingsLinkagesForUpdate(
                    command, accountForUpdate);
            if(investmentAccountSavingsLinkages.size() > 0){
                accountForUpdate.updateInvestmentAccountSavingsLinkages(investmentAccountSavingsLinkages);
            }
        }
        
        if (changes.containsKey(InvestmentAccountApiConstants.chargesParamName)) {
            Set<InvestmentAccountCharge> chargesSet = createInvestmentAccountCharges(command, accountForUpdate,
                    accountForUpdate.getMaturityAmount().subtract(accountForUpdate.getInvestmentAmount()));
            if(chargesSet.size() > 0){
                accountForUpdate.updateInvestmentAccountCharges(chargesSet);
            }
        }
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
                String accountHolder = null;
                if(savingsAccount.getClient() != null){
                    accountHolder = savingsAccount.getClient().getDisplayName();
                }else{
                    accountHolder = savingsAccount.getGroup().getName();
                }
                InvestmentAccountSavingsLinkages accountLink = new InvestmentAccountSavingsLinkages(investmentAccount, accountHolder, savingsAccount, individualInvestmentAmount,
                         status);
                savingsAccountlinkages.add(accountLink);
            }      
            investmentAccount.setInvestmentAccountSavingsLinkages(savingsAccountlinkages);
        }
        
        return investmentAccount;
    }
    
    private Set<InvestmentAccountSavingsLinkages> assembleInvestmentAccountSavingsLinkagesForUpdate(JsonCommand command,
            InvestmentAccount investmentAccount) {
        Locale locale = command.extractLocale();
        final JsonArray actions = command.arrayOfParameterNamed(InvestmentAccountApiConstants.savingsAccountsParamName);
        Set<InvestmentAccountSavingsLinkages> savingsAccountlinkages = new HashSet<>();
        if (actions != null) {
            for (int i = 0; i < actions.size(); i++) {
                final JsonObject actionElement = actions.get(i).getAsJsonObject();
                final Long id = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.idParamName, actionElement);
                final Long savingsAccountId = this.fromApiJsonHelper
                        .extractLongNamed(InvestmentAccountApiConstants.savingsAccountIdParamName, actionElement);
                final SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsAccountId);
                final BigDecimal individualInvestmentAmount = this.fromApiJsonHelper
                        .extractBigDecimalNamed(InvestmentAccountApiConstants.individualInvestmentAmountParamName, actionElement, locale);
                final Integer status = InvestmentAccountStatus.PENDING_APPROVAL.getValue();
                if (id == null) {
                    String accountHolder = null;
                    if (savingsAccount.getClient() != null) {
                        accountHolder = savingsAccount.getClient().getDisplayName();
                    } else {
                        accountHolder = savingsAccount.getGroup().getName();
                    }
                    InvestmentAccountSavingsLinkages accountLink = new InvestmentAccountSavingsLinkages(investmentAccount, accountHolder,
                            savingsAccount, individualInvestmentAmount, status);
                    savingsAccountlinkages.add(accountLink);
                } else {
                    InvestmentAccountSavingsLinkages accountLink = this.investmentAccountSavingsLinkagesRepositoryWrapper
                            .findOneWithNotFoundDetection(id);
                    accountLink.updateInvestmentAmount(individualInvestmentAmount);
                    savingsAccountlinkages.add(accountLink);
                }
            }
        }
        investmentAccountDataValidator.validateForSavingsAccountlinkages(savingsAccountlinkages, investmentAccount.getInvestmentAmount());
        return savingsAccountlinkages;
    }
    
    private InvestmentAccount assembleInvestmentAccountCharges(JsonCommand command, InvestmentAccount investmentAccount,
            BigDecimal interestAmount) {
        Set<InvestmentAccountCharge> chargesSet = createInvestmentAccountCharges(command, investmentAccount, interestAmount);
        investmentAccount.setInvestmentAccountCharges(chargesSet);
        return investmentAccount;
    }
    
    private Set<InvestmentAccountCharge> createInvestmentAccountCharges(JsonCommand command, InvestmentAccount investmentAccount,
            BigDecimal interestAmount) {
        Set<InvestmentAccountCharge> chargesSet = new HashSet<>();
        final JsonArray charges = command.arrayOfParameterNamed(InvestmentAccountApiConstants.chargesParamName);
        if (charges != null && charges.size() > 0) {
            for (int i = 0; i < charges.size(); i++) {
                final JsonObject actionElement = charges.get(i).getAsJsonObject();
                final Long chargeId = this.fromApiJsonHelper.extractLongNamed(InvestmentAccountApiConstants.chargeIdParamName,
                        actionElement);
                final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                boolean isPenality = this.fromApiJsonHelper.extractBooleanNamed(InvestmentAccountApiConstants.isPentalityParamName,
                        actionElement);
                boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(InvestmentAccountApiConstants.isActiveParamName,
                        actionElement);
                Date inactivationDate = null;
                if (this.fromApiJsonHelper.parameterExists(InvestmentAccountApiConstants.inactivationDateParamName, actionElement)) {
                    inactivationDate = this.fromApiJsonHelper
                            .extractLocalDateNamed(InvestmentAccountApiConstants.inactivationDateParamName, actionElement).toDate();
                    inactivationDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.inactivationDateParamName)
                            .toDate();
                }
                InvestmentAccountCharge investmentAccountCharge = null;
                if (charge.isPercentageOfInterest()) {
                    BigDecimal chargeAmount = Money
                            .of(investmentAccount.getCurrency(), MathUtility.percentageOf(interestAmount, charge.getAmount())).getAmount();
                    investmentAccountCharge = new InvestmentAccountCharge(investmentAccount, charge, chargeAmount, isPenality, isActive,
                            inactivationDate);
                } else {
                    investmentAccountCharge = new InvestmentAccountCharge(investmentAccount, charge, charge.getAmount(), isPenality,
                            isActive, inactivationDate);
                    if (charge.isPercentageOfInterest()) {
                        BigDecimal chargeAmount = MathUtility.percentageOf(interestAmount, charge.getAmount());
                        investmentAccountCharge = new InvestmentAccountCharge(investmentAccount, charge, chargeAmount, isPenality, isActive,
                                inactivationDate);
                    } else {
                        investmentAccountCharge = new InvestmentAccountCharge(investmentAccount, charge, charge.getAmount(), isPenality,
                                isActive, inactivationDate);
                    }

                    chargesSet.add(investmentAccountCharge);
                }

            }
        }
        return chargesSet;
    }
    

}
