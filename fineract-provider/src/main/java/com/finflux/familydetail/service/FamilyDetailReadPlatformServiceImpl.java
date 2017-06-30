package com.finflux.familydetail.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.data.FamilyDetailData;
import com.finflux.familydetail.data.FamilyDetailTemplateData;
import com.finflux.familydetail.exception.FamilyDetailsNotFoundException;
import com.finflux.portfolio.cashflow.data.CashFlowCategoryData;
import com.finflux.portfolio.cashflow.data.CashFlowCategoryTypeEnums;
import com.finflux.portfolio.cashflow.data.IncomeExpenseData;
import com.finflux.portfolio.cashflow.service.CashFlowCategoryReadPlatformService;
import com.finflux.portfolio.cashflow.service.IncomeExpenseReadPlatformService;

@Service
public class FamilyDetailReadPlatformServiceImpl implements FamilyDetailsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final CashFlowCategoryReadPlatformService cashFlowCategoryReadPlatformService;
    private final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService;

    @Autowired
    public FamilyDetailReadPlatformServiceImpl(RoutingDataSource dataSource, CodeValueReadPlatformService codeValueReadPlatformService,
            final CashFlowCategoryReadPlatformService cashFlowCategoryReadPlatformService,
            final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.cashFlowCategoryReadPlatformService = cashFlowCategoryReadPlatformService;
        this.incomeExpenseReadPlatformService = incomeExpenseReadPlatformService;
    }

    @Override
    public FamilyDetailTemplateData retrieveTemplate() {

        final Collection<CodeValueData> salutationOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.salutationParamName);

        final Collection<CodeValueData> relationshipOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.relationshipParamName);

        final Collection<CodeValueData> genderOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.genderParamName);

        final Integer categoryEnumId = CashFlowCategoryTypeEnums.OCCUPATION.getValue();
        final Integer typeEnumId = null;
        final Boolean isActive = true;
        final Boolean isFetchIncomeExpenseDatas = true;
        final Collection<CashFlowCategoryData> occupationOptions = this.cashFlowCategoryReadPlatformService.retrieveAll(categoryEnumId,
                typeEnumId, isActive, isFetchIncomeExpenseDatas);

        final Collection<CodeValueData> educationOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.educationParamName);

        return FamilyDetailTemplateData
                .template(salutationOptions, relationshipOptions, genderOptions, educationOptions, occupationOptions);
    }

    @Override
    public Collection<FamilyDetailData> retrieveAllFamilyDetails(final Long clientId) {

        final FamilyDetailsMapper familyDetailsMapper = new FamilyDetailsMapper(this.incomeExpenseReadPlatformService);

        final String sql = "SELECT " + familyDetailsMapper.schema() + "WHERE f.client_id = ? or f.client_reference = ?";

        return this.jdbcTemplate.query(sql, familyDetailsMapper, new Object[] { clientId, clientId });

    }

    @Override
    public FamilyDetailData retrieveOneFamilyDetail(final Long familyDetailsId) {

        final FamilyDetailsMapper familyDetailsMapper = new FamilyDetailsMapper(this.incomeExpenseReadPlatformService);

        final String sql = "SELECT " + familyDetailsMapper.schema() + "WHERE f.id = ? ";

        final Collection<FamilyDetailData> familyDetailDatas = this.jdbcTemplate.query(sql, familyDetailsMapper,
                new Object[] { familyDetailsId });
        if (familyDetailDatas == null || familyDetailDatas.isEmpty()) { throw new FamilyDetailsNotFoundException(familyDetailsId); }
        return familyDetailDatas.iterator().next();
    }

    private static final class FamilyDetailsMapper implements RowMapper<FamilyDetailData> {

        private final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService;

        FamilyDetailsMapper(final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService) {
            this.incomeExpenseReadPlatformService = incomeExpenseReadPlatformService;
        }

        public String schema() {
            StringBuilder query = new StringBuilder();
            query.append("f.id as id, f.client_id as clientId ").append(",mcv1.id as salutationId, mcv1.code_value AS salutationName ")
                    .append(",f.firstname as firstname, f.middlename as middlename,f.lastname as lastname ")
                    .append(",mcv2.id as relationshipId, mcv2.code_value as relationshipName ")
                    .append(",mcv3.id as genderId, mcv3.code_value as genderName ").append(",f.date_of_birth as dateOfBirth, f.age as age ")
                    .append(",ie.id as occupationId ").append(",mcv5.id as educationId, mcv5.code_value as educationName ")
                    .append(",f.is_dependent as isDependent, f.is_serious_illness as isSeriousIllness, f.is_deceased as isDeceased ")
                    .append(", f.client_reference as clientReference ")
                    .append(",mc.id as memberClientId,mc.display_name as displayName,mc.account_no as accountNo ")
                    .append("FROM f_family_details f ").append("JOIN m_client mc ON mc.id = f.client_id ")
                    .append("LEFT JOIN f_income_expense ie ON ie.id = f.occupation_details_id ")
                    .append("LEFT JOIN m_code_value mcv1 ON mcv1.id = f.salutation_cv_id ")
                    .append("LEFT JOIN m_code_value mcv2 ON mcv2.id = f.relationship_cv_id ")
                    .append("LEFT JOIN m_code_value mcv3 ON mcv3.id = f.gender_cv_id ")
                    .append("LEFT JOIN m_code_value mcv5 ON mcv5.id = f.education_cv_id ");
            return query.toString();
        }

        @Override
        public FamilyDetailData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long salutationId = rs.getLong("salutationId");
            CodeValueData salutation = null;
            if (salutationId != null && salutationId > 0) {
                salutation = CodeValueData.instanceIdAndName(rs.getLong("salutationId"), rs.getString("salutationName"));
            }
            final String firstname = rs.getString("firstname");
            final String middlename = rs.getString("middlename");
            final String lastname = rs.getString("lastname");

            CodeValueData relationship = null;
            final Long relationshipId = rs.getLong("relationshipId");
            if (relationshipId != null && relationshipId > 0) {
                relationship = CodeValueData.instanceIdAndName(rs.getLong("relationshipId"), rs.getString("relationshipName"));
            }

            CodeValueData gender = null;
            final Long genderId = rs.getLong("genderId");
            if (genderId != null && genderId > 0) {
                gender = CodeValueData.instanceIdAndName(rs.getLong("genderId"), rs.getString("genderName"));
            }
            final Date dateOfBirth = rs.getDate("dateOfBirth");
            Integer age = rs.getInt("age");
            if (age == 0) {
                age = null;
            }
            IncomeExpenseData occupation = null;
            final Long occupationId = rs.getLong("occupationId");
            if (occupationId != null && occupationId > 0) {
                final Boolean isFetchCashflowCategoryData = false;
                occupation = this.incomeExpenseReadPlatformService.retrieveOne(occupationId, isFetchCashflowCategoryData);
            }
            CodeValueData education = null;
            final Long educationId = rs.getLong("educationId");
            if (educationId != null && educationId > 0) {
                education = CodeValueData.instanceIdAndName(rs.getLong("educationId"), rs.getString("educationName"));
            }
            final Boolean isDependent = rs.getBoolean("isDependent");
            final Boolean isSeriousIllness = rs.getBoolean("isSeriousIllness");
            final Boolean isDeceased = rs.getBoolean("isDeceased");
            Long clientReference = null;
            if (rs.getLong("clientReference") > 0) {
                clientReference = rs.getLong("clientReference");
            }
            final Long memberClientId = rs.getLong("memberClientId");
            final String displayName = rs.getString("displayName");
            final String accountNo = rs.getString("accountNo");
            return new FamilyDetailData(id, firstname, middlename, lastname, salutation, relationship, gender, dateOfBirth, age, education,
                    occupation, isDependent, isSeriousIllness, isDeceased, clientReference, memberClientId, displayName, accountNo);
        }
    }
}