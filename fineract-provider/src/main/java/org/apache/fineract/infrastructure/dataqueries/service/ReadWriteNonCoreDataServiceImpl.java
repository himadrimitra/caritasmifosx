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
package org.apache.fineract.infrastructure.dataqueries.service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.exception.CodeValueNotFoundException;
import org.apache.fineract.infrastructure.codes.service.CodeReadPlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.exception.PlatformServiceUnavailableException;
import org.apache.fineract.infrastructure.core.serialization.DatatableCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.JsonParserHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.dataqueries.api.DataTableApiConstant;
import org.apache.fineract.infrastructure.dataqueries.data.AllowedValueOptions;
import org.apache.fineract.infrastructure.dataqueries.data.DataTableValidator;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultSetColumnAndData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetRowData;
import org.apache.fineract.infrastructure.dataqueries.data.ScopeCriteriaData;
import org.apache.fineract.infrastructure.dataqueries.data.ScopeOptionsData;
import org.apache.fineract.infrastructure.dataqueries.data.SectionData;
import org.apache.fineract.infrastructure.dataqueries.domain.DataTableScopes;
import org.apache.fineract.infrastructure.dataqueries.exception.DatatableNotFoundException;
import org.apache.fineract.infrastructure.dataqueries.exception.DatatableSystemErrorException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Service
public class ReadWriteNonCoreDataServiceImpl implements ReadWriteNonCoreDataService {

    private final static String DATATABLE_NAME_REGEX_PATTERN = "^[a-zA-Z][a-zA-Z0-9\\-_\\s]{0,48}[a-zA-Z0-9]$";

    private final static String CODE_VALUES_TABLE = "m_code_value";

    private final static Logger logger = LoggerFactory.getLogger(ReadWriteNonCoreDataServiceImpl.class);
    private final static HashMap<String, String> apiTypeToMySQL = new HashMap<String, String>() {

        {
            put("string", "VARCHAR");
            put("number", "INT");
            put("boolean", "BIT");
            put("decimal", "DECIMAL");
            put("date", "DATE");
            put("datetime", "DATETIME");
            put("text", "TEXT");
            put("dropdown", "INT");
        }
    };

    private final static List<String> stringDataTypes = Arrays.asList("char", "varchar", "blob", "text", "tinyblob", "tinytext",
            "mediumblob", "mediumtext", "longblob", "longtext");

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final PlatformSecurityContext context;
    private final FromJsonHelper fromJsonHelper;
    private final JsonParserHelper helper;
    private final GenericDataService genericDataService;
    private final DatatableCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final ConfigurationDomainService configurationDomainService;
    private final CodeReadPlatformService codeReadPlatformService;
    private final DataTableValidator dataTableValidator;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    // private final GlobalConfigurationWritePlatformServiceJpaRepositoryImpl
    // configurationWriteService;

