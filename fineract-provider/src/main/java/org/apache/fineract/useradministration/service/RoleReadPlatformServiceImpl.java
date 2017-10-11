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
package org.apache.fineract.useradministration.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.useradministration.data.RoleBasedLimitData;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.exception.RoleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

@Service
public class RoleReadPlatformServiceImpl implements RoleReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final RoleWithRoleBasedLimitsExtractor roleExtractor;

    @Autowired
    public RoleReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.roleExtractor = new RoleWithRoleBasedLimitsExtractor();
    }

    @Override
    public Collection<RoleData> retrieveAll() {
        final String sql = this.roleExtractor.schema() + " order by r.id";

        return this.jdbcTemplate.query(sql, this.roleExtractor);
    }

    @Override
    public Collection<RoleData> retrieveAllActiveRoles() {
        final String sql = this.roleExtractor.schema() + " where r.is_disabled = 0 order by r.id";

        return this.jdbcTemplate.query(sql, this.roleExtractor);
    }

    @Override
    public RoleData retrieveOne(final Long id) {

        try {
            final String sql = this.roleExtractor.schema() + " where r.id=?";

            final List<RoleData> roleDatas = this.jdbcTemplate.query(sql, this.roleExtractor, new Object[] { id });
            if (roleDatas == null || roleDatas.isEmpty()) { throw new RoleNotFoundException(id); }
            final RoleData roleData = roleDatas.get(0);
            return roleData;
        } catch (final EmptyResultDataAccessException e) {
            throw new RoleNotFoundException(id);
        }
    }

    protected static final class RoleWithRoleBasedLimitsExtractor implements ResultSetExtractor<List<RoleData>> {

        @Override
        public List<RoleData> extractData(final ResultSet rs) throws SQLException {

            final Map<Long, RoleData> roleDatasMap = new HashMap<>();
            RoleData roleData = null;
            RoleBasedLimitData roleBasedLimitData = null;

            while (rs.next()) {

                final Long roleId = JdbcSupport.getLong(rs, "id");
                roleData = roleDatasMap.get(roleId);

                if (roleData == null) {
                    final String name = rs.getString("name");
                    final String description = rs.getString("description");
                    final Boolean disabled = rs.getBoolean("disabled");
                    final List<RoleBasedLimitData> roleBasedLimits = new ArrayList<>();
                    ;
                    roleData = new RoleData(roleId, name, description, disabled, roleBasedLimits);
                    roleDatasMap.put(roleId, roleData);
                }

                // continue process
                final Long roleBasedLimitId = JdbcSupport.getLong(rs, "roleBasedLimitId");
                if (roleBasedLimitId != null) {
                    final String code = rs.getString("currencyCode");
                    final String name = rs.getString("currencyName");
                    final int decimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
                    final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "currencyMultiplesOf");
                    final String displaySymbol = rs.getString("currencyDisplaySymbol");
                    final String nameCode = rs.getString("currencyInternationalizedNameCode");
                    final CurrencyData currencyData = new CurrencyData(code, name, decimalPlaces, inMultiplesOf, displaySymbol, nameCode);
                    /** Populate the Approval Amount **/
                    final BigDecimal maxLoanApprovalAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "maxLoanApprovalAmount");
                    roleBasedLimitData = new RoleBasedLimitData(roleBasedLimitId, code, currencyData, maxLoanApprovalAmount);
                    roleData.getRoleBasedLimits().add(roleBasedLimitData);
                }
            }
            return new ArrayList<>(roleDatasMap.values());
        }

        public String schema() {
            final StringBuilder stringBuilder = new StringBuilder("select r.id as id, r.name as name, r.description as description, r.is_disabled as disabled, rbl.currency_id, ")
                    .append("c.code as currencyCode, c.name as currencyName, c.decimal_places as currencyDecimalPlaces, c.currency_multiplesof as currencyMultiplesOf, c.display_symbol as currencyDisplaySymbol, c.internationalized_name_code as currencyInternationalizedNameCode, ")
                    .append("rbl.id as roleBasedLimitId, rbl.max_loan_approval_amount as maxLoanApprovalAmount ")
                    .append("from m_role r left join m_role_based_limit rbl on r.id = rbl.role_id ")
                    .append("left join m_currency c on rbl.currency_id = c.id ");

            return stringBuilder.toString();
        }

    }

    @Override
    public Collection<RoleData> retrieveAppUserRoles(final Long appUserId) {
        final String sql = this.roleExtractor.schema() + " join m_appuser_role apr on apr.role_id = r.id where apr.appuser_id = ?";

        return this.jdbcTemplate.query(sql, this.roleExtractor, new Object[] { appUserId });
    }
}