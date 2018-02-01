package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.api.InvestmentProductApiconstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountChargeData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountSavingsLinkagesData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountTimelineData;
import com.finflux.portfolio.investmenttracker.data.InvestmentProductData;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountStatus;
import com.finflux.portfolio.investmenttracker.domain.InvestmentProduct;
import com.finflux.portfolio.investmenttracker.domain.InvestmentProductRepositoryWrapper;
import com.google.gson.Gson;

@Service
public class InvestmentAccountReadServiceImpl implements InvestmentAccountReadService {

    private final JdbcTemplate jdbcTemplate;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final InvestmentProductReadService investmentProductReadService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final InvestmentTrackerDropDownReadService dropdownReadPlatformService;
    private final PlatformSecurityContext context;
    private final StaffReadPlatformService staffReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final InvestmentProductRepositoryWrapper investmentProductRepositoryWrapper;
    
    @Autowired
    public InvestmentAccountReadServiceImpl(final RoutingDataSource dataSource,
             final ChargeReadPlatformService chargeReadPlatformService,
             final InvestmentProductReadService investmentProductReadService,
             final OfficeReadPlatformService officeReadPlatformService,
             final CodeValueReadPlatformService codeValueReadPlatformService,
             final InvestmentTrackerDropDownReadService dropdownReadPlatformService,
             final PlatformSecurityContext context,
             final StaffReadPlatformService staffReadPlatformService,
             final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
             final InvestmentProductRepositoryWrapper investmentProductRepositoryWrapper){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.investmentProductReadService = investmentProductReadService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.context = context;
        this.staffReadPlatformService = staffReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.investmentProductRepositoryWrapper = investmentProductRepositoryWrapper;
    }
    
