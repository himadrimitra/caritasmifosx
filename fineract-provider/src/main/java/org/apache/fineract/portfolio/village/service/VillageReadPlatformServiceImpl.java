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
package org.apache.fineract.portfolio.village.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
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
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.data.GroupRoleData;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.portfolio.village.data.VillageTimelineData;
import org.apache.fineract.portfolio.village.domain.VillageTypeEnumerations;
import org.apache.fineract.portfolio.village.exception.VillageNotFoundException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;


@Service
public class VillageReadPlatformServiceImpl implements VillageReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    
    private final OfficeReadPlatformService officeReadPlatformService;
    private final PaginationHelper<VillageData> paginationHelper = new PaginationHelper<>();
    private final PaginationParametersDataValidator paginationParametersDataValidator;
    private final static Set<String> supportedOrderByValues = new HashSet<>(Arrays.asList("id", "name", "officeId", "officeName"));
    
    private final RetrieveOneMapper oneVillageMapper = new RetrieveOneMapper();
    private final RetrieveHierarchyMapper hierarchyMapper = new RetrieveHierarchyMapper();
    private final ConfigurationDomainService configurationDomainService;
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    
    @Autowired
    public VillageReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource, final OfficeReadPlatformService
            officeReadPlatformService, final PaginationParametersDataValidator paginationParametersDataValidator,
            final ConfigurationDomainService configurationDomainService, final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository) {

        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeReadPlatformService = officeReadPlatformService;
        this.paginationParametersDataValidator = paginationParametersDataValidator;
        this.configurationDomainService = configurationDomainService ;
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository ;
    }
    
    @Override
    public VillageData retrieveTemplate(final Long officeId) {

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);
        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        
        return VillageData.template(defaultOfficeId, officeOptions);
    }
    
    @Override
    public Page<VillageData> retrievePagedAll(SearchParameters searchParameters, PaginationParameters paginationParameters) {
        final VillageDataMapper villageDataMapper = new VillageDataMapper(isVillageWorkflowEnabled());
        this.paginationParametersDataValidator.validateParameterValues(paginationParameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        
        StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(villageDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");
        
        final String extraCriteria = getVillageExtraCriteria(searchParameters);
        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }
        
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append("o."+searchParameters.getOrderBy());
            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
            }
        }
        
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), new Object[] { TaskEntityType.VILLAGE.getValue(), hierarchySearchString},
                villageDataMapper);
    }
    
    private boolean isVillageWorkflowEnabled() {
        boolean isEnabled = false ;
        if(this.configurationDomainService.isWorkFlowEnabled()) {
            final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                    .findOneByEntityTypeAndEntityId(TaskConfigEntityType.VILLAGEONBOARDING.getValue(), -1L);
            if(taskConfigEntityTypeMapping != null) {
                isEnabled = true ;
            }
        }
        return isEnabled ;
    }
    
    @Override
    public Collection<VillageData> retrieveAll(SearchParameters searchParameters, PaginationParameters paginationParameters) {
        final VillageDataMapper villageDataMapper = new VillageDataMapper(isVillageWorkflowEnabled());
        this.paginationParametersDataValidator.validateParameterValues(paginationParameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(villageDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");
        
        final String extraCriteria = getVillageExtraCriteria(searchParameters);
        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }
        
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
            }
        }
        
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        
        return this.jdbcTemplate.query(sqlBuilder.toString(), villageDataMapper, new Object[] {  TaskEntityType.VILLAGE.getValue(), hierarchySearchString });
    }
    
    @Override
    public Collection<CenterData> retrieveHierarchy(final Long villageId) {

        try {
            
            final String sql = "SELECT "+ this.hierarchyMapper.schema() +"WHERE vc.village_id=?";
            return this.jdbcTemplate.query(sql, this.hierarchyMapper, new Object[] { villageId });
            
        } catch (final EmptyResultDataAccessException e) {
            throw new VillageNotFoundException(villageId);
        }
    }
    
    private static final class RetrieveHierarchyMapper implements RowMapper<CenterData> {
        private final String schema;
        
        public RetrieveHierarchyMapper() {
            
            final StringBuilder builder = new StringBuilder(400);
            
            builder.append("vc.center_id as centerId,c.display_name AS centerName,g.id AS groupId,g.display_name AS groupName, ");
            builder.append("mc.id AS clientId,mc.display_name AS clientName, ml.id as loanId, ml.account_no as loanAccountNo, lp.short_name as productShortName ");
            builder.append("FROM chai_village_center vc ");
            builder.append("JOIN m_group c ON vc.center_id = c.id ");
            builder.append("LEFT JOIN m_group g ON g.parent_id=c.id ");
            builder.append("LEFT JOIN m_group_client gc ON gc.group_id=g.id ");
            builder.append("LEFT JOIN m_client mc ON mc.id=gc.client_id ");
            builder.append("left join m_loan ml on ml.client_id = mc.id ");
            builder.append("left join m_product_loan lp on ml.product_id = lp.id ");
            this.schema = builder.toString();
        }
        
        public String schema() {
            return this.schema;
        }
        @Override
        public CenterData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long centerId = rs.getLong("centerId");
            final String centerName = rs.getString("centerName");
            CenterData centerData = CenterData.formCenterData(centerId, centerName);
            final Collection<ClientData> activeClientMembers = null;
            final Collection<GroupRoleData> groupRoles = null;
            final Collection<CalendarData> calendarsData = null;
            final CalendarData collectionMeetingCalendar = null;
            final Collection<ClientData> clientMembers=null;
            Collection<GroupGeneralData> groups = new ArrayList<>();
            centerData = CenterData.withAssociations(centerData, groups, collectionMeetingCalendar,clientMembers);
            do {
                if (!centerId.equals(rs.getLong("centerId"))) {
                    rs.previous();
                    break;
                }
                final Long groupId = JdbcSupport.getLong(rs, "groupId");
                final String groupName = rs.getString("groupName");
                if (groupId == null) {
                    break;
                }
                GroupGeneralData groupData = GroupGeneralData.formGroupData(groupId, groupName);
                Collection<ClientData> clients = new ArrayList<>();
                groupData = GroupGeneralData.withAssocations(groupData, clients, activeClientMembers, groupRoles, calendarsData,
                        collectionMeetingCalendar);
                groups.add(groupData);

                do {
                    if (!groupId.equals(rs.getLong("groupId"))) {
                        rs.previous();
                        break;
                    }
                    final Long clientId = JdbcSupport.getLong(rs, "clientId");
                    final String clientName = rs.getString("clientName");
                    if (clientId == null) {
                        break;
                    }
                    final ClientData clientData = ClientData.formClientData(clientId, clientName);
                    clients.add(clientData);
                    do {
                        if (!groupId.equals(rs.getLong("groupId")) || !clientId.equals(rs.getLong("clientId"))) {
                            rs.previous();
                            break;
                        }
                        final Long loanId = JdbcSupport.getLong(rs, "loanId");
                        final String productShortName = rs.getString("productShortName");
                        final String accountNo = rs.getString("loanAccountNo");
                        if (loanId == null) {
                            break;
                        }
                        LoanAccountSummaryData loanData = LoanAccountSummaryData.formLoanAccountSummaryData(loanId, accountNo,
                                productShortName);
                        clientData.addLoanAccountSummaryData(loanData);
                    } while (rs.next());
                } while (rs.next());
            } while (rs.next());
            return centerData;
        }
    
    }
    
    
    @Override
    public VillageData retrieveOne(final Long villageId) {

        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";
            
            final String sql = "select " + this.oneVillageMapper.schema() + " where v.id = ? and o.hierarchy like ? ";
            return this.jdbcTemplate.queryForObject(sql, this.oneVillageMapper, new Object[] { villageId, hierarchySearchString });
            
        } catch (final EmptyResultDataAccessException e) {
            throw new VillageNotFoundException(villageId);
        }
    }
    
    private static final class RetrieveOneMapper implements RowMapper<VillageData> {

        private final String schema;
        
        public RetrieveOneMapper() {
            
            final StringBuilder builder = new StringBuilder(400);
            
            builder.append("v.id as villageId, v.external_id as externalId, v.office_id as officeId, o.name as officeName, v.village_code as villageCode, ");
            builder.append("v.village_name as villageName, v.counter as counter,");
            builder.append("v.status as status, ");
            
            builder.append("v.activatedon_date as activatedOnDate, ");
            builder.append("acu.username as activatedByUsername, ");
            builder.append("acu.firstname as activatedByFirstname, ");
            builder.append("acu.lastname as activatedByLastname, ");
            
            builder.append("v.submitedon_date as submittedOnDate, ");
            builder.append("sbu.username as submittedByUsername, ");
            builder.append("sbu.firstname as submittedByFirstname, ");
            builder.append("sbu.lastname as submittedByLastname ");
            
            builder.append("from chai_villages v ");
            builder.append("join m_office o on o.id = v.office_id ");
            builder.append("left join m_appuser sbu on sbu.id = v.submitedon_userid ");
            builder.append("left join m_appuser acu on acu.id = v.activatedon_userid ");
            
            this.schema = builder.toString();
        }
        
        public String schema() {
            return this.schema;
        }
        
        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("villageId");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String villageCode = rs.getString("villageCode");
            final String villageName = rs.getString("villageName");
            final Long counter = rs.getLong("counter");
            final Integer status = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData statusName = VillageTypeEnumerations.status(status);
            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstName = rs.getString("activatedByFirstName");
            final String activatedByLastName = rs.getString("activatedByLastName");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstName = rs.getString("submittedByFirstName");
            final String submittedByLastName = rs.getString("submittedByLastName");
            
            final VillageTimelineData timeline = new VillageTimelineData(activatedOnDate, activatedByUsername, activatedByFirstName, 
                    activatedByLastName, submittedOnDate, submittedByUsername, submittedByFirstName, submittedByLastName);

            return VillageData.instance(id, externalId, officeId, officeName, villageCode, villageName, counter, statusName, timeline, null, false);
        }
        
    }
    
    private String getVillageExtraCriteria(SearchParameters searchParameters) {

        String extraCriteria = "";
        
        String sqlSearch = searchParameters.getSqlSearch();
        final Long officeId = searchParameters.getOfficeId();
        final String externalId = searchParameters.getExternalId();
        final String name = searchParameters.getName();
        
        if (sqlSearch != null) {
            sqlSearch = sqlSearch.replaceAll(" village_name ", " v.village_name ");
            sqlSearch = sqlSearch.replaceAll("village_name ", "v.village_name ");
            extraCriteria = " and (" + sqlSearch + ")";
        }
        
        if (officeId != null) {
            extraCriteria += " and v.office_id = " + officeId;
        }
        
        if (externalId != null) {
            extraCriteria += " and v.external_id like " + ApiParameterHelper.sqlEncodeString(externalId);
        }
        
        if (externalId != null) {
            extraCriteria += " and v.village_name like " + ApiParameterHelper.sqlEncodeString(name);
        }
        
        if (searchParameters.isScopedByOfficeHierarchy()) {
            extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString(searchParameters.getHierarchy() + "%");
        }

        if (StringUtils.isNotBlank(extraCriteria)) {
            extraCriteria = extraCriteria.substring(4);
        }
        
        return extraCriteria;
    }

    private static final class VillageDataMapper implements RowMapper<VillageData> {
        
        private final String schema;
        private final boolean isWorkflowEnabled ;
        
        public VillageDataMapper(final Boolean isWorkflowEnabled) {
            this.isWorkflowEnabled = isWorkflowEnabled ;
            final StringBuilder builder = new StringBuilder(200);
            
            builder.append(" v.id as id, v.external_id as externalId, v.office_id as officeId, o.name as officeName, ");
            builder.append(" v.village_code as villageCode, v.village_name as villageName, v.counter as counter, v.`status` as status,");
            builder.append(" v.activatedon_date as activatedOnDate, v.submitedon_date as submittedOnDate, ");
            builder.append(" acu.username as activatedByUsername, acu.firstname as activatedByFirstName, acu.lastname as activatedByLastName, ");
            builder.append(" sbu.username as submittedByUsername, sbu.firstname as submittedByFirstName, sbu.lastname as submittedByLastName, ");
            builder.append(" task.id as workflowId ") ;
            builder.append(" from chai_villages v  ");
            builder.append(" join m_office o on o.id = v.office_id ");
            builder.append(" left join m_appuser acu on acu.id = v.activatedon_userid ");
            builder.append(" left join m_appuser sbu on sbu.id = v.submitedon_userid ");
            builder.append(" LEFT JOIN f_task task ON task.entity_type=? and task.parent_id is null and task.entity_id = v.id  ");
            
            this.schema = builder.toString();
        }
        
        public final String schema() {
            return this.schema;
        }

        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String villageCode = rs.getString("villageCode");
            final String villageName = rs.getString("villageName");
            final Long counter = rs.getLong("counter");
            final Integer status = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData statusName = VillageTypeEnumerations.status(status);
            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstName = rs.getString("activatedByFirstName");
            final String activatedByLastName = rs.getString("activatedByLastName");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstName = rs.getString("submittedByFirstName");
            final String submittedByLastName = rs.getString("submittedByLastName");
            final Long workflowId = rs.getLong("workflowId") ;
            final VillageTimelineData timeline = new VillageTimelineData(activatedOnDate, activatedByUsername, activatedByFirstName, 
                    activatedByLastName, submittedOnDate, submittedByUsername, submittedByFirstName, submittedByLastName);

            return VillageData.instance(id, externalId, officeId, officeName, villageCode, villageName, counter, statusName, timeline, workflowId, this.isWorkflowEnabled);
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
    public Collection<VillageData> retrieveVillagesForLookup(final Long officeId) {

        final AppUser currentUser = this.context.authenticatedUser();
        /*final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySerachingString = hierarchy + "%";*/
        
        final VillageLookupDataMapper rm = new VillageLookupDataMapper();
        final String sql = "Select "+ rm.schema() + " where v.office_id = ? "; //and o.hierarchy like ? ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { officeId });
    }
    
    private static final class VillageLookupDataMapper implements RowMapper<VillageData> {

        public final String schema() {
            return "v.id as id, v.village_name as villageName from chai_villages v ";  //join m_office o on o.id = v.office_id ";
        }
        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String villageName = rs.getString("villageName");
            
            return VillageData.lookup(id, villageName);
        }
        
    }
    
    @Override
    public VillageData getCountValue(final Long villageId){

        this.context.authenticatedUser();
        final VillageCountValueMapper vc = new VillageCountValueMapper();
        final String sql = "Select "+ vc.schemaForCount() + " where v.id = ? ";
        return this.jdbcTemplate.queryForObject(sql, vc, new Object[] { villageId });
    }
    
    private final class VillageCountValueMapper implements RowMapper<VillageData> {

        public final String schemaForCount(){
            return " v.counter as counter, v.village_name as villageName from chai_villages v ";
        }
        
        @Override
        public VillageData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long counter = rs.getLong("counter");
            final String villageName = rs.getString("villageName");
            return VillageData.countValue(counter, villageName);
        }
        
    }
    
    @Override
	public VillageData retrieveVillageDetails(Long centerId) {
		try {
			final String sql = "select v.id as id,v.village_name as villageName from chai_villages v left join chai_village_center vc on vc.village_id = v.id"
					+ " where vc.center_id = ? ";
			final VillageLookupDataMapper vm = new VillageLookupDataMapper();

			return this.jdbcTemplate.queryForObject(sql, vm, new Object[] { centerId });
		} catch (final EmptyResultDataAccessException e) {
			return null;

		}
	}
	
  
}
