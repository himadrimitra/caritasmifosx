package com.finflux.portfolio.loan.purpose.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupTemplateData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeTemplateData;
import com.finflux.portfolio.loan.purpose.exception.LoanPurposeGroupNotFoundException;
import com.finflux.portfolio.loan.purpose.exception.LoanPurposeNotFoundException;

@Service
public class LoanPurposeGroupReadPlatformServiceImpl implements LoanPurposeGroupReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final LoanPurposeGroupDataExtractor loanPurposeGroupDataExtractor = new LoanPurposeGroupDataExtractor();
    private final LoanPurposeDataExtractor loanPurposeDataExtractor = new LoanPurposeDataExtractor();
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public LoanPurposeGroupReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @Override
    public LoanPurposeGroupTemplateData retrieveTemplate() {
        final Collection<CodeValueData> loanPurposeGroupTypeOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(LoanPurposeGroupApiConstants.loanPurposeGroupType);
        return LoanPurposeGroupTemplateData.template(loanPurposeGroupTypeOptions);
    }

    @Override
    public Collection<LoanPurposeGroupData> retrieveAllLoanPurposeGroups(final Integer loanPurposeGroupTypeId,
            final Boolean isFetchLoanPurposeDatas, final Boolean isActive) {
        this.context.authenticatedUser();
        int active = 0;
        if (isActive != null && isActive) {
            active = 1;
        }
        String sql = "SELECT " + this.loanPurposeGroupDataExtractor.schema(isFetchLoanPurposeDatas) + " ";
        if (loanPurposeGroupTypeId != null) {
            sql += "WHERE lpg.type_cv_id = " + loanPurposeGroupTypeId + " ";
            if (isActive != null) {
                sql += "AND lpg.is_active = " + active + " ";
            }
        } else if (isActive != null) {
            sql += "WHERE lpg.is_active = " + active + " ";
        }
        sql += "ORDER BY lpg.name ";
        return this.jdbcTemplate.query(sql, this.loanPurposeGroupDataExtractor, new Object[] {});
    }

    @Override
    public LoanPurposeGroupData retrieveOneLoanPurposeGroup(final Long loanPurposeGroupId, final Boolean isFetchLoanPurposeDatas) {
        this.context.authenticatedUser();
        final String sql = "SELECT " + this.loanPurposeGroupDataExtractor.schema(isFetchLoanPurposeDatas) + " WHERE lpg.id = ? ";
        final Collection<LoanPurposeGroupData> loanPurposeGroupData = this.jdbcTemplate.query(sql, this.loanPurposeGroupDataExtractor,
                new Object[] { loanPurposeGroupId });
        if (loanPurposeGroupData == null || loanPurposeGroupData.isEmpty()) { throw new LoanPurposeGroupNotFoundException(
                loanPurposeGroupId); }
        return loanPurposeGroupData.iterator().next();
    }

    @Override
    public LoanPurposeTemplateData retrieveLoanPurposeTemplate(final Boolean isActive) {
        final Integer loanPurposeGroupTypeId = null;
        final Boolean isFetchLoanPurposeDatas = false;
        final Collection<LoanPurposeGroupData> loanPurposeGroupDatas = retrieveAllLoanPurposeGroups(loanPurposeGroupTypeId,
                isFetchLoanPurposeDatas, isActive);
        return LoanPurposeTemplateData.template(loanPurposeGroupDatas);
    }

    @Override
    public Collection<LoanPurposeData> retrieveAllLoanPurposes(final Integer loanPurposeGroupTypeId,
            final Boolean isFetchLoanPurposeGroupDatas, final Boolean isActive) {
        this.context.authenticatedUser();
        int active = 0;
        if (isActive != null && isActive) {
            active = 1;
        }
        String sql = "SELECT " + this.loanPurposeDataExtractor.schema(isFetchLoanPurposeGroupDatas) + " ";
        if (loanPurposeGroupTypeId != null) {
            if (isFetchLoanPurposeGroupDatas == null || isFetchLoanPurposeGroupDatas == false) {
                sql += "LEFT JOIN f_loan_purpose_group_mapping lpgm ON lpgm.loan_purpose_id = lp.id LEFT JOIN f_loan_purpose_group lpg ON lpg.id = lpgm.loan_purpose_group_id ";
            }
            sql += "WHERE lpg.type_cv_id = " + loanPurposeGroupTypeId + " ";
            if (isActive != null) {
                sql += "AND lp.is_active = " + active + " ";
            }
        } else if (isActive != null) {
            sql += "WHERE lp.is_active = " + active + " ";
        }
        sql += "ORDER BY lp.name ";
        return this.jdbcTemplate.query(sql, this.loanPurposeDataExtractor, new Object[] {});
    }

    @Override
    public LoanPurposeData retrieveOneLoanPurpose(final Long loanPurposeId, final Boolean isFetchLoanPurposeGroupDatas) {
        this.context.authenticatedUser();
        final String sql = "SELECT " + this.loanPurposeDataExtractor.schema(isFetchLoanPurposeGroupDatas) + " WHERE lp.id = ?";
        final Collection<LoanPurposeData> loanPurposeData = this.jdbcTemplate.query(sql, this.loanPurposeDataExtractor,
                new Object[] { loanPurposeId });
        if (loanPurposeData == null || loanPurposeData.isEmpty()) { throw new LoanPurposeNotFoundException(loanPurposeId); }
        return loanPurposeData.iterator().next();
    }

    private static final class LoanPurposeGroupDataMapper implements RowMapper<LoanPurposeGroupData> {

        public String schema(final Boolean isFetchLoanPurposeDatas) {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder
                    .append("lpg.id as lpgId, lpg.name as lpgName, lpg.system_code as lpgSystemCode,lpg.description AS lpgDescription ")
                    .append(",lpgtype.id as lpgTypeId, lpgtype.code_value as lpgTypeName, lpg.is_active as lpgIsActive, lpg.is_system_defined as lpgIsSystemDefined ");
            if (isFetchLoanPurposeDatas) {
                sqlBuilder
                        .append(",lp.id as lpId, lp.name as lpName,lp.system_code as lpSystemCode, lp.description AS lpDescription,lp.is_active as lpIsActive ");
            }
            sqlBuilder.append("FROM f_loan_purpose_group lpg ");
            if (isFetchLoanPurposeDatas) {
                sqlBuilder.append("LEFT JOIN f_loan_purpose_group_mapping lpgm ON lpgm.loan_purpose_group_id = lpg.id ").append(
                        "LEFT JOIN f_loan_purpose lp ON lp.id = lpgm.loan_purpose_id ");
            }
            sqlBuilder.append("LEFT JOIN m_code_value lpgtype ON lpgtype.id = lpg.type_cv_id ");
            return sqlBuilder.toString();
        }

        @Override
        public LoanPurposeGroupData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "lpgId");
            if (id == null) { return null; }
            final String name = rs.getString("lpgName");
            final String systemCode = rs.getString("lpgSystemCode");
            final String description = rs.getString("lpgDescription");
            CodeValueData loanPurposeGroupType = null;
            final Long lpgTypeId = rs.getLong("lpgTypeId");
            if (lpgTypeId != null && lpgTypeId > 0) {
                loanPurposeGroupType = CodeValueData.instanceIdAndName(rs.getLong("lpgTypeId"), rs.getString("lpgTypeName"));
            }
            final boolean isActive = rs.getBoolean("lpgIsActive");
            final boolean isSystemDefined = rs.getBoolean("lpgIsSystemDefined");
            return LoanPurposeGroupData.instance(id, name, systemCode, description, loanPurposeGroupType, isActive, isSystemDefined);
        }
    }

    private static final class LoanPurposeGroupDataExtractor implements ResultSetExtractor<Collection<LoanPurposeGroupData>> {

        final LoanPurposeGroupDataMapper loanPurposeGroupDataMapper = new LoanPurposeGroupDataMapper();
        final LoanPurposeDataMapper loanPurposeDataMapper = new LoanPurposeDataMapper();

        private String schemaSql;
        private Boolean isFetchLoanPurposeDatas = false;

        public String schema(final Boolean isFetchLoanPurposeGroupDatas) {
            this.isFetchLoanPurposeDatas = isFetchLoanPurposeGroupDatas;
            if (this.isFetchLoanPurposeDatas == null) {
                this.isFetchLoanPurposeDatas = false;
            }
            this.schemaSql = this.loanPurposeGroupDataMapper.schema(this.isFetchLoanPurposeDatas);
            return this.schemaSql;
        }

        private LoanPurposeGroupDataExtractor() {
            this.schemaSql = this.loanPurposeGroupDataMapper.schema(this.isFetchLoanPurposeDatas);
        }

        @Override
        public Collection<LoanPurposeGroupData> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<LoanPurposeGroupData> loanPurposeGroupDataList = new ArrayList<>();

            LoanPurposeGroupData loanPurposeGroupData = null;
            Long loanPurposeGroupId = null;
            int lpgIndex = 0;// Loan purpose group index
            int lpIndex = 0;// Loan purpose index

            while (rs.next()) {
                final Long tempLpgId = rs.getLong("lpgId");
                if (loanPurposeGroupData == null || (loanPurposeGroupId != null && !loanPurposeGroupId.equals(tempLpgId))) {
                    loanPurposeGroupId = tempLpgId;
                    loanPurposeGroupData = this.loanPurposeGroupDataMapper.mapRow(rs, lpgIndex++);
                    loanPurposeGroupDataList.add(loanPurposeGroupData);
                }
                if (this.isFetchLoanPurposeDatas) {
                    final LoanPurposeData loanPurposeData = this.loanPurposeDataMapper.mapRow(rs, lpIndex++);
                    if (loanPurposeData != null) {
                        loanPurposeGroupData.addLoanPurposeData(loanPurposeData);
                    }
                }
            }
            return loanPurposeGroupDataList;
        }
    }

    private static final class LoanPurposeDataExtractor implements ResultSetExtractor<Collection<LoanPurposeData>> {

        final LoanPurposeDataMapper loanPurposeDataMapper = new LoanPurposeDataMapper();
        final LoanPurposeGroupDataMapper loanPurposeGroupDataMapper = new LoanPurposeGroupDataMapper();

        private String schemaSql;
        private Boolean isFetchLoanPurposeGroupDatas = false;

        public String schema(final Boolean isFetchLoanPurposeGroupDatas) {
            this.isFetchLoanPurposeGroupDatas = isFetchLoanPurposeGroupDatas;
            if (this.isFetchLoanPurposeGroupDatas == null) {
                this.isFetchLoanPurposeGroupDatas = false;
            }
            this.schemaSql = this.loanPurposeDataMapper.schema(this.isFetchLoanPurposeGroupDatas);
            return this.schemaSql;
        }

        private LoanPurposeDataExtractor() {
            this.schemaSql = this.loanPurposeDataMapper.schema(this.isFetchLoanPurposeGroupDatas);
        }

        @Override
        public Collection<LoanPurposeData> extractData(ResultSet rs) throws SQLException, DataAccessException {

            final List<LoanPurposeData> loanPurposeDataList = new ArrayList<>();

            LoanPurposeData loanPurposeData = null;
            Long loanPurposeId = null;
            int lpIndex = 0;// Loan purpose index
            int lpgIndex = 0;// Loan purpose group index

            while (rs.next()) {
                final Long tempLpId = rs.getLong("lpId");
                if (loanPurposeData == null || (loanPurposeId != null && !loanPurposeId.equals(tempLpId))) {
                    loanPurposeId = tempLpId;
                    loanPurposeData = this.loanPurposeDataMapper.mapRow(rs, lpIndex++);
                    loanPurposeDataList.add(loanPurposeData);
                }
                if (loanPurposeData != null && this.isFetchLoanPurposeGroupDatas) {
                    final LoanPurposeGroupData loanPurposeGroupData = this.loanPurposeGroupDataMapper.mapRow(rs, lpgIndex++);
                    if (loanPurposeGroupData != null) {
                        loanPurposeData.addLoanPurposeGroupData(loanPurposeGroupData);
                    }
                }
            }
            return loanPurposeDataList;
        }
    }

    private static final class LoanPurposeDataMapper implements RowMapper<LoanPurposeData> {

        private Boolean isFetchLoanPurposeGroupDatas = false;

        public String schema(final Boolean isFetchLoanPurposeGroupDatas) {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder
                    .append("lp.id as lpId, lp.name as lpName, lp.system_code as lpSystemCode,lp.description AS lpDescription,lp.is_active as lpIsActive ");
            if (isFetchLoanPurposeGroupDatas) {
                sqlBuilder
                        .append(",lpg.id as lpgId, lpg.name as lpgName, lpg.system_code as lpgSystemCode, lpg.description AS lpgDescription ")
                        .append(",lpgtype.id as lpgTypeId, lpgtype.code_value as lpgTypeName, lpg.is_active as lpgIsActive,lpg.is_system_defined as lpgIsSystemDefined ");
            }
            sqlBuilder.append("FROM f_loan_purpose lp ");
            if (isFetchLoanPurposeGroupDatas) {
                sqlBuilder.append("LEFT JOIN f_loan_purpose_group_mapping lpgm ON lpgm.loan_purpose_id = lp.id ")
                        .append("LEFT JOIN f_loan_purpose_group lpg ON lpg.id = lpgm.loan_purpose_group_id ")
                        .append("LEFT JOIN m_code_value lpgtype ON lpgtype.id = lpg.type_cv_id ");
            }
            return sqlBuilder.toString();
        }

        @Override
        public LoanPurposeData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "lpId");
            if (id == null) { return null; }
            final String name = rs.getString("lpName");
            final String systemCode = rs.getString("lpSystemCode");
            final String description = rs.getString("lpDescription");
            final boolean isActive = rs.getBoolean("lpIsActive");
            if (this.isFetchLoanPurposeGroupDatas) {

            }
            return LoanPurposeData.instance(id, name, systemCode, description, isActive);
        }
    }
}