    @Autowired(required = true)
    public ReadWriteNonCoreDataServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context,
            final FromJsonHelper fromJsonHelper, final GenericDataService genericDataService,
            final DatatableCommandFromApiJsonDeserializer fromApiJsonDeserializer, final CodeReadPlatformService codeReadPlatformService,
            final ConfigurationDomainService configurationDomainService, final DataTableValidator dataTableValidator,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final SavingsProductReadPlatformService savingsProductReadPlatformService) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.context = context;
        this.fromJsonHelper = fromJsonHelper;
        this.helper = new JsonParserHelper();
        this.genericDataService = genericDataService;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.codeReadPlatformService = codeReadPlatformService;
        this.configurationDomainService = configurationDomainService;
        this.dataTableValidator = dataTableValidator;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        // this.configurationWriteService = configurationWriteService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.savingsProductReadPlatformService = savingsProductReadPlatformService;
    }

    @Override
    public List<DatatableData> retrieveDatatableNames(final String appTable, final Long associatedEntityId,
            final boolean isFetchBasicData) {

        String andClause;
        if (appTable == null) {
            andClause = "";
        } else {
            andClause = " and application_table_name = '" + appTable + "'";
        }

        // PERMITTED datatables
        final String sql = "select id, application_table_name, registered_table_name, scoping_criteria_enum,registered_table_display_name"
                + " from x_registered_table " + " where exists" + " (select 'f'" + " from m_appuser_role ur "
                + " join m_role r on r.id = ur.role_id" + " left join m_role_permission rp on rp.role_id = r.id"
                + " left join m_permission p on p.id = rp.permission_id" + " where ur.appuser_id = "
                + this.context.authenticatedUser().getId()
                + " and (p.code in ('ALL_FUNCTIONS', 'ALL_FUNCTIONS_READ') or p.code = concat('READ_', registered_table_name))) "
                + andClause + " order by application_table_name, registered_table_name";

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        final List<DatatableData> datatables = new ArrayList<>();
        while (rs.next()) {
            final Integer id = rs.getInt("id");
            boolean isEntityIdAssociatedWithDatatable = false;
            final String appTableName = rs.getString("application_table_name");
            final String registeredDatatableName = rs.getString("registered_table_name");
            final Long scopingCriteriaEnum = rs.getLong("scoping_criteria_enum");
            final String registeredDataTableDisplayName = rs.getString("registered_table_display_name");
            List<SectionData> sectionedColumnList = null;
            if (!isFetchBasicData) {
                if (associatedEntityId != null && scopingCriteriaEnum != null && scopingCriteriaEnum > 0) {
                    isEntityIdAssociatedWithDatatable = isEntityIdAssociatedWithDatatable(id, associatedEntityId, scopingCriteriaEnum);
                    if (isEntityIdAssociatedWithDatatable) {
                        final List<ResultsetColumnHeaderData> columnHeaderData = this.genericDataService
                                .fillResultsetColumnHeaders(registeredDatatableName);
                        sectionedColumnList = this.genericDataService.fetchSections(id);
                        if (sectionedColumnList != null && sectionedColumnList.size() > 0) {

                            sectionedColumnList = SectionData.organizeList(columnHeaderData, sectionedColumnList);
                        }

                        datatables.add(DatatableData.instance(id, appTableName, registeredDatatableName, columnHeaderData,
                                scopingCriteriaEnum, null, registeredDataTableDisplayName, sectionedColumnList));
                    }
                } else {
                    final List<ResultsetColumnHeaderData> columnHeaderData = this.genericDataService
                            .fillResultsetColumnHeaders(registeredDatatableName);

                    sectionedColumnList = this.genericDataService.fetchSections(id);
                    if (sectionedColumnList != null && sectionedColumnList.size() > 0) {

                        sectionedColumnList = SectionData.organizeList(columnHeaderData, sectionedColumnList);
                    }

                    datatables.add(DatatableData.instance(id, appTableName, registeredDatatableName, columnHeaderData, scopingCriteriaEnum,
                            null, registeredDataTableDisplayName, sectionedColumnList));
                }
            } else {
                datatables.add(DatatableData.instance(appTableName, registeredDatatableName, registeredDataTableDisplayName));
            }
        }

        return datatables;
    }

    private boolean isEntityIdAssociatedWithDatatable(final Integer id, final Long associatedEntityId, final Long scopingCriteriaEnum) {
        boolean isScopeIdAssociateWithDatatable = false;
        StringBuilder sql = new StringBuilder(
                "select id from f_registered_table_scoping " + " where registered_table_id = " + id + " and ");
        if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isLoanProduct()) {
            sql = sql.append(" loan_product_id = " + associatedEntityId + "");
        } else if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isSavingsProduct()) {
            sql = sql.append(" savings_product_id = " + associatedEntityId + "");
        } else if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isClientType()
                || DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isClientClassification()) {
            sql = sql.append(" code_value_id = " + associatedEntityId + "");
        } else if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isClientLegalForm()) {
            sql = sql.append(" legal_form_enum = " + associatedEntityId + "");
        }

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql.toString());
        if (rs.next()) {
            isScopeIdAssociateWithDatatable = true;
        }

        return isScopeIdAssociateWithDatatable;
    }

    @Override
    public DatatableData retrieveDatatable(final String datatable) {

        // PERMITTED datatables
        final String sql = "select id, application_table_name, registered_table_name, scoping_criteria_enum,registered_table_display_name"
                + " from x_registered_table " + " where exists" + " (select 'f'" + " from m_appuser_role ur "
                + " join m_role r on r.id = ur.role_id" + " left join m_role_permission rp on rp.role_id = r.id"
                + " left join m_permission p on p.id = rp.permission_id" + " where ur.appuser_id = "
                + this.context.authenticatedUser().getId() + " and registered_table_name='" + datatable + "'"
                + " and (p.code in ('ALL_FUNCTIONS', 'ALL_FUNCTIONS_READ') or p.code = concat('READ_', registered_table_name))) "
                + " order by application_table_name, registered_table_name";

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        DatatableData datatableData = null;
        while (rs.next()) {
            final Integer id = rs.getInt("id");
            final String appTableName = rs.getString("application_table_name");
            final String registeredDatatableName = rs.getString("registered_table_name");
            Long scopingCriteriaEnum = rs.getLong("scoping_criteria_enum");
            if (rs.wasNull()) {
                scopingCriteriaEnum = null;
            }
            final List<ScopeCriteriaData> scopeCriteriaData = this.genericDataService.fetchDatatableScopesByIdAndScopingCriteria(id,
                    scopingCriteriaEnum);
            final String registeredDataTableDisplayName = rs.getString("registered_table_display_name");
            final List<ResultsetColumnHeaderData> columnHeaderData = this.genericDataService
                    .fillResultsetColumnHeaders(registeredDatatableName);
            List<SectionData> sectionDataList = this.genericDataService.fetchSections(id);
            if (sectionDataList != null && sectionDataList.size() > 0) {

                sectionDataList = SectionData.organizeList(columnHeaderData, sectionDataList);
            }

            datatableData = DatatableData.instance(id, appTableName, registeredDatatableName, columnHeaderData, scopingCriteriaEnum,
                    scopeCriteriaData, registeredDataTableDisplayName, sectionDataList);
        }

        return datatableData;
    }

    private void logAsErrorUnexpectedDataIntegrityException(final Exception dve) {
        logger.error(dve.getMessage(), dve);
    }

    @Transactional
    @Override
    public void registerDatatable(final String dataTableName, final String applicationTableName, final Long scopingCriteriaEnum,
            final String dataTableDisplayName) {

        final Integer category = DataTableApiConstant.CATEGORY_DEFAULT;

        final String permissionSql = _getPermissionSql(dataTableName);
        _registerDataTable(applicationTableName, dataTableName, category, permissionSql, scopingCriteriaEnum, dataTableDisplayName);

    }

    @Transactional
    @Override
    public void registerDatatable(final JsonCommand command) {

        final String applicationTableName = getTableName(command.getUrl());
        final String dataTableName = getDataTableName(command.getUrl());
        final Long scopingCriteriaEnum = null;

        final Integer category = getCategory(command);
        final String dataTableDisplayName = null;
        this.dataTableValidator.validateDataTableRegistration(command.json());
        final String permissionSql = _getPermissionSql(dataTableName);
        _registerDataTable(applicationTableName, dataTableName, category, permissionSql, scopingCriteriaEnum, dataTableDisplayName);

    }

    @Transactional
    @Override
    public void registerDatatable(final JsonCommand command, final String permissionSql) {
        final String applicationTableName = getTableName(command.getUrl());
        final String dataTableName = getDataTableName(command.getUrl());
        final Long scopingCriteriaEnum = null;
        final String dataTableDisplayName = null;
        final Integer category = getCategory(command);

        this.dataTableValidator.validateDataTableRegistration(command.json());

        _registerDataTable(applicationTableName, dataTableName, category, permissionSql, scopingCriteriaEnum, dataTableDisplayName);

    }

    @Transactional
    private void _registerDataTable(final String applicationTableName, final String dataTableName, final Integer category,
            final String permissionsSql, final Long scopingCriteriaEnum, final String dataTableDisplayName) {

        validateAppTable(applicationTableName);
        assertDataTableExists(dataTableName);
        String displayName = null;
        if (dataTableDisplayName != null) {
            displayName = "'" + dataTableDisplayName + "'";
        }

        final String registerDatatableSql = "insert into x_registered_table (registered_table_name, application_table_name,category,scoping_criteria_enum, "
                + "registered_table_display_name ) values ('" + dataTableName + "',  '" + applicationTableName + "', '" + category + "',  "
                + scopingCriteriaEnum + ", " + displayName + ")";

        try {

            final String[] sqlArray = { registerDatatableSql, permissionsSql };
            this.jdbcTemplate.batchUpdate(sqlArray);

            // add the registered table to the config if it is a ppi
            if (isSurveyCategory(category)) {
                this.jdbcTemplate.execute("insert into c_configuration (name, value, enabled ) values('" + dataTableName + "', '0','0')");
            }

        } catch (final DataIntegrityViolationException dve) {
            final Throwable cause = dve.getCause();
            final Throwable realCause = dve.getMostSpecificCause();
            // even if duplicate is only due to permission duplicate, okay to
            // show duplicate datatable error msg
            if (realCause.getMessage().contains("Duplicate entry") || cause.getMessage()
                    .contains("Duplicate entry")) { throw new PlatformDataIntegrityException("error.msg.datatable.registered",
                            "Datatable `" + dataTableName + "` is already registered against an application table.", "dataTableName",
                            dataTableName); }
            logAsErrorUnexpectedDataIntegrityException(dve);
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }

    }

    private String _getPermissionSql(final String dataTableName) {
        final String createPermission = "'CREATE_" + dataTableName + "'";
        final String createPermissionChecker = "'CREATE_" + dataTableName + "_CHECKER'";
        final String readPermission = "'READ_" + dataTableName + "'";
        final String updatePermission = "'UPDATE_" + dataTableName + "'";
        final String updatePermissionChecker = "'UPDATE_" + dataTableName + "_CHECKER'";
        final String deletePermission = "'DELETE_" + dataTableName + "'";
        final String deletePermissionChecker = "'DELETE_" + dataTableName + "_CHECKER'";

        return "insert into m_permission (grouping, code, action_name, entity_name, can_maker_checker) values " + "('datatable', "
                + createPermission + ", 'CREATE', '" + dataTableName + "', true)," + "('datatable', " + createPermissionChecker
                + ", 'CREATE', '" + dataTableName + "', false)," + "('datatable', " + readPermission + ", 'READ', '" + dataTableName
                + "', false)," + "('datatable', " + updatePermission + ", 'UPDATE', '" + dataTableName + "', true)," + "('datatable', "
                + updatePermissionChecker + ", 'UPDATE', '" + dataTableName + "', false)," + "('datatable', " + deletePermission
                + ", 'DELETE', '" + dataTableName + "', true)," + "('datatable', " + deletePermissionChecker + ", 'DELETE', '"
                + dataTableName + "', false)";

    }

    private Integer getCategory(final JsonCommand command) {
        Integer category = command.integerValueOfParameterNamedDefaultToNullIfZero(DataTableApiConstant.categoryParamName);
        if (category == null) {
            category = DataTableApiConstant.CATEGORY_DEFAULT;
        }
        return category;
    }

    private boolean isSurveyCategory(final Integer category) {
        return category.equals(DataTableApiConstant.CATEGORY_PPI);
    }

    @Override
    public String getDataTableName(final String url) {

        final String[] urlParts = url.split("/");

        return urlParts[3];

    }

    @Override
    public String getTableName(final String url) {
        final String[] urlParts = url.split("/");
        return urlParts[4];
    }

    @Transactional
    @Override
    public void deregisterDatatable(final String datatable) {
        final String permissionList = "('CREATE_" + datatable + "', 'CREATE_" + datatable + "_CHECKER', 'READ_" + datatable + "', 'UPDATE_"
                + datatable + "', 'UPDATE_" + datatable + "_CHECKER', 'DELETE_" + datatable + "', 'DELETE_" + datatable + "_CHECKER')";

        final String deleteRolePermissionsSql = "delete from m_role_permission where m_role_permission.permission_id in (select id from m_permission where code in "
                + permissionList + ")";

        final String deletePermissionsSql = "delete from m_permission where code in " + permissionList;

        final String deleteRegisteredDatatableSql = "delete from x_registered_table where registered_table_name = '" + datatable + "'";

        final String deleteFromConfigurationSql = "delete from c_configuration where name ='" + datatable + "'";

        final String deleteRegisteredDatatableScope = "DELETE rts FROM f_registered_table_scoping rts INNER JOIN x_registered_table rt ON rt.id = rts.registered_table_id "
                + " WHERE rt.registered_table_name =  '" + datatable + "'";

        final String deleteRegisteredDatatableSections = "DELETE section from f_registered_table_section section INNER JOIN  x_registered_table rt ON section.registered_table_id = rt.id WHERE rt.registered_table_name =  '"
                + datatable + "'";

        final String[] sqlArray = new String[6];
        sqlArray[0] = deleteRolePermissionsSql;
        sqlArray[1] = deletePermissionsSql;
        sqlArray[2] = deleteRegisteredDatatableScope;
        sqlArray[3] = deleteRegisteredDatatableSections;
        sqlArray[4] = deleteRegisteredDatatableSql;
        sqlArray[5] = deleteFromConfigurationSql;

        this.jdbcTemplate.batchUpdate(sqlArray);
    }

    @Transactional
    @Override
    public CommandProcessingResult createNewDatatableEntry(final JsonCommand command) {

        final String dataTableName = command.entityName();
        String apptableIdentifier = null;

        try {
            final String appTable = queryForApplicationTableName(dataTableName);
            apptableIdentifier = getAppTableIdenfier(appTable, command);
            final CommandProcessingResult commandProcessingResult = checkMainResourceExistsWithinScope(appTable, apptableIdentifier);

            final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(dataTableName);
            final Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
            final Map<String, String> dataParams = this.fromJsonHelper.extractDataMap(typeOfMap, command.json());

            final String sql = getAddSql(columnHeaders, dataTableName, getFKField(appTable), apptableIdentifier, dataParams, appTable);

            this.jdbcTemplate.update(sql);

            return commandProcessingResult; //

        } catch (final DataAccessException dve) {
            final Throwable cause = dve.getCause();
            final Throwable realCause = dve.getMostSpecificCause();
            if (realCause.getMessage().contains("Duplicate entry") || cause.getMessage()
                    .contains("Duplicate entry")) { throw new PlatformDataIntegrityException(
                            "error.msg.datatable.entry.duplicate", "An entry already exists for datatable `" + dataTableName
                                    + "` and application table with identifier `" + apptableIdentifier + "`.",
                            "dataTableName", dataTableName, apptableIdentifier); }

            logAsErrorUnexpectedDataIntegrityException(dve);
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    @Override
    public CommandProcessingResult createPPIEntry(final String dataTableName, final String apptableIdentifier, final JsonCommand command) {

        try {
            final String appTable = queryForApplicationTableName(dataTableName);
            final CommandProcessingResult commandProcessingResult = checkMainResourceExistsWithinScope(appTable, apptableIdentifier);

            final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(dataTableName);

            final Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
            final Map<String, String> dataParams = this.fromJsonHelper.extractDataMap(typeOfMap, command.json());

            final String sql = getAddSqlWithScore(columnHeaders, dataTableName, getFKField(appTable), apptableIdentifier, dataParams);

            this.jdbcTemplate.update(sql);

            return commandProcessingResult; //

        } catch (final DataAccessException dve) {
            final Throwable cause = dve.getCause();
            final Throwable realCause = dve.getMostSpecificCause();
            if (realCause.getMessage().contains("Duplicate entry") || cause.getMessage()
                    .contains("Duplicate entry")) { throw new PlatformDataIntegrityException(
                            "error.msg.datatable.entry.duplicate", "An entry already exists for datatable `" + dataTableName
                                    + "` and application table with identifier `" + apptableIdentifier + "`.",
                            "dataTableName", dataTableName, apptableIdentifier); }

            logAsErrorUnexpectedDataIntegrityException(dve);
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    private boolean isRegisteredDataTable(final String name) {
        // PERMITTED datatables
        final String sql = "select if((exists (select 1 from x_registered_table where registered_table_name = ?)) = 1, 'true', 'false')";
        final String isRegisteredDataTable = this.jdbcTemplate.queryForObject(sql, String.class, new Object[] { name });
        return new Boolean(isRegisteredDataTable);
    }

    private void assertDataTableExists(final String datatableName) {
        final String sql = "select if((exists (select 1 from information_schema.tables where table_schema = schema() and table_name = ?)) = 1, 'true', 'false')";
        final String dataTableExistsString = this.jdbcTemplate.queryForObject(sql, String.class, new Object[] { datatableName });
        final boolean dataTableExists = new Boolean(dataTableExistsString);
        if (!dataTableExists) { throw new PlatformDataIntegrityException("error.msg.invalid.datatable",
                "Invalid Data Table: " + datatableName, "name", datatableName); }
    }

    private void validateDatatableName(final String name) {

        if (name == null || name.isEmpty()) {
            throw new PlatformDataIntegrityException("error.msg.datatables.datatable.null.name", "Data table name must not be blank.");
        } else if (!name.matches(DATATABLE_NAME_REGEX_PATTERN)) { throw new PlatformDataIntegrityException(
                "error.msg.datatables.datatable.invalid.name.regex", "Invalid data table name.", name); }
    }

    private String datatableColumnNameToCodeValueName(final String columnName, final String code) {

        return (code + "_cd_" + columnName);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    private void parseDatatableColumnObjectForCreate(final JsonObject column, StringBuilder sqlBuilder,
            final StringBuilder constrainBuilder, final String dataTableNameAlias, final Map<String, Long> codeMappings,
            final boolean isConstraintApproach) {

        String name = (column.has("name")) ? column.get("name").getAsString() : null;
        final String type = (column.has("type")) ? column.get("type").getAsString().toLowerCase() : null;
        final Integer length = (column.has("length")) ? column.get("length").getAsInt() : null;
        final Boolean mandatory = (column.has("mandatory")) ? column.get("mandatory").getAsBoolean() : false;
        final String code = (column.has("code")) ? column.get("code").getAsString() : null;

        if (StringUtils.isNotBlank(code)) {
            if (isConstraintApproach) {
                codeMappings.put(dataTableNameAlias + "_" + name, this.codeReadPlatformService.retriveCode(code).getCodeId());
                constrainBuilder.append(", CONSTRAINT `fk_").append(dataTableNameAlias).append("_").append(name).append("` ")
                        .append("FOREIGN KEY (`" + name + "`) ").append("REFERENCES `").append(CODE_VALUES_TABLE).append("` (`id`)");
            } else {
                name = datatableColumnNameToCodeValueName(name, code);
            }
        }

        final String mysqlType = apiTypeToMySQL.get(type);
        sqlBuilder = sqlBuilder.append("`" + name + "` " + mysqlType);

        if (type != null) {
            if (type.equalsIgnoreCase("String")) {
                sqlBuilder = sqlBuilder.append("(" + length + ")");
            } else if (type.equalsIgnoreCase("Decimal")) {
                sqlBuilder = sqlBuilder.append("(19,6)");
            } else if (type.equalsIgnoreCase("Dropdown")) {
                sqlBuilder = sqlBuilder.append("(11)");
            }
        }
        if (mandatory != null) {
            if (mandatory) {
                sqlBuilder = sqlBuilder.append(" NOT NULL");
            } else {
                sqlBuilder = sqlBuilder.append(" DEFAULT NULL");
            }
        }

        sqlBuilder = sqlBuilder.append(", ");
    }

    @Transactional
    @Override
    public CommandProcessingResult createDatatable(final JsonCommand command) {

        String datatableName = null;

        try {
            this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final JsonElement element = this.fromJsonHelper.parse(command.json());
            JsonArray sections = null;
            JsonArray columns = null;
            if (this.fromJsonHelper.parameterExists(DataTableApiConstant.sectionsParamName, element)) {
                sections = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.sectionsParamName, element);
            } else {
                columns = this.fromJsonHelper.extractJsonArrayNamed("columns", element);
            }

            datatableName = this.fromJsonHelper.extractStringNamed("datatableName", element);
            final String dataTableDisplayName = this.fromJsonHelper.extractStringNamed("dataTableDisplayName", element);
            final String apptableName = this.fromJsonHelper.extractStringNamed("apptableName", element);
            Boolean multiRow = this.fromJsonHelper.extractBooleanNamed("multiRow", element);
            final Long scopingCriteriaEnum = this.fromJsonHelper.extractLongNamed("scopingCriteriaEnum", element);
            final JsonObject jsonObject = element.getAsJsonObject();
            final JsonElement scope = jsonObject.get(DataTableApiConstant.scopeParamName);

            /***
             * In cases of tables storing hierarchical entities (like m_group),
             * different entities would end up being stored in the same table.
             *
             * Ex: Centers are a specific type of group, add abstractions for
             * the same
             ***/
            final String actualAppTableName = mapToActualAppTable(apptableName);

            if (multiRow == null) {
                multiRow = false;
            }

            validateDatatableName(datatableName);
            validateAppTable(apptableName);
            final boolean isConstraintApproach = this.configurationDomainService.isConstraintApproachEnabledForDatatables();
            String fkColumnName = null;
            Boolean isJournalEntryDataTable = false;
            if (apptableName.equals(DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME)) {
                isJournalEntryDataTable = true;
            }
            fkColumnName = apptableName.substring(apptableName.indexOf('_') + 1) + "_id";
            final String dataTableNameAlias = datatableName.toLowerCase().replaceAll("\\s", "_");
            final String fkName = dataTableNameAlias + "_" + fkColumnName;
            StringBuilder sqlBuilder = new StringBuilder();
            final StringBuilder constrainBuilder = new StringBuilder();
            final Map<String, Long> codeMappings = new HashMap<>();
            sqlBuilder = sqlBuilder.append("CREATE TABLE `" + datatableName + "` (");

            if (multiRow) {
                sqlBuilder = sqlBuilder.append("`id` BIGINT(20) NOT NULL AUTO_INCREMENT, ");
            }
            if (isJournalEntryDataTable) {
                sqlBuilder = sqlBuilder.append("`" + fkColumnName + "` VARCHAR(30) NOT NULL, ");
            } else {
                sqlBuilder = sqlBuilder.append("`" + fkColumnName + "` BIGINT(20) NOT NULL, ");
            }

            if (columns != null) {
                for (final JsonElement column : columns) {
                    parseDatatableColumnObjectForCreate(column.getAsJsonObject(), sqlBuilder, constrainBuilder, dataTableNameAlias,
                            codeMappings, isConstraintApproach);
                }
            } else if (sections != null) {
                for (final JsonElement section : sections) {
                    final JsonArray sectionedColumns = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.columnsParamName,
                            section);
                    for (final JsonElement column : sectionedColumns) {
                        parseDatatableColumnObjectForCreate(column.getAsJsonObject(), sqlBuilder, constrainBuilder, dataTableNameAlias,
                                codeMappings, isConstraintApproach);
                    }
                }
            }

            // Remove trailing comma and space
            sqlBuilder = sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());

            if (!isJournalEntryDataTable) {
                if (multiRow) {
                    sqlBuilder = sqlBuilder.append(", PRIMARY KEY (`id`)")
                            .append(", KEY `fk_" + fkColumnName + "` (`" + fkColumnName + "`)").append(", CONSTRAINT `fk_" + fkName + "` ")
                            .append("FOREIGN KEY (`" + fkColumnName + "`) ").append("REFERENCES `" + actualAppTableName + "` (`id`)");
                } else {
                    sqlBuilder = sqlBuilder.append(", PRIMARY KEY (`" + fkColumnName + "`)").append(", CONSTRAINT `fk_" + fkName + "` ")
                            .append("FOREIGN KEY (`" + fkColumnName + "`) ").append("REFERENCES `" + actualAppTableName + "` (`id`)");
                }
            } else if (multiRow) {
                sqlBuilder = sqlBuilder.append(", PRIMARY KEY (`id`)");
            }

            sqlBuilder.append(constrainBuilder);

            sqlBuilder = sqlBuilder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            this.jdbcTemplate.execute(sqlBuilder.toString());

            registerDatatable(datatableName, apptableName, scopingCriteriaEnum, dataTableDisplayName);
            registerColumnCodeMapping(codeMappings);
            final boolean isCreateDataTable = true;
            Long sectionId = null;
            if (sections != null && sections.size() > 0) {
                for (final JsonElement section : sections) {
                    sectionId = registerDataTableSectionData(datatableName, section);
                    final JsonArray sectionedcolumns = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.columnsParamName,
                            section);
                    if (sectionedcolumns != null && sectionedcolumns.size() > 0) {

                        final JsonArray sectionedColumns = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.columnsParamName,
                                section);
                        registerDatatableMetadata(datatableName, sectionedColumns, sectionId);
                        updateXRegisteredDisplayRules(datatableName, sectionedcolumns, isCreateDataTable);
                    }
                }

            } else {
                registerDatatableMetadata(datatableName, columns, sectionId);
                updateXRegisteredDisplayRules(datatableName, columns, isCreateDataTable);
            }

            if (scope != null) {
                if (scope.isJsonArray()) {
                    final JsonArray scopeArray = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.scopeParamName, element);
                    for (final JsonElement ele : scopeArray) {
                        final Long id = this.fromJsonHelper.extractLongNamed(DataTableApiConstant.idParamName, ele);
                        final JsonArray allowedValues = this.fromJsonHelper
                                .extractJsonArrayNamed(DataTableApiConstant.allowedValuesParamName, ele);
                        updateDataTableScope(datatableName, id, allowedValues);
                    }
                } else {
                    final Long id = this.fromJsonHelper.extractLongNamed(DataTableApiConstant.idParamName, scope);
                    final JsonArray allowedValues = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.allowedValuesParamName,
                            scope);
                    updateDataTableScope(datatableName, id, allowedValues);
                }
            }

        } catch (final DataAccessException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("datatable");
            if (realCause.getMessage().toLowerCase().contains("duplicate column name")) {
                baseDataValidator.reset().parameter("name").failWithCode("duplicate.column.name");
            } else if (realCause.getMessage().contains("Table") && realCause.getMessage().contains("already exists")) {
                baseDataValidator.reset().parameter("datatableName").value(datatableName).failWithCode("datatable.already.exists");
            } else if (realCause.getMessage().contains("Column") && realCause.getMessage().contains("big")) {
                baseDataValidator.reset().parameter("column").failWithCode("length.too.big");
            } else if (realCause.getMessage().contains("Row") && realCause.getMessage().contains("large")) {
                baseDataValidator.reset().parameter("row").failWithCode("size.too.large");
            }

            throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withResourceIdAsString(datatableName).build();
    }

    private void updateDataTableScope(final String datatableName, final Long scopeId, final JsonArray allowedValues) {
        final String sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + datatableName + "'";
        final Integer xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);

        final StringBuilder deleteSql = new StringBuilder(
                "delete from f_registered_table_scoping where registered_table_id = " + xResgisteredTableId + " ");
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO f_registered_table_scoping(registered_table_id,");

        if (DataTableScopes.fromInt(scopeId.intValue()).isLoanProduct()) {
            sqlQuery = sqlQuery.append(" loan_product_id ) ");
        }
        if (DataTableScopes.fromInt(scopeId.intValue()).isSavingsProduct()) {
            sqlQuery = sqlQuery.append(" savings_product_id ) ");
        }
        if (DataTableScopes.fromInt(scopeId.intValue()).isClientLegalForm()) {
            sqlQuery = sqlQuery.append(" legal_form_enum ) ");
        }
        if (DataTableScopes.fromInt(scopeId.intValue()).isClientType()
                || DataTableScopes.fromInt(scopeId.intValue()).isClientClassification()) {
            sqlQuery = sqlQuery.append(" code_value_id ) ");
        }

        deleteExistingScopeExceptAllowedValues(deleteSql);

        for (final JsonElement allowedValue : allowedValues) {
            String insertsql = "";
            insertsql = insertsql + "VALUES(" + xResgisteredTableId + "," + allowedValue.getAsLong() + ");";
            final String query = sqlQuery + insertsql;
            this.jdbcTemplate.execute(query);
        }

    }

    private void deleteExistingScopeExceptAllowedValues(final StringBuilder deleteSql) {
        this.jdbcTemplate.execute(deleteSql.toString());
    }

    private void updateXRegisteredDisplayRules(final String datatableName, final JsonArray columns, final boolean isCreateDataTable) {

        final String sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + datatableName + "'";
        final Integer xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);
        for (final JsonElement column : columns) {
            final String name = this.fromJsonHelper.extractStringNamed("name", column);
            final String dependsOn = this.fromJsonHelper.extractStringNamed("dependsOn", column);
            final JsonArray visibilityCriteria = this.fromJsonHelper.extractJsonArrayNamed("visibilityCriteria", column);
            final String displayName = this.fromJsonHelper.extractStringNamed("displayName", column);
            final Long displayPosition = this.fromJsonHelper.extractLongNamed("displayPosition", column);
            final Boolean visible = this.fromJsonHelper.extractBooleanNamed("visible", column);
            final Boolean mandatoryIfVisible = this.fromJsonHelper.extractBooleanNamed("mandatoryIfVisible", column);

            Long associatedColumnId = null;
            if (dependsOn != null) {
                final String sqlQuery = "SELECT xrtm.id FROM x_registered_table_metadata xrtm WHERE xrtm.column_name = '" + dependsOn + "' "
                        + "and xrtm.registered_table_id = " + xResgisteredTableId + "";
                associatedColumnId = this.jdbcTemplate.queryForLong(sqlQuery);
                final String updateQuery = "UPDATE x_registered_table_metadata xrtm SET xrtm.associate_with = " + associatedColumnId + ", "
                        + "xrtm.display_name = '" + displayName + "', xrtm.order_position = " + displayPosition + ", xrtm.visible = "
                        + visible + ", " + "xrtm.mandatory_if_visible = " + mandatoryIfVisible + "  WHERE " + "xrtm.column_name = '" + name
                        + "' AND xrtm.registered_table_id = " + xResgisteredTableId + "";
                this.jdbcTemplate.update(updateQuery);
            }
            if (visibilityCriteria != null) {
                for (final JsonElement criteria : visibilityCriteria) {
                    final String columnName = this.fromJsonHelper.extractStringNamed("columnName", criteria);
                    final String value = this.fromJsonHelper.extractStringNamed("value", criteria);
                    final CodeValueData codeValueData = this.codeValueReadPlatformService.retriveCodeValueByCodeValueName(value);
                    if (codeValueData == null) { throw new CodeValueNotFoundException(value, "code value not found"); }
                    final String watchColumnSql = "SELECT xrtm.id from x_registered_table_metadata xrtm where xrtm.column_name = '"
                            + columnName + "' " + "and xrtm.registered_table_id = " + xResgisteredTableId + "";
                    final Integer watchColumnId = this.jdbcTemplate.queryForInt(watchColumnSql);
                    final String selectSql = "SELECT xrtm.id from x_registered_table_metadata xrtm where xrtm.column_name = '" + name + "' "
                            + "and xrtm.registered_table_id = " + xResgisteredTableId + "";
                    final Integer xRegisterTableMetadataId = this.jdbcTemplate.queryForInt(selectSql);
                    if (isCreateDataTable) {
                        final String insertQuery = "INSERT INTO x_registered_table_display_rules (registered_table_metadata_id, watch_column) VALUES"
                                + "(" + xRegisterTableMetadataId + "," + watchColumnId + ")";
                        this.jdbcTemplate.execute(insertQuery);
                        final String query = "SELECT dtrv.id FROM x_registered_table_display_rules dtrv WHERE dtrv.registered_table_metadata_id = "
                                + xRegisterTableMetadataId + "";
                        final Integer rulesValueId = this.jdbcTemplate.queryForInt(query);
                        final String sqlQuery = "INSERT INTO x_registered_table_display_rules_value(code_value_id, registered_table_display_rules_id)"
                                + "VALUES(" + codeValueData.getId() + "," + rulesValueId + ")";
                        this.jdbcTemplate.execute(sqlQuery);
                    } else {
                        final String updateQuery = "UPDATE x_registered_table_display_rules  xrtdr SET  watch_column = " + watchColumnId
                                + " " + "WHERE registered_table_metadata_id = " + xRegisterTableMetadataId + " ";
                        final String[] queries = new String[] { updateQuery };
                        final int[] count = this.jdbcTemplate.batchUpdate(queries);
                        if (count[0] < 1) {
                            final String insertQuery = "INSERT INTO x_registered_table_display_rules (registered_table_metadata_id, watch_column) VALUES"
                                    + "(" + xRegisterTableMetadataId + "," + watchColumnId + ")";
                            final String[] insertQueries = new String[] { insertQuery };
                            this.jdbcTemplate.batchUpdate(insertQueries);
                        }
                        final String query = "SELECT dtrv.id FROM x_registered_table_display_rules dtrv WHERE dtrv.registered_table_metadata_id = "
                                + xRegisterTableMetadataId + "";
                        final Integer rulesValueId = this.jdbcTemplate.queryForInt(query);
                        final String updateSql = "UPDATE x_registered_table_display_rules_value xrtdr SET code_value_id = "
                                + codeValueData.getId() + "" + " WHERE registered_table_display_rules_id = " + rulesValueId + "";
                        final String[] updateDisplayRules = new String[] { updateSql };
                        final int[] displayCount = this.jdbcTemplate.batchUpdate(updateDisplayRules);
                        if (displayCount[0] < 1) {
                            final String insertQuery = "INSERT INTO x_registered_table_display_rules_value(code_value_id, registered_table_display_rules_id)"
                                    + "VALUES(" + codeValueData.getId() + "," + rulesValueId + ")";
                            final String[] insertQueries = new String[] { insertQuery };
                            this.jdbcTemplate.batchUpdate(insertQueries);
                        }
                    }
                }
            }
        }

    }

    private void registerDatatableMetadata(final String datatableName, final JsonArray columns, final Long sectionId) {
        String sql = null;
        String query = null;
        sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + datatableName + "'";
        final int xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);

        final String[] sqlInsertQueries = new String[columns.size()];
        int index = 0;
        for (final JsonElement column : columns) {
            final String name = this.fromJsonHelper.extractStringNamed("name", column);
            final String displayName = this.fromJsonHelper.extractStringNamed("displayName", column);
            final Long displayPosition = this.fromJsonHelper.extractLongNamed("displayPosition", column);
            final Boolean visible = this.fromJsonHelper.extractBooleanNamed("visible", column);
            final Boolean mandatoryIfVisible = this.fromJsonHelper.extractBooleanNamed("mandatoryIfVisible", column);

            final Long associatedColumnId = null;
            query = "insert into x_registered_table_metadata(registered_table_id, column_name, associate_with, display_name, order_position, visible, mandatory_if_visible, section_id) "
                    + "values(" + xResgisteredTableId + ", '" + name + "', " + associatedColumnId + ", '" + displayName + "', "
                    + displayPosition + ", " + visible + ", " + mandatoryIfVisible + ", " + sectionId + ")";
            sqlInsertQueries[index] = query;
            index++;
        }
        this.jdbcTemplate.batchUpdate(sqlInsertQueries);
    }

    private void parseDatatableColumnForUpdate(final JsonObject column,
            final Map<String, ResultsetColumnHeaderData> mapColumnNameDefinition, StringBuilder sqlBuilder, final String datatableName,
            final StringBuilder constrainBuilder, final Map<String, Long> codeMappings, final List<String> removeMappings,
            final boolean isConstraintApproach) {

        String name = (column.has("name")) ? column.get("name").getAsString() : null;
        final String lengthStr = (column.has("length")) ? column.get("length").getAsString() : null;
        Integer length = (StringUtils.isNotBlank(lengthStr)) ? Integer.parseInt(lengthStr) : null;
        String newName = (column.has("newName")) ? column.get("newName").getAsString() : name;
        final Boolean mandatory = (column.has("mandatory")) ? column.get("mandatory").getAsBoolean() : false;
        final String after = (column.has("after")) ? column.get("after").getAsString() : null;
        final String code = (column.has("code")) ? column.get("code").getAsString() : null;
        final String newCode = (column.has("newCode")) ? column.get("newCode").getAsString() : null;
        final String dataTableNameAlias = datatableName.toLowerCase().replaceAll("\\s", "_");
        if (isConstraintApproach) {
            if (StringUtils.isBlank(newName)) {
                newName = name;
            }
            if (!StringUtils.equalsIgnoreCase(code, newCode) || !StringUtils.equalsIgnoreCase(name, newName)) {
                if (StringUtils.equalsIgnoreCase(code, newCode)) {
                    final int codeId = getCodeIdForColumn(dataTableNameAlias, name);
                    if (codeId > 0) {
                        removeMappings.add(dataTableNameAlias + "_" + name);
                        constrainBuilder.append(", DROP FOREIGN KEY `fk_").append(dataTableNameAlias).append("_").append(name).append("` ");
                        codeMappings.put(dataTableNameAlias + "_" + newName, (long) codeId);
                        constrainBuilder.append(",ADD CONSTRAINT  `fk_").append(dataTableNameAlias).append("_").append(newName).append("` ")
                                .append("FOREIGN KEY (`" + newName + "`) ").append("REFERENCES `").append(CODE_VALUES_TABLE)
                                .append("` (`id`)");
                    }

                } else {
                    if (code != null) {
                        removeMappings.add(dataTableNameAlias + "_" + name);
                        if (newCode == null || !StringUtils.equalsIgnoreCase(name, newName)) {
                            constrainBuilder.append(", DROP FOREIGN KEY `fk_").append(dataTableNameAlias).append("_").append(name)
                                    .append("` ");
                        }
                    }
                    if (newCode != null) {
                        codeMappings.put(dataTableNameAlias + "_" + newName, this.codeReadPlatformService.retriveCode(newCode).getCodeId());
                        if (code == null || !StringUtils.equalsIgnoreCase(name, newName)) {
                            constrainBuilder.append(",ADD CONSTRAINT  `fk_").append(dataTableNameAlias).append("_").append(newName)
                                    .append("` ").append("FOREIGN KEY (`" + newName + "`) ").append("REFERENCES `")
                                    .append(CODE_VALUES_TABLE).append("` (`id`)");
                        }
                    }
                }
            }
        } else {
            if (StringUtils.isNotBlank(code)) {
                name = datatableColumnNameToCodeValueName(name, code);
                if (StringUtils.isNotBlank(newCode)) {
                    newName = datatableColumnNameToCodeValueName(newName, newCode);
                } else {
                    newName = datatableColumnNameToCodeValueName(newName, code);
                }
            }
        }
        if (!mapColumnNameDefinition.containsKey(name)) { throw new PlatformDataIntegrityException(
                "error.msg.datatable.column.missing.update.parse", "Column " + name + " does not exist.", name); }
        final String type = mapColumnNameDefinition.get(name).getColumnType();
        if (length == null && type.toLowerCase().equals("varchar")) {
            length = mapColumnNameDefinition.get(name).getColumnLength().intValue();
        }

        sqlBuilder = sqlBuilder.append(", CHANGE `" + name + "` `" + newName + "` " + type);
        if (length != null && length > 0) {
            if (type.toLowerCase().equals("decimal")) {
                sqlBuilder.append("(19,6)");
            } else if (type.toLowerCase().equals("varchar")) {
                sqlBuilder.append("(" + length + ")");
            }
        }

        if (mandatory != null) {
            if (mandatory) {
                sqlBuilder = sqlBuilder.append(" NOT NULL");
            } else {
                sqlBuilder = sqlBuilder.append(" DEFAULT NULL");
            }
        }
        if (after != null) {
            sqlBuilder = sqlBuilder.append(" AFTER `" + after + "`");
        }
    }

    @SuppressWarnings("deprecation")
    private int getCodeIdForColumn(final String dataTableNameAlias, final String name) {
        final StringBuilder checkColumnCodeMapping = new StringBuilder();
        checkColumnCodeMapping.append("select ccm.code_id from x_table_column_code_mappings ccm where ccm.column_alias_name='")
                .append(dataTableNameAlias).append("_").append(name).append("'");
        int codeId = 0;
        try {
            codeId = this.jdbcTemplate.queryForInt(checkColumnCodeMapping.toString());
        } catch (final EmptyResultDataAccessException e) {
            logger.info(e.getMessage());
        }
        return codeId;
    }

    private void parseDatatableColumnForAdd(final JsonObject column, StringBuilder sqlBuilder, final String dataTableNameAlias,
            final StringBuilder constrainBuilder, final Map<String, Long> codeMappings, final boolean isConstraintApproach) {

        String name = (column.has("name")) ? column.get("name").getAsString() : null;
        final String type = (column.has("type")) ? column.get("type").getAsString().toLowerCase() : null;
        final Integer length = (column.has("length")) ? column.get("length").getAsInt() : null;
        final Boolean mandatory = (column.has("mandatory")) ? column.get("mandatory").getAsBoolean() : false;
        final String after = (column.has("after")) ? column.get("after").getAsString() : null;
        final String code = (column.has("code")) ? column.get("code").getAsString() : null;

        if (StringUtils.isNotBlank(code)) {
            if (isConstraintApproach) {
                codeMappings.put(dataTableNameAlias + "_" + name, this.codeReadPlatformService.retriveCode(code).getCodeId());
                constrainBuilder.append(",ADD CONSTRAINT  `fk_").append(dataTableNameAlias).append("_").append(name).append("` ")
                        .append("FOREIGN KEY (`" + name + "`) ").append("REFERENCES `").append(CODE_VALUES_TABLE).append("` (`id`)");
            } else {
                name = datatableColumnNameToCodeValueName(name, code);
            }
        }

        final String mysqlType = apiTypeToMySQL.get(type);
        sqlBuilder = sqlBuilder.append(", ADD `" + name + "` " + mysqlType);

        if (type != null) {
            if (type.equalsIgnoreCase("String") && length != null) {
                sqlBuilder = sqlBuilder.append("(" + length + ")");
            } else if (type.equalsIgnoreCase("Decimal")) {
                sqlBuilder = sqlBuilder.append("(19,6)");
            } else if (type.equalsIgnoreCase("Dropdown")) {
                sqlBuilder = sqlBuilder.append("(11)");
            }
        }
        if (mandatory != null) {
            if (mandatory) {
                sqlBuilder = sqlBuilder.append(" NOT NULL");
            } else {
                sqlBuilder = sqlBuilder.append(" DEFAULT NULL");
            }
        }
        if (after != null) {
            sqlBuilder = sqlBuilder.append(" AFTER `" + after + "`");
        }
    }

    private void parseDatatableColumnForDrop(final JsonObject column, StringBuilder sqlBuilder, final String datatableName,
            final StringBuilder constrainBuilder, final List<String> codeMappings) {
        final String datatableAlias = datatableName.toLowerCase().replaceAll("\\s", "_");
        final String name = (column.has("name")) ? column.get("name").getAsString() : null;
        sqlBuilder = sqlBuilder.append(", DROP COLUMN `" + name + "`");
        final StringBuilder findFKSql = new StringBuilder();
        findFKSql.append("SELECT count(*)").append("FROM information_schema.TABLE_CONSTRAINTS i")
                .append(" WHERE i.CONSTRAINT_TYPE = 'FOREIGN KEY'").append(" AND i.TABLE_SCHEMA = DATABASE()")
                .append(" AND i.TABLE_NAME = '").append(datatableName).append("' AND i.CONSTRAINT_NAME = 'fk_").append(datatableAlias)
                .append("_").append(name).append("' ");
        @SuppressWarnings("deprecation")
        final int count = this.jdbcTemplate.queryForInt(findFKSql.toString());
        if (count > 0) {
            codeMappings.add(datatableAlias + "_" + name);
            constrainBuilder.append(", DROP FOREIGN KEY `fk_").append(datatableAlias).append("_").append(name).append("` ");
        }
    }

    private void registerColumnCodeMapping(final Map<String, Long> codeMappings) {
        if (codeMappings != null && !codeMappings.isEmpty()) {
            final String[] addSqlList = new String[codeMappings.size()];
            int i = 0;
            for (final Map.Entry<String, Long> mapEntry : codeMappings.entrySet()) {
                addSqlList[i++] = "insert into x_table_column_code_mappings (column_alias_name, code_id) values ('" + mapEntry.getKey()
                        + "'," + mapEntry.getValue() + ");";
            }

            this.jdbcTemplate.batchUpdate(addSqlList);
        }
    }

    private void deleteColumnCodeMapping(final List<String> columnNames) {
        if (columnNames != null && !columnNames.isEmpty()) {
            final String[] deleteSqlList = new String[columnNames.size()];
            int i = 0;
            for (final String columnName : columnNames) {
                deleteSqlList[i++] = "DELETE FROM x_table_column_code_mappings WHERE  column_alias_name='" + columnName + "';";
            }

            this.jdbcTemplate.batchUpdate(deleteSqlList);
        }

    }

    /**
     * Update data table, set column value to empty string where current value
     * is NULL. Run update SQL only if the "mandatory" property is set to true
     *
     * @param datatableName
     *            Name of data table
     * @param column
     *            JSON encoded array of column properties
     * @see https://mifosforge.jira.com/browse/MIFOSX-1145
     **/
    private void removeNullValuesFromStringColumn(final String datatableName, final JsonObject column,
            final Map<String, ResultsetColumnHeaderData> mapColumnNameDefinition) {
        final Boolean mandatory = (column.has("mandatory")) ? column.get("mandatory").getAsBoolean() : false;
        final String name = (column.has("name")) ? column.get("name").getAsString() : "";
        final String type = (mapColumnNameDefinition.containsKey(name)) ? mapColumnNameDefinition.get(name).getColumnType() : "";

        if (StringUtils.isNotEmpty(type)) {
            if (mandatory && stringDataTypes.contains(type.toLowerCase())) {
                final StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("UPDATE `" + datatableName + "` SET `" + name + "` = '' WHERE `" + name + "` IS NULL");

                this.jdbcTemplate.update(sqlBuilder.toString());
            }
        }
    }

    @Transactional
    @Override
    public void updateDatatable(final String datatableName, final JsonCommand command) {

        try {
            this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            final JsonElement element = this.fromJsonHelper.parse(command.json());
            final JsonArray changeColumns = this.fromJsonHelper.extractJsonArrayNamed("changeColumns", element);
            final JsonArray addColumns = this.fromJsonHelper.extractJsonArrayNamed("addColumns", element);
            final JsonArray dropColumns = this.fromJsonHelper.extractJsonArrayNamed("dropColumns", element);
            final String apptableName = this.fromJsonHelper.extractStringNamed("apptableName", element);
            final String dataTableDisplayName = this.fromJsonHelper.extractStringNamed("dataTableDisplayName", element);
            final Long scopingCriteriaEnum = this.fromJsonHelper.extractLongNamed("scopingCriteriaEnum", element);
            final JsonArray addSections = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.addSectionsParamName, element);
            final JsonArray dropSections = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.dropSectionsParamName, element);
            final JsonArray reorderSections = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.changeSectionsParamName,
                    element);

            Boolean isDataTableSectioned = false;
            JsonArray sections = null;
            if (this.fromJsonHelper.parameterExists(DataTableApiConstant.sectionsParamName, element)) {
                sections = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.sectionsParamName, element);
                if (sections != null && sections.size() > 0) {
                    isDataTableSectioned = true;
                }
            }
            final JsonObject jsonObject = element.getAsJsonObject();
            final JsonElement scope = jsonObject.get(DataTableApiConstant.scopeParamName);

            validateDatatableName(datatableName);

            final List<ResultsetColumnHeaderData> columnHeaderData = this.genericDataService.fillResultsetColumnHeaders(datatableName);
            final Map<String, ResultsetColumnHeaderData> mapColumnNameDefinition = new HashMap<>();
            for (final ResultsetColumnHeaderData columnHeader : columnHeaderData) {
                mapColumnNameDefinition.put(columnHeader.getColumnName(), columnHeader);
            }

            final boolean isConstraintApproach = this.configurationDomainService.isConstraintApproachEnabledForDatatables();
            updateTableDisplayName(dataTableDisplayName, datatableName);
            if (!StringUtils.isBlank(apptableName)) {
                validateAppTable(apptableName);

                final String oldApptableName = queryForApplicationTableName(datatableName);
                if (!StringUtils.equals(oldApptableName, apptableName)) {
                    final String oldFKName = oldApptableName.substring(2) + "_id";
                    final String newFKName = apptableName.substring(2) + "_id";
                    final String actualAppTableName = mapToActualAppTable(apptableName);
                    final String oldConstraintName = datatableName.toLowerCase().replaceAll("\\s", "_") + "_" + oldFKName;
                    final String newConstraintName = datatableName.toLowerCase().replaceAll("\\s", "_") + "_" + newFKName;
                    StringBuilder sqlBuilder = new StringBuilder();

                    if (mapColumnNameDefinition.containsKey("id")) {
                        sqlBuilder = sqlBuilder.append("ALTER TABLE `" + datatableName + "` ").append("DROP KEY `fk_" + oldFKName + "`,")
                                .append("DROP FOREIGN KEY `fk_" + oldConstraintName + "`,")
                                .append("CHANGE COLUMN `" + oldFKName + "` `" + newFKName + "` BIGINT(20) NOT NULL,")
                                .append("ADD KEY `fk_" + newFKName + "` (`" + newFKName + "`),")
                                .append("ADD CONSTRAINT `fk_" + newConstraintName + "` ").append("FOREIGN KEY (`" + newFKName + "`) ")
                                .append("REFERENCES `" + actualAppTableName + "` (`id`)");
                    } else {
                        sqlBuilder = sqlBuilder.append("ALTER TABLE `" + datatableName + "` ")
                                .append("DROP FOREIGN KEY `fk_" + oldConstraintName + "`,")
                                .append("CHANGE COLUMN `" + oldFKName + "` `" + newFKName + "` BIGINT(20) NOT NULL,")
                                .append("ADD CONSTRAINT `fk_" + newConstraintName + "` ").append("FOREIGN KEY (`" + newFKName + "`) ")
                                .append("REFERENCES `" + actualAppTableName + "` (`id`)");
                    }

                    this.jdbcTemplate.execute(sqlBuilder.toString());

                    deregisterDatatable(datatableName);
                    registerDatatable(datatableName, apptableName, scopingCriteriaEnum, dataTableDisplayName);
                }
            }

            if (dropColumns != null) {
                StringBuilder sqlBuilder = new StringBuilder("ALTER TABLE `" + datatableName + "`");
                final StringBuilder constrainBuilder = new StringBuilder();
                final List<String> codeMappings = new ArrayList<>();
                final DatatableData datatableData = retrieveDatatable(datatableName);
                final Integer xRegisterTableId = datatableData.getId();
                updateScopeCriteriaEnum(scopingCriteriaEnum, xRegisterTableId);
                final StringBuilder deleteColumnValues = new StringBuilder();
                final StringBuilder sql = new StringBuilder("DELETE FROM `x_registered_table_metadata` WHERE ");
                int i = 0;
                for (final JsonElement column : dropColumns) {
                    parseDatatableColumnForDrop(column.getAsJsonObject(), sqlBuilder, datatableName, constrainBuilder, codeMappings);
                    final String name = (column.getAsJsonObject().has("name")) ? column.getAsJsonObject().get("name").getAsString() : null;
                    if (i == 0) {
                        deleteColumnValues.append("'" + name + "'");
                    } else {
                        deleteColumnValues.append(", '" + name + "'");
                    }
                    i++;
                }
                sql.append("column_name IN  (" + deleteColumnValues + ") AND registered_table_id = " + xRegisterTableId + "");
                // Remove the first comma, right after ALTER TABLE `datatable`
                final int indexOfFirstComma = sqlBuilder.indexOf(",");
                if (indexOfFirstComma != -1) {
                    sqlBuilder = sqlBuilder.deleteCharAt(indexOfFirstComma);
                }
                sqlBuilder.append(constrainBuilder);
                this.jdbcTemplate.execute(sqlBuilder.toString());
                this.jdbcTemplate.execute(sql.toString());
                deleteColumnCodeMapping(codeMappings);
            }
            Long sectionId = null;

            if (!isDataTableSectioned && addColumns != null) {

                addColumnsToDatatable(datatableName, addColumns, scopingCriteriaEnum, isConstraintApproach, sectionId);
            }

            if (!isDataTableSectioned && changeColumns != null) {

                alterColumnsInDatatable(datatableName, changeColumns, scopingCriteriaEnum, mapColumnNameDefinition, isConstraintApproach,
                        sectionId);
            }
            if (addSections != null && addSections.size() > 0) {
                for (final JsonElement tempsection : addSections) {
                    registerDataTableSectionData(datatableName, tempsection);
                }
            }
            if (dropSections != null && dropSections.size() > 0) {
                for (final JsonElement tempsection : dropSections) {
                    removeSection(datatableName, tempsection);
                }
            }
            if (reorderSections != null && reorderSections.size() > 0) {
                for (final JsonElement tempsection : reorderSections) {
                    reOrderSection(datatableName, tempsection);
                }
            }

            if (isDataTableSectioned && sections != null && sections.size() > 0) {
                for (final JsonElement section : sections) {
                    final String sectionName = this.fromJsonHelper.extractStringNamed(DataTableApiConstant.displayNameParamName, section);
                    sectionId = retrieveSectionId(datatableName, sectionName);// registerDataTableSectionData(datatableName,
                                                                              // section);
                    JsonArray changedSectionColumns = null;
                    if (this.fromJsonHelper.parameterExists(DataTableApiConstant.changeColumnsParamName, section)) {
                        changedSectionColumns = this.fromJsonHelper.extractJsonArrayNamed("changeColumns", section);
                        if (changedSectionColumns != null && changedSectionColumns.size() > 0) {
                            alterColumnsInDatatable(datatableName, changedSectionColumns, scopingCriteriaEnum, mapColumnNameDefinition,
                                    isConstraintApproach, sectionId);
                        }
                    }
                    JsonArray addSectionColumns = null;
                    if (this.fromJsonHelper.parameterExists(DataTableApiConstant.addColumnsParamName, section)) {
                        addSectionColumns = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.addColumnsParamName, section);
                        if (addSectionColumns != null && addSectionColumns.size() > 0) {
                            addColumnsToDatatable(datatableName, addSectionColumns, scopingCriteriaEnum, isConstraintApproach, sectionId);
                        }
                    }
                }
            }
            if (scope != null) {
                if (scope.isJsonArray()) {
                    final JsonArray scopeArray = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.scopeParamName, element);
                    for (final JsonElement ele : scopeArray) {
                        final Long id = this.fromJsonHelper.extractLongNamed(DataTableApiConstant.idParamName, ele);
                        final JsonArray allowedValues = this.fromJsonHelper
                                .extractJsonArrayNamed(DataTableApiConstant.allowedValuesParamName, ele);
                        updateDataTableScope(datatableName, id, allowedValues);
                    }
                } else {
                    final Long id = this.fromJsonHelper.extractLongNamed(DataTableApiConstant.idParamName, scope);
                    final JsonArray allowedValues = this.fromJsonHelper.extractJsonArrayNamed(DataTableApiConstant.allowedValuesParamName,
                            scope);
                    updateDataTableScope(datatableName, id, allowedValues);
                }
            }
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("datatable");

            if (realCause.getMessage().toLowerCase().contains("unknown column")) {
                baseDataValidator.reset().parameter("name").failWithCode("does.not.exist");
            } else if (realCause.getMessage().toLowerCase().contains("can't drop")) {
                baseDataValidator.reset().parameter("name").failWithCode("does.not.exist");
            } else if (realCause.getMessage().toLowerCase().contains("duplicate column")) {
                baseDataValidator.reset().parameter("name").failWithCode("column.already.exists");
            }

            throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }
    }

    private void alterColumnsInDatatable(final String datatableName, final JsonArray changeColumns, final Long scopingCriteriaEnum,
            final Map<String, ResultsetColumnHeaderData> mapColumnNameDefinition, final boolean isConstraintApproach,
            final Long sectionId) {
        StringBuilder sqlBuilder = new StringBuilder("ALTER TABLE `" + datatableName + "`");
        final StringBuilder constrainBuilder = new StringBuilder();
        final Map<String, Long> codeMappings = new HashMap<>();
        final List<String> removeMappings = new ArrayList<>();
        final DatatableData datatableData = retrieveDatatable(datatableName);
        final Integer xRegisterTableId = datatableData.getId();
        updateScopeCriteriaEnum(scopingCriteriaEnum, xRegisterTableId);
        for (final JsonElement column : changeColumns) {
            // remove NULL values from column where mandatory is true
            removeNullValuesFromStringColumn(datatableName, column.getAsJsonObject(), mapColumnNameDefinition);

            parseDatatableColumnForUpdate(column.getAsJsonObject(), mapColumnNameDefinition, sqlBuilder, datatableName, constrainBuilder,
                    codeMappings, removeMappings, isConstraintApproach);
            final String name = (column.getAsJsonObject().has("name")) ? column.getAsJsonObject().get("name").getAsString() : null;
            final String newName = (column.getAsJsonObject().has("newName")) ? column.getAsJsonObject().get("newName").getAsString() : name;

            final String displayName = (column.getAsJsonObject().has("displayName"))
                    ? column.getAsJsonObject().get("displayName").getAsString()
                    : null;
            final StringBuilder updateSqlBuilder = new StringBuilder(
                    "UPDATE `x_registered_table_metadata` SET column_name = '" + newName + "' ");
            if (displayName != null) {
                updateSqlBuilder.append(", display_name = '" + displayName + "' ");
            }
            final Integer displayPosition = (column.getAsJsonObject().has("displayPosition"))
                    ? column.getAsJsonObject().get("displayPosition").getAsInt()
                    : null;

            if (displayPosition != null) {
                updateSqlBuilder.append(", order_position = '" + displayPosition + "' ");
            }
            final Boolean visible = (column.getAsJsonObject().has("visible")) ? column.getAsJsonObject().get("visible").getAsBoolean()
                    : null;

            if (visible != null) {

                updateSqlBuilder.append(", visible = '" + visible.compareTo(Boolean.FALSE) + "' ");
            }
            final Boolean mandatoryIfVisible = (column.getAsJsonObject().has("mandatoryIfVisible"))
                    ? column.getAsJsonObject().get("mandatoryIfVisible").getAsBoolean()
                    : null;

            if (mandatoryIfVisible != null) {
                updateSqlBuilder.append(", mandatory_if_visible = '" + mandatoryIfVisible.compareTo(Boolean.FALSE) + "' ");
            }
            updateSqlBuilder.append(", section_id = " + sectionId);
            updateSqlBuilder.append(" WHERE column_name = '" + name + "' AND registered_table_id = " + xRegisterTableId);

            this.jdbcTemplate.execute(updateSqlBuilder.toString());

        }
        updateXRegisteredDisplayRules(datatableName, changeColumns, false);
        // Remove the first comma, right after ALTER TABLE `datatable`
        final int indexOfFirstComma = sqlBuilder.indexOf(",");
        if (indexOfFirstComma != -1) {
            sqlBuilder = sqlBuilder.deleteCharAt(indexOfFirstComma);
        }
        sqlBuilder.append(constrainBuilder);
        try {
            this.jdbcTemplate.execute(sqlBuilder.toString());
            deleteColumnCodeMapping(removeMappings);
            registerColumnCodeMapping(codeMappings);
        } catch (final Exception e) {
            if (e.getMessage().contains("Error on rename")) { throw new PlatformServiceUnavailableException(
                    "error.msg.datatable.column.update.not.allowed", "One of the column name modification not allowed"); }
            // handle all other exceptions in here

            // check if exception message contains the
            // "invalid use of null value" SQL exception message
            // throw a 503 HTTP error -
            // PlatformServiceUnavailableException
            if (e.getMessage().toLowerCase().contains("invalid use of null value")) { throw new PlatformServiceUnavailableException(
                    "error.msg.datatable.column.update.not.allowed", "One of the data table columns contains null values"); }
        }
    }

    private void addColumnsToDatatable(final String datatableName, final JsonArray addColumns, final Long scopingCriteriaEnum,
            final boolean isConstraintApproach, final Long sectionId) {
        StringBuilder sqlBuilder = new StringBuilder("ALTER TABLE `" + datatableName + "`");
        final StringBuilder constrainBuilder = new StringBuilder();
        final Map<String, Long> codeMappings = new HashMap<>();
        for (final JsonElement column : addColumns) {
            parseDatatableColumnForAdd(column.getAsJsonObject(), sqlBuilder, datatableName.toLowerCase().replaceAll("\\s", "_"),
                    constrainBuilder, codeMappings, isConstraintApproach);
        }
        final String sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + datatableName + "'";
        final int xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);
        updateScopeCriteriaEnum(scopingCriteriaEnum, xResgisteredTableId);
        registerDatatableMetadata(datatableName, addColumns, sectionId);
        updateXRegisteredDisplayRules(datatableName, addColumns, false);
        // Remove the first comma, right after ALTER TABLE `datatable`
        final int indexOfFirstComma = sqlBuilder.indexOf(",");
        if (indexOfFirstComma != -1) {
            sqlBuilder = sqlBuilder.deleteCharAt(indexOfFirstComma);
        }
        sqlBuilder.append(constrainBuilder);
        this.jdbcTemplate.execute(sqlBuilder.toString());
        registerColumnCodeMapping(codeMappings);
    }

    private void updateScopeCriteriaEnum(final Long scopingCriteriaEnum, final Integer xRegisterTableId) {
        final String updateSql = "UPDATE `x_registered_table` SET scoping_criteria_enum = " + scopingCriteriaEnum + " where id = "
                + xRegisterTableId + "";
        this.jdbcTemplate.execute(updateSql);
    }

    private void updateTableDisplayName(final String displayName, final String xRegisterTableName) {
        if (displayName != null) {
            final String updateSql = "UPDATE `x_registered_table` SET registered_table_display_name = '" + displayName
                    + "' where registered_table_name = '" + xRegisterTableName + "'";
            this.jdbcTemplate.execute(updateSql);
        }

    }

    @Transactional
    @Override
    public void deleteDatatable(final String datatableName) {

        try {
            this.context.authenticatedUser();
            if (!isRegisteredDataTable(datatableName)) { throw new DatatableNotFoundException(datatableName); }
            validateDatatableName(datatableName);
            assertDataTableEmpty(datatableName);
            deregisterDatatable(datatableName);
            String[] sqlArray = null;
            if (this.configurationDomainService.isConstraintApproachEnabledForDatatables()) {
                final String deleteColumnCodeSql = "delete from x_table_column_code_mappings where column_alias_name like'"
                        + datatableName.toLowerCase().replaceAll("\\s", "_") + "_%'";
                sqlArray = new String[2];
                sqlArray[1] = deleteColumnCodeSql;
            } else {
                sqlArray = new String[1];
            }
            final String sql = "DROP TABLE `" + datatableName + "`";
            sqlArray[0] = sql;
            this.jdbcTemplate.batchUpdate(sqlArray);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("datatable");
            if (realCause.getMessage().contains("Unknown table")) {
                baseDataValidator.reset().parameter("datatableName").failWithCode("does.not.exist");
            }

            throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }
    }

    private void assertDataTableEmpty(final String datatableName) {
        final String sql = "select count(*) from `" + datatableName + "`";
        final int rowCount = this.jdbcTemplate.queryForObject(sql, Integer.class);
        if (rowCount != 0) { throw new GeneralPlatformDomainRuleException("error.msg.non.empty.datatable.cannot.be.deleted",
                "Non-empty datatable cannot be deleted."); }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateDatatableEntryOneToOne(final JsonCommand command) {

        final String dataTableName = command.entityName();
        final String appTable = queryForApplicationTableName(dataTableName);
        final String apptableIdentifier = getAppTableIdenfier(appTable, command);

        return updateDatatableEntry(dataTableName, apptableIdentifier, null, command);
    }

    @Transactional
    @Override
    public CommandProcessingResult updateDatatableEntryOneToMany(final JsonCommand command) {

        final String dataTableName = command.entityName();
        final String appTable = queryForApplicationTableName(dataTableName);
        final String apptableIdentifier = getAppTableIdenfier(appTable, command);
        final String datatableId = command.subentityId().toString();

        return updateDatatableEntry(dataTableName, apptableIdentifier, datatableId, command);
    }

    private CommandProcessingResult updateDatatableEntry(final String dataTableName, final String apptableIdentifier,
            final String datatableId, final JsonCommand command) {

        final String appTable = queryForApplicationTableName(dataTableName);
        final CommandProcessingResult commandProcessingResult = checkMainResourceExistsWithinScope(appTable, apptableIdentifier);

        final GenericResultsetData grs = retrieveDataTableGenericResultSetForUpdate(appTable, dataTableName, apptableIdentifier,
                datatableId);

        if (grs.hasNoEntries()) { throw new DatatableNotFoundException(dataTableName, apptableIdentifier); }

        if (grs.hasMoreThanOneEntry()) { throw new PlatformDataIntegrityException("error.msg.attempting.multiple.update",
                "Application table: " + dataTableName + " Foreign key id: " + apptableIdentifier); }

        final Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        final Map<String, String> dataParams = this.fromJsonHelper.extractDataMap(typeOfMap, command.json());

        String pkName = "id"; // 1:M datatable
        if (datatableId == null) {
            pkName = getFKField(appTable);
        } // 1:1 datatable

        final Map<String, Object> changes = getAffectedAndChangedColumns(grs, dataParams, pkName);

        if (!changes.isEmpty()) {
            String pkValue = apptableIdentifier;
            if (datatableId != null) {
                pkValue = datatableId;
            }
            final String sql = getUpdateSql(grs.getColumnHeaders(), dataTableName, pkName, pkValue, changes, appTable);
            logger.info("Update sql: " + sql);
            if (StringUtils.isNotBlank(sql)) {
                this.jdbcTemplate.update(sql);
                changes.put("locale", dataParams.get("locale"));
                changes.put("dateFormat", "yyyy-MM-dd");
            } else {
                logger.info("No Changes");
            }
        }

        return new CommandProcessingResultBuilder() //
                .withOfficeId(commandProcessingResult.getOfficeId()) //
                .withGroupId(commandProcessingResult.getGroupId()) //
                .withClientId(commandProcessingResult.getClientId()) //
                .withSavingsId(commandProcessingResult.getSavingsId()) //
                .withLoanId(commandProcessingResult.getLoanId()) //
                .withTransactionId(commandProcessingResult.getTransactionId()) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteDatatableEntries(final JsonCommand command) {

        final String dataTableName = command.entityName();
        final String appTable = queryForApplicationTableName(dataTableName);
        final String apptableIdentifier = getAppTableIdenfier(appTable, command);

        final CommandProcessingResult commandProcessingResult = checkMainResourceExistsWithinScope(appTable, apptableIdentifier);

        final String deleteOneToOneEntrySql = getDeleteEntriesSql(dataTableName, getFKField(appTable),
                getApptableIdentifier(appTable, apptableIdentifier));

        final int rowsDeleted = this.jdbcTemplate.update(deleteOneToOneEntrySql);
        if (rowsDeleted < 1) { throw new DatatableNotFoundException(dataTableName, apptableIdentifier); }

        return commandProcessingResult;
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteDatatableEntry(final JsonCommand command) {

        final String dataTableName = command.entityName();
        final String appTable = queryForApplicationTableName(dataTableName);
        final String apptableIdentifier = getAppTableIdenfier(appTable, command);
        final String datatableId = command.subentityId().toString();

        final CommandProcessingResult commandProcessingResult = checkMainResourceExistsWithinScope(appTable, apptableIdentifier);

        final String sql = getDeleteEntrySql(dataTableName, datatableId);

        this.jdbcTemplate.update(sql);
        return commandProcessingResult;
    }

    @Override
    public GenericResultsetData retrieveDataTableGenericResultSet(final String dataTableName, final String apptableIdentifier,
            final String order, final Long id) {

        final String appTable = queryForApplicationTableName(dataTableName);
        checkMainResourceExistsWithinScope(appTable, apptableIdentifier);

        final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(dataTableName);

        String sql = "";

        // id only used for reading a specific entry in a one to many datatable
        // (when updating)
        if (id == null) {
            sql = sql + "select * from `" + dataTableName + "` where " + getFKField(appTable) + " = "
                    + getApptableIdentifier(appTable, apptableIdentifier);
        } else {
            sql = sql + "select * from `" + dataTableName + "` where id = " + id;
        }

        final Integer registeredDataTableId = queryForDataTableId(dataTableName);
        List<SectionData> sectionedColumnList = null;
        sectionedColumnList = this.genericDataService.fetchSections(registeredDataTableId);
        if (sectionedColumnList != null && sectionedColumnList.size() > 0) {
            sectionedColumnList = SectionData.organizeList(columnHeaders, sectionedColumnList);
        }

        if (order != null) {
            sql = sql + " order by " + order;
        }

        final List<ResultsetRowData> result = fillDatatableResultSetDataRows(sql);
        final String registeredDataTableDisplayName = queryForApplicationTableDisplayName(dataTableName);
        final List<ResultSetColumnAndData> columnValueList = fillDatatableResultSetColumnAndvalues(sql);
        return new GenericResultsetData(columnHeaders, result, registeredDataTableDisplayName, sectionedColumnList, columnValueList);
    }

    private GenericResultsetData retrieveDataTableGenericResultSetForUpdate(final String appTable, final String dataTableName,
            final String apptableIdentifier, final String id) {

        final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(dataTableName);

        String sql = "";

        // id only used for reading a specific entry in a one to many datatable
        // (when updating)
        if (id == null) {
            sql = sql + "select * from `" + dataTableName + "` where " + getFKField(appTable) + " = "
                    + getApptableIdentifier(appTable, apptableIdentifier);
        } else {
            sql = sql + "select * from `" + dataTableName + "` where id = " + id;
        }

        final List<ResultsetRowData> result = fillDatatableResultSetDataRows(sql);

        final String registeredDataTableDisplayName = null;
        final List<SectionData> sectionedColumnList = null;
        final List<ResultSetColumnAndData> columnValueList = fillDatatableResultSetColumnAndvalues(sql);
        return new GenericResultsetData(columnHeaders, result, registeredDataTableDisplayName, sectionedColumnList, columnValueList);
    }

    private CommandProcessingResult checkMainResourceExistsWithinScope(final String appTable, final String apptableIdentifier) {

        final String sql = dataScopedSQL(appTable, apptableIdentifier);
        logger.info("data scoped sql: " + sql);
        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        if (!rs.next()) { throw new DatatableNotFoundException(appTable, apptableIdentifier); }

        final Long officeId = getLongSqlRowSet(rs, "officeId");
        final Long groupId = getLongSqlRowSet(rs, "groupId");
        final Long clientId = getLongSqlRowSet(rs, "clientId");
        final Long savingsId = getLongSqlRowSet(rs, "savingsId");
        final Long LoanId = getLongSqlRowSet(rs, "loanId");
        final Long entityId = getLongSqlRowSet(rs, "entityId");
        final String transactionId = rs.getString("transactionId");

        if (rs.next()
                && !appTable.equalsIgnoreCase(DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME)) { throw new DatatableSystemErrorException(
                        "System Error: More than one row returned from data scoping query"); }

        return new CommandProcessingResultBuilder() //
                .withOfficeId(officeId) //
                .withGroupId(groupId) //
                .withClientId(clientId) //
                .withSavingsId(savingsId) //
                .withLoanId(LoanId).withEntityId(entityId)//
                .withTransactionId(transactionId)//
                .build();
    }

    private Long getLongSqlRowSet(final SqlRowSet rs, final String column) {
        Long val = rs.getLong(column);
        if (val == 0) {
            val = null;
        }
        return val;
    }

    private String dataScopedSQL(final String appTable, final String apptableIdentifier) {
        /*
         * unfortunately have to, one way or another, be able to restrict data
         * to the users office hierarchy. Here, a few key tables are done. But
         * if additional fields are needed on other tables the same pattern
         * applies
         */

        final AppUser currentUser = this.context.authenticatedUser();
        /*
         * m_loan and m_savings_account are connected to an m_office thru either
         * an m_client or an m_group If both it means it relates to an m_client
         * that is in a group (still an m_client account)
         */

        final String scopedSQL = DataScopedSqlServiceFactory.getDataScopedSqlService(appTable).getDataScopedSql(currentUser,
                apptableIdentifier);

        if (scopedSQL == null) { throw new PlatformDataIntegrityException("error.msg.invalid.dataScopeCriteria",
                "Application Table: " + appTable + " not catered for in data Scoping"); }

        return scopedSQL;

    }

    private void validateAppTable(final String appTable) {

        if (appTable.equalsIgnoreCase("m_loan")) { return; }
        if (appTable.equalsIgnoreCase("m_savings_account")) { return; }
        if (appTable.equalsIgnoreCase("m_client")) { return; }
        if (appTable.equalsIgnoreCase("m_group")) { return; }
        if (appTable.equalsIgnoreCase("m_center")) { return; }
        if (appTable.equalsIgnoreCase("m_office")) { return; }
        if (appTable.equalsIgnoreCase("m_product_loan")) { return; }
        if (appTable.equalsIgnoreCase("m_savings_product")) { return; }
        if (appTable.equalsIgnoreCase(DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME)) { return; }
        if (appTable.equalsIgnoreCase(DataTableApiConstant.LOAN_APPLICATION_REFERENCE)) { return; }
        if (appTable.equalsIgnoreCase(DataTableApiConstant.VILLAGE)) { return; }
        if (appTable.equalsIgnoreCase(DataTableApiConstant.DISTRICT)) { return; }

        throw new PlatformDataIntegrityException("error.msg.invalid.application.table", "Invalid Application Table: " + appTable, "name",
                appTable);
    }

    private String mapToActualAppTable(final String appTable) {
        if (appTable.equalsIgnoreCase("m_center")) { return "m_group"; }
        return appTable;
    }

    private List<ResultsetRowData> fillDatatableResultSetDataRows(final String sql) {

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        final List<ResultsetRowData> resultsetDataRows = new ArrayList<>();

        final SqlRowSetMetaData rsmd = rs.getMetaData();

        while (rs.next()) {
            final List<String> columnValues = new ArrayList<>();
            final List<ResultSetColumnAndData> objectList = new ArrayList<>();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                final String columnName = rsmd.getColumnName(i + 1);
                final String columnValue = rs.getString(columnName);
                columnValues.add(columnValue);
                objectList.add(new ResultSetColumnAndData(columnName, columnValue));
            }

            final ResultsetRowData resultsetDataRow = ResultsetRowData.create(columnValues);
            resultsetDataRows.add(resultsetDataRow);
        }

        return resultsetDataRows;
    }

    private List<ResultSetColumnAndData> fillDatatableResultSetColumnAndvalues(final String sql) {

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        final List<ResultSetColumnAndData> columns = new ArrayList<>();

        final SqlRowSetMetaData rsmd = rs.getMetaData();

        while (rs.next()) {
            final List<ResultSetColumnAndData> objectList = new ArrayList<>();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                final String columnName = rsmd.getColumnName(i + 1);
                final String columnValue = rs.getString(columnName);
                objectList.add(new ResultSetColumnAndData(columnName, columnValue));
            }

            final ResultSetColumnAndData columnAndValueData = ResultSetColumnAndData.create(objectList);
            columns.add(columnAndValueData);
        }

        return columns;
    }

    private String queryForApplicationTableName(final String datatable) {
        final String sql = "SELECT application_table_name FROM x_registered_table where registered_table_name = '" + datatable + "'";

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        String applicationTableName = null;
        if (rs.next()) {
            applicationTableName = rs.getString("application_table_name");
        } else {
            throw new DatatableNotFoundException(datatable);
        }

        return applicationTableName;
    }

    private String queryForApplicationTableDisplayName(final String datatable) {
        final String sql = "SELECT registered_table_display_name FROM x_registered_table where registered_table_name = '" + datatable + "'";
        String applicationTableDisplayName = null;
        try {
            applicationTableDisplayName = this.jdbcTemplate.queryForObject(sql, String.class);
        } catch (final EmptyResultDataAccessException e) {}

        return applicationTableDisplayName;
    }

    private Integer queryForDataTableId(final String datatable) {
        final String sql = "SELECT id FROM x_registered_table where registered_table_name = '" + datatable + "'";
        Integer id = null;
        try {
            id = this.jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (final EmptyResultDataAccessException e) {}

        return id;
    }

    private String getFKField(final String applicationTableName) {
        final String fkColumnName = applicationTableName.substring(applicationTableName.indexOf('_') + 1) + "_id";
        return fkColumnName;
    }

    private String getApptableIdentifier(final String appTable, String apptableIdentifier) {
        if (appTable.equalsIgnoreCase(DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME)) {
            apptableIdentifier = "'" + apptableIdentifier + "'";
        }
        return apptableIdentifier;
    }

    private String getAddSql(final List<ResultsetColumnHeaderData> columnHeaders, final String datatable, final String fkName,
            final String apptableIdentifier, final Map<String, String> queryParams, final String appTable) {

        final Map<String, String> affectedColumns = getAffectedColumns(columnHeaders, queryParams, fkName);

        String pValueWrite = "";
        String addSql = "";
        final String singleQuote = "'";

        String insertColumns = "";
        String selectColumns = "";
        String columnName = "";
        String pValue = null;
        for (final ResultsetColumnHeaderData pColumnHeader : columnHeaders) {
            final String key = pColumnHeader.getColumnName();
            if (affectedColumns.containsKey(key)) {
                pValue = affectedColumns.get(key);
                if (StringUtils.isEmpty(pValue)) {
                    pValueWrite = "null";
                } else {
                    if ("bit".equalsIgnoreCase(pColumnHeader.getColumnType())) {
                        pValueWrite = BooleanUtils.toString(BooleanUtils.toBooleanObject(pValue), "1", "0", "null");
                    } else {
                        pValueWrite = singleQuote + this.genericDataService.replace(pValue, singleQuote, singleQuote + singleQuote)
                                + singleQuote;
                    }

                }
                columnName = "`" + key + "`";
                insertColumns += ", " + columnName;
                selectColumns += "," + pValueWrite + " as " + columnName;
            }
        }
        String id = "id";
        if (appTable.equals(DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME)) {
            id = fkName;
        }
        addSql = "insert into `" + datatable + "` (`" + fkName + "` " + insertColumns + ")" + " select "
                + getApptableIdentifier(appTable, apptableIdentifier) + " as " + id + " " + selectColumns;

        logger.info(addSql);

        return addSql;
    }

    /**
     * This method is used special for ppi cases Where the score need to be
     * computed
     *
     * @param columnHeaders
     * @param datatable
     * @param fkName
     * @param apptableIdentifier
     * @param queryParams
     * @return
     */
    public String getAddSqlWithScore(final List<ResultsetColumnHeaderData> columnHeaders, final String datatable, final String fkName,
            final String apptableIdentifier, final Map<String, String> queryParams) {

        final Map<String, String> affectedColumns = getAffectedColumns(columnHeaders, queryParams, fkName);

        String pValueWrite = "";
        String scoresId = " ";
        final String singleQuote = "'";

        String insertColumns = "";
        String selectColumns = "";
        String columnName = "";
        String pValue = null;
        for (final String key : affectedColumns.keySet()) {
            pValue = affectedColumns.get(key);

            if (StringUtils.isEmpty(pValue)) {
                pValueWrite = "null";
            } else {
                pValueWrite = singleQuote + this.genericDataService.replace(pValue, singleQuote, singleQuote + singleQuote) + singleQuote;

                scoresId += pValueWrite + " ,";

            }
            columnName = "`" + key + "`";
            insertColumns += ", " + columnName;
            selectColumns += "," + pValueWrite + " as " + columnName;
        }

        scoresId = scoresId.replaceAll(" ,$", "");

        final String vaddSql = "insert into `" + datatable + "` (`" + fkName + "` " + insertColumns + ", `score` )" + " select "
                + apptableIdentifier + " as id" + selectColumns
                + " , ( SELECT SUM( code_score ) FROM m_code_value WHERE m_code_value.id IN (" + scoresId + " ) ) as score";

        logger.info(vaddSql);

        return vaddSql;
    }

    private String getUpdateSql(final List<ResultsetColumnHeaderData> columnHeaders, final String datatable, final String keyFieldName,
            final String keyFieldValue, final Map<String, Object> changedColumns, final String appTable) {

        // just updating fields that have changed since pre-update read - though
        // its possible these values are different from the page the user was
        // looking at and even different from the current db values (if some
        // other update got in quick) - would need a version field for
        // completeness but its okay to take this risk with additional fields
        // data

        if (changedColumns.size() == 0) { return null; }

        String pValue = null;
        String pValueWrite = "";
        final String singleQuote = "'";
        boolean firstColumn = true;
        String sql = "update `" + datatable + "` ";
        for (final ResultsetColumnHeaderData pColumnHeader : columnHeaders) {
            final String key = pColumnHeader.getColumnName();
            if (changedColumns.containsKey(key)) {
                if (firstColumn) {
                    sql += " set ";
                    firstColumn = false;
                } else {
                    sql += ", ";
                }

                pValue = (String) changedColumns.get(key);
                if (StringUtils.isEmpty(pValue)) {
                    pValueWrite = "null";
                } else {
                    if ("bit".equalsIgnoreCase(pColumnHeader.getColumnType())) {
                        pValueWrite = BooleanUtils.toString(BooleanUtils.toBooleanObject(pValue), "1", "0", "null");
                    } else {
                        pValueWrite = singleQuote + this.genericDataService.replace(pValue, singleQuote, singleQuote + singleQuote)
                                + singleQuote;
                    }
                }
                sql += "`" + key + "` = " + pValueWrite;
            }
        }
        sql += " where " + keyFieldName + " = " + getApptableIdentifier(appTable, keyFieldValue);

        return sql;
    }

    private Map<String, Object> getAffectedAndChangedColumns(final GenericResultsetData grs, final Map<String, String> queryParams,
            final String fkName) {

        final Map<String, String> affectedColumns = getAffectedColumns(grs.getColumnHeaders(), queryParams, fkName);
        final Map<String, Object> affectedAndChangedColumns = new HashMap<>();

        for (final String key : affectedColumns.keySet()) {
            final String columnValue = affectedColumns.get(key);
            final String colType = grs.getColTypeOfColumnNamed(key);
            if (columnChanged(key, columnValue, colType, grs)) {
                affectedAndChangedColumns.put(key, columnValue);
            }
        }

        return affectedAndChangedColumns;
    }

    private boolean columnChanged(final String key, final String keyValue, final String colType, final GenericResultsetData grs) {

        final List<String> columnValues = grs.getData().get(0).getRow();

        String columnValue = null;
        for (int i = 0; i < grs.getColumnHeaders().size(); i++) {

            if (key.equals(grs.getColumnHeaders().get(i).getColumnName())) {
                columnValue = columnValues.get(i);

                if (notTheSame(columnValue, keyValue, colType)) { return true; }
                return false;
            }
        }

        throw new PlatformDataIntegrityException("error.msg.invalid.columnName", "Parameter Column Name: " + key + " not found");
    }

    public Map<String, String> getAffectedColumns(final List<ResultsetColumnHeaderData> columnHeaders,
            final Map<String, String> queryParams, final String keyFieldName) {

        final String dateFormat = queryParams.get("dateFormat");
        Locale clientApplicationLocale = null;
        final String localeQueryParam = queryParams.get("locale");
        if (!(StringUtils.isBlank(localeQueryParam))) {
            clientApplicationLocale = new Locale(queryParams.get("locale"));
        }

        final String underscore = "_";
        final String space = " ";
        String pValue = null;
        String queryParamColumnUnderscored;
        String columnHeaderUnderscored;
        boolean notFound;

        final Map<String, String> affectedColumns = new HashMap<>();
        final Set<String> keys = queryParams.keySet();
        for (final String key : keys) {
            // ignores id and foreign key fields
            // also ignores locale and dateformat fields that are used for
            // validating numeric and date data
            if (!((key.equalsIgnoreCase("id")) || (key.equalsIgnoreCase(keyFieldName)) || (key.equals("locale"))
                    || (key.equals("dateFormat")))) {
                notFound = true;
                // matches incoming fields with and without underscores (spaces
                // and underscores considered the same)
                queryParamColumnUnderscored = this.genericDataService.replace(key, space, underscore);
                for (final ResultsetColumnHeaderData columnHeader : columnHeaders) {
                    if (notFound) {
                        columnHeaderUnderscored = this.genericDataService.replace(columnHeader.getColumnName(), space, underscore);
                        if (queryParamColumnUnderscored.equalsIgnoreCase(columnHeaderUnderscored)) {
                            pValue = queryParams.get(key);
                            pValue = validateColumn(columnHeader, pValue, dateFormat, clientApplicationLocale);
                            affectedColumns.put(columnHeader.getColumnName(), pValue);
                            notFound = false;
                        }
                    }

                }
                if (notFound) { throw new PlatformDataIntegrityException("error.msg.column.not.found", "Column: " + key + " Not Found"); }
            }
        }
        return affectedColumns;
    }

    private String validateColumn(final ResultsetColumnHeaderData columnHeader, final String pValue, final String dateFormat,
            final Locale clientApplicationLocale) {

        String paramValue = pValue;
        if (columnHeader.isDateDisplayType() || columnHeader.isDateTimeDisplayType() || columnHeader.isIntegerDisplayType()
                || columnHeader.isDecimalDisplayType() || columnHeader.isBooleanDisplayType()) {
            // only trim if string is not empty and is not null.
            // throws a NULL pointer exception if the check below is not applied
            paramValue = StringUtils.isNotEmpty(paramValue) ? paramValue.trim() : paramValue;
        }

        if (StringUtils.isEmpty(paramValue) && columnHeader.isMandatory()) {

            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.column.mandatory", "Mandatory",
                    columnHeader.getColumnName());
            dataValidationErrors.add(error);
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }

        if (StringUtils.isNotEmpty(paramValue)) {

            if (columnHeader.hasColumnValues()) {
                if (columnHeader.isCodeValueDisplayType()) {

                    if (columnHeader.isColumnValueNotAllowed(paramValue)) {
                        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                        final ApiParameterError error = ApiParameterError.parameterError("error.msg.invalid.columnValue",
                                "Value not found in Allowed Value list", columnHeader.getColumnName(), paramValue);
                        dataValidationErrors.add(error);
                        throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                                dataValidationErrors);
                    }

                    return paramValue;
                } else if (columnHeader.isCodeLookupDisplayType()) {

                    final Integer codeLookup = Integer.valueOf(paramValue);
                    if (columnHeader.isColumnCodeNotAllowed(codeLookup)) {
                        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                        final ApiParameterError error = ApiParameterError.parameterError("error.msg.invalid.columnValue",
                                "Value not found in Allowed Value list", columnHeader.getColumnName(), paramValue);
                        dataValidationErrors.add(error);
                        throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                                dataValidationErrors);
                    }

                    return paramValue;
                } else {
                    throw new PlatformDataIntegrityException("error.msg.invalid.columnType.", "Code: " + columnHeader.getColumnName()
                            + " - Invalid Type " + columnHeader.getColumnType() + " (neither varchar nor int)");
                }
            }

            if (columnHeader.isDateDisplayType()) {
                final LocalDate tmpDate = JsonParserHelper.convertFrom(paramValue, columnHeader.getColumnName(), dateFormat,
                        clientApplicationLocale);
                if (tmpDate == null) {
                    paramValue = null;
                } else {
                    paramValue = tmpDate.toString();
                }
            } else if (columnHeader.isDateTimeDisplayType()) {
                final LocalDateTime tmpDateTime = JsonParserHelper.convertDateTimeFrom(paramValue, columnHeader.getColumnName(), dateFormat,
                        clientApplicationLocale);
                if (tmpDateTime == null) {
                    paramValue = null;
                } else {
                    paramValue = tmpDateTime.toString();
                }
            } else if (columnHeader.isIntegerDisplayType()) {
                final Integer tmpInt = this.helper.convertToInteger(paramValue, columnHeader.getColumnName(), clientApplicationLocale);
                if (tmpInt == null) {
                    paramValue = null;
                } else {
                    paramValue = tmpInt.toString();
                }
            } else if (columnHeader.isDecimalDisplayType()) {
                final BigDecimal tmpDecimal = this.helper.convertFrom(paramValue, columnHeader.getColumnName(), clientApplicationLocale);
                if (tmpDecimal == null) {
                    paramValue = null;
                } else {
                    paramValue = tmpDecimal.toString();
                }
            } else if (columnHeader.isBooleanDisplayType()) {

                final Boolean tmpBoolean = BooleanUtils.toBooleanObject(paramValue);
                if (tmpBoolean == null) {
                    final ApiParameterError error = ApiParameterError
                            .parameterError(
                                    "validation.msg.invalid.boolean.format", "The parameter " + columnHeader.getColumnName()
                                            + " has value: " + paramValue + " which is invalid boolean value.",
                                    columnHeader.getColumnName(), paramValue);
                    final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                    dataValidationErrors.add(error);
                    throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                            dataValidationErrors);
                }
                paramValue = tmpBoolean.toString();
            } else if (columnHeader.isString()) {
                if (paramValue.length() > columnHeader.getColumnLength()) {
                    final ApiParameterError error = ApiParameterError.parameterError(
                            "validation.msg.datatable.entry.column.exceeds.maxlength",
                            "The column `" + columnHeader.getColumnName() + "` exceeds its defined max-length ",
                            columnHeader.getColumnName(), paramValue);
                    final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                    dataValidationErrors.add(error);
                    throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                            dataValidationErrors);
                }
            }
        }

        return paramValue;
    }

    private String getDeleteEntriesSql(final String datatable, final String FKField, final String apptableIdentifier) {

        return "delete from `" + datatable + "` where `" + FKField + "` = " + apptableIdentifier;

    }

    private String getDeleteEntrySql(final String datatable, final String datatableId) {

        return "delete from `" + datatable + "` where `id` = " + datatableId;

    }

    private boolean notTheSame(final String currValue, final String pValue, final String colType) {
        if (StringUtils.isEmpty(currValue) && StringUtils.isEmpty(pValue)) { return false; }

        if (StringUtils.isEmpty(currValue)) { return true; }

        if (StringUtils.isEmpty(pValue)) { return true; }

        if ("DECIMAL".equalsIgnoreCase(colType)) {
            final BigDecimal currentDecimal = BigDecimal.valueOf(Double.valueOf(currValue));
            final BigDecimal newDecimal = BigDecimal.valueOf(Double.valueOf(pValue));

            return currentDecimal.compareTo(newDecimal) != 0;
        }

        if (currValue.equals(pValue)) { return false; }

        return true;
    }

    @Override
    public ScopeOptionsData retriveAllScopeOptions() {
        final List<ScopeCriteriaData> loan = new ArrayList<>();
        final List<ScopeCriteriaData> savings = new ArrayList<>();
        final List<ScopeCriteriaData> client = new ArrayList<>();
        final Collection<EnumOptionData> dataTableScopes = DataTableScopes.entityTypeOptions();
        for (final EnumOptionData dataTableScope : dataTableScopes) {
            if (DataTableScopes.fromInt(dataTableScope.getId().intValue()).isLoanProduct()) {
                final List<AllowedValueOptions> allowedValueOptions = new ArrayList<>();
                final Collection<LoanProductData> loanProductDatas = this.loanProductReadPlatformService.retrieveAllLoanProducts();
                for (final LoanProductData loanProductData : loanProductDatas) {
                    allowedValueOptions.add(AllowedValueOptions.createNew(loanProductData.getId(), loanProductData.getName()));
                }
                loan.add(ScopeCriteriaData.createNew(dataTableScope.getId(), dataTableScope.getCode(), dataTableScope.getValue(),
                        allowedValueOptions));
            }
            if (DataTableScopes.fromInt(dataTableScope.getId().intValue()).isSavingsProduct()) {
                final List<AllowedValueOptions> allowedValueOptions = new ArrayList<>();
                final Collection<SavingsProductData> savingsProductDatas = this.savingsProductReadPlatformService.retrieveAll();
                for (final SavingsProductData savingsProductData : savingsProductDatas) {
                    allowedValueOptions.add(AllowedValueOptions.createNew(savingsProductData.getId(), savingsProductData.getName()));
                }
                savings.add(ScopeCriteriaData.createNew(dataTableScope.getId(), dataTableScope.getCode(), dataTableScope.getValue(),
                        allowedValueOptions));
            }
            if (DataTableScopes.fromInt(dataTableScope.getId().intValue()).isClientLegalForm()) {
                final List<AllowedValueOptions> allowedValueOptions = new ArrayList<>();
                final Collection<EnumOptionData> clientLegalFormOptions = LegalForm.legalFormTypeOptions();
                for (final EnumOptionData clientLegalFormOption : clientLegalFormOptions) {
                    allowedValueOptions.add(AllowedValueOptions.createNew(clientLegalFormOption.getId(), clientLegalFormOption.getValue()));
                }
                client.add(ScopeCriteriaData.createNew(dataTableScope.getId(), dataTableScope.getCode(), dataTableScope.getValue(),
                        allowedValueOptions));
            }
            if (DataTableScopes.fromInt(dataTableScope.getId().intValue()).isClientType()) {
                final List<AllowedValueOptions> allowedValueOptions = new ArrayList<>();
                final Collection<CodeValueData> clientTypeOptions = this.codeValueReadPlatformService
                        .retrieveCodeValuesByCode(ClientApiConstants.CLIENT_TYPE);
                for (final CodeValueData clientTypeOption : clientTypeOptions) {
                    allowedValueOptions.add(AllowedValueOptions.createNew(clientTypeOption.getId(), clientTypeOption.getName()));
                }
                client.add(ScopeCriteriaData.createNew(dataTableScope.getId(), dataTableScope.getCode(), dataTableScope.getValue(),
                        allowedValueOptions));
            }

            if (DataTableScopes.fromInt(dataTableScope.getId().intValue()).isClientClassification()) {
                final List<AllowedValueOptions> allowedValueOptions = new ArrayList<>();
                final Collection<CodeValueData> clientClassificationOptions = this.codeValueReadPlatformService
                        .retrieveCodeValuesByCode(ClientApiConstants.CLIENT_CLASSIFICATION);
                for (final CodeValueData clientClassificationOption : clientClassificationOptions) {
                    allowedValueOptions
                            .add(AllowedValueOptions.createNew(clientClassificationOption.getId(), clientClassificationOption.getName()));
                }
                client.add(ScopeCriteriaData.createNew(dataTableScope.getId(), dataTableScope.getCode(), dataTableScope.getValue(),
                        allowedValueOptions));
            }
        }

        return new ScopeOptionsData(loan, savings, client);
    }

    private String getAppTableIdenfier(final String appTable, final JsonCommand command) {
        String appTableIdenfier = "";
        if (appTable.equalsIgnoreCase(DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME)) {
            appTableIdenfier = command.getTransactionId();
        } else {
            appTableIdenfier = command.entityId().toString();
        }
        return appTableIdenfier;
    }

    private Long registerDataTableSectionData(final String dataTableName, final JsonElement section) {
        assertDataTableExists(dataTableName);
        String sql = null;
        String query = null;
        Long sectionId;
        String sectionQuery = null;

        sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + dataTableName + "'";
        final int xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);

        final String displayName = this.fromJsonHelper.extractStringNamed(DataTableApiConstant.displayNameParamName, section);
        final Long displayPosition = this.fromJsonHelper.extractLongNamed(DataTableApiConstant.displayPositionParamName, section);
        query = "insert into f_registered_table_section (registered_table_id, display_name, display_position) values ("
                + xResgisteredTableId + ", " + "'" + displayName + "', " + displayPosition + ")";
        this.jdbcTemplate.execute(query);
        sectionQuery = "select section.id from f_registered_table_section section INNER JOIN x_registered_table rt ON section.registered_table_id = rt.id and rt.id = "
                + xResgisteredTableId + " where section.display_name =";
        sectionQuery = sectionQuery + " '" + displayName + "'";
        sectionId = this.jdbcTemplate.queryForLong(sectionQuery);
        return sectionId;
    }

    private void removeSection(final String dataTableName, final JsonElement section) {
        assertDataTableExists(dataTableName);
        String sql = null;
        String query = null;

        sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + dataTableName + "'";
        final int xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);

        final String displayName = section.getAsString();
        query = "delete from f_registered_table_section where registered_table_id = " + xResgisteredTableId + " and  display_name = '"
                + displayName + "'";
        this.jdbcTemplate.execute(query);
    }

    private void reOrderSection(final String dataTableName, final JsonElement section) {
        assertDataTableExists(dataTableName);
        String sql = null;
        String query = null;

        sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + dataTableName + "'";
        final int xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);

        final String displayName = this.fromJsonHelper.extractStringNamed(DataTableApiConstant.displayNameParamName, section);
        final Long displayPosition = this.fromJsonHelper.extractLongNamed(DataTableApiConstant.displayPositionParamName, section);
        query = "update f_registered_table_section set display_position = " + displayPosition + " where registered_table_id = "
                + xResgisteredTableId + " and  display_name = '" + displayName + "'";
        this.jdbcTemplate.execute(query);
    }

    private Long retrieveSectionId(final String dataTableName, final String sectionName) {
        String sql = null;
        final String query = null;
        Long sectionId;
        String sectionQuery = null;

        sql = "Select xrt.id from x_registered_table xrt where xrt.registered_table_name = '" + dataTableName + "'";
        final int xResgisteredTableId = this.jdbcTemplate.queryForInt(sql);

        sectionQuery = "select section.id from f_registered_table_section section INNER JOIN x_registered_table rt ON section.registered_table_id = rt.id and rt.id = "
                + xResgisteredTableId + "  where section.display_name =";
        sectionQuery = sectionQuery + " '" + sectionName + "'";
        sectionId = this.jdbcTemplate.queryForLong(sectionQuery);
        return sectionId;
    }

}