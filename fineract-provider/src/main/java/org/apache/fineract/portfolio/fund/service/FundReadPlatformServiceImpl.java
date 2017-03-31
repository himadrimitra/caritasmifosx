/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.fund.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.DropdownReadPlatformService;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.data.FundLoanPurposeData;
import org.apache.fineract.portfolio.fund.exception.FundNotFoundException;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FundReadPlatformServiceImpl implements FundReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final LoanDropdownReadPlatformService loanDropdownReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final DropdownReadPlatformService dropdownReadPlatformService;

    @Autowired
    public FundReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final LoanDropdownReadPlatformService loanDropdownReadPlatformService,
            final LoanProductReadPlatformService loanProductReadPlatformService, final OfficeReadPlatformService officeReadPlatformService,
            final DropdownReadPlatformService dropdownReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.loanDropdownReadPlatformService = loanDropdownReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
    }

    public static final class FundMapper implements RowMapper<FundData> {

        public String schema() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("fund.id as id, fund.external_id as externalId,");
            sql.append("fundSourceCodeValue.id as fundSourceId, fundSourceCodeValue.code_value as fundSourceValue, ");
            sql.append("fundCategoryCodeValue.id as fundCategoryId, fundCategoryCodeValue.code_value as fundCategoryValue, ");
            sql.append("facilityTypeCodeValue.id as facilityTypeId, facilityTypeCodeValue.code_value as facilityTypeValue, ");
            sql.append("fund.name as name, ");
            sql.append("fund.assignment_start_date as assignmentStartDate, fund.assignment_end_date as assignmentEndDate, ");
            sql.append("fund.sanctioned_date as sanctionedDate, fund.sanctioned_amount as sanctionedAmount, ");
            sql.append("fund.disbursed_date as disbursedDate, fund.disbursed_amount as disbursedAmount, ");
            sql.append("fund.maturity_date as maturityDate, ");
            sql.append("fund.interest_rate as interestRate, ");
            sql.append("fundRepaymentFrequencyCodeValue.id as fundRepaymentFrequencyId, fundRepaymentFrequencyCodeValue.code_value as fundRepaymentFrequencyValue, ");
            sql.append("fund.tenure_frequency as tenureFrequency, fund.tenure as tenure, ");
            sql.append("fund.morotorium_frequency as morotoriumFrequency, fund.morotorium as morotorium, ");
            sql.append("fund.loan_portfolio_fee as loanPortfolioFee, ");
            sql.append("fund.book_debt_hypothecation as bookDebtHypothecation, ");
            sql.append("fund.cash_collateral as cashCollateral, ");
            sql.append("fund.personal_gurantee as personalGurantee, ");
            sql.append("fund.is_active as isActive, fund.is_loan_assigned as isLoanAssigned ");
            sql.append("from m_fund fund ");
            sql.append(" LEFT JOIN  m_code_value fundSourceCodeValue on fundSourceCodeValue.id = fund.fund_source ");
            sql.append(" LEFT JOIN  m_code_value fundCategoryCodeValue on fundCategoryCodeValue.id = fund.fund_category ");
            sql.append(" LEFT JOIN  m_code_value facilityTypeCodeValue on facilityTypeCodeValue.id = fund.facility_type ");
            sql.append(" LEFT JOIN  m_code_value fundRepaymentFrequencyCodeValue on fundRepaymentFrequencyCodeValue.id = fund.fund_repayment_frequency ");
            return sql.toString();

        }

        @SuppressWarnings("unused")
        @Override
        public FundData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String externalId = rs.getString("externalId");
            final Long fundSourceId = rs.getLong("fundSourceId");
            final String fundSourceValue = rs.getString("fundSourceValue");
            CodeValueData fundSource = CodeValueData.instance(fundSourceId, fundSourceValue);

            final Long fundCategoryId = rs.getLong("fundCategoryId");
            final String fundCategoryValue = rs.getString("fundCategoryValue");
            CodeValueData fundCategory = CodeValueData.instance(fundCategoryId, fundCategoryValue);

            final Long facilityTypeId = rs.getLong("facilityTypeId");
            final String facilityTypeValue = rs.getString("facilityTypeValue");
            CodeValueData facilityType = CodeValueData.instance(facilityTypeId, facilityTypeValue);

            final String name = rs.getString("name");
            final LocalDate assignmentStartDate = JdbcSupport.getLocalDate(rs, "assignmentStartDate");
            final LocalDate assignmentEndDate = JdbcSupport.getLocalDate(rs, "assignmentEndDate");
            final LocalDate sanctionedDate = JdbcSupport.getLocalDate(rs, "sanctionedDate");
            final BigDecimal sanctionedAmount = rs.getBigDecimal("sanctionedAmount");
            final LocalDate disbursedDate = JdbcSupport.getLocalDate(rs, "disbursedDate");
            final BigDecimal disbursedAmount = rs.getBigDecimal("disbursedAmount");
            final LocalDate maturityDate = JdbcSupport.getLocalDate(rs, "maturityDate");
            final BigDecimal interestRate = rs.getBigDecimal("interestRate");

            final Long fundRepaymentFrequencyId = rs.getLong("fundRepaymentFrequencyId");
            final String fundRepaymentFrequencyValue = rs.getString("fundRepaymentFrequencyValue");
            CodeValueData fundRepaymentFrequency = CodeValueData.instance(fundRepaymentFrequencyId, fundRepaymentFrequencyValue);

            final int tenureFrequencyIntValue = rs.getInt("tenureFrequency");
            final EnumOptionData tenureFrequency = PeriodFrequencyType.periodFrequencyType(tenureFrequencyIntValue);
            final int tenure = rs.getInt("tenure");

            final int morotoriumFrequencyIntValue = rs.getInt("morotoriumFrequency");
            final EnumOptionData morotoriumFrequency = PeriodFrequencyType.periodFrequencyType(morotoriumFrequencyIntValue);
            final int morotorium = rs.getInt("morotorium");

            final BigDecimal loanPortfolioFee = rs.getBigDecimal("loanPortfolioFee");
            final BigDecimal bookDebtHypothecation = rs.getBigDecimal("bookDebtHypothecation");
            final BigDecimal cashCollateral = rs.getBigDecimal("cashCollateral");
            final String personalGurantee = rs.getString("personalGurantee");
            Boolean isActive = rs.getBoolean("isActive");
            Boolean isLoanAssigned = rs.getBoolean("isLoanAssigned");
            return FundData.instance(id, externalId, fundSource, fundCategory, facilityType, name, assignmentStartDate, assignmentEndDate,
                    sanctionedDate, sanctionedAmount, disbursedDate, disbursedAmount, maturityDate, interestRate, fundRepaymentFrequency,
                    tenure, tenureFrequency, morotorium, morotoriumFrequency, loanPortfolioFee, bookDebtHypothecation, cashCollateral,
                    personalGurantee, isActive, isLoanAssigned);
        }
    }

    @Override
    @Cacheable(value = "funds", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('fn')")
    public Collection<FundData> retrieveAllFunds(String command) {

        this.context.authenticatedUser();

        final FundMapper rm = new FundMapper();
        String sql = "select " + rm.schema();
        if(command != null && command.equalsIgnoreCase(FundApiConstants.activeParamName)){
        	sql = sql + " WHERE fund.is_active = 1 ";
        };
        sql = sql + " order by fund.name";
        Collection<FundData> fundDataList = this.jdbcTemplate.query(sql, rm, new Object[] {});
        for(FundData fundData : fundDataList){
            fundData.setFundLoanPurposeData(retrieveLoanPurposeByFundId(fundData.getId()));
        }
        return fundDataList;
    }

    @Override
    public FundData retrieveFund(final Long fundId) {

        try {
            this.context.authenticatedUser();

            final FundMapper rm = new FundMapper();
            final String sql = "select " + rm.schema() + " where fund.id = ?";

            final FundData selectedFund = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { fundId });
            selectedFund.setFundLoanPurposeData(retrieveLoanPurposeByFundId(selectedFund.getId()));
            return selectedFund;
        } catch (final EmptyResultDataAccessException e) {
            throw new FundNotFoundException(fundId);
        }
    }

    @Override
    public Map<String, Object> retrieveTemplate(String command) {
        Map<String, Object> responseMap = new HashMap<>();
        if (command != null && command.equalsIgnoreCase("search")) {
            final Collection<LoanProductData> loanProducts = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup();
            final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

            final List<CodeValueData> genderOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.GENDER_CODE_VALUE));

            final List<CodeValueData> clientTypeOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.CLIENT_TYPE_CODE_VALUE));

            final List<CodeValueData> clientClassificationOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.CLIENT_CLASSIFICATION_CODE_VALUE));

            final List<CodeValueData> loanPurposeCategoryOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.LOAN_PURPOSE_CATEGORY_CODE_VALUE));

            final List<CodeValueData> loanPurposeOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.LOAN_PURPOSE_CODE_VALUE));

            final List<FundData> fundOptions = (List<FundData>) this.retrieveAllFunds(FundApiConstants.activeParamName);
            
            List<EnumOptionData> conditionalOperatorOptions = this.dropdownReadPlatformService.retrieveConditionalOperatorOptions();
            responseMap.put("loanProducts", loanProducts);
            responseMap.put("offices", offices);
            responseMap.put("genderOptions", genderOptions);
            responseMap.put("clientTypeOptions", clientTypeOptions);
            responseMap.put("clientClassificationOptions", clientClassificationOptions);
            responseMap.put("loanPurposeCategoryOptions", loanPurposeCategoryOptions);
            responseMap.put("loanPurposeOptions", loanPurposeOptions);
            responseMap.put("fundOptions", fundOptions);
            responseMap.put("conditionalOperatorOptions", conditionalOperatorOptions);
        } else {
            final List<EnumOptionData> repaymentFrequencyTypeOptions = this.loanDropdownReadPlatformService
                    .retrieveRepaymentFrequencyTypeOptions();

            final List<CodeValueData> fundSourceOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.FUND_SOURCE_CODE_VALUE));

            final List<CodeValueData> categoryOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.CATEGORY_CODE_VALUE));

            final List<CodeValueData> facilityTypeOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.FACILITY_TYPE_CODE_VALUE));

            final List<CodeValueData> fundRepaymentFrequencyOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.FUND_REPAYMENT_FREQUENCY_CODE_VALUE));
            
            final List<CodeValueData> loanPurposeOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(FundApiConstants.LOAN_PURPOSE_CODE_VALUE));

            responseMap.put("fundSourceOptions", fundSourceOptions);
            responseMap.put("categoryOptions", categoryOptions);
            responseMap.put("facilityTypeOptions", facilityTypeOptions);
            responseMap.put("loanPurposeOptions", loanPurposeOptions);
            responseMap.put("fundRepaymentFrequencyOptions", fundRepaymentFrequencyOptions);
            responseMap.put("repaymentFrequencyTypeOptions", repaymentFrequencyTypeOptions);
        }

        return responseMap;
    }
    
    public static final class FundLoanPurposeMapper implements RowMapper<FundLoanPurposeData> {
        
        public String schema() {
            return " flp.id as id ,flp.loan_purpose_amount loanPurposeAmount, f.id as fundId, lp.id as loanPurposeId, "+
                    "lp.code_value as loanPurposeCodeValue, f.sanctioned_amount as sanctionedAmount "+
                    " from f_fund_loan_purpose flp "+
                    "left join m_fund f on f.id = flp.fund_id "+
                    "left join m_code_value lp on lp.id = flp.loan_purpose_id ";
        }

        @SuppressWarnings("unused")
        @Override
        public FundLoanPurposeData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long fundId = JdbcSupport.getLong(rs, "fundId");
            final Long loanPurposeId = rs.getLong("loanPurposeId");
            final String loanPurposeCodeValue = rs.getString("loanPurposeCodeValue");
            CodeValueData loanPurpose = CodeValueData.instance(loanPurposeId, loanPurposeCodeValue);
            final BigDecimal loanPurposeAmount = rs.getBigDecimal("loanPurposeAmount");
            BigDecimal totalAmount = BigDecimal.ZERO;
            final BigDecimal sanctionedAmount = rs.getBigDecimal("sanctionedAmount");
            totalAmount = BigDecimal.valueOf((sanctionedAmount.doubleValue()/100)*(loanPurposeAmount.doubleValue()));
            return FundLoanPurposeData.instance(id, fundId, loanPurpose, loanPurposeAmount, totalAmount);
        }
    }

    @Override
    public List<FundLoanPurposeData> retrieveLoanPurposeByFundId(Long fundId) {
        FundLoanPurposeMapper fundLoanPurposeMapper = new FundLoanPurposeMapper();
        String sql = "select "+fundLoanPurposeMapper.schema()+" where flp.fund_id = ? ";
        return this.jdbcTemplate.query(sql, fundLoanPurposeMapper, new Object[] {fundId});
    }
}