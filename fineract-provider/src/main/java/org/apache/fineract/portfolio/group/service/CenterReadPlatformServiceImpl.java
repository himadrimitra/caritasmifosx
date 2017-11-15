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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.data.PaginationParametersDataValidator;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.service.CalendarEnumerations;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.group.api.GroupingTypesApiConstants;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.data.GroupTimelineData;
import org.apache.fineract.portfolio.group.data.StaffCenterData;
import org.apache.fineract.portfolio.group.domain.GroupTypes;
import org.apache.fineract.portfolio.group.domain.GroupingTypeEnumerations;
import org.apache.fineract.portfolio.group.exception.CenterNotFoundException;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.portfolio.village.service.VillageReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.common.constant.CommonConstants;
import com.finflux.task.configuration.service.TaskConfigurationUtils;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskEntityType;

@Service
public class CenterReadPlatformServiceImpl implements CenterReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final VillageReadPlatformService villageReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    public static LocalDate datePassed;

    // data mappers
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    // data mappers
    private final GroupDataMapper groupDataMapper = new GroupDataMapper();
    private final CentersAssociatedMapper centers = new CentersAssociatedMapper();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PaginationHelper<CenterData> paginationHelper = new PaginationHelper<>();
    private final PaginationParametersDataValidator paginationParametersDataValidator;
    private final static Set<String> supportedOrderByValues = new HashSet<>(Arrays.asList("id", "name", "officeId", "officeName"));

    private final TaskConfigurationUtils taskConfigurationUtils;

    @Autowired
    public CenterReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final ClientReadPlatformService clientReadPlatformService, final OfficeReadPlatformService officeReadPlatformService,
            final VillageReadPlatformService villageReadPlatformService, final StaffReadPlatformService staffReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final PaginationParametersDataValidator paginationParametersDataValidator,
            final ConfigurationDomainService configurationDomainService, final CalendarReadPlatformService calendarReadPlatformService,
            final TaskConfigurationUtils taskConfigurationUtils) {
        this.context = context;
        this.clientReadPlatformService = clientReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeReadPlatformService = officeReadPlatformService;
        this.villageReadPlatformService = villageReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.paginationParametersDataValidator = paginationParametersDataValidator;
        this.configurationDomainService = configurationDomainService;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.taskConfigurationUtils = taskConfigurationUtils;
    }

    // 'g.' preffix because of ERROR 1052 (23000): Column 'column_name' in where
    // clause is ambiguous
    // caused by the same name of columns in m_office and m_group tables
    private String getCenterExtraCriteria(final SearchParameters searchCriteria) {

        final StringBuffer extraCriteria = new StringBuffer(200);
        extraCriteria.append(" and g.level_id = " + GroupTypes.CENTER.getId());

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
            extraCriteria.append(" and g.display_name like ").append(ApiParameterHelper.sqlEncodeString(name + "%"));
        }

        final String hierarchy = searchCriteria.getHierarchy();
        if (hierarchy != null) {
            extraCriteria.append(" and o.hierarchy like ").append(ApiParameterHelper.sqlEncodeString(hierarchy + "%"));
        }

        if (StringUtils.isNotBlank(extraCriteria.toString())) {
            extraCriteria.delete(0, 4);
        }

        final Long staffId = searchCriteria.getStaffId();
        if (staffId != null) {
            extraCriteria.append(" and g.staff_id = ").append(staffId);
        }

        return extraCriteria.toString();
    }

    private static final String sqlQuery = "g.id as id, g.account_no as accountNo, g.external_id as externalId, g.display_name as name, "
            + "g.office_id as officeId, o.name as officeName, " //
            + "g.staff_id as staffId, s.display_name as staffName, " //
            + "g.status_enum as statusEnum, g.activation_date as activationDate, " //
            + "g.hierarchy as hierarchy, " //
            + "g.level_id as groupLevel," //
            + "g.closedon_date as closedOnDate, " + "g.submittedon_date as submittedOnDate, " + "sbu.username as submittedByUsername, "
            + "sbu.firstname as submittedByFirstname, " + "sbu.lastname as submittedByLastname, " + "clu.username as closedByUsername, "
            + "clu.firstname as closedByFirstname, " + "clu.lastname as closedByLastname, " + "acu.username as activatedByUsername, "
            + "acu.firstname as activatedByFirstname, " + "acu.lastname as activatedByLastname, " + "task.id as workflowId "
            + "from m_group g " //
            + "join m_office o on o.id = g.office_id " + "left join m_staff s on s.id = g.staff_id "
            + "left join m_group pg on pg.id = g.parent_id " + "left join m_appuser sbu on sbu.id = g.submittedon_userid "
            + "left join m_appuser acu on acu.id = g.activatedon_userid " + "left join m_appuser clu on clu.id = g.closedon_userid "
            + "LEFT JOIN f_task task ON task.entity_type=? and task.parent_id is null and task.entity_id = g.id ";

    private static final class CenterDataMapper implements RowMapper<CenterData> {

        private final String schemaSql;
        private final Boolean isWorkflowEnabled;

        public CenterDataMapper(final TaskConfigurationUtils taskConfigurationUtils) {
            this.isWorkflowEnabled = taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.CENTERONBOARDING);
            this.schemaSql = sqlQuery;
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public CenterData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String name = rs.getString("name");
            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = GroupingTypeEnumerations.status(statusEnum);
            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final String staffName = rs.getString("staffName");
            final String hierarchy = rs.getString("hierarchy");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstname = rs.getString("activatedByFirstname");
            final String activatedByLastname = rs.getString("activatedByLastname");
            final BigDecimal totalCollected = null;
            final BigDecimal totalOverdue = null;
            final BigDecimal totaldue = null;
            final BigDecimal installmentDue = null;
            final Long workflowId = JdbcSupport.getLong(rs, "workflowId");

            final GroupTimelineData timeline = new GroupTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
                    submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
                    closedByUsername, closedByFirstname, closedByLastname);

            return CenterData.instance(id, accountNo, name, externalId, status, activationDate, officeId, officeName, staffId, staffName,
                    hierarchy, timeline, null, totalCollected, totalOverdue, totaldue, installmentDue, this.isWorkflowEnabled, workflowId);
        }
    }

    private static final class CenterCalendarDataMapper implements RowMapper<CenterData> {

        private final String schemaSql;

        public CenterCalendarDataMapper() {

            this.schemaSql = "select ce.id as id, g.account_no as accountNo,"
                    + "ce.display_name as name, g.office_id as officeId, g.staff_id as staffId, s.display_name as staffName,"
                    + " g.external_id as externalId,  g.status_enum as statusEnum, g.activation_date as activationDate,"
                    + " g.hierarchy as hierarchy,   c.id as calendarId, ci.id as calendarInstanceId, ci.entity_id as entityId,"
                    + " ci.entity_type_enum as entityTypeId, c.title as title,  c.description as description,"
                    + "c.location as location, c.start_date as startDate, c.end_date as endDate, c.recurrence as recurrence,c.meeting_time as meetingTime,"
                    + "sum(if(l.loan_status_id=300 and lrs.duedate = :meetingDate,"
                    + "(ifnull(lrs.principal_amount,0)) + (ifnull(lrs.interest_amount,0))+(ifnull(lrs.fee_charges_amount,0))+ (ifnull(lrs.penalty_charges_amount,0)),0)) as installmentDue,"
                    + "sum(if(l.loan_status_id=300 and lrs.duedate = :meetingDate,"
                    + "(ifnull(lrs.principal_completed_derived,0)) + (ifnull(lrs.interest_completed_derived,0))+ (ifnull(lrs.fee_charges_completed_derived,0))+ (ifnull(lrs.penalty_charges_completed_derived,0)),0)) as totalCollected,"
                    + "sum(if(l.loan_status_id=300 and lrs.duedate <= :meetingDate, (ifnull(lrs.principal_amount,0)) + (ifnull(lrs.interest_amount,0))+ (ifnull(lrs.fee_charges_amount,0))+ (ifnull(lrs.penalty_charges_amount,0)),0))"
                    + "- sum(if(l.loan_status_id=300 and lrs.duedate <= :meetingDate, (ifnull(lrs.principal_completed_derived,0)) + (ifnull(lrs.interest_completed_derived,0))+ (ifnull(lrs.fee_charges_completed_derived,0))+ (ifnull(lrs.penalty_charges_completed_derived,0)),0)) as totaldue, "
                    + "sum(if(l.loan_status_id=300 and lrs.duedate < :meetingDate, (ifnull(lrs.principal_amount,0)) + (ifnull(lrs.interest_amount,0))+ (ifnull(lrs.fee_charges_amount,0))+ (ifnull(lrs.penalty_charges_amount,0)),0))"
                    + "- sum(if(l.loan_status_id=300 and lrs.duedate < :meetingDate, (ifnull(lrs.principal_completed_derived,0)) + (ifnull(lrs.interest_completed_derived,0))+ (ifnull(lrs.fee_charges_completed_derived,0))+ (ifnull(lrs.penalty_charges_completed_derived,0)),0)) as totaloverdue"
                    + " from m_calendar c join m_calendar_instance ci on ci.calendar_id=c.id and ci.entity_type_enum=:entityType"
                    + " join m_group ce on ce.id = ci.entity_id" + " join m_group g   on g.parent_id = ce.id"
                    + " join m_group_client gc on gc.group_id=g.id" + " join m_client cl on cl.id=gc.client_id"
                    + " join m_loan l on l.client_id = cl.id"
                    + " join m_loan_repayment_schedule lrs on lrs.loan_id=l.id join m_staff s on g.staff_id = s.id"
                    + " where g.office_id=:officeId";
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public CenterData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String name = rs.getString("name");
            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = GroupingTypeEnumerations.status(statusEnum);
            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final String staffName = rs.getString("staffName");
            final String hierarchy = rs.getString("hierarchy");

            final Long calendarId = rs.getLong("calendarId");
            final Long calendarInstanceId = rs.getLong("calendarInstanceId");
            final Long entityId = rs.getLong("entityId");
            final Integer entityTypeId = rs.getInt("entityTypeId");
            final EnumOptionData entityType = CalendarEnumerations.calendarEntityType(entityTypeId);
            final String title = rs.getString("title");
            final String description = rs.getString("description");
            final String location = rs.getString("location");
            final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
            final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
            final String recurrence = rs.getString("recurrence");
            final LocalTime meetingTime = JdbcSupport.getLocalTime(rs, "meetingTime");
            final BigDecimal totalCollected = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalCollected");
            final BigDecimal totalOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalOverdue");
            final BigDecimal totaldue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totaldue");
            final BigDecimal installmentDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "installmentDue");
            final Collection<Integer> monthOnDay = CalendarUtils.getMonthOnDay(recurrence);
            final Boolean isWorkflowEnabled = null;
            final Long workflowId = null;

            final CalendarData calendarData = CalendarData.instance(calendarId, calendarInstanceId, entityId, entityType, title,
                    description, location, startDate, endDate, null, null, false, recurrence, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, meetingTime, monthOnDay);
            return CenterData.instance(id, accountNo, name, externalId, status, activationDate, officeId, null, staffId, staffName,
                    hierarchy, null, calendarData, totalCollected, totalOverdue, totaldue, installmentDue, isWorkflowEnabled, workflowId);
        }
    }

    private static final class GroupDataMapper implements RowMapper<GroupGeneralData> {

        private final String schemaSql;

        public GroupDataMapper() {

            this.schemaSql = sqlQuery;
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public GroupGeneralData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String name = rs.getString("name");
            final String externalId = rs.getString("externalId");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = ClientEnumerations.status(statusEnum);
            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");

            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final String staffName = rs.getString("staffName");
            final String hierarchy = rs.getString("hierarchy");
            final String groupLevel = rs.getString("groupLevel");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstname = rs.getString("activatedByFirstname");
            final String activatedByLastname = rs.getString("activatedByLastname");

            final Boolean isWorkflowEnabled = null;
            final Long workflowId = null;

            final GroupTimelineData timeline = new GroupTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
                    submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
                    closedByUsername, closedByFirstname, closedByLastname);

            return GroupGeneralData.instance(id, accountNo, name, externalId, status, activationDate, officeId, officeName, null, null,
                    staffId, staffName, hierarchy, groupLevel, timeline, isWorkflowEnabled, workflowId);
        }
    }

    @Override
    public Page<CenterData> retrievePagedAll(final SearchParameters searchParameters, final PaginationParameters parameters) {

        this.paginationParametersDataValidator.validateParameterValues(parameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final CenterDataMapper centerMapper = new CenterDataMapper(this.taskConfigurationUtils);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        if (parameters.isOrderByRequested()) {
            sqlBuilder.append("select SQL_CALC_FOUND_ROWS * from (select ");
        } else {
            sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        }
        sqlBuilder.append(centerMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");

        final String extraCriteria = getCenterExtraCriteria(searchParameters);

        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }

        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder())
                    .append(" ) tempTable ");
        }

        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
                new Object[] { TaskEntityType.CENTER.getValue(), hierarchySearchString }, centerMapper);
    }

    @Override
    public Collection<CenterData> retrieveAll(final SearchParameters searchParameters, final PaginationParameters parameters) {

        this.paginationParametersDataValidator.validateParameterValues(parameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final CenterDataMapper centerMapper = new CenterDataMapper(this.taskConfigurationUtils);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(centerMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");

        final String extraCriteria = getCenterExtraCriteria(searchParameters);

        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }

        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder());
        }

        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        return this.jdbcTemplate.query(sqlBuilder.toString(), centerMapper,
                new Object[] { TaskEntityType.CENTER.getValue(), hierarchySearchString });
    }

    @Override
    public Collection<CenterData> retrieveAllForDropdown(final Long officeId) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final CenterDataMapper centerMapper = new CenterDataMapper(this.taskConfigurationUtils);
        final String sql = "select " + centerMapper.schema()
                + " where g.office_id = ? and g.parent_id is null and g.level_Id = ? and o.hierarchy like ? order by g.hierarchy";

        return this.jdbcTemplate.query(sql, centerMapper,
                new Object[] { TaskEntityType.CENTER.getValue(), officeId, GroupTypes.CENTER.getId(), hierarchySearchString });
    }

    @Override
    public CenterData retrieveTemplate(final Long officeId, final Long villageId, final boolean villagesInSelectedOfficeOnly,
            final boolean staffInSelectedOfficeOnly) {

        final Long officeIdDefaulted = defaultToUsersOfficeIfNull(officeId);

        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

        final boolean loanOfficersOnly = false;
        Collection<StaffData> staffOptions = null;
        if (staffInSelectedOfficeOnly) {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(officeIdDefaulted);
        } else {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(officeIdDefaulted,
                    loanOfficersOnly);
        }

        if (CollectionUtils.isEmpty(staffOptions)) {
            staffOptions = null;
        }

        Collection<GroupGeneralData> groupMembersOptions = retrieveAllGroupsForCenterDropdown(officeIdDefaulted);
        if (CollectionUtils.isEmpty(groupMembersOptions)) {
            groupMembersOptions = null;
        }
        final String accountNo = null;
        Collection<VillageData> villageOptions = null;
        if (villagesInSelectedOfficeOnly) {
            villageOptions = this.villageReadPlatformService.retrieveVillagesForLookup(officeIdDefaulted);
        }

        if (CollectionUtils.isEmpty(villageOptions)) {
            villageOptions = null;
        }

        VillageData villageCounter = null;
        if (villageId != null) {
            villageCounter = this.villageReadPlatformService.getCountValue(villageId);
        }
        final BigDecimal totalCollected = null;
        final BigDecimal totalOverdue = null;
        final BigDecimal totaldue = null;
        final BigDecimal installmentDue = null;
        // final boolean clientPendingApprovalAllowed =
        // this.configurationDomainService.isClientPendingApprovalAllowedEnabled();
        final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.CENTERONBOARDING);

        return CenterData.template(officeIdDefaulted, accountNo, DateUtils.getLocalDateOfTenant(), officeOptions, villageOptions,
                villageCounter, staffOptions, groupMembersOptions, totalCollected, totalOverdue, totaldue, installmentDue,
                isWorkflowEnabled);
    }

    private Collection<GroupGeneralData> retrieveAllGroupsForCenterDropdown(final Long officeId) {

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);

        final AllGroupTypesDataMapper allGroupTypesDataMapper = new AllGroupTypesDataMapper(this.taskConfigurationUtils);
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final String sql = "select " + allGroupTypesDataMapper.schema()
                + " where g.office_id = ? and g.parent_id is null and g.level_Id = ? and o.hierarchy like ? order by g.hierarchy";

        return this.jdbcTemplate.query(sql, allGroupTypesDataMapper, new Object[] { TaskEntityType.GROUP_ONBOARDING.getValue(),
                defaultOfficeId, GroupTypes.GROUP.getId(), hierarchySearchString });

    }

    private Long defaultToUsersOfficeIfNull(final Long officeId) {
        Long defaultOfficeId = officeId;
        if (defaultOfficeId == null) {
            defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
        }
        return defaultOfficeId;
    }

    @Override
    public CenterData retrieveOne(final Long centerId) {

        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";

            final CenterDataMapper centerMapper = new CenterDataMapper(this.taskConfigurationUtils);
            final String sql = "select " + centerMapper.schema() + " where g.id = ? and o.hierarchy like ?";
            return this.jdbcTemplate.queryForObject(sql, centerMapper,
                    new Object[] { TaskEntityType.CENTER.getValue(), centerId, hierarchySearchString });

        } catch (final EmptyResultDataAccessException e) {
            throw new CenterNotFoundException(centerId);
        }
    }

    @Override
    public GroupGeneralData retrieveCenterGroupTemplate(final Long centerId) {

        final CenterData center = retrieveOne(centerId);

        final Long centerOfficeId = center.officeId();
        final OfficeData centerOffice = this.officeReadPlatformService.retrieveOffice(centerOfficeId);

        StaffData staff = null;
        final Long staffId = center.staffId();
        String staffName = null;
        if (staffId != null) {
            staff = this.staffReadPlatformService.retrieveStaff(staffId);
            staffName = staff.getDisplayName();
        }

        final Collection<CenterData> centerOptions = Arrays.asList(center);
        final Collection<OfficeData> officeOptions = Arrays.asList(centerOffice);

        Collection<StaffData> staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(centerOfficeId);
        if (CollectionUtils.isEmpty(staffOptions)) {
            staffOptions = null;
        }

        Collection<ClientData> clientOptions = this.clientReadPlatformService.retrieveAllForLookupByOfficeId(centerOfficeId);
        if (CollectionUtils.isEmpty(clientOptions)) {
            clientOptions = null;
        }
        final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.GROUPONBARDING);
        return GroupGeneralData.template(centerOfficeId, center.getId(), center.getAccountNo(), center.getName(), staffId, staffName,
                centerOptions, officeOptions, staffOptions, clientOptions, null, isWorkflowEnabled);
    }

    @Override
    public Collection<GroupGeneralData> retrieveAssociatedGroups(final Long centerId) {
        final String sql = "select " + this.groupDataMapper.schema() + " where g.parent_id = ? ";
        return this.jdbcTemplate.query(sql, this.groupDataMapper, new Object[] { TaskEntityType.GROUP_ONBOARDING.getValue(), centerId });
    }

    @Override
    public Collection<CenterData> retrieveAssociatedCenters(final Long villageId) {
        final String sql = "select " + this.centers.schema() + " where vc.village_id = ? ";

        return this.jdbcTemplate.query(sql, this.centers, new Object[] { villageId });
    }

    private static final class CentersAssociatedMapper implements RowMapper<CenterData> {

        private final String schema;

        public CentersAssociatedMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("g.id as id, g.account_no as accountNo, g.external_id as externalId, g.display_name as name,  ");
            builder.append("g.office_id as officeId, o.name as officeName, "); //
            builder.append("g.staff_id as staffId, s.display_name as staffName, "); //
            builder.append("g.status_enum as statusEnum, "); //
            builder.append("g.hierarchy as hierarchy, "); //

            builder.append("g.closedon_date as closedOnDate, ");
            builder.append("clu.username as closedByUsername, ");
            builder.append("clu.firstname as closedByFirstname, ");
            builder.append("clu.lastname as closedByLastname, ");

            builder.append("g.submittedon_date as submittedOnDate, ");
            builder.append("sbu.username as submittedByUsername, ");
            builder.append("sbu.firstname as submittedByFirstname, ");
            builder.append("sbu.lastname as submittedByLastname, ");

            builder.append("g.activation_date as activationDate, ");
            builder.append("acu.username as activatedByUsername, ");
            builder.append("acu.firstname as activatedByFirstname, ");
            builder.append("acu.lastname as activatedByLastname ");

            builder.append("from m_group g "); //
            builder.append("join m_office o on o.id = g.office_id ");
            builder.append("left join m_staff s on s.id = g.staff_id ");
            builder.append("left join chai_village_center vc on vc.center_id = g.id ");
            builder.append("left join m_appuser sbu on sbu.id = g.submittedon_userid ");
            builder.append("left join m_appuser acu on acu.id = g.activatedon_userid ");
            builder.append("left join m_appuser clu on clu.id = g.closedon_userid ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public CenterData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String name = rs.getString("name");
            final String externalId = rs.getString("externalId");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = ClientEnumerations.status(statusEnum);
            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");

            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final String staffName = rs.getString("staffName");
            final String hierarchy = rs.getString("hierarchy");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstname = rs.getString("activatedByFirstname");
            final String activatedByLastname = rs.getString("activatedByLastname");
            final BigDecimal totalCollected = null;
            final BigDecimal totalOverdue = null;
            final BigDecimal totaldue = null;
            final BigDecimal installmentDue = null;
            final Boolean isWorkflowEnabled = null;
            final Long workflowId = null;

            final GroupTimelineData timeline = new GroupTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
                    submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
                    closedByUsername, closedByFirstname, closedByLastname);

            return CenterData.instance(id, accountNo, name, externalId, status, activationDate, officeId, officeName, staffId, staffName,
                    hierarchy, timeline, null, totalCollected, totalOverdue, totaldue, installmentDue, isWorkflowEnabled, workflowId);
        }

    }

    @Override
    public CenterData retrieveCenterWithClosureReasons() {
        final List<CodeValueData> closureReasons = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(GroupingTypesApiConstants.CENTER_CLOSURE_REASON));
        return CenterData.withClosureReasons(closureReasons);
    }

    @Override
    public Collection<StaffCenterData> retriveAllCentersByMeetingDate(final Long officeId, final Date meetingDate, final Long staffId) {
        validateForGenerateCollectionSheet(staffId);
        final LocalDate meetingDateTolocalDateFormat = new LocalDate(meetingDate);
        final CenterCalendarDataMapper centerCalendarMapper = new CenterCalendarDataMapper();
        String sql = centerCalendarMapper.schema();
        Collection<CenterData> centerDataArray = null;
        final String formattedMeetingDate = this.formatter.print(meetingDateTolocalDateFormat);
        final Map<String, Object> paramMap = new HashMap<>(5);
        if (staffId != null) {
            sql += " and g.staff_id=:staffid ";
            sql += "and lrs.duedate<=:meetingDate and l.loan_type_enum=:loantype";
            sql += " group by c.id,ci.id";

            paramMap.put("meetingDate", formattedMeetingDate);
            paramMap.put("loantype", AccountType.JLG.getValue());
            paramMap.put("staffid", staffId);
            paramMap.put("officeId", officeId);
            paramMap.put("entityType", CalendarEntityType.CENTERS.getValue());

            centerDataArray = this.namedParameterJdbcTemplate.query(sql, paramMap, centerCalendarMapper);
        } else {
            centerDataArray = this.namedParameterJdbcTemplate.query(sql, paramMap, centerCalendarMapper);
        }

        final Collection<StaffCenterData> staffCenterDataArray = new ArrayList<>();
        Boolean flag = false;
        Integer numberOfDays = 0;
        final boolean isSkipRepaymentOnFirstMonthEnabled = this.configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
        if (isSkipRepaymentOnFirstMonthEnabled) {
            numberOfDays = this.configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
        }
        for (final CenterData centerData : centerDataArray) {
            if (isValidRecurringDate(centerData, meetingDate, isSkipRepaymentOnFirstMonthEnabled, numberOfDays)) {
                if (staffCenterDataArray.size() <= 0) {
                    final Collection<CenterData> meetingFallCenter = new ArrayList<>();
                    meetingFallCenter.add(centerData);
                    staffCenterDataArray.add(StaffCenterData.instance(centerData.staffId(), centerData.getStaffName(), meetingFallCenter));
                } else {
                    for (final StaffCenterData staffCenterData : staffCenterDataArray) {
                        flag = false;
                        if (staffCenterData.getStaffId().equals(centerData.staffId())) {
                            staffCenterData.getMeetingFallCenters().add(centerData);

                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        final Collection<CenterData> meetingFallCenter = new ArrayList<>();
                        meetingFallCenter.add(centerData);
                        staffCenterDataArray
                                .add(StaffCenterData.instance(centerData.staffId(), centerData.getStaffName(), meetingFallCenter));
                    }
                }

            }
        }
        return staffCenterDataArray;
    }

    private boolean isValidRecurringDate(final CenterData centerData, final Date meetingDate,
            final boolean isSkipRepaymentOnFirstMonthEnabled, final Integer numberOfDays) {
        final Long caliendarInstanceId = centerData.getCollectionMeetingCalendar().getCalendarInstanceId();
        final CalendarData calendarHistoryData = this.calendarReadPlatformService
                .retrieveCalendarHistoryByCalendarInstanceAndDueDate(meetingDate, caliendarInstanceId);
        if (calendarHistoryData != null
                && meetingDate != null) { return CalendarUtils.isValidRedurringDate(calendarHistoryData.getRecurrence(),
                        calendarHistoryData.getStartDate(), new LocalDate(meetingDate), isSkipRepaymentOnFirstMonthEnabled, numberOfDays); }
        return centerData.getCollectionMeetingCalendar().isValidRecurringDate(new LocalDate(meetingDate),
                isSkipRepaymentOnFirstMonthEnabled, numberOfDays);
    }

    public void validateForGenerateCollectionSheet(final Long staffId) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("productivecollectionsheet");
        baseDataValidator.reset().parameter("staffId").value(staffId).notNull();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }

    }

    @Override
    public CenterData retrieveCenterAndMembersDetailsTemplate(final Long centerId) {
        Collection<GroupGeneralData> groups = null;
        CenterData centerAccount = retrieveOne(centerId);
        // get group associations
        groups = retrieveAssociatedGroups(centerId);
        /* attach group members in group */
        final ArrayList<GroupGeneralData> groupsToAssociateClient = (ArrayList<GroupGeneralData>) groups;
        for (final GroupGeneralData groupGeneralData : groupsToAssociateClient) {
            ArrayList<ClientData> membersOfGroup = (ArrayList<ClientData>) this.clientReadPlatformService
                    .retrieveActiveClientMembersOfGroup(groupGeneralData.getId());
            groupGeneralData.update(membersOfGroup);
            if (!CollectionUtils.isEmpty(membersOfGroup)) {
                membersOfGroup = null;
                final CalendarData collectionMeetingCalendar = null;
                centerAccount = CenterData.withAssociations(centerAccount, groups, collectionMeetingCalendar, membersOfGroup);

            }

        }

        return centerAccount;
    }

    @Override
    public boolean isCenter(final Long centerId) {
        final StringBuilder sql = new StringBuilder("SELECT ");
        sql.append("if(center.level_id = gl.id,true,false)");
        sql.append("from m_group center ");
        sql.append("left join m_group_level gl on gl.level_name = 'Center' ");
        sql.append("where center.id = ? ");
        return this.jdbcTemplate.queryForObject(sql.toString(), Boolean.class, centerId);
    }

}