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
package org.apache.fineract.accounting.glaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.glaccount.domain.GLAccountUsage;
import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.accounting.glaccount.exception.GLAccountInvalidClassificationException;
import org.apache.fineract.accounting.glaccount.exception.GLAccountNotFoundException;
import org.apache.fineract.accounting.glaccount.exception.GLClassificationTypeInvalidException;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GLAccountReadPlatformServiceImpl implements GLAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final static String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(hierarchy) - LENGTH(REPLACE(hierarchy, '.', '')) - 1) * 4)), name)";

    @Autowired
    public GLAccountReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }

    private static final class GLAccountMapper implements RowMapper<GLAccountData> {

        private final JournalEntryAssociationParametersData associationParametersData;

        public GLAccountMapper(final JournalEntryAssociationParametersData associationParametersData) {
            if (associationParametersData == null) {
                this.associationParametersData = new JournalEntryAssociationParametersData();
            } else {
                this.associationParametersData = associationParametersData;
            }
        }

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(
                    " gl.id as id, gl.name as name, gl.parent_id as parentId, gl_code as glCode, disabled as disabled, manual_journal_entries_allowed as manualEntriesAllowed, ")
                    .append("classification_enum as classification, account_usage as accountUsage, gl.description as description, gl.gl_classification_type as glClassificationTypeEnum, ")
                    .append(nameDecoratedBaseOnHierarchy).append(" as nameDecorated, ")
                    .append("cv.id as codeId, cv.code_value as codeValue ");
            if (this.associationParametersData.isRunningBalanceRequired()) {
                sb.append(",gl_j.closing_balance as organizationRunningBalance ");
            }
            sb.append("from acc_gl_account gl left join m_code_value cv on tag_id=cv.id ");
            if (this.associationParametersData.isRunningBalanceRequired()) {
                sb.append(" LEFT OUTER JOIN (SELECT date(ifnull(rcc.computed_till_date,MAX(rc.date))) AS minDate, rc.account_id AS accountId ");
                sb.append(" FROM f_org_running_balance rc left join f_running_balance_computation_detail rcc on rc.account_id=rcc.account_id ");
                sb.append(" GROUP BY rc.account_id) as rbd on  rbd.accountId = gl.id");
                sb.append(" LEFT OUTER JOIN f_org_running_balance gl_j ON gl_j.account_id = gl.id and gl_j.date = rbd.minDate ");
            }
            return sb.toString();
        }

        @Override
        public GLAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final Long parentId = JdbcSupport.getLong(rs, "parentId");
            final String glCode = rs.getString("glCode");
            final boolean disabled = rs.getBoolean("disabled");
            final boolean manualEntriesAllowed = rs.getBoolean("manualEntriesAllowed");
            final int accountTypeId = JdbcSupport.getInteger(rs, "classification");
            final EnumOptionData accountType = AccountingEnumerations.gLAccountType(accountTypeId);
            final int usageId = JdbcSupport.getInteger(rs, "accountUsage");
            final EnumOptionData usage = AccountingEnumerations.gLAccountUsage(usageId);
            final String description = rs.getString("description");
            final String nameDecorated = rs.getString("nameDecorated");
            final Long codeId = rs.wasNull() ? null : rs.getLong("codeId");
            final String codeValue = rs.getString("codeValue");
            final CodeValueData tagId = CodeValueData.instance(codeId, codeValue);
            Long organizationRunningBalance = null;
            if (associationParametersData.isRunningBalanceRequired()) {
                organizationRunningBalance = rs.getLong("organizationRunningBalance");
            }
            final Integer glClassificationTypeEnum = JdbcSupport.getInteger(rs, "glClassificationTypeEnum");
            EnumOptionData glClassificationType = null;
            if(glClassificationTypeEnum != null){
            	glClassificationType = GlAccountEnumerations.glClassificationType(glClassificationTypeEnum);
            }
            return new GLAccountData(id, name, parentId, glCode, disabled, manualEntriesAllowed, accountType, usage, description,
                    nameDecorated, tagId, organizationRunningBalance, glClassificationType);
        }
    }

    @Override
    public List<GLAccountData> retrieveAllGLAccounts(final Integer accountClassification, final String searchParam, final Integer usage,
            final Boolean manualTransactionsAllowed, final Boolean disabled,
            JournalEntryAssociationParametersData associationParametersData, final Integer glClassificationType) {
        if (accountClassification != null) {
            if (!checkValidGLAccountType(accountClassification)) { throw new GLAccountInvalidClassificationException(accountClassification); }
        }
        if (usage != null) {
            if (!checkValidGLAccountUsage(usage)) { throw new GLAccountInvalidClassificationException(accountClassification); }
        }
        if (glClassificationType != null) {
            if (!checkValidGLClassificationType(glClassificationType)) { throw new GLClassificationTypeInvalidException(glClassificationType); }
        }
        final GLAccountMapper rm = new GLAccountMapper(associationParametersData);
        final StringBuilder sb = new StringBuilder(rm.schema().length()+50);
        sb.append("select " + rm.schema());
        final Object[] paramaterArray = new Object[3];
        int arrayPos = 0;
        boolean filtersPresent = false;
        if ((accountClassification != null) || StringUtils.isNotBlank(searchParam) || (usage != null)
                || (manualTransactionsAllowed != null) || (disabled != null) || (glClassificationType != null)) {
            filtersPresent = true;
            sb.append(" where");
        }
        if (filtersPresent) {
            boolean firstWhereConditionAdded = false;
            if (accountClassification != null) {
                sb.append(" classification_enum like ?");
                paramaterArray[arrayPos] = accountClassification;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
            if (StringUtils.isNotBlank(searchParam)) {
                if (firstWhereConditionAdded) {
                    sb.append(" and ");
                }
                sb.append(" ( name like %?% or gl_code like %?% )");
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
            if (usage != null) {
                if (firstWhereConditionAdded) {
                    sb.append(" and ");
                }
                if (GLAccountUsage.HEADER.getValue().equals(usage)) {
                    sb.append(" account_usage = 2 ");
                } else if (GLAccountUsage.DETAIL.getValue().equals(usage)) {
                    sb.append(" account_usage = 1 ");
                }
                firstWhereConditionAdded = true;
            }
            if (manualTransactionsAllowed != null) {
                if (firstWhereConditionAdded) {
                    sb.append(" and ");
                }
                if (manualTransactionsAllowed) {
                    sb.append(" manual_journal_entries_allowed = 1");
                } else {
                    sb.append(" manual_journal_entries_allowed = 0");
                }
                firstWhereConditionAdded = true;
            }
            if (disabled != null) {
                if (firstWhereConditionAdded) {
                    sb.append(" and ");
                }

                if (disabled) {
                    sb.append(" disabled = 1");
                } else {
                    sb.append(" disabled = 0");
                }
                firstWhereConditionAdded = true;
            }
            if (glClassificationType != null) {
                if (firstWhereConditionAdded) {
                    sb.append(" and ");
                }
                sb.append(" gl_classification_type = ? ");
                paramaterArray[arrayPos] = glClassificationType;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
        }
        final Object[] finalObjectArray = Arrays.copyOf(paramaterArray, arrayPos);
        return this.jdbcTemplate.query(sb.toString(), rm, finalObjectArray);
    }

    private boolean checkValidGLClassificationType(final Integer glClassificationType) {
        for (final GLClassificationType gLClassificationType : GLClassificationType.values()) {
            if (gLClassificationType.getValue().equals(glClassificationType)) { return true; }
        }
        return false;
    }

	@Override
    public GLAccountData retrieveGLAccountById(final long glAccountId, JournalEntryAssociationParametersData associationParametersData) {
        try {

            final GLAccountMapper rm = new GLAccountMapper(associationParametersData);
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
            sql.append(" where gl.id = ?");
            if (associationParametersData.isRunningBalanceRequired()) {
                sql.append("  ORDER BY gl_j.date LIMIT 1");
            }
            final GLAccountData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { glAccountId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new GLAccountNotFoundException(glAccountId);
        }
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts(final GLAccountType accountType) {
        final Integer classificationType = null;
        return retrieveAllGLAccounts(accountType.getValue(), null, GLAccountUsage.DETAIL.getValue(), null, false,
                new JournalEntryAssociationParametersData(), classificationType);
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts() {
        final Integer classificationType = null;
        return retrieveAllGLAccounts(null, null, GLAccountUsage.DETAIL.getValue(), null, false,
                new JournalEntryAssociationParametersData(), classificationType);
    }

    private static boolean checkValidGLAccountType(final int type) {
        for (final GLAccountType accountType : GLAccountType.values()) {
            if (accountType.getValue().equals(type)) { return true; }
        }
        return false;
    }

    private static boolean checkValidGLAccountUsage(final int type) {
        for (final GLAccountUsage accountUsage : GLAccountUsage.values()) {
            if (accountUsage.getValue().equals(type)) { return true; }
        }
        return false;
    }

    @Override
    public GLAccountData retrieveNewGLAccountDetails(final Integer type) {
        return GLAccountData.sensibleDefaultsForNewGLAccountCreation(type);
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledHeaderGLAccounts(final GLAccountType accountType) {
        final Integer classificationType = null;
        return retrieveAllGLAccounts(accountType.getValue(), null, GLAccountUsage.HEADER.getValue(), null, false,
                new JournalEntryAssociationParametersData(), classificationType);
    }

    @Override
    public List<GLAccountDataForLookup> retrieveAccountsByTagId(final Long ruleId, final Integer transactionType) {
        final GLAccountDataLookUpMapper mapper = new GLAccountDataLookUpMapper();
        final String sql = "Select " + mapper.schema() + " where rule.id=? and tags.acc_type_enum=?";
        return this.jdbcTemplate.query(sql, mapper, new Object[] { ruleId, transactionType });
    }

    @Override
    public List<GLAccountDataForLookup> retrieveAccountsByTags(Collection<Long> tagIds) {
        final GLAccountDataLookUpMapper mapper = new GLAccountDataLookUpMapper();
        final StringBuilder sqlBuilder = new StringBuilder(400);
        sqlBuilder.append("select gl.id as id,gl.name as name,gl.gl_code as glCode ").append(" from  acc_gl_account gl ").append(" where gl.tag_id in (:tagIds) ");
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("tagIds", tagIds);
        return this.namedParameterJdbcTemplate.query(sqlBuilder.toString(), paramMap, mapper);
    }
    
    private static final class GLAccountDataLookUpMapper implements RowMapper<GLAccountDataForLookup> {

        public String schema() {
            return " gl.id as id, gl.name as name, gl.gl_code as glCode from acc_accounting_rule rule join acc_rule_tags tags on tags.acc_rule_id = rule.id join acc_gl_account gl on gl.tag_id=tags.tag_id";
        }

        @Override
        public GLAccountDataForLookup mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String glCode = rs.getString("glCode");
            return new GLAccountDataForLookup(id, name, glCode);
        }

    }
}