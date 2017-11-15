package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.data.InvestmentAccountChargeData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountSavingsLinkagesData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountTimelineData;
import com.finflux.portfolio.investmenttracker.data.InvestmentProductData;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountStatus;

@Service
public class InvestmentAccountReadServiceImpl implements InvestmentAccountReadService {

    private final JdbcTemplate jdbcTemplate;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final InvestmentProductReadService investmentProductReadService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final InvestmentTrackerDropDownReadService dropdownReadPlatformService;
    
    @Autowired
    public InvestmentAccountReadServiceImpl(final RoutingDataSource dataSource,
             final ChargeReadPlatformService chargeReadPlatformService,
             final InvestmentProductReadService investmentProductReadService,
             final OfficeReadPlatformService officeReadPlatformService,
             final CodeValueReadPlatformService codeValueReadPlatformService,
             final InvestmentTrackerDropDownReadService dropdownReadPlatformService){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.investmentProductReadService = investmentProductReadService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
    }
    
    @Override
    public InvestmentAccountData retrieveInvestmentAccountTemplate(InvestmentAccountData investmentAccountData) {
        
        Collection<ChargeData> investmentChargeOptions = this.chargeReadPlatformService.retrieveInvestmentProductApplicableCharges();
        Collection<CodeValueData> partnerOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Investmentpartners");
        Collection<OfficeData> officeDataOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        Collection<InvestmentProductData> investmentProductOptions = this.investmentProductReadService.retrieveAll();
        final List<EnumOptionData> interestRateFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInvestmentTermFrequencyTypeOptions();
        
        if(investmentAccountData != null){
            return InvestmentAccountData.withTemplateData(investmentAccountData,partnerOptions, investmentProductOptions, investmentChargeOptions, officeDataOptions,
                    interestRateFrequencyTypeOptions, investmentTermFrequencyTypeOptions);
        }
        
        return InvestmentAccountData.onlyTemplateData(partnerOptions, investmentProductOptions, investmentChargeOptions, officeDataOptions,
                interestRateFrequencyTypeOptions, investmentTermFrequencyTypeOptions);
       
    }

    @Override
    public Collection<InvestmentAccountData> retrieveAll() {
        InvestmentAccountMapper investmentAccountMapper = new InvestmentAccountMapper();
        String sql = "SELECT " + investmentAccountMapper.schema();
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
        InvestmentAccountSavingsLinkagesMapper linkageMapper = new InvestmentAccountSavingsLinkagesMapper();
        
        String sql = "SELECT " + linkageMapper.schema() + " WHERE ia.id = ? order by sa.id;";
        
        Collection<InvestmentAccountSavingsLinkagesData> linkagesData = this.jdbcTemplate.query(sql, linkageMapper, investmentAccountId);
        
        return linkagesData;
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
            sqlBuilder.append(" off.id as officeId, off.name as officeName,");
            sqlBuilder.append(" cv.id  as partnerId, cv.code_value as partnerName,");
            sqlBuilder.append(" fip.id as investmentProductId, fip.name as investmentProductName");
            sqlBuilder.append(" from f_investment_account fia");
            sqlBuilder.append(" join m_currency curr on curr.code = fia.currency_code");
            sqlBuilder.append(" join m_office off on off.id = fia.office_id");
            sqlBuilder.append(" join m_code_value cv on cv.id = fia.partner_id");
            sqlBuilder.append(" join f_investment_product fip on fip.id =  fia.investment_product_id");
            sqlBuilder.append(" left join m_appuser sbu on sbu.id = fia.submittedon_userid");
            sqlBuilder.append(" left join m_appuser apu on apu.id = fia.approvedon_userid");
            sqlBuilder.append(" left join m_appuser acu on acu.id = fia.activatedon_userid");
            sqlBuilder.append(" left join m_appuser ivu on ivu.id = fia.investmenton_userid");
            sqlBuilder.append(" left join m_appuser mu on mu.id = fia.maturityon_userid");
            
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
            
            final Long partnerId = rs.getLong("partner_id");
            final String partnerName = rs.getString("partnerName");
            CodeValueData partner = null;
            if(partnerId != null){
                partner = CodeValueData.instance(partnerId, partnerName);
            }
            
            final Long officeId = rs.getLong("office_id");
            final String officeName = rs.getString("officeName");
            OfficeData officeData = null;
            if(officeId != null){
                officeData = OfficeData.lookup(officeId, officeName);
            }
            
            final Long investmentProductId = rs.getLong("investment_product_id");
            final String investmentProductName = rs.getString("investmentProductName");
            InvestmentProductData investmentProductData= null;
            if(investmentProductId != null){
                investmentProductData = InvestmentProductData.lookup(investmentProductId, investmentProductName);
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
            
            InvestmentAccountTimelineData investmentAccountTimelineData = new InvestmentAccountTimelineData(submittedOnDate,submittedByUsername, submittedByFirstname,
                     submittedByLastname,  approvedOnDate, approvedByUsername, approvedByFirstname, approvedByLastname,  activatedOnDate, activatedByUsername,  activatedByFirstname,
                     activatedByLastname,  investmentOnData,  investedByUsername,  investedByFirstname,
                     investedByLastname,  maturityOnDate,  maturityByUsername,  maturityByFirstname, maturityByLastname);


            return InvestmentAccountData.instance(id, accountNo, externalId,currency, interestRate,  interestRateTypeEnum,  investmentTerm,
                    invesmentTermPeriodEnum, investmentAccountTimelineData,  investmentAmount,
                     maturityAmount,  reinvestAfterMaturity, null, null,  officeData, partner, investmentProductData, statusEnumData);
        }
    }
    
    private static final class InvestmentAccountSavingsLinkagesMapper implements RowMapper<InvestmentAccountSavingsLinkagesData>{
        
        private final String sqlSchema;
        
        public InvestmentAccountSavingsLinkagesMapper(){
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" sa.id as savingsAccountId, sa.account_no as savingsAccountNumber, ias.investment_account_id as investmentAccountId,");
            sqlBuilder.append(" ias.investment_amount as individualInvestmentAmount");
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
             Long savingsAccountId = rs.getLong("savingsAccountId");
             String savingsAccountNumber = rs.getString("savingsAccountNumber");
             Long investmentAccountId = rs.getLong("investmentAccountId");
             BigDecimal individualInvestmentAmount = rs.getBigDecimal("individualInvestmentAmount");
             InvestmentAccountSavingsLinkagesData data = new InvestmentAccountSavingsLinkagesData(savingsAccountId, savingsAccountNumber, investmentAccountId,individualInvestmentAmount);
            return data;
        }
    
    }
    
    private static final class InvestmentAccountChargesMapper implements RowMapper<InvestmentAccountChargeData>{
        
        private final String sqlSchema;
        
        public InvestmentAccountChargesMapper(){
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" iac.charge_id as chargeId, iac.investment_account_id as investmentAccountId, iac.is_penalty,");
            sqlBuilder.append(" iac.is_active, iac.inactivated_on_date");
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
             InvestmentAccountChargeData data = new InvestmentAccountChargeData(chargeId, investmentAccountId, isActive,isPenality,inactivationDate);
            return data;
        }
    
    }

}
