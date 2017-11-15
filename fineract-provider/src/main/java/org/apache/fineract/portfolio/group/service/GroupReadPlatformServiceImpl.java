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
package org.apache.fineract.portfolio.group.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.data.PaginationParametersDataValidator;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.group.api.GroupingTypesApiConstants;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.domain.GroupTypes;
import org.apache.fineract.portfolio.group.exception.GroupNotFoundException;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.common.constant.CommonConstants;
import com.finflux.task.configuration.service.TaskConfigurationUtils;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskEntityType;

@Service
public class GroupReadPlatformServiceImpl implements GroupReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CenterReadPlatformService centerReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    private final PaginationHelper<GroupGeneralData> paginationHelper = new PaginationHelper<>();
    private final PaginationParametersDataValidator paginationParametersDataValidator;
    private final TaskConfigurationUtils taskConfigurationUtils;

    private final static Set<String> supportedOrderByValues = new HashSet<>(Arrays.asList("id", "name", "officeId", "officeName"));

    @Autowired
    public GroupReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final ClientReadPlatformService clientReadPlatformService, final CenterReadPlatformService centerReadPlatformService,
            final OfficeReadPlatformService officeReadPlatformService, final StaffReadPlatformService staffReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final PaginationParametersDataValidator paginationParametersDataValidator,
            final TaskConfigurationUtils taskConfigurationUtils) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.centerReadPlatformService = centerReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.paginationParametersDataValidator = paginationParametersDataValidator;
        this.taskConfigurationUtils = taskConfigurationUtils;
    }

    @Override
    public GroupGeneralData retrieveTemplate(final Long officeId, final boolean isCenterGroup, final boolean staffInSelectedOfficeOnly) {

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);

        Collection<CenterData> centerOptions = null;
        if (isCenterGroup) {
            centerOptions = this.centerReadPlatformService.retrieveAllForDropdown(defaultOfficeId);
        }

        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

        final boolean loanOfficersOnly = false;
        Collection<StaffData> staffOptions = null;
        if (staffInSelectedOfficeOnly) {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(defaultOfficeId);
        } else {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(defaultOfficeId,
                    loanOfficersOnly);
        }

        if (CollectionUtils.isEmpty(staffOptions)) {
            staffOptions = null;
        }

        Collection<ClientData> clientOptions = this.clientReadPlatformService.retrieveAllForLookupByOfficeId(defaultOfficeId);
        if (CollectionUtils.isEmpty(clientOptions)) {
            clientOptions = null;
        }

        final Collection<CodeValueData> availableRoles = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(GroupingTypesApiConstants.GROUP_ROLE_NAME);

        final Long centerId = null;
        final String accountNo = null;
        final String centerName = null;
        final Long staffId = null;
        final String staffName = null;
        final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.GROUPONBARDING);

        return GroupGeneralData.template(defaultOfficeId, centerId, accountNo, centerName, staffId, staffName, centerOptions, officeOptions,
                staffOptions, clientOptions, availableRoles, isWorkflowEnabled);
    }

    private Long defaultToUsersOfficeIfNull(final Long officeId) {
        Long defaultOfficeId = officeId;
        if (defaultOfficeId == null) {
            defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
        }
        return defaultOfficeId;
    }

    @Override
    public Page<GroupGeneralData> retrievePagedAll(final SearchParameters searchParameters, final PaginationParameters parameters) {

        this.paginationParametersDataValidator.validateParameterValues(parameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final AllGroupTypesDataMapper allGroupTypesDataMapper = new AllGroupTypesDataMapper(this.taskConfigurationUtils);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        if (parameters.isOrderByRequested()) {
            sqlBuilder.append("select SQL_CALC_FOUND_ROWS * from (select ");
        } else {
            sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        }

        sqlBuilder.append(allGroupTypesDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");

        final String extraCriteria = getGroupExtraCriteria(searchParameters);

        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }

        if (parameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder())
                    .append(" ) tempTable ");
        }

        if (parameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
                new Object[] { TaskEntityType.GROUP_ONBOARDING.getValue(), hierarchySearchString }, allGroupTypesDataMapper);
    }

    @Override
    public Collection<GroupGeneralData> retrieveAll(final SearchParameters searchParameters, final PaginationParameters parameters) {
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final AllGroupTypesDataMapper allGroupTypesDataMapper = new AllGroupTypesDataMapper(this.taskConfigurationUtils);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(allGroupTypesDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");

        final String extraCriteria = getGroupExtraCriteria(searchParameters);

        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }

        if (parameters.isOrderByRequested()) {
            sqlBuilder.append(parameters.orderBySql());
        }

        if (parameters.isLimited()) {
            sqlBuilder.append(parameters.limitSql());
        }

        return this.jdbcTemplate.query(sqlBuilder.toString(), allGroupTypesDataMapper,
                new Object[] { TaskEntityType.GROUP_ONBOARDING.getValue(), hierarchySearchString });
    }

    // 'g.' preffix because of ERROR 1052 (23000): Column 'column_name' in where
    // clause is ambiguous
    // caused by the same name of columns in m_office and m_group tables
    private String getGroupExtraCriteria(final SearchParameters searchCriteria) {

        final StringBuffer extraCriteria = new StringBuffer(200);
        extraCriteria.append(" and g.level_Id = ").append(GroupTypes.GROUP.getId());
        final Map<String, String> searchConditions = searchCriteria.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.GROUP_DISPLAY_NAME:
                    extraCriteria.append(" and ( g.display_name = '").append(value).append("' ) ");
                break;
                default:
                break;
            }
        });

        final Long officeId = searchCriteria.getOfficeId();
        if (officeId != null) {
            extraCriteria.append(" and g.office_id = ").append(officeId);
        }

        final String externalId = searchCriteria.getExternalId();
        if (externalId != null) {
            extraCriteria.append(" and g.external_id = ").append(ApiParameterHelper.sqlEncodeString(externalId));
        }

        final String name = searchCriteria.getName();
        if (name != null) {
            extraCriteria.append(" and g.display_name like ").append(ApiParameterHelper.sqlEncodeString("%" + name + "%"));
        }

        final String hierarchy = searchCriteria.getHierarchy();
        if (hierarchy != null) {
            extraCriteria.append(" and o.hierarchy like ").append(ApiParameterHelper.sqlEncodeString(hierarchy + "%"));
        }

        if (searchCriteria.isStaffIdPassed()) {
            extraCriteria.append(" and g.staff_id = ").append(searchCriteria.getStaffId());
        }

        if (StringUtils.isNotBlank(extraCriteria.toString())) {
            extraCriteria.delete(0, 4);
        }

        final Long staffId = searchCriteria.getStaffId();
        if (staffId != null) {
            extraCriteria.append(" and g.staff_id = ").append(staffId);
        }

        if (searchCriteria.isOrphansOnly()) {
            extraCriteria.append(" and g.parent_id IS NULL");
        }

        return extraCriteria.toString();
    }

    @Override
    public GroupGeneralData retrieveOne(final Long groupId) {

        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";

            final AllGroupTypesDataMapper allGroupTypesDataMapper = new AllGroupTypesDataMapper(this.taskConfigurationUtils);
            final String sql = "select " + allGroupTypesDataMapper.schema() + " where g.id = ? and o.hierarchy like ?";
            return this.jdbcTemplate.queryForObject(sql, allGroupTypesDataMapper,
                    new Object[] { TaskEntityType.GROUP_ONBOARDING.getValue(), groupId, hierarchySearchString });
        } catch (final EmptyResultDataAccessException e) {
            throw new GroupNotFoundException(groupId);
        }
    }

    @Override
    public GroupGeneralData retrieveCenterDetailsWithGroup(final GroupGeneralData groupGeneralData) {
        try {
            final HierarchyLookupMapper hm = new HierarchyLookupMapper(groupGeneralData);
            final String sql = "SELECT " + hm.schema() + " where g.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, hm, new Object[] { groupGeneralData.getId() });
        } catch (final EmptyResultDataAccessException e) {

            // If group doesn't have parentId we send back groupDetail
            return groupGeneralData;
        }
    }

    @Override
    public Collection<GroupGeneralData> retrieveGroupsForLookup(final Long officeId) {
        this.context.authenticatedUser();
        final GroupLookupDataMapper rm = new GroupLookupDataMapper();
        final String sql = "Select " + rm.schema() + " and g.office_id=?";
        return this.jdbcTemplate.query(sql, rm, new Object[] { officeId });
    }

    private static final class GroupLookupDataMapper implements RowMapper<GroupGeneralData> {

        public final String schema() {
            return "g.id as id, g.account_no as accountNo, g.display_name as displayName,g.level_id as groupLevel from m_group g where g.level_id = 2 ";
        }

        @Override
        public GroupGeneralData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String accountNo = rs.getString("accountNo");
            final String displayName = rs.getString("displayName");
            final String groupLevel = rs.getString("groupLevel");
            return GroupGeneralData.lookup(id, accountNo, displayName, groupLevel);
        }
    }

    private static final class HierarchyLookupMapper implements RowMapper<GroupGeneralData> {

        private final GroupGeneralData groupGeneralData;

        public HierarchyLookupMapper(final GroupGeneralData groupGeneralData) {
            this.groupGeneralData = groupGeneralData;
        }

        public final String schema() {
            return "o.id as officeId,o.name as officeName,g.parent_id AS centerId,pg.display_name as centerName, v.id as villageId,v.village_name as villageName "
                    + "FROM m_group g " + "join m_group pg on g.parent_id = pg.id "
                    + "left join chai_village_center vc on (vc.center_id = g.id or vc.center_id = g.parent_id)"
                    + "LEFT JOIN chai_villages v ON vc.village_id = v.id " + "left join m_office o on pg.office_id = o.id ";
        }

        @Override
        public GroupGeneralData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            // Long officeId = null;
            // String officeName = null;
            final Long centerId = JdbcSupport.getLong(rs, "centerId");
            final String centerName = rs.getString("centerName");
            final Long villageId = JdbcSupport.getLong(rs, "villageId");
            final String villageName = rs.getString("villageName");

            final VillageData villageData = VillageData.lookup(villageId, villageName);
            final Long officeId = JdbcSupport.getLong(rs, "officeId");
            final String officeName = rs.getString("officeName");

            // officeId = JdbcSupport.getLong(rs, "officeId");
            // officeName = rs.getString("officeName");
            return GroupGeneralData.lookupforhierarchy(centerId, centerName, villageData, this.groupGeneralData, officeId, officeName);
        }
    }

    @Override
    public GroupGeneralData retrieveGroupWithClosureReasons() {
        final List<CodeValueData> closureReasons = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(GroupingTypesApiConstants.GROUP_CLOSURE_REASON));
        return GroupGeneralData.withClosureReasons(closureReasons);
    }

}