    @Override
    public InvestmentAccountData retrieveInvestmentAccountTemplate(InvestmentAccountData investmentAccountData,
            final boolean staffInSelectedOfficeOnly, final Long officeId) {
        
        Collection<ChargeData> investmentChargeOptions = this.chargeReadPlatformService.retrieveInvestmentProductApplicableCharges();
        Collection<CodeValueData> partnerOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Investmentpartners");
        Collection<OfficeData> officeDataOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        Collection<InvestmentProductData> investmentProductOptions = this.investmentProductReadService.retrieveAllLookUpData();
        final List<EnumOptionData> interestRateFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInvestmentTermFrequencyTypeOptions();
        
        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);
        final boolean loanOfficersOnly = false;
        Collection<StaffData> staffOptions = null;
        if (staffInSelectedOfficeOnly) {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(defaultOfficeId);
        } else {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(defaultOfficeId,
                    loanOfficersOnly);
        }
        List<SavingsAccountData> savingsAccounts = null;
        if(officeId != null){
            OfficeData officeData = this.officeReadPlatformService.retrieveOffice(officeId);
             savingsAccounts = this.savingsAccountReadPlatformService.retrieveAllActiveSavingsAccountsByOffice(officeData.getHierarchy());
        }        
        final List<CodeValueData> categoryOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(InvestmentProductApiconstants.investmentCategory)); 
        Collection<EnumOptionData> investmentAccountStatus = InvestmentAccountStatus.investmentAccountStatusTypeOptions();
        
        if(investmentAccountData != null){
            return InvestmentAccountData.withTemplateData(investmentAccountData,partnerOptions, investmentProductOptions, investmentChargeOptions, officeDataOptions,
                    interestRateFrequencyTypeOptions, investmentTermFrequencyTypeOptions,staffOptions,savingsAccounts, investmentAccountStatus, categoryOptions);
        }
        
        return InvestmentAccountData.onlyTemplateData(partnerOptions, investmentProductOptions, investmentChargeOptions, officeDataOptions,
                interestRateFrequencyTypeOptions, investmentTermFrequencyTypeOptions,staffOptions,savingsAccounts, investmentAccountStatus, categoryOptions);
       
    }

    @Override
    public Collection<InvestmentAccountData> retrieveAll(final SearchParameters searchParameters) {
        InvestmentAccountMapper investmentAccountMapper = new InvestmentAccountMapper();
        String sql = "SELECT " + investmentAccountMapper.schema();
        Long officeId = searchParameters.getOfficeId();
        Long partnerId = searchParameters.getPartnerId();
        Long categoryId = searchParameters.getCategoryId();
        if(categoryId != null){
            sql = sql+"";
        }
        Long investmetnProductId = searchParameters.getProductId();
        Integer status = searchParameters.getStatus();
        LocalDate maturityStartDate = null;
        if(searchParameters.getStartDate() != null){
             maturityStartDate = new LocalDate(searchParameters.getStartDate()); 
        }
        LocalDate maturityEndDate = null;
        if(searchParameters.getEndDate() != null){
            maturityEndDate = new LocalDate(searchParameters.getEndDate()); 
        }
        StringBuilder queryParams = new StringBuilder();
        if(officeId != null){
            queryParams.append(" WHERE fia.office_id = ").append(officeId);      
        }
        if( partnerId != null){
            if(queryParams.length() > 0)
                queryParams.append(" and fia.partner_id = ").append(partnerId);
            else
                queryParams.append(" WHERE fia.partner_id = ").append(partnerId);
        }
        if( categoryId != null){
            if(queryParams.length() > 0)
                queryParams.append(" and fip.category = ").append(categoryId);
            else
                queryParams.append(" WHERE fip.category = ").append(categoryId);
        }
        if( investmetnProductId != null){
            if(queryParams.length() > 0)
                queryParams.append(" and fia.investment_product_id = ").append(investmetnProductId);
            else
                queryParams.append(" WHERE fia.investment_product_id = ").append(investmetnProductId);
        }
        if( status != null){
            if(queryParams.length() > 0)
                queryParams.append(" and fia.status_enum = ").append(status);
            else
                queryParams.append(" WHERE fia.status_enum = ").append(status);
        }
        if( maturityStartDate != null){
            if(queryParams.length() > 0)
                queryParams.append(" and fia.maturityon_date >= ").append("'"+maturityStartDate+"'");
            else
                queryParams.append(" WHERE fia.maturityon_date >= ").append("'"+maturityStartDate+"'");
        }
        if( maturityEndDate != null){
            if(queryParams.length() > 0)
                queryParams.append(" and fia.maturityon_date <= ").append("'"+maturityEndDate+"'");
            else
                queryParams.append(" WHERE fia.maturityon_date <= ").append("'"+maturityEndDate+"'");
        }
        
        if(queryParams.length() > 0){
            sql = sql + queryParams.toString() + " ORDER BY fia.maturityon_date ";
        }else{
            sql = sql + " ORDER BY fia.maturityon_date ";
        }
        
        Collection<InvestmentAccountData> investmentAccountDetails = this.jdbcTemplate.query(sql, investmentAccountMapper);
        return investmentAccountDetails;
    }
    
    @Override
    public InvestmentAccountData retrieveInvestmentAccount(Long investmentAccountId) {
        InvestmentAccountMapper investmentAccountMapper = new InvestmentAccountMapper();
        String sql = "SELECT " + investmentAccountMapper.schema() +  " where fia.id = ?;";
        InvestmentAccountData investmentAccountData = this.jdbcTemplate.queryForObject(sql, investmentAccountMapper, investmentAccountId);
        return investmentAccountData;
    }
    
    @Override
    public Collection<InvestmentAccountSavingsLinkagesData> retrieveInvestmentAccountSavingLinkages(Long investmentAccountId) {
        try{
        InvestmentAccountSavingsLinkagesMapper linkageMapper = new InvestmentAccountSavingsLinkagesMapper();
        
        String sql = "SELECT " + linkageMapper.schema() + " WHERE ia.id = ? order by sa.id;";
        
        Collection<InvestmentAccountSavingsLinkagesData> linkagesData = this.jdbcTemplate.query(sql, linkageMapper, investmentAccountId);
        
        return linkagesData;
        } catch (final EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Long> retrieveInvestmentAccountSavingLinkagesIds(Long investmentAccountId) {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" SELECT ias.id ");
            sqlBuilder.append(" from m_savings_account sa");
            sqlBuilder.append(" join f_investment_account_savings_linkages ias on ias.savings_account_id = sa.id");
            sqlBuilder.append(" join f_investment_account ia on ia.id = ias.investment_account_id");
            sqlBuilder.append(" WHERE ia.id = ");
            sqlBuilder.append(investmentAccountId);
            sqlBuilder.append(" order by sa.id");
            List<Long> linkagesDataIds = this.jdbcTemplate.queryForList(sqlBuilder.toString(), Long.class);
            return linkagesDataIds;
        } catch (final EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public InvestmentAccountSavingsLinkagesData retrieveInvestmentSavingsLinkageAccountData(final Long investmentAccountId, final Long savingsLinkageAccountId) {
        InvestmentAccountSavingsLinkagesMapper linkageMapper = new InvestmentAccountSavingsLinkagesMapper();
        
        String sql = "SELECT " + linkageMapper.schema() + " WHERE ia.id = ? and ias.id = ? order by sa.id;";
        
        InvestmentAccountSavingsLinkagesData savingLinkageData = this.jdbcTemplate.queryForObject(sql, linkageMapper, new Object[]{ investmentAccountId, savingsLinkageAccountId});
        
        return savingLinkageData;
        
    }

    @Override
    public Collection<InvestmentAccountChargeData> retrieveInvestmentAccountCharges(Long investmentAccountId) {
        InvestmentAccountChargesMapper chargeMapper = new InvestmentAccountChargesMapper();
        String sql = "SELECT " + chargeMapper.schema() + " WHERE ia.id = ? order by c.id;";
        
        Collection<InvestmentAccountChargeData> chargesData = this.jdbcTemplate.query(sql, chargeMapper, investmentAccountId);
        
        return chargesData;
    }
    
    private static final class InvestmentAccountMapper implements RowMapper<InvestmentAccountData> {

        private final String schemaSql;

        public InvestmentAccountMapper() {
            StringBuilder sqlBuilder = new StringBuilder(300);
            sqlBuilder.append(" fia.id, fia.account_no, fia.partner_id, fia.office_id, fia.investment_product_id, fia.external_id, fia.status_enum,");
            sqlBuilder.append(" fia.currency_code, fia.currency_digits, fia.currency_multiplesof,");
            sqlBuilder.append(" curr.name as currencyName, curr.internationalized_name_code as currencyNameCode,");
            sqlBuilder.append(" curr.display_symbol as currencyDisplaySymbol,  fia.submittedon_date, fia.submittedon_userid,");
            sqlBuilder.append(" fia.approvedon_date, fia.approvedon_userid, fia.activatedon_date, fia.activatedon_userid, fia.investmenton_date,");
            sqlBuilder.append(" fia.investmenton_userid, fia.investment_amount, fia.interest_rate, fia.interest_rate_type, fia.investment_term,");
            sqlBuilder.append(" fia.investment_term_type, fia.maturityon_date, fia.maturityon_userid, fia.maturity_amount, fia.reinvest_after_maturity,");
            sqlBuilder.append(" sbu.username as submittedByUsername, sbu.firstname as submittedByFirstname, sbu.lastname as submittedByLastname,");
            sqlBuilder.append(" apu.username as approvedByUsername, sbu.firstname as approvedByFirstname, sbu.lastname as approvedByLastname,");
            sqlBuilder.append(" acu.username as activatedByUsername, acu.firstname as activatedByFirstname, acu.lastname as activatedByLastname,");
            sqlBuilder.append(" ivu.username as investedByUsername, ivu.firstname as investedByFirstname, ivu.lastname as investedByLastname,");
            sqlBuilder.append(" mu.username as maturityByUsername, mu.firstname as maturityByFirstname, mu.lastname as maturityByLastname,");
            sqlBuilder.append(" off.id as officeId, off.name as officeName, staff.display_name, fia.staff_id,");
            sqlBuilder.append(" cv.id  as partnerId, cv.code_value as partnerName,fia.track_source_accounts,");
            sqlBuilder.append(" fip.id as investmentProductId, fip.name as investmentProductName,");
            sqlBuilder.append(" fia.rejecton_date,ru.username as rejectByUsername, ru.firstname as rejectByFirstname, ru.lastname as rejectByLastname,");
            sqlBuilder.append(" fia.closeon_date,cu.username as closeByUsername, cu.firstname as closeByFirstname, cu.lastname as closeByLastname");
            sqlBuilder.append(" from f_investment_account fia");
            sqlBuilder.append(" join m_currency curr on curr.code = fia.currency_code");
            sqlBuilder.append(" join m_office off on off.id = fia.office_id");
            sqlBuilder.append(" join m_code_value cv on cv.id = fia.partner_id");
            sqlBuilder.append(" join f_investment_product fip on fip.id =  fia.investment_product_id");
            sqlBuilder.append(" left join m_code_value category_cv on category_cv.id = fip.category");
            sqlBuilder.append(" left join m_appuser sbu on sbu.id = fia.submittedon_userid");
            sqlBuilder.append(" left join m_appuser apu on apu.id = fia.approvedon_userid");
            sqlBuilder.append(" left join m_appuser acu on acu.id = fia.activatedon_userid");
            sqlBuilder.append(" left join m_appuser ivu on ivu.id = fia.investmenton_userid");
            sqlBuilder.append(" left join m_appuser mu on mu.id = fia.maturityon_userid");
            sqlBuilder.append(" left join m_appuser ru on ru.id = fia.rejecton_userid");
            sqlBuilder.append(" left join m_appuser cu on cu.id = fia.closeon_userid");
            sqlBuilder.append(" left join m_staff staff on staff.id = fia.staff_id");
            
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public InvestmentAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("account_no");
            final String externalId = rs.getString("external_id");
            final Integer statusEnum = JdbcSupport.getInteger(rs, "status_enum"); 
            EnumOptionData statusEnumData = null;
            if(statusEnum != null){
                 statusEnumData =  InvestmentAccountStatus.fromInt(statusEnum).getEnumOptionData();
            }
            final String currencyCode = rs.getString("currency_code");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currency_digits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "currency_multiplesof");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);
            final BigDecimal investmentAmount = rs.getBigDecimal("investment_amount");
            final BigDecimal interestRate = rs.getBigDecimal("interest_rate");
            final Integer interestRateType = JdbcSupport.getInteger(rs, "interest_rate_type");
            final EnumOptionData interestRateTypeEnum = InvestmentTrackerEnumerations.interestRateFrequencyType(interestRateType);
            final Integer investmentTerm = JdbcSupport.getInteger(rs, "investment_term");
            final Integer investmentType = JdbcSupport.getInteger(rs, "investment_term_type");
            final EnumOptionData invesmentTermPeriodEnum = InvestmentTrackerEnumerations
                    .investmentTermFrequencyType(investmentType);
            final BigDecimal maturityAmount = rs.getBigDecimal("maturity_amount");
            final boolean reinvestAfterMaturity = rs.getBoolean("reinvest_after_maturity");
            
            final Long partnerId = JdbcSupport.getLongDefaultToNullIfZero(rs, "partner_id");
            final String partnerName = rs.getString("partnerName");
            CodeValueData partner = null;
            if(partnerId != null){
                partner = CodeValueData.instance(partnerId, partnerName);
            }
            
            final Long officeId =  JdbcSupport.getLongDefaultToNullIfZero(rs, "office_id");
            final String officeName = rs.getString("officeName");
            OfficeData officeData = null;
            if(officeId != null){
                officeData = OfficeData.lookup(officeId, officeName);
            }
            
            final Long investmentProductId =  JdbcSupport.getLongDefaultToNullIfZero(rs, "investment_product_id");
            final String investmentProductName = rs.getString("investmentProductName");
            InvestmentProductData investmentProductData= null;
            if(investmentProductId != null){
                investmentProductData = InvestmentProductData.lookup(investmentProductId, investmentProductName);
            }
            
            final boolean trackSourceAccounts = rs.getBoolean("track_source_accounts");
            
            final Long staffId = JdbcSupport.getLongDefaultToNullIfZero(rs, "staff_id");
            final String staffName = rs.getString("display_name");
            StaffData staffData = null;
            if(staffId != null){
                staffData = StaffData.lookup(staffId, staffName);
            }
            
            
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedon_date");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");
            
            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedon_date");
            final String approvedByUsername = rs.getString("approvedByUsername");
            final String approvedByFirstname = rs.getString("approvedByFirstname");
            final String approvedByLastname = rs.getString("approvedByLastname");
            
            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedon_date");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstname = rs.getString("activatedByFirstname");
            final String activatedByLastname = rs.getString("activatedByLastname");
            
            final LocalDate investmentOnData = JdbcSupport.getLocalDate(rs, "investmenton_date");
            final String investedByUsername = rs.getString("investedByUsername");
            final String investedByFirstname = rs.getString("investedByFirstname");
            final String investedByLastname = rs.getString("investedByLastname");
            
            final LocalDate maturityOnDate = JdbcSupport.getLocalDate(rs, "maturityon_date");
            final String maturityByUsername = rs.getString("maturityByUsername");
            final String maturityByFirstname = rs.getString("maturityByFirstname");
            final String maturityByLastname = rs.getString("maturityByLastname");
            
            final LocalDate rejectOnDate = JdbcSupport.getLocalDate(rs, "rejecton_date");
            final String rejectByUsername = rs.getString("rejectByUsername");
            final String rejectByFirstname = rs.getString("rejectByFirstname");
            final String rejectByLastname = rs.getString("rejectByLastname");
            
            final LocalDate closeOnDate = JdbcSupport.getLocalDate(rs, "closeon_date");
            final String closeByUsername = rs.getString("closeByUsername");
            final String closeByFirstname = rs.getString("closeByFirstname");
            final String closeByLastname = rs.getString("closeByLastname");
            
            InvestmentAccountTimelineData investmentAccountTimelineData = new InvestmentAccountTimelineData(submittedOnDate,submittedByUsername, submittedByFirstname,
                     submittedByLastname,  approvedOnDate, approvedByUsername, approvedByFirstname, approvedByLastname,  activatedOnDate, activatedByUsername,  activatedByFirstname,
                     activatedByLastname,  investmentOnData,  investedByUsername,  investedByFirstname,
                     investedByLastname,  maturityOnDate,  maturityByUsername,  maturityByFirstname, maturityByLastname,
                     rejectOnDate,rejectByUsername,rejectByFirstname,rejectByLastname,
                     closeOnDate,closeByUsername,closeByFirstname,closeByLastname);


            return InvestmentAccountData.instance(id, accountNo, externalId,currency, interestRate,  interestRateTypeEnum,  investmentTerm,
                    invesmentTermPeriodEnum, investmentAccountTimelineData,  investmentAmount,
                     maturityAmount,  reinvestAfterMaturity, null, null,  officeData, partner, investmentProductData, statusEnumData,
                     staffData,trackSourceAccounts, null);
        }
    }
    
    private static final class InvestmentAccountSavingsLinkagesMapper implements RowMapper<InvestmentAccountSavingsLinkagesData>{
        
        private final String sqlSchema;
        
        public InvestmentAccountSavingsLinkagesMapper(){
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" sa.id as savingsAccountId, sa.account_no as savingsAccountNumber, ias.investment_account_id as investmentAccountId,");
            sqlBuilder.append(" ias.id as id, ias.investment_amount as individualInvestmentAmount,ia.external_id as investmentExternalId,");
            
            sqlBuilder.append(" ias.expected_interest_amount as expectedInterestAmount, ias.interest_amount as interestAmount,");
            sqlBuilder.append(" ias.expected_charge_amount as expectedChargeAmount, ias.charge_amount as chargeAmount,");
            sqlBuilder.append(" ias.expected_maturity_amount as expectedMaturityAmount, ias.maturity_amount as maturityAmount,");

            sqlBuilder.append(" ias.status, ias.account_holder as accountHolder , ias.active_from_date, ias.active_to_date");
            sqlBuilder.append(" from m_savings_account sa");
            sqlBuilder.append(" join f_investment_account_savings_linkages ias on ias.savings_account_id = sa.id");
            sqlBuilder.append(" join f_investment_account ia on ia.id = ias.investment_account_id"); 
            
            sqlSchema = sqlBuilder.toString();
        }
        
        public String schema(){
            return this.sqlSchema;
        }


        @Override
        public InvestmentAccountSavingsLinkagesData mapRow(ResultSet rs, int rowNum) throws SQLException {
             Long id = rs.getLong("id"); 
             Long savingsAccountId = rs.getLong("savingsAccountId");
             String savingsAccountNumber = rs.getString("savingsAccountNumber");
             Long investmentAccountId = rs.getLong("investmentAccountId");
             BigDecimal individualInvestmentAmount = rs.getBigDecimal("individualInvestmentAmount");             
             BigDecimal expectedInterestAmount = rs.getBigDecimal("expectedInterestAmount");
             BigDecimal interestAmount = rs.getBigDecimal("interestAmount");
             BigDecimal expectedChargeAmount = rs.getBigDecimal("expectedChargeAmount");
             BigDecimal chargeAmount = rs.getBigDecimal("chargeAmount");
             BigDecimal expectedMaturityAmount = rs.getBigDecimal("expectedMaturityAmount");
             BigDecimal maturityAmount = rs.getBigDecimal("maturityAmount");
             
             Integer status = JdbcSupport.getInteger(rs, "status");
             EnumOptionData statusEnumData = null;
             if(status != null){
                  statusEnumData =  InvestmentAccountStatus.fromInt(status).getEnumOptionData();
             }
             LocalDate activeFrom = JdbcSupport.getLocalDate(rs, "active_from_date");
             LocalDate activeTo = JdbcSupport.getLocalDate(rs, "active_to_date");
             String accountHolder = rs.getString("accountHolder");
             String investmentExternalId = rs.getString("investmentExternalId");
             InvestmentAccountSavingsLinkagesData data = new InvestmentAccountSavingsLinkagesData(id, savingsAccountId, savingsAccountNumber, investmentAccountId,individualInvestmentAmount,
                     statusEnumData,activeFrom,activeTo, accountHolder, expectedInterestAmount, expectedChargeAmount, expectedMaturityAmount,
                     interestAmount, chargeAmount, maturityAmount,investmentExternalId);
            return data;
        }
    
    }
    
    private static final class InvestmentAccountChargesMapper implements RowMapper<InvestmentAccountChargeData>{
        
        private final String sqlSchema;
        
        public InvestmentAccountChargesMapper(){
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" iac.charge_id as chargeId, iac.investment_account_id as investmentAccountId, iac.is_penalty,");
            sqlBuilder.append(" iac.is_active, iac.inactivated_on_date, iac.amount as amount, ");
            sqlBuilder.append(" c.name, c.charge_time_enum, c.charge_calculation_enum, c.amount as amountOrPercentage ");
            sqlBuilder.append(" from m_charge c");
            sqlBuilder.append(" join f_investment_account_charge iac on iac.charge_id = c.id");
            sqlBuilder.append(" join f_investment_account ia on ia.id = iac.investment_account_id"); 
            
            sqlSchema = sqlBuilder.toString();
        }
        
        public String schema(){
            return this.sqlSchema;
        }


        @Override
        public InvestmentAccountChargeData mapRow(ResultSet rs, int rowNum) throws SQLException {
             Long chargeId = rs.getLong("chargeId");
             Long investmentAccountId = rs.getLong("investmentAccountId");
             boolean isPenality =  rs.getBoolean("is_penalty");
             boolean isActive = rs.getBoolean("is_active");
             LocalDate inactivationDate = JdbcSupport.getLocalDate(rs, "inactivated_on_date");
             int chargeTime = rs.getInt("charge_time_enum");
             String chargeName = rs.getString("name");
             BigDecimal amountOrPercentage = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "amountOrPercentage");
             final ChargeTimeType chargeTimeTypeEnum = ChargeTimeType.fromInt(chargeTime);
             final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTimeTypeEnum);
             final int chargeCalculation = rs.getInt("charge_calculation_enum");
             final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
             BigDecimal amount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "amount");

             InvestmentAccountChargeData data = new InvestmentAccountChargeData(chargeId, investmentAccountId, isActive,isPenality,inactivationDate,
                     chargeName, amountOrPercentage, chargeTimeType, chargeCalculationType, amount);
            return data;
        }
    
    }
    
    private Long defaultToUsersOfficeIfNull(final Long officeId) {
        Long defaultOfficeId = officeId;
        if (defaultOfficeId == null) {
            defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
        }
        return defaultOfficeId;
    }

    @Override
    public InvestmentAccountData retrieveReinvestmentAccountTemplateData(Long investmentAccountId) {
        
        InvestmentAccountData investmentAccountData = retrieveInvestmentAccount(investmentAccountId);
        
        InvestmentProductData investmentProductData = this.investmentProductReadService.retrieveOne(investmentAccountData.getInvestmentProductData().getId());
        investmentAccountData.setInvestmentProductData(investmentProductData);
        
        Collection<InvestmentAccountSavingsLinkagesData> investmentAccSavingsLinkages = retrieveInvestmentSavingLinkagesAccountData(investmentAccountId, InvestmentAccountStatus.MATURED.getValue());
        investmentAccountData.setInvestmentSavingsLinkagesData(investmentAccSavingsLinkages);

        Collection<InvestmentAccountChargeData> charges = retrieveInvestmentAccountCharges(investmentAccountId);
        investmentAccountData.setInvestmentAccountCharges(charges);
        
        boolean isStaffInSelectedOffice = true;
        investmentAccountData = retrieveInvestmentAccountTemplate(investmentAccountData, isStaffInSelectedOffice, investmentAccountData.getOfficeData().getId());
        
        investmentAccountData = investmentAccountReInvestCalculations(investmentAccountData);

        return investmentAccountData;
    }
    

    private InvestmentAccountData investmentAccountReInvestCalculations(final InvestmentAccountData investmentAccountData) {

        // Investment amount calculation
        BigDecimal totalReinvestmentAmount = BigDecimal.ZERO;
        for (InvestmentAccountSavingsLinkagesData linkageData : investmentAccountData.getInvestmentSavingsLinkagesData()) {

            BigDecimal savingsAccInvAmount = linkageData.getMaturityAmount();
            linkageData.setIndividualInvestmentAmount(savingsAccInvAmount);
            totalReinvestmentAmount = totalReinvestmentAmount.add(savingsAccInvAmount);

        }
        investmentAccountData.setInvestmentAmount(totalReinvestmentAmount);

        // maturity amount and maturity date calculation
        BigDecimal rateOfInterestPerDay = BigDecimal.ZERO;
        int investmentTermInDays = 0;
        BigDecimal maturityAmount = BigDecimal.ZERO;
        BigDecimal interestEarned = BigDecimal.ZERO;
        int monthsInYear = 12;
        int daysInWeek = 7;
        BigDecimal daysInYear = new BigDecimal(365);
        LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        LocalDate reinvestmentDate = currentDate;
        LocalDate maturityDate = currentDate;
        switch (investmentAccountData.getInvestmentProductData().getInterestRateType().getId().intValue()) {
        // InvestmentFrequencyType.MONTHS = 2
            case 2:
                rateOfInterestPerDay = MathUtility.multiply(
                        investmentAccountData.getInvestmentProductData().getDefaultNominalInterestRate(), monthsInYear).divide(daysInYear,2, RoundingMode.HALF_UP);
            break;
            // InvestmentFrequencyType.YEARS = 3
            case 3:
                rateOfInterestPerDay = investmentAccountData.getInvestmentProductData().getDefaultNominalInterestRate().divide(daysInYear,2, RoundingMode.HALF_UP);
            break;
        }

        switch (investmentAccountData.getInvestmentProductData().getInvesmentTermPeriodType().getId().intValue()) {
        // InvestmentTermFrequenceyType.DAYS = 0
            case 0:
                investmentTermInDays = investmentAccountData.getInvesmentTermPeriod();
                maturityDate = currentDate.plusDays(investmentTermInDays);
            break;
            // InvestmentTermFrequenceyType.WEEKS = 1
            case 1:
                investmentTermInDays = investmentAccountData.getInvesmentTermPeriod() * daysInWeek;
                maturityDate = currentDate.plusWeeks(investmentAccountData.getInvesmentTermPeriod());
            break;
            // InvestmentTermFrequenceyType.MONTHS = 2
            case 2:
                maturityDate = currentDate.plusMonths(investmentAccountData.getInvesmentTermPeriod());
                investmentTermInDays = Days.daysBetween(reinvestmentDate, maturityDate).getDays();
            break;
        }
        interestEarned = MathUtility.multiply(MathUtility.multiply(totalReinvestmentAmount, investmentTermInDays), rateOfInterestPerDay)
                .divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
        maturityAmount = MathUtility.add(totalReinvestmentAmount, interestEarned);
        investmentAccountData.setMaturityAmount(maturityAmount);
        investmentAccountData.getTimelineData().setMaturityOnDate(maturityDate);
        investmentAccountData.getTimelineData().setInvestmentOnData(reinvestmentDate);

        return investmentAccountData;
    }
    
    @Override
    public Collection<InvestmentAccountSavingsLinkagesData> retrieveInvestmentSavingLinkagesAccountData(final Long investmentAccountId, final Integer status) {
        try{
        InvestmentAccountSavingsLinkagesMapper linkageMapper = new InvestmentAccountSavingsLinkagesMapper();
        
        String sql = "SELECT " + linkageMapper.schema() + " WHERE ia.id = ? and ia.status_enum = ? and ias.status = ? order by sa.id;";
        
        Collection<InvestmentAccountSavingsLinkagesData> linkagesData = this.jdbcTemplate.query(sql, linkageMapper, investmentAccountId, status, status);
        
        return linkagesData;
        } catch (final EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String calculateMaturity(Long investmentProductId, BigDecimal investmentAmount, Long investmentRate, Integer investmentRateType,
            Integer investmentTerm, Integer investmentTermType, Date investmentDate, Date marturityDate) {
        Gson gson = new Gson();
        BigDecimal maturityAmount = investmentAmount;
        HashMap<String, Object> responseMap = new HashMap<>();
        if (investmentProductId != null && investmentRate != null && investmentRateType != null && investmentTerm != null
                && investmentTermType != null && investmentDate != null) {
            marturityDate = getMaturitydate(investmentTerm, investmentTermType, investmentDate);
            InvestmentProduct investmentProductData = this.investmentProductRepositoryWrapper
                    .findOneWithNotFoundDetection(investmentProductId);
            Integer numberOfDays = Days.daysBetween(LocalDate.fromDateFields(investmentDate), LocalDate.fromDateFields(marturityDate))
                    .getDays();           
            BigDecimal dailyInterestRate = getDailyInterestRate(investmentRate, investmentRateType);
            BigDecimal interestEarned = calcualteInterest(investmentAmount, dailyInterestRate, numberOfDays);
            
            interestEarned = Money.of(investmentProductData.getCurrency(), interestEarned).getAmount();
            
            maturityAmount = investmentAmount.add(Money.of(investmentProductData.getCurrency(), interestEarned).getAmount());
        }
        responseMap.put("maturityAmount", maturityAmount);
        responseMap.put("marturityDate", marturityDate);
        return gson.toJson(responseMap);
    }

    public static BigDecimal getDailyInterestRate(Long investmentRate, Integer investmentRateType) {
        BigDecimal interestRate = BigDecimal.valueOf(investmentRate);
        switch (investmentRateType) {
            case 2:
                interestRate = BigDecimal.valueOf((interestRate.doubleValue() * 12 / 365));
            break;
            case 3:
                interestRate = BigDecimal.valueOf(interestRate.doubleValue() / 365);
            break;
        }
        return interestRate;
    }

    public static Date getMaturitydate(Integer investmentTerm, Integer investmentTermType, Date investmentDate) {
        LocalDate date = LocalDate.fromDateFields(investmentDate);
        switch (investmentTermType) {
            case 0:
                date = date.plusDays(investmentTerm);
            break;
            case 1:
                date = date.plusWeeks(investmentTerm);
            break;
            case 2:
                date = date.plusMonths(investmentTerm);
            break;
        }
        return date.toDate();
    }

    BigDecimal calcualteInterest(BigDecimal investmentAmount, BigDecimal interestRate, Integer investmentTerm) {
        BigDecimal oneTermInterestAmount = MathUtility.multiply(interestRate, investmentAmount);
        Double interest = MathUtility.multiply(oneTermInterestAmount, investmentTerm).doubleValue() / 100;
        return BigDecimal.valueOf(interest);
    }

    @Override
    public Collection<InvestmentAccountSavingsLinkagesData> getAllInvestmentBySavingsId(Long savingsId) {
        try {
            InvestmentAccountSavingsLinkagesMapper mapper = new InvestmentAccountSavingsLinkagesMapper();
            String sql = "select " + mapper.schema() + " where ias.savings_account_id = ? ";
            return this.jdbcTemplate.query(sql, mapper, savingsId);
        } catch (final EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

}
