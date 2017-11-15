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
package org.apache.fineract.portfolio.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
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
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientNonPersonData;
import org.apache.fineract.portfolio.client.data.ClientTimelineData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.common.constant.CommonConstants;
import com.finflux.kyc.address.data.AddressData;
import com.finflux.kyc.address.service.AddressReadPlatformService;
import com.finflux.task.configuration.service.TaskConfigurationUtils;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskEntityType;

@Service
public class ClientReadPlatformServiceImpl implements ClientReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    private final ConfigurationDomainService configurationDomainService;

    // data mappers
    private final PaginationHelper<ClientData> paginationHelper = new PaginationHelper<>();
    private final ClientLookupMapper lookupMapper = new ClientLookupMapper();
    private final ClientMembersOfGroupMapper membersOfGroupMapper = new ClientMembersOfGroupMapper();
    private final ParentGroupsMapper clientGroupsMapper = new ParentGroupsMapper();
    private final AddressReadPlatformService addressReadPlatformService;
    private final TaskConfigurationUtils taskConfigurationUtils;

    @Autowired
    public ClientReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final OfficeReadPlatformService officeReadPlatformService, final StaffReadPlatformService staffReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final SavingsProductReadPlatformService savingsProductReadPlatformService,
            final ConfigurationDomainService configurationDomainService, final AddressReadPlatformService addressReadPlatformService,
            final TaskConfigurationUtils taskConfigurationUtils) {
        this.context = context;
        this.officeReadPlatformService = officeReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.staffReadPlatformService = staffReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.savingsProductReadPlatformService = savingsProductReadPlatformService;
        this.configurationDomainService = configurationDomainService;
        this.addressReadPlatformService = addressReadPlatformService;
        this.taskConfigurationUtils = taskConfigurationUtils;
    }

    @Override
    public ClientData retrieveTemplate(final Long officeId, final boolean staffInSelectedOfficeOnly) {
        this.context.authenticatedUser();

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);

        final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

        final Collection<SavingsProductData> savingsProductDatas = this.savingsProductReadPlatformService.retrieveAllForLookupByType(null);

        Collection<StaffData> staffOptions = null;

        final boolean loanOfficersOnly = false;
        if (staffInSelectedOfficeOnly) {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(defaultOfficeId);
        } else {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(defaultOfficeId,
                    loanOfficersOnly);
        }
        if (CollectionUtils.isEmpty(staffOptions)) {
            staffOptions = null;
        }
        final List<CodeValueData> genderOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.GENDER));

        final List<CodeValueData> clientTypeOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_TYPE));

        final List<CodeValueData> clientClassificationOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_CLASSIFICATION));
        
        final List<CodeValueData> clientNonPersonConstitutionOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_NON_PERSON_CONSTITUTION));
        
        final List<CodeValueData> clientNonPersonMainBusinessLineOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_NON_PERSON_MAIN_BUSINESS_LINE));
        
		final List<CodeValueData> closureReasons = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_CLOSURE_REASON));

        final List<EnumOptionData> clientLegalFormOptions = ClientEnumerations.legalForm(LegalForm.values());
        final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.CLIENTONBOARDING);

        return ClientData.template(defaultOfficeId, DateUtils.getLocalDateOfTenant(), offices, staffOptions, null, genderOptions, savingsProductDatas,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions,
                clientLegalFormOptions, closureReasons, isWorkflowEnabled);
    }

    @Override
    public Page<ClientData> retrieveAll(final SearchParameters searchParameters) {

        final String userOfficeHierarchy = this.context.officeHierarchy();
        final String underHierarchySearchString = userOfficeHierarchy + "%";
        final String appUserID = String.valueOf(context.authenticatedUser().getId());
        final Long groupId = searchParameters.getGroupId();
        // if (searchParameters.isScopedByOfficeHierarchy()) {
        // this.context.validateAccessRights(searchParameters.getHierarchy());
        // underHierarchySearchString = searchParameters.getHierarchy() + "%";
        // }
        final ClientMapper clientMapper = new ClientMapper(this.taskConfigurationUtils);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(clientMapper.schema());
        sqlBuilder.append(" where (o.hierarchy like ? or transferToOffice.hierarchy like ?) ");
        
        if(searchParameters.isSelfUser()){
        	sqlBuilder.append(" and c.id in (select umap.client_id from m_selfservice_user_client_mapping as umap where umap.appuser_id = ? ) ");
        }

        final String extraCriteria = buildSqlStringFromClientCriteria(searchParameters);

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

        final String sqlCountRows = "SELECT FOUND_ROWS()";
		List<Object> params = new ArrayList<>();
		params.add(TaskEntityType.CLIENT.getValue());
		params.add(underHierarchySearchString);
		params.add(underHierarchySearchString);
		if (groupId != null && configurationDomainService.allowClientsInMultipleGroups()) {
			params.add(groupId);
		}
		if (searchParameters.isSelfUser()) {
			params.add(appUserID);
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), params.toArray(), clientMapper);
    }

    private String buildSqlStringFromClientCriteria(final SearchParameters searchParameters) {
        final Long officeId = searchParameters.getOfficeId();
        final String externalId = searchParameters.getExternalId();
        final String displayName = searchParameters.getName();
        final String firstname = searchParameters.getFirstname();
        final String lastname = searchParameters.getLastname();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        final Map<String, String> searchConditions = searchParameters.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.CLIENT_DISPLAY_NAME:
                    sqlBuilder.append(" and ( c.display_name = '").append(value).append("' ) ");
                break;
                default:
                break;
            }
        });

        if (officeId != null) {
            sqlBuilder.append(" and c.office_id = ").append(officeId);
        }

        if (externalId != null) {
            sqlBuilder.append(" and c.external_id like ").append(ApiParameterHelper.sqlEncodeString(externalId));
        }

        if (displayName != null) {
            sqlBuilder.append(" and c.display_name like ").append(ApiParameterHelper.sqlEncodeString("%" + displayName + "%"));
        }

        if (firstname != null) {
            sqlBuilder.append(" and c.firstname like ").append(ApiParameterHelper.sqlEncodeString(firstname));
        }

        if (lastname != null) {
            sqlBuilder.append(" and c.lastname like ").append(ApiParameterHelper.sqlEncodeString(lastname));
        }

        if (searchParameters.isScopedByOfficeHierarchy()) {
            sqlBuilder.append(" and o.hierarchy like ").append(ApiParameterHelper.sqlEncodeString(searchParameters.getHierarchy() + "%"));
        }

        if (searchParameters.isOrphansOnly()) {
            if (this.configurationDomainService.allowClientsInMultipleGroups()) {
                sqlBuilder.append(" and c.id NOT IN (select client_id from m_group_client gc where gc.group_id =?)");
            } else {
                sqlBuilder.append(" and c.id NOT IN (select client_id from m_group_client) ");
            }
        }
        String extraCriteria = sqlBuilder.toString();
        if (StringUtils.isNotBlank(extraCriteria)) {
            extraCriteria = extraCriteria.substring(4);
        }
        return extraCriteria;
    }

    @Override
    public ClientData retrieveOne(final Long clientId) {
        try {
            final String hierarchy = this.context.officeHierarchy();
            final String hierarchySearchString = hierarchy + "%";

            final ClientMapper clientMapper = new ClientMapper(this.taskConfigurationUtils);
            final String sql = "select  " + clientMapper.schema()
                    + " where ( o.hierarchy like ? or transferToOffice.hierarchy like ?) and c.id = ?";
            final ClientData clientData = this.jdbcTemplate.queryForObject(sql, clientMapper,
                    new Object[] { TaskEntityType.CLIENT.getValue(), hierarchySearchString, hierarchySearchString, clientId });

            final String clientGroupsSql = "select " + this.clientGroupsMapper.parentGroupsSchema();

            final Collection<GroupGeneralData> parentGroups = this.jdbcTemplate.query(clientGroupsSql, this.clientGroupsMapper,
                    new Object[] { clientId });

            return ClientData.setParentGroups(clientData, parentGroups);

        } catch (final EmptyResultDataAccessException e) {
            throw new ClientNotFoundException(clientId);
        }
    }
    
    @Override
    public ClientData retrieveOneWithBasicDetails(final Long clientId) {
        try {
            final String hierarchy = this.context.officeHierarchy();
            final String hierarchySearchString = hierarchy + "%";
            ClientBasicDetailsMapper mapper = new ClientBasicDetailsMapper();
            final String sql = "select  " + mapper.schema() + " where ( o.hierarchy like ?) and c.id = ?";
            ClientData clientData = this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { hierarchySearchString, clientId });
            final String entityType = "clients";
            final boolean isTemplateRequired = false;
            final boolean fetchNonVerifiedData = true;
            final Collection<AddressData> addressData = addressReadPlatformService.retrieveAddressesByEntityTypeAndEntityId(entityType,
                    clientId, isTemplateRequired, fetchNonVerifiedData);
            clientData.updateAddressData(addressData);
            return clientData;
        } catch (final EmptyResultDataAccessException e) {
            throw new ClientNotFoundException(clientId);
        }
    }

    @Override
    public Collection<ClientData> retrieveAllForLookup(final String extraCriteria) {

        String sql = "select " + this.lookupMapper.schema();

        if (StringUtils.isNotBlank(extraCriteria)) {
            sql += " and (" + extraCriteria + ")";
        }

        return this.jdbcTemplate.query(sql, this.lookupMapper, new Object[] {});
    }

    @Override
    public Collection<ClientData> retrieveAllForLookupByOfficeId(final Long officeId) {

        final String sql = "select " + this.lookupMapper.schema() + " where c.office_id = ? and c.status_enum != ?";

        return this.jdbcTemplate.query(sql, this.lookupMapper, new Object[] { officeId, ClientStatus.CLOSED.getValue() });
    }

    @Override
    public Collection<ClientData> retrieveClientMembersOfGroup(final Long groupId) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final String sql = "select " + this.membersOfGroupMapper.schema() + " where o.hierarchy like ? and pgc.group_id = ?";

        return this.jdbcTemplate.query(sql, this.membersOfGroupMapper, new Object[] { hierarchySearchString, groupId });
    }

    @Override
    public Collection<ClientData> retrieveActiveClientMembersOfGroup(final Long groupId) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final String sql = "select " + this.membersOfGroupMapper.schema()
                + " where o.hierarchy like ? and pgc.group_id = ? and c.status_enum = ? ";

        return this.jdbcTemplate.query(sql, this.membersOfGroupMapper,
                new Object[] { hierarchySearchString, groupId, ClientStatus.ACTIVE.getValue() });
    }

    private static final class ClientMembersOfGroupMapper implements RowMapper<ClientData> {

        private final String schema;

        public ClientMembersOfGroupMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);

            sqlBuilder
                    .append("c.id as id, c.account_no as accountNo, c.external_id as externalId, c.status_enum as statusEnum,c.sub_status as subStatus, ");
            sqlBuilder
                    .append("cvSubStatus.code_value as subStatusValue,cvSubStatus.code_description as subStatusDesc,c.office_id as officeId, o.name as officeName, ");
            sqlBuilder.append("c.transfer_to_office_id as transferToOfficeId, transferToOffice.name as transferToOfficeName, ");
            sqlBuilder.append("c.firstname as firstname, c.middlename as middlename, c.lastname as lastname, ");
            sqlBuilder.append("c.fullname as fullname, c.display_name as displayName, ");
            sqlBuilder.append("c.mobile_no as mobileNo, ");
            sqlBuilder.append("c.alternate_mobile_no as alternateMobileNo, ");
            sqlBuilder.append("c.email_id as emailId, ");
            sqlBuilder.append("c.date_of_birth as dateOfBirth, ");
            sqlBuilder.append("c.gender_cv_id as genderId, ");
            sqlBuilder.append("cv.code_value as genderValue, ");
            sqlBuilder.append("c.client_type_cv_id as clienttypeId, ");
            sqlBuilder.append("cvclienttype.code_value as clienttypeValue, ");
            sqlBuilder.append("c.client_classification_cv_id as classificationId, ");
            sqlBuilder.append("cvclassification.code_value as classificationValue, ");
            sqlBuilder.append("c.legal_form_enum as legalFormEnum, ");
            sqlBuilder.append("c.activation_date as activationDate, c.image_id as imageId, ");
            sqlBuilder.append("c.staff_id as staffId, s.display_name as staffName,");
            sqlBuilder.append("c.default_savings_product as savingsProductId, sp.name as savingsProductName, ");
            sqlBuilder.append("c.default_savings_account as savingsAccountId, ");

            sqlBuilder.append("c.submittedon_date as submittedOnDate, ");
            sqlBuilder.append("sbu.username as submittedByUsername, ");
            sqlBuilder.append("sbu.firstname as submittedByFirstname, ");
            sqlBuilder.append("sbu.lastname as submittedByLastname, ");

            sqlBuilder.append("c.closedon_date as closedOnDate, ");
            sqlBuilder.append("clu.username as closedByUsername, ");
            sqlBuilder.append("clu.firstname as closedByFirstname, ");
            sqlBuilder.append("clu.lastname as closedByLastname, ");

            sqlBuilder.append("acu.username as activatedByUsername, ");
            sqlBuilder.append("acu.firstname as activatedByFirstname, ");
            sqlBuilder.append("acu.lastname as activatedByLastname, ");
            sqlBuilder.append("c.national_id as nationalId, ");
            
            sqlBuilder.append("cnp.constitution_cv_id as constitutionId, ");
            sqlBuilder.append("cvConstitution.code_value as constitutionValue, ");
            sqlBuilder.append("cnp.incorp_no as incorpNo, ");
            sqlBuilder.append("cnp.incorp_validity_till as incorpValidityTill, ");
            sqlBuilder.append("cnp.main_business_line_cv_id as mainBusinessLineId, ");
            sqlBuilder.append("cvMainBusinessLine.code_value as mainBusinessLineValue, ");
            sqlBuilder.append("cnp.remarks as remarks ");
            sqlBuilder.append(",c.is_locked as isLocked ");

            sqlBuilder.append("from m_client c ");
            sqlBuilder.append("join m_office o on o.id = c.office_id ");
            sqlBuilder.append("left join m_client_non_person cnp on cnp.client_id = c.id ");
            sqlBuilder.append("join m_group_client pgc on pgc.client_id = c.id ");
            sqlBuilder.append("left join m_staff s on s.id = c.staff_id ");
            sqlBuilder.append("left join m_savings_product sp on sp.id = c.default_savings_product ");
            sqlBuilder.append("left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");

            sqlBuilder.append("left join m_appuser sbu on sbu.id = c.submittedon_userid ");
            sqlBuilder.append("left join m_appuser acu on acu.id = c.activatedon_userid ");
            sqlBuilder.append("left join m_appuser clu on clu.id = c.closedon_userid ");
            sqlBuilder.append("left join m_code_value cv on cv.id = c.gender_cv_id ");
            sqlBuilder.append("left join m_code_value cvclienttype on cvclienttype.id = c.client_type_cv_id ");
            sqlBuilder.append("left join m_code_value cvclassification on cvclassification.id = c.client_classification_cv_id ");
            sqlBuilder.append("left join m_code_value cvSubStatus on cvSubStatus.id = c.sub_status ");
            sqlBuilder.append("left join m_code_value cvConstitution on cvConstitution.id = cnp.constitution_cv_id ");
            sqlBuilder.append("left join m_code_value cvMainBusinessLine on cvMainBusinessLine.id = cnp.main_business_line_cv_id ");

            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String accountNo = rs.getString("accountNo");
            final String nationalId = rs.getString("nationalId");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = ClientEnumerations.status(statusEnum);

            final Long subStatusId = JdbcSupport.getLong(rs, "subStatus");
            final String subStatusValue = rs.getString("subStatusValue");
            final String subStatusDesc = rs.getString("subStatusDesc");
            final boolean isActive = false;
            final CodeValueData subStatus = CodeValueData.instance(subStatusId, subStatusValue, subStatusDesc, isActive);

            final Long officeId = JdbcSupport.getLong(rs, "officeId");
            final String officeName = rs.getString("officeName");

            final Long transferToOfficeId = JdbcSupport.getLong(rs, "transferToOfficeId");
            final String transferToOfficeName = rs.getString("transferToOfficeName");

            final Long id = JdbcSupport.getLong(rs, "id");
            final String firstname = rs.getString("firstname");
            final String middlename = rs.getString("middlename");
            final String lastname = rs.getString("lastname");
            final String fullname = rs.getString("fullname");
            final String displayName = rs.getString("displayName");
            final String externalId = rs.getString("externalId");
            final String mobileNo = rs.getString("mobileNo");
            final String alternateMobileNo = rs.getString("alternateMobileNo");
            final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");
            final Long genderId = JdbcSupport.getLong(rs, "genderId");
            final String genderValue = rs.getString("genderValue");
            final CodeValueData gender = CodeValueData.instance(genderId, genderValue);

            final Long clienttypeId = JdbcSupport.getLong(rs, "clienttypeId");
            final String clienttypeValue = rs.getString("clienttypeValue");
            final CodeValueData clienttype = CodeValueData.instance(clienttypeId, clienttypeValue);

            final Long classificationId = JdbcSupport.getLong(rs, "classificationId");
            final String classificationValue = rs.getString("classificationValue");
            final CodeValueData classification = CodeValueData.instance(classificationId, classificationValue);

            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
            final Long imageId = JdbcSupport.getLong(rs, "imageId");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final String staffName = rs.getString("staffName");

            final Long savingsProductId = JdbcSupport.getLong(rs, "savingsProductId");
            final String savingsProductName = rs.getString("savingsProductName");

            final Long savingsAccountId = JdbcSupport.getLong(rs, "savingsAccountId");
			CodeValueData closurereason = null;

            
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
            
            final Integer legalFormEnum = JdbcSupport.getInteger(rs, "legalFormEnum");
            EnumOptionData legalForm = null;
            if(legalFormEnum != null)
            		legalForm = ClientEnumerations.legalForm(legalFormEnum);
            
            final Long constitutionId = JdbcSupport.getLong(rs, "constitutionId");
            final String constitutionValue = rs.getString("constitutionValue");
            final CodeValueData constitution = CodeValueData.instance(constitutionId, constitutionValue);
            final String incorpNo = rs.getString("incorpNo");
            final LocalDate incorpValidityTill = JdbcSupport.getLocalDate(rs, "incorpValidityTill");
            final Long mainBusinessLineId = JdbcSupport.getLong(rs, "mainBusinessLineId");            
            final String mainBusinessLineValue = rs.getString("mainBusinessLineValue");
            final CodeValueData mainBusinessLine = CodeValueData.instance(mainBusinessLineId, mainBusinessLineValue);
            final String remarks = rs.getString("remarks");
            final String emailId = rs.getString("emailId");
            final boolean isLocked = rs.getBoolean("isLocked");
            final Boolean isWorkflowEnabled = null;
            final Long workflowId = null;
            final ClientNonPersonData clientNonPerson = new ClientNonPersonData(constitution, incorpNo, incorpValidityTill, mainBusinessLine, remarks);

            final ClientTimelineData timeline = new ClientTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
                    submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
                    closedByUsername, closedByFirstname, closedByLastname);

            return ClientData.instance(accountNo, nationalId, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName,
                    id, firstname, middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth,
                    gender, activationDate, imageId, staffId, staffName, timeline, savingsProductId, savingsProductName,
                    savingsAccountId, clienttype, classification, legalForm, clientNonPerson, closurereason, emailId, isLocked, isWorkflowEnabled, workflowId);

        }
    }

    @Override
    public Collection<ClientData> retrieveActiveClientMembersOfCenter(final Long centerId) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final String sql = "select "
                + this.membersOfGroupMapper.schema()
                + " left join m_group g on pgc.group_id=g.id where o.hierarchy like ? and g.parent_id = ? and c.status_enum = ? group by c.id";

        return this.jdbcTemplate.query(sql, this.membersOfGroupMapper,
                new Object[] { hierarchySearchString, centerId, ClientStatus.ACTIVE.getValue() });
    }

    private static final class ClientMapper implements RowMapper<ClientData> {

        private final String schema;
        private final Boolean isWorkflowEnabled;

        public ClientMapper(final TaskConfigurationUtils taskConfigurationUtils) {
            this.isWorkflowEnabled = taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.CLIENTONBOARDING);
            final StringBuilder builder = new StringBuilder(400);

            builder.append("c.id as id, c.account_no as accountNo, c.external_id as externalId, c.status_enum as statusEnum,c.sub_status as subStatus, ");
            builder.append("cvSubStatus.code_value as subStatusValue,cvSubStatus.code_description as subStatusDesc,c.office_id as officeId, o.name as officeName, ");
            builder.append("c.transfer_to_office_id as transferToOfficeId, transferToOffice.name as transferToOfficeName, ");
            builder.append("c.firstname as firstname, c.middlename as middlename, c.lastname as lastname, ");
            builder.append("c.fullname as fullname, c.display_name as displayName, ");
            builder.append("c.mobile_no as mobileNo, ");
            builder.append("c.alternate_mobile_no as alternateMobileNo, ");
            builder.append("c.date_of_birth as dateOfBirth, ");
            builder.append("c.gender_cv_id as genderId, ");
            builder.append("cv.code_value as genderValue, ");
            builder.append("c.client_type_cv_id as clienttypeId, ");
            builder.append("cvclienttype.code_value as clienttypeValue, ");
            builder.append("c.client_classification_cv_id as classificationId, ");
            builder.append("cvclassification.code_value as classificationValue, ");
            builder.append("c.legal_form_enum as legalFormEnum, ");
			builder.append("c.closure_reason_cv_id as closurereasonId, ");
		    builder.append("cvclosurereason.code_value as closurereasonValue, ");

	    builder.append("c.email_id as emailId, ");
            builder.append("c.submittedon_date as submittedOnDate, ");
            builder.append("sbu.username as submittedByUsername, ");
            builder.append("sbu.firstname as submittedByFirstname, ");
            builder.append("sbu.lastname as submittedByLastname, ");

            builder.append("c.closedon_date as closedOnDate, ");
            builder.append("clu.username as closedByUsername, ");
            builder.append("clu.firstname as closedByFirstname, ");
            builder.append("clu.lastname as closedByLastname, ");

            // builder.append("c.submittedon as submittedOnDate, ");
            builder.append("acu.username as activatedByUsername, ");
            builder.append("acu.firstname as activatedByFirstname, ");
            builder.append("acu.lastname as activatedByLastname, ");

            builder.append("c.national_id as nationalId, ");
            builder.append("cnp.constitution_cv_id as constitutionId, ");
            builder.append("cvConstitution.code_value as constitutionValue, ");
            builder.append("cnp.incorp_no as incorpNo, ");
            builder.append("cnp.incorp_validity_till as incorpValidityTill, ");
            builder.append("cnp.main_business_line_cv_id as mainBusinessLineId, ");
            builder.append("cvMainBusinessLine.code_value as mainBusinessLineValue, ");
            builder.append("cnp.remarks as remarks, ");

            builder.append("c.activation_date as activationDate, c.image_id as imageId, ");
            builder.append("c.staff_id as staffId, s.display_name as staffName, ");
            builder.append("task.id as workflowId, ") ;
            builder.append("c.default_savings_product as savingsProductId, sp.name as savingsProductName, ");
            builder.append("c.default_savings_account as savingsAccountId ");
            builder.append(",c.is_locked as isLocked ");
            builder.append("from m_client c ");
            builder.append("join m_office o on o.id = c.office_id ");
            builder.append("left join m_client_non_person cnp on cnp.client_id = c.id ");
            builder.append("left join m_staff s on s.id = c.staff_id ");
            builder.append("left join m_savings_product sp on sp.id = c.default_savings_product ");
            builder.append("left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");
            builder.append("left join m_appuser sbu on sbu.id = c.submittedon_userid ");
            builder.append("left join m_appuser acu on acu.id = c.activatedon_userid ");
            builder.append("left join m_appuser clu on clu.id = c.closedon_userid ");
            builder.append("left join m_code_value cv on cv.id = c.gender_cv_id ");
            builder.append("left join m_code_value cvclienttype on cvclienttype.id = c.client_type_cv_id ");
			builder.append("left join m_code_value cvclosurereason on cvclosurereason.id = c.closure_reason_cv_id ");
            builder.append("left join m_code_value cvclassification on cvclassification.id = c.client_classification_cv_id ");
            builder.append("left join m_code_value cvSubStatus on cvSubStatus.id = c.sub_status ");
            builder.append("left join m_code_value cvConstitution on cvConstitution.id = cnp.constitution_cv_id ");
            builder.append("left join m_code_value cvMainBusinessLine on cvMainBusinessLine.id = cnp.main_business_line_cv_id ");
            builder.append("LEFT JOIN f_task task ON task.entity_type=? and task.parent_id is null and task.entity_id = c.id  ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String accountNo = rs.getString("accountNo");
            final String nationalId = rs.getString("nationalId");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = ClientEnumerations.status(statusEnum);

            final Long subStatusId = JdbcSupport.getLong(rs, "subStatus");
            final String subStatusValue = rs.getString("subStatusValue");
            final String subStatusDesc = rs.getString("subStatusDesc");
            final boolean isActive = false;
            final CodeValueData subStatus = CodeValueData.instance(subStatusId, subStatusValue, subStatusDesc, isActive);

            final Long officeId = JdbcSupport.getLong(rs, "officeId");
            final String officeName = rs.getString("officeName");

            final Long transferToOfficeId = JdbcSupport.getLong(rs, "transferToOfficeId");
            final String transferToOfficeName = rs.getString("transferToOfficeName");

            final Long id = JdbcSupport.getLong(rs, "id");
            final String firstname = rs.getString("firstname");
            final String middlename = rs.getString("middlename");
            final String lastname = rs.getString("lastname");
            final String fullname = rs.getString("fullname");
            final String displayName = rs.getString("displayName");
            final String externalId = rs.getString("externalId");
            final String mobileNo = rs.getString("mobileNo");
            final String alternateMobileNo = rs.getString("alternateMobileNo");
            final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");
            final Long genderId = JdbcSupport.getLong(rs, "genderId");
            final String genderValue = rs.getString("genderValue");
            final CodeValueData gender = CodeValueData.instance(genderId, genderValue);

            final Long clienttypeId = JdbcSupport.getLong(rs, "clienttypeId");
            final String clienttypeValue = rs.getString("clienttypeValue");
            final CodeValueData clienttype = CodeValueData.instance(clienttypeId, clienttypeValue);
            
			CodeValueData closurereason = null;
			final Long closurereasonId = JdbcSupport.getLong(rs, "closurereasonId");
			if (closurereasonId != null) {
				final String closurereasonValue = rs.getString("closurereasonValue");
				closurereason = CodeValueData.instance(closurereasonId, closurereasonValue);
			}

            final Long classificationId = JdbcSupport.getLong(rs, "classificationId");
            final String classificationValue = rs.getString("classificationValue");
            final CodeValueData classification = CodeValueData.instance(classificationId, classificationValue);

            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
            final Long imageId = JdbcSupport.getLong(rs, "imageId");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final String staffName = rs.getString("staffName");

            final Long savingsProductId = JdbcSupport.getLong(rs, "savingsProductId");
            final String savingsProductName = rs.getString("savingsProductName");
            final Long savingsAccountId = JdbcSupport.getLong(rs, "savingsAccountId");

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
            
            final Integer legalFormEnum = JdbcSupport.getInteger(rs, "legalFormEnum");
            EnumOptionData legalForm = null;
            if(legalFormEnum != null)
            		legalForm = ClientEnumerations.legalForm(legalFormEnum);
            
            final Long constitutionId = JdbcSupport.getLong(rs, "constitutionId");
            final String constitutionValue = rs.getString("constitutionValue");
            final CodeValueData constitution = CodeValueData.instance(constitutionId, constitutionValue);
            final String incorpNo = rs.getString("incorpNo");
            final LocalDate incorpValidityTill = JdbcSupport.getLocalDate(rs, "incorpValidityTill");
            final Long mainBusinessLineId = JdbcSupport.getLong(rs, "mainBusinessLineId");            
            final String mainBusinessLineValue = rs.getString("mainBusinessLineValue");
            final CodeValueData mainBusinessLine = CodeValueData.instance(mainBusinessLineId, mainBusinessLineValue);
            final String remarks = rs.getString("remarks");
            final String emailId =rs.getString("emailId");
            final boolean isLocked = rs.getBoolean("isLocked");
            final Long workflowId = JdbcSupport.getLong(rs, "workflowId");
            final ClientNonPersonData clientNonPerson = new ClientNonPersonData(constitution, incorpNo, incorpValidityTill, mainBusinessLine, remarks);

            final ClientTimelineData timeline = new ClientTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
                    submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
                    closedByUsername, closedByFirstname, closedByLastname);

            return ClientData.instance(accountNo, nationalId, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName,
                    id, firstname, middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth,
                    gender, activationDate, imageId, staffId, staffName, timeline, savingsProductId, savingsProductName,
                    savingsAccountId, clienttype, classification, legalForm, clientNonPerson, closurereason, emailId, isLocked,
                    this.isWorkflowEnabled, workflowId);
        }
    }
    
    
    private static final class ClientBasicDetailsMapper implements RowMapper<ClientData> {

        private final String schema;

        public ClientBasicDetailsMapper() {
            final StringBuilder builder = new StringBuilder(400);
            builder.append("c.id as id, ");
            builder.append("c.fullname as fullname, c.display_name as displayName, ");
            builder.append("c.mobile_no as mobileNo, ");
            builder.append("c.date_of_birth as dateOfBirth, ");
            builder.append("c.gender_cv_id as genderId, ");
            builder.append("cv.code_value as genderValue ");
            builder.append("from m_client c ");
            builder.append("join m_office o on o.id = c.office_id ");
            builder.append("left join m_code_value cv on cv.id = c.gender_cv_id ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String displayName = rs.getString("displayName");
            final String mobileNo = rs.getString("mobileNo");
            final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");
            final Long genderId = JdbcSupport.getLong(rs, "genderId");
            final String genderValue = rs.getString("genderValue");
            final CodeValueData gender = CodeValueData.instance(genderId, genderValue);
            return ClientData.formClientData(id, displayName, dateOfBirth, gender, mobileNo);

        }
    }

    private static final class ParentGroupsMapper implements RowMapper<GroupGeneralData> {

        public String parentGroupsSchema() {
            return "gp.id As groupId , gp.account_no as accountNo, gp.display_name As groupName, gp.level_id as groupLevel from m_client cl JOIN m_group_client gc ON cl.id = gc.client_id "
                    + "JOIN m_group gp ON gp.id = gc.group_id WHERE cl.id  = ?";
        }

        @Override
        public GroupGeneralData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long groupId = JdbcSupport.getLong(rs, "groupId");
            final String groupName = rs.getString("groupName");
            final String accountNo = rs.getString("accountNo");
            final String groupLevel=rs.getString("groupLevel");

            return GroupGeneralData.lookup(groupId, accountNo, groupName,groupLevel);
        }
    }

    private static final class ClientLookupMapper implements RowMapper<ClientData> {

        private final String schema;

        public ClientLookupMapper() {
            final StringBuilder builder = new StringBuilder(200);

            builder.append("c.id as id, c.display_name as displayName, ");
            builder.append("c.office_id as officeId, o.name as officeName, ");
            builder.append("c.external_id as externalId ");
            builder.append("from m_client c ");
            builder.append("join m_office o on o.id = c.office_id ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String displayName = rs.getString("displayName");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String externalId = rs.getString("externalId");

            return ClientData.lookup(id, displayName, officeId, officeName,externalId);
        }
    }
    
    private static final class ClientTaskLookupMapper implements RowMapper<ClientData> {
        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String displayName = rs.getString("displayName");
            final String accountNo = rs.getString("accountNo");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final Long staffId = rs.getLong("staffId");
            final String staffName = rs.getString("staffName");

            return ClientData.lookup(id, displayName, accountNo,officeId, officeName,staffId,staffName);
        }
    }
    @Override
    public Collection<ClientData> retrieveAllForTaskLookupBySearchParameters(final SearchParameters searchParameters) {
        final AppUser currentUser = this.context.authenticatedUser();
        String hierarchy = currentUser.getOffice().getHierarchy() + "%";
        final StringBuilder builderSelectQuery = new StringBuilder(400);
        /**
         * This is used for to build the multiple where conditions.
         */
        final StringBuilder builderWhereConditions = new StringBuilder(100);

        final ClientTaskLookupMapper mapper = new ClientTaskLookupMapper();
        final List<Object> params = new ArrayList<>();
        builderSelectQuery.append("select ");
        builderSelectQuery
                .append("c.id as id, c.account_no as accountNo,c.status_enum as statusEnum,c.office_id as officeId, o.name as officeName,");
        builderSelectQuery.append("c.fullname as fullname, c.display_name as displayName, ");
        builderSelectQuery.append("c.staff_id as staffId, s.display_name as staffName ");
        builderSelectQuery.append("from m_client c ");
        builderSelectQuery.append("join m_office o on o.id = c.office_id and o.hierarchy like ?");
        builderSelectQuery.append("left join m_staff s on s.id = c.staff_id ");
        if ((searchParameters.getGroupId() != null) || (searchParameters.getCenterId() != null)) {
            builderSelectQuery.append("JOIN m_group_client gc ON c.id = gc.client_id ");
            builderSelectQuery.append("join m_group g on g.id = gc.group_id ");
        }
        params.add(hierarchy);
        if (searchParameters.getOfficeId() != null) {
            builderWhereConditions.append(" and o.id = ?");
            params.add(searchParameters.getOfficeId());
        }
        if (searchParameters.getStaffId() != null) {
            builderWhereConditions.append(" and s.id = ?");
            params.add(searchParameters.getStaffId());
        }
        if (searchParameters.getGroupId() != null) {
            builderWhereConditions.append(" and g.id = ?");
            params.add(searchParameters.getGroupId());
        }
        if (searchParameters.getCenterId() != null) {
            builderWhereConditions.append(" and g.parent_id = ?");
            params.add(searchParameters.getCenterId());
        }
        final Map<String, String> searchConditions = searchParameters.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.CLIENT_STATUS:
                    builderWhereConditions.append(" and ( c.status_enum = ").append(value).append(" ) ");
                break;
                default:
                break;
            }
        });
        builderSelectQuery.append(builderWhereConditions.toString().replaceFirst("(?i)and", " where "));
        return this.jdbcTemplate.query(builderSelectQuery.toString(), mapper, params.toArray());
    }


    @Override
    public ClientData retrieveClientByIdentifier(final Long identifierTypeId, final String identifierKey) {
        try {
            final ClientIdentifierMapper mapper = new ClientIdentifierMapper();

            final String sql = "select " + mapper.clientLookupByIdentifierSchema();

            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { identifierTypeId, identifierKey });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Long retrieveSavingsAccountIdByMobileNo(final String mobileno) {
        try {

        	final String sql = "select default_savings_account from m_client c WHERE c.mobile_no = ? " ; 
        	return this.jdbcTemplate.queryForObject(sql,Long.class,mobileno);

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class ClientIdentifierMapper implements RowMapper<ClientData> {

        public String clientLookupByIdentifierSchema() {
            return "c.id as id, c.account_no as accountNo, c.national_id as nationalId, c.firstname as firstname, c.middlename as middlename, c.lastname as lastname, "
                    + "c.fullname as fullname, c.display_name as displayName," + "c.office_id as officeId, o.name as officeName "
                    + " from m_client c, m_office o, m_client_identifier ci " + "where o.id = c.office_id and c.id=ci.client_id "
                    + "and ci.document_type_id= ? and ci.document_key like ?";
        }

        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String nationalId = rs.getString("nationalId");

            final String firstname = rs.getString("firstname");
            final String middlename = rs.getString("middlename");
            final String lastname = rs.getString("lastname");
            final String fullname = rs.getString("fullname");
            final String displayName = rs.getString("displayName");

            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");

            return ClientData.clientIdentifier(id, accountNo, nationalId, firstname, middlename, lastname, fullname, displayName, officeId, officeName);
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
    public ClientData retrieveAllNarrations(final String clientNarrations) {
        final List<CodeValueData> narrations = new ArrayList<>(this.codeValueReadPlatformService.retrieveCodeValuesByCode(clientNarrations));
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final List<CodeValueData> closureReasons = null;
        final Boolean isWorkflowEnabled = null;
        return ClientData.template(null, null, null, null, narrations, null, null, clientTypeOptions, clientClassificationOptions,
                clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, clientLegalFormOptions, closureReasons,
                isWorkflowEnabled);
    }

    @Override
    public Long fetchDefaultLoanOfficerFromGroup(final Long clientId) {
        final StringBuilder builder = new StringBuilder(400);
    	builder.append("SELECT IFNULL(pgs.id,gs.id) ");
    	builder.append("FROM m_group_client gc "); 
    	builder.append("LEFT JOIN m_group g ON g.id=gc.group_id ");
    	builder.append("LEFT JOIN m_group pg ON pg.id = g.parent_id ");
    	builder.append("LEFT JOIN m_staff gs ON (g.staff_id = gs.id and gs.is_loan_officer = 1) ");
    	builder.append("LEFT JOIN m_staff pgs ON (pg.staff_id = pgs.id and pgs.is_loan_officer = 1) ");
    	builder.append("where gc.client_id=? ");
    	builder.append("group by gc.client_id ");
    	try{
            return this.jdbcTemplate.queryForObject(builder.toString(), Long.class, clientId);
    }catch (final EmptyResultDataAccessException e) {
        return null;
    }
  }
    
    @Override
    public ClientData retrieveHierarchyLookupForClient(final ClientData clientData) {
        try {
            final HierarchyLookupMapper hm = new HierarchyLookupMapper(clientData);
            final String sql = "SELECT " + hm.schema() + " where c.id = ? ";
            return this.jdbcTemplate.query(sql, hm, new Object[] { clientData.id() });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }
    
  private static final class HierarchyLookupMapper implements ResultSetExtractor<ClientData> {
        
        private final ClientData clientData;
        public HierarchyLookupMapper(final ClientData clientData) {
            this.clientData = clientData;
        }

        public final String schema() {
            return " o.id as officeId,o.name as officeName,go.id as groupOfficeId,go.name as groupOfficeName,v.id AS villageId,v.village_name AS villageName, g.parent_id AS centerId,pg.display_name AS centerName,g.id AS goupId,g.display_name as groupName "
                    + "FROM m_client c "
                    + "JOIN m_group_client gc ON gc.client_id = c.id "
                    + "JOIN m_group g ON gc.group_id = g.id "
                    + "LEFT JOIN m_group pg ON g.parent_id = pg.id "
                    + "LEFT JOIN chai_village_center vc ON (vc.center_id = g.id OR vc.center_id = g.parent_id) "
                    + "left JOIN chai_villages v ON vc.village_id = v.id "
                    + "left join m_office go on g.office_id = go.id "
                    + "join m_office o on o.id = c.office_id ";
                    
        }
       
        @Override
        public ClientData extractData(ResultSet rs) throws SQLException, DataAccessException {
            Collection<GroupGeneralData> parentGroups = new ArrayList<>();
            Long officeId = null;
            String officeName =  null;
            while (rs.next()) {
                final Long villageId = JdbcSupport.getLong(rs, "villageId");
                final String villageName = rs.getString("villageName");
                final Long centerId = JdbcSupport.getLong(rs, "centerId");
                final String centerName = rs.getString("centerName");
                final Long goupId = JdbcSupport.getLong(rs, "goupId");
                final String groupName = rs.getString("groupName");
                final Long groupOfficeId = JdbcSupport.getLong(rs, "groupOfficeId");
                final String groupOfficeName = rs.getString("groupOfficeName");

                
                officeId = JdbcSupport.getLong(rs, "officeId");
                officeName = rs.getString("officeName");


                VillageData villageData = VillageData.lookup(villageId, villageName);
                GroupGeneralData groupGeneralData = GroupGeneralData.lookupforhierarchy(goupId, groupName, centerId, centerName,
                        villageData, groupOfficeId, groupOfficeName);
                parentGroups.add(groupGeneralData);
            }
            
            return ClientData.lookupforhierarchy(clientData, parentGroups,officeId,officeName);
        }
    }

    @Override
    public Long retrieveOfficeId(Long clientId) {
       String sql = "select office_id as officeId from m_client where id = ?";
       return this.jdbcTemplate.queryForObject(sql, Long.class, clientId);
    }
}