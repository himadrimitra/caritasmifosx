package com.finflux.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
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
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.domain.Role;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.task.api.TaskApiConstants;
import com.finflux.task.data.LoanProductTaskSummaryData;
import com.finflux.task.data.TaskActionLogData;
import com.finflux.task.data.TaskActionType;
import com.finflux.task.data.TaskActivityData;
import com.finflux.task.data.TaskActivityType;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.data.TaskInfoData;
import com.finflux.task.data.TaskNoteData;
import com.finflux.task.data.TaskPriority;
import com.finflux.task.data.TaskStatusType;
import com.finflux.task.data.TaskSummaryData;
import com.finflux.task.data.TaskTemplateData;
import com.finflux.task.data.TaskType;
import com.finflux.task.data.WorkFlowSummaryData;
import com.finflux.task.exception.TaskNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class TaskPlatformReadServiceImpl implements TaskPlatformReadService {

    private final static Logger logger = LoggerFactory.getLogger(TaskPlatformReadServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final TaskIdMapper taskIdMapper = new TaskIdMapper();
    private final TaskNoteDataMapper noteDataMapper = new TaskNoteDataMapper();
    private final TaskActionLogDataMapper actionLogDataMapper = new TaskActionLogDataMapper();
    private final RoleDataMapper roleDataMapper = new RoleDataMapper();
    private final NamedParameterJdbcTemplate namedParameterjdbcTemplate;
    private final PaginationHelper<TaskInfoData> paginationHelper = new PaginationHelper<>();;

    @Autowired
    public TaskPlatformReadServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context,
            final OfficeReadPlatformService officeReadPlatformService, final StaffReadPlatformService staffReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.context = context;
        this.officeReadPlatformService = officeReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
    }

    @Override
    public TaskTemplateData retrieveTemplate(Long officeId, boolean staffInSelectedOfficeOnly) {
        this.context.authenticatedUser();

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);

        final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

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
        return TaskTemplateData.template(defaultOfficeId, offices, staffOptions);
    }

    private Long defaultToUsersOfficeIfNull(final Long officeId) {
        Long defaultOfficeId = officeId;
        if (defaultOfficeId == null) {
            defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
        }
        return defaultOfficeId;
    }

    @Override
    public List<Long> getChildTaskConfigIds(final Long taskConfigId) {
        return this.jdbcTemplate.query("SELECT tc.id FROM f_task_config tc WHERE tc.parent_id = ? ORDER BY tc.task_config_order ASC ",
                new ParameterizedRowMapper<Long>() {

                    @SuppressWarnings("unused")
                    @Override
                    public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                        return rs.getLong(1);
                    }
                }, taskConfigId);
    }

    @Override
    public TaskExecutionData getTaskDetailsByEntityTypeAndEntityId(final TaskEntityType taskEntityType, final Long entityId) {
        try {
            TaskDataMapper rm = new TaskDataMapper();
            if (entityId != null) {
                final String sql = "SELECT " + rm.schema() + " WHERE t.entity_type = ? AND t.entity_id = ?  AND t.parent_id is null";
                return this.jdbcTemplate.queryForObject(sql, rm, taskEntityType.getValue(), entityId);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public TaskExecutionData getTaskDetails(Long taskId) {
        try {
            TaskDataMapper rm = new TaskDataMapper();
            final String sql = "SELECT " + rm.schema() + " WHERE t.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, rm, taskId);
        }catch (EmptyResultDataAccessException e){
            throw new TaskNotFoundException(taskId);
        }
    }

    @Override
    public List<TaskExecutionData> getTaskChildren(final Long parentTaskId) {
        TaskDataMapper rm = new TaskDataMapper();
        final String sql = "SELECT " + rm.schema() + " WHERE t.parent_id = ? order by t.task_order ASC ";
        return this.jdbcTemplate.query(sql, rm, parentTaskId);
    }

    @Override
    public List<Long> getChildTasksByOrder(Long parentTaskId, int orderId) {
        String sql = "SELECT " + taskIdMapper.schema() + "  WHERE task.parent_id = ? and task.task_order = ? ";
        return this.jdbcTemplate.query(sql, taskIdMapper, parentTaskId, orderId);
    }

    private static final class TaskIdMapper implements RowMapper<Long> {

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("task.id AS taskId ");
            sqlBuilder.append("FROM f_task task  ");

            return sqlBuilder.toString();
        }

        @Override
        public Long mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskId");
            return id;
        }
    }

    private static final class TaskDataMapper implements RowMapper<TaskExecutionData> {

        public String schema() {
            final StringBuilder sb = new StringBuilder(400);
            sb.append("t.id AS taskId ");
            sb.append(",t.parent_id AS taskParentId ");
            sb.append(",t.name as taskName ");
            sb.append(",t.short_name AS taskShortName ");
            sb.append(",t.entity_type AS taskEntityTypeId ");
            sb.append(",t.entity_id AS taskEntityId ");
            sb.append(",t.`status` AS taskStatusId ");
            sb.append(",t.priority AS taskPriorityId ");
            sb.append(",t.due_date AS taskDueDate ");
            sb.append(",t.task_type AS taskType ");
            sb.append(",t.current_action AS taskCurrentActionId ");
            sb.append(",t.created_date AS taskCreatedOn ");
            sb.append(",appuser.id AS taskAssignedToId ");
            sb.append(",CONCAT(appuser.firstname,' ',appuser.lastname) AS taskAssignedTo ");
            sb.append(",t.task_order AS taskOrder ");
            sb.append(",t.criteria_id AS taskCriteriaId ");
            sb.append(",t.approval_logic AS taskApprovalLogic ");
            sb.append(",t.rejection_logic AS taskRejectionLogic ");
            sb.append(",t.config_values AS taskConfigValues ");
            sb.append(",c.id AS taskClientId ");
            sb.append(",c.display_name AS taskClientName ");
            sb.append(",o.id AS taskOfficeId ");
            sb.append(",o.name AS taskOfficeName ");
            sb.append(",t.action_group_id AS taskActionGroupId ");
            sb.append(",t.criteria_result AS taskCriteriaResult ");
            sb.append(",t.criteria_action AS taskCriteriaActionId ");
            sb.append(",t.task_config_id AS taskConfigId ");
            sb.append(",t.parent_id AS parentTaskId ");
            sb.append(",t.action_group_id AS taskActionGroupId ");
            sb.append(",t.description AS description ");
            sb.append(",ta.id AS taskActivityId ");
            sb.append(",ta.name AS taskActivityName ");
            sb.append(",ta.identifier AS taskActivityIdentifier ");
            sb.append(",ta.config_values AS taskActivityConfigValues ");
            sb.append(",ta.supported_actions AS taskActivitySupportedActions ");
            sb.append(",ta.`type` AS taskActivityTypeId ");
            sb.append("FROM f_task t ");
            sb.append("LEFT JOIN f_task_activity ta ON ta.id = t.task_activity_id ");
            sb.append("LEFT JOIN m_appuser appuser ON appuser.id = t.assigned_to ");
            sb.append("LEFT JOIN m_client c ON c.id = t.client_id ");
            sb.append("LEFT JOIN m_office o ON o.id = t.office_id ");
            return sb.toString();
        }

        @Override
        public TaskExecutionData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long taskId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskId");
            if (taskId == null) { return null; }
            final Long taskParentId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskParentId");
            final String taskName = rs.getString("taskName");
            final String taskShortName = rs.getString("taskShortName");
            final Integer taskEntityTypeId = JdbcSupport.getIntegeActualValue(rs, "taskEntityTypeId");
            EnumOptionData taskEntityType = null;
            if (taskEntityTypeId != null && taskEntityTypeId > 0) {
                taskEntityType = TaskEntityType.fromInt(taskEntityTypeId).getEnumOptionData();
            }
            final Long taskEntityId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskEntityId");
            final Integer taskStatusId = JdbcSupport.getIntegeActualValue(rs, "taskStatusId");
            EnumOptionData taskStatus = null;
            List<EnumOptionData> taskPossibleActions = null;
            if (taskEntityId != null && taskEntityId > 0) {
                final TaskStatusType obj = TaskStatusType.fromInt(taskStatusId);
                taskStatus = obj.getEnumOptionData();
                taskPossibleActions = obj.getPossibleActionsEnumOption();
            }

            final Integer taskPriorityId = JdbcSupport.getIntegeActualValue(rs, "taskPriorityId");
            EnumOptionData taskPriority = null;
            if (taskPriorityId != null && taskPriorityId > 0) {
                taskPriority = TaskPriority.fromInt(taskPriorityId).getEnumOptionData();
            }
            final Date taskDueDate = rs.getTimestamp("taskDueDate");
            final Integer taskTypeId = JdbcSupport.getIntegeActualValue(rs, "taskType");
            EnumOptionData taskType = null;
            if (taskTypeId != null && taskTypeId >= 0) {
                taskType = TaskType.fromInt(taskTypeId).getEnumOptionData();
            }
            final Integer taskCurrentActionId = JdbcSupport.getIntegeActualValue(rs, "taskCurrentActionId");
            EnumOptionData taskCurrentAction = null;
            if (taskCurrentActionId != null && taskCurrentActionId > 0) {
                taskCurrentAction = TaskActionType.fromInt(taskCurrentActionId).getEnumOptionData();
            }
            final Long taskAssignedToId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskAssignedToId");
            final String taskAssignedTo = rs.getString("taskAssignedTo");
            final Integer taskOrder = JdbcSupport.getIntegeActualValue(rs, "taskOrder");
            final Long taskCriteriaId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskCriteriaId");
            final String taskApprovalLogic = rs.getString("taskApprovalLogic");
            final String taskRejectionLogic = rs.getString("taskRejectionLogic");
            final String taskConfigValuesStr = rs.getString("taskConfigValues");
            final String description = rs.getString("description");
            Map<String, String> taskConfigValues = null;
            if (taskConfigValuesStr != null) {
                taskConfigValues = new Gson().fromJson(taskConfigValuesStr, new TypeToken<HashMap<String, String>>() {}.getType());
            }
            final Long taskClientId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskClientId");
            final String taskClientName = rs.getString("taskClientName");
            final Long taskOfficeId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskOfficeId");
            final String taskOfficeName = rs.getString("taskOfficeName");
            final Long taskActionGroupId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskActionGroupId");
            final String criteriaResultStr = rs.getString("taskCriteriaResult");
            EligibilityResult taskCriteriaResult = null;
            if (criteriaResultStr != null) {
                taskCriteriaResult = new Gson().fromJson(criteriaResultStr, new TypeToken<EligibilityResult>() {}.getType());
            }
            final Integer taskCriteriaActionId = JdbcSupport.getIntegeActualValue(rs, "taskCriteriaActionId");
            final LocalDate taskCreatedOn = JdbcSupport.getLocalDate(rs, "taskCreatedOn");
            final TaskExecutionData taskData = TaskExecutionData.instance(taskId, taskParentId, taskName, taskShortName, taskEntityType,
                    taskEntityId, taskStatus, taskPriority, taskDueDate, taskCurrentAction, taskAssignedToId, taskAssignedTo, taskOrder,
                    taskCriteriaId, taskApprovalLogic, taskRejectionLogic, taskConfigValues, taskClientId, taskClientName, taskOfficeId,
                    taskOfficeName, taskActionGroupId, taskCriteriaResult, taskCriteriaActionId, taskPossibleActions, taskType,
                    taskCreatedOn, description);

            final Long taskActivityId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskActivityId");
            if (taskActivityId != null) {
                final String taskActivityName = rs.getString("taskActivityName");
                final String taskActivityIdentifier = rs.getString("taskActivityIdentifier");
                final Integer taskActivityTypeId = JdbcSupport.getIntegeActualValue(rs, "taskActivityTypeId");
                EnumOptionData taskActivityType = null;
                if (taskActivityTypeId != null) {
                    taskActivityType = TaskActivityType.fromInt(taskActivityTypeId).getEnumOptionData();
                }
                final TaskActivityData taskActivityData = TaskActivityData.instance(taskActivityId, taskActivityName,
                        taskActivityIdentifier, taskActivityType);
                taskData.setTaskActivity(taskActivityData);
            }

            return taskData;
        }
    }

    @SuppressWarnings({ "unused", "null" })
    @Override
    public List<LoanProductData> retrieveLoanProductTaskSummary(final Long loanProductId, final Long officeId) {
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder
                .append("SELECT DISTINCT co.id, lp.id AS loanProductId,lp.name AS loanProductName,tc.id AS workFlowId, tc.name AS workFlowName ");
        sqlBuilder.append(",co.id AS officeId, co.name AS officeName, st.status AS taskStatus ");
        sqlBuilder.append(",st.id AS taskId, st.name AS taskName,st.short_name AS taskShortName ");
        sqlBuilder.append(",SUM(IF(st.status BETWEEN 2 AND 6,1,0)) AS noOfCount ");
        sqlBuilder.append("FROM f_task_config tc ");
        sqlBuilder.append("JOIN f_task_config_entity_type_mapping tcm ON tcm.task_config_id = tc.id AND tcm.entity_type = 1 ");
        sqlBuilder.append("JOIN m_product_loan lp ON lp.id = tcm.entity_id ");
        sqlBuilder.append("JOIN f_task pt ON pt.task_config_id = tc.id AND pt.entity_type = 1 ");
        sqlBuilder.append("JOIN f_loan_application_reference lar ON lar.id = pt.entity_id AND lp.id = lar.loan_product_id ");
        sqlBuilder.append("JOIN m_client c ON c.id = pt.client_id ");
        sqlBuilder.append("JOIN m_office o ON o.id = pt.office_id ");
        sqlBuilder.append("JOIN m_office co ON co.hierarchy LIKE CONCAT(o.hierarchy, '%') AND co.id = c.office_id ");
        sqlBuilder.append("JOIN f_task st ON st.parent_id = pt.id ");
        sqlBuilder.append("WHERE st.status BETWEEN 1 AND 7 ");
        if (officeId != null) {
            sqlBuilder.append(" AND o.id = ").append(officeId).append(" ");
        }
        if (loanProductId != null) {
            sqlBuilder.append("AND lp.id = ").append(loanProductId).append(" ");
        }
        sqlBuilder.append("GROUP BY lp.id,tc.id,co.id,st.status,st.name,co.id,o.id ");
        sqlBuilder.append("ORDER BY lp.name,tc.id,co.id,st.task_order,st.status ");
        final List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sqlBuilder.toString());
        final List<LoanProductData> loanProducts = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            LoanProductData loanProduct = null;
            LoanProductTaskSummaryData loanProductWorkFlowSummary = null;
            List<OfficeData> offices = null;
            OfficeData office = null;
            List<WorkFlowSummaryData> workFlowSummaries = null;
            WorkFlowSummaryData workFlowSummary = null;
            List<TaskSummaryData> taskSummaries = null;
            TaskSummaryData taskSummary = null;

            for (final Map<String, Object> l : list) {
                final Integer taskStatusId = Integer.parseInt(l.get("taskStatus").toString());
                if (!(TaskStatusType.fromInt(taskStatusId).getValue() > 7)) {
                    final Long lpId = (Long) l.get("loanProductId");
                    final String loanProductName = (String) l.get("loanProductName");
                    final Long oId = (Long) l.get("officeId");
                    final String officeName = (String) l.get("officeName");
                    final Long workFlowId = (Long) l.get("workFlowId");
                    final String workFlowName = (String) l.get("workFlowName");
                    final Long taskId = (Long) l.get("taskId");
                    final String taskName = (String) l.get("taskName");
                    final String taskShortName = (String) l.get("taskShortName");
                    final Long noOfCount = Long.parseLong(l.get("noOfCount").toString());
                    final String taskStatus = TaskStatusType.fromInt(taskStatusId).toString();

                    /**
                     * Product
                     */
                    Boolean isLoanProductData = false;
                    for (final LoanProductData lp : loanProducts) {
                        if (lp.getId() == lpId) {
                            isLoanProductData = true;
                            break;
                        }
                    }
                    if (!isLoanProductData) {
                        loanProductWorkFlowSummary = new LoanProductTaskSummaryData();
                        loanProduct = LoanProductData.lookup(lpId, loanProductName);
                        loanProduct.setLoanProductWorkFlowSummary(loanProductWorkFlowSummary);
                        loanProducts.add(loanProduct);
                        offices = new ArrayList<OfficeData>();
                        loanProductWorkFlowSummary.setOfficeSummaries(offices);
                    }

                    /**
                     * Office
                     */
                    Boolean isOfficeData = false;
                    for (final OfficeData o : offices) {
                        if (o.getId() == oId) {
                            isOfficeData = true;
                            break;
                        }
                    }
                    if (!isOfficeData) {
                        workFlowSummaries = new ArrayList<WorkFlowSummaryData>();
                        office = OfficeData.lookup(oId, officeName);
                        office.setWorkFlowSummaries(workFlowSummaries);
                        offices.add(office);
                    }

                    /**
                     * work Flow Summary
                     */
                    Boolean isWorkFlowData = false;
                    for (final WorkFlowSummaryData ws : workFlowSummaries) {
                        if (ws.getStepName().equalsIgnoreCase(taskName)) {
                            isWorkFlowData = true;
                            ws.setNoOfCount(ws.getNoOfCount() + noOfCount);
                            break;
                        }
                    }
                    if (!isWorkFlowData) {
                        taskSummaries = new ArrayList<TaskSummaryData>();
                        workFlowSummary = new WorkFlowSummaryData(taskName, taskShortName, noOfCount);
                        workFlowSummary.setStepSummaries(taskSummaries);
                        workFlowSummaries.add(workFlowSummary);
                    }
                    taskSummary = new TaskSummaryData(taskStatus, noOfCount);
                    taskSummaries.add(taskSummary);
                }
            }
        }
        return loanProducts;
    }

    @Override
    public Page<TaskInfoData> retrieveTaskInformations(final String filterBy, SearchParameters searchParameters, final Long parentConfigId,
            final Long childConfigId) {
        final WorkFlowStepActionDataMapper dataMapper = new WorkFlowStepActionDataMapper();
        final Set<Role> loggedInUserRoles = this.context.authenticatedUser().getRoles();
        final List<Long> loggedInUserRoleIds = new ArrayList<>();
        final String hierarchy = this.context.authenticatedUser().getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        final StringBuilder sqlBuilder = new StringBuilder(500);
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        for (final Role r : loggedInUserRoles) {
            loggedInUserRoleIds.add(r.getId());
        }
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("roles", loggedInUserRoleIds);
        params.addValue("hierarchy", hierarchySearchString);
        sqlBuilder.append("SELECT SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(dataMapper.schema());
        if (filterBy != null){
            if (TaskApiConstants.ASSIGNED.equalsIgnoreCase(filterBy)) {
                params.addValue("assignedTo", this.context.authenticatedUser().getId());
                sqlBuilder.append(" AND t.assigned_to = :assignedTo ");
            }else if (TaskApiConstants.UNASSIGNED.equalsIgnoreCase(filterBy)) {
                sqlBuilder.append(" AND t.assigned_to IS NULL ");
            }else if (TaskApiConstants.ASSIGNED_NONWORKFLOW.equalsIgnoreCase(filterBy)) {
                sqlBuilder.append(" AND t.assigned_to = :assignedTo ");
                sqlBuilder.append(" AND t.parent_id is null ");
                sqlBuilder.append(" AND t.task_type = :taskType ");
                params.addValue("assignedTo", this.context.authenticatedUser().getId());
                params.addValue("taskType", TaskType.SINGLE.getValue());
            }else if (TaskApiConstants.ASSIGNED_WORKFLOW.equalsIgnoreCase(filterBy)) {
                sqlBuilder.append(" AND t.assigned_to = :assignedTo ");
                sqlBuilder.append(" AND t.parent_id is not null ");
                sqlBuilder.append(" AND pt.task_type = :taskType ");
                params.addValue("assignedTo", this.context.authenticatedUser().getId());
                params.addValue("taskType", TaskType.WORKFLOW.getValue());
            }
        }
        if (null != searchParameters.getOfficeId()) {
            sqlBuilder.append(" AND t.office_id = :officeId ");
            params.addValue("officeId", searchParameters.getOfficeId());
        }

        if (null != parentConfigId) {
            sqlBuilder.append(" AND pt.task_config_id = :parentConfigId ");
            params.addValue("parentConfigId", parentConfigId);
        }

        if (null != childConfigId) {
            sqlBuilder.append(" AND t.task_config_id = :childConfigId ");
            params.addValue("childConfigId", childConfigId);
        }

        sqlBuilder.append("GROUP BY taskId ");
        sqlBuilder.append("ORDER BY taskId ");
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        return this.paginationHelper.fetchPage(this.namedParameterjdbcTemplate, sqlCountRows, sqlBuilder.toString(), params, dataMapper);
    }

    @Override
    public List<TaskNoteData> getTaskNotes(Long taskId) {
        final String sql = "SELECT " + noteDataMapper.schema() + " WHERE tn.task_id = ? order by tn.created_date DESC ";
        return this.jdbcTemplate.query(sql, noteDataMapper, taskId);
    }

    @Override
    public List<TaskActionLogData> getActionLogs(Long taskId) {
        final String sql = "SELECT " + actionLogDataMapper.schema() + " WHERE tal.task_id = ? order by tal.action_on DESC ";
        return this.jdbcTemplate.query(sql, actionLogDataMapper, taskId);
    }

    @Override
    public List<RoleData> getRolesForAnAction(Long actionGroupId, Long actionId) {
        final String sql = "SELECT " + roleDataMapper.schema() + " WHERE ta.action_group_id = ? and ta.action = ?";
        return this.jdbcTemplate.query(sql, roleDataMapper, actionGroupId,actionId);
    }

    private static final class TaskNoteDataMapper implements RowMapper<TaskNoteData> {

        private final String schemaSql;

        public TaskNoteDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("tn.id as id, tn.task_id as taskId, ").append("tn.note as note, ")
                    .append("CONCAT(appuser.firstname,' ',appuser.lastname) AS createdBy, ")
                    .append("tn.created_date as createdOn ")
                    .append("from f_task_note tn ").append("LEFT JOIN m_appuser appuser ON appuser.id = tn.createdby_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public TaskNoteData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long taskId = rs.getLong("taskId");
            final String note = rs.getString("note");
            final String createdBy = rs.getString("createdBy");
            final LocalDate createdOn = JdbcSupport.getLocalDate(rs, "createdOn");

            return TaskNoteData.instance(id, taskId, note, createdBy, createdOn);
        }

    }

    private static final class TaskActionLogDataMapper implements RowMapper<TaskActionLogData> {

        private final String schemaSql;

        public TaskActionLogDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" tal.id as id, tal.task_id as taskId, ").append("tal.action as action, ")
                    .append(" CONCAT(appuser.firstname,' ',appuser.lastname) AS actionBy, ")
                    .append(" tal.action_on as actionOn ")
                    .append(" from f_task_action_log tal ")
                    .append(" LEFT JOIN m_appuser appuser ON appuser.id = tal.action_by ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public TaskActionLogData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long taskId = rs.getLong("taskId");
            final Integer action = rs.getInt("action");
            EnumOptionData actionEnumData = TaskActionType.fromInt(action).getEnumOptionData();
            final String actionBy = rs.getString("actionBy");
            final Date actionOn = rs.getTimestamp("actionOn");

            return TaskActionLogData.instance(id, actionEnumData, actionOn, actionBy);
        }

    }

    private static final class RoleDataMapper implements RowMapper<RoleData> {

        private final String schemaSql;

        public RoleDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" r.id as id, r.name as name, r.description as description, r.is_disabled as isDisabled ")
                    .append(" from f_task_action_role tar ")
                    .append(" LEFT JOIN m_role r ON tar.role_id = r.id ")
                    .append(" LEFT JOIN f_task_action ta ON tar.task_action_id = ta.id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public RoleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final Boolean isDisabled = rs.getBoolean("isDisabled");
            return new RoleData(id,name,description, isDisabled, null);
        }

    }

    private static final class WorkFlowStepActionDataMapper implements RowMapper<TaskInfoData> {

        private String schema;

        public WorkFlowStepActionDataMapper() {

        }

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(500);
            sqlBuilder
                    .append(" t.id AS taskId,t.parent_id AS parentTaskId,t.name AS taskName, t.status AS taskStatusId, t.description As description ");
            sqlBuilder.append(" ,t.current_action AS currentActionId, tar.role_id AS roleId,appuser.id AS assignedId ");
            sqlBuilder.append(" ,CONCAT(appuser.firstname,' ',appuser.lastname) AS assignedTo ");
            sqlBuilder.append(" ,t.entity_type AS entityTypeId,t.entity_id AS entityId, t.config_values as configValues ");
            sqlBuilder.append(" ,c.id AS clientId,c.display_name AS clientName ");
            sqlBuilder.append(" ,o.id AS officeId,o.name AS officeName, t.created_date as createdOn ");
            sqlBuilder.append(" FROM f_task t ");
            sqlBuilder.append(" LEFT JOIN f_task pt ON pt.id = t.parent_id ");
            sqlBuilder.append(" LEFT JOIN m_client c ON c.id = t.client_id ");
            sqlBuilder.append(" LEFT JOIN m_office o ON o.id = t.office_id ");
            sqlBuilder.append(" LEFT JOIN f_task_action ta ON ta.action_group_id = t.action_group_id AND t.current_action = ta.action ");
            sqlBuilder.append(" LEFT JOIN f_task_action_role tar ON tar.task_action_id = ta.id ");
            sqlBuilder.append(" LEFT JOIN m_appuser appuser ON appuser.id = t.assigned_to ");
            sqlBuilder.append(" WHERE o.hierarchy like :hierarchy AND t.`status` BETWEEN 2 AND  6 AND t.current_action IS NOT NULL ");
            sqlBuilder.append(" AND (tar.role_id IN (:roles) OR tar.role_id IS NULL) ");
            return sqlBuilder.toString();
        }

        @SuppressWarnings({ "unused" })
        @Override
        public TaskInfoData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long taskId = rs.getLong("taskId");
            final Long parentTaskId = JdbcSupport.getLongActualValue(rs, "parentTaskId");
            final String taskName = rs.getString("taskName");
            final String taskStatus = TaskStatusType.fromInt(rs.getInt("taskStatusId")).toString();
            final String currentAction = TaskActionType.fromInt(rs.getInt("currentActionId")).toString();
            final Long assignedId = rs.getLong("assignedId");
            final String assignedTo = rs.getString("assignedTo");
            final Integer entityTypeId = rs.getInt("entityTypeId");
            final String entityType = TaskEntityType.fromInt(entityTypeId).toString();
            final Long entityId = rs.getLong("entityId");
            final Long clientId = rs.getLong("clientId");
            final String description  = rs.getString("description");
            final Date createdOn  = rs.getTimestamp("createdOn");
            ClientData clientData = null;
            if (clientId != null) {
                final String clientName = rs.getString("clientName");
                clientData = ClientData.lookup(clientId, clientName, null, null);
            }
            final Long officeId = rs.getLong("officeId");
            OfficeData officeData = null;
            if (officeId != null) {
                final String officeName = rs.getString("officeName");
                officeData = OfficeData.lookup(officeId, officeName);
            }
            String nextActionUrl = "";
            if (entityType != null && entityType.equalsIgnoreCase(TaskEntityType.LOAN_APPLICATION.toString())) {
                nextActionUrl = "/viewtask/" + taskId;
            }
            final String configValues = rs.getString("configValues");
            Map<String, String> configValueMap = new HashMap<>();
            if (configValues != null) {
                configValueMap = new Gson().fromJson(configValues, new TypeToken<HashMap<String, String>>() {}.getType());
            }
            return TaskInfoData.instance(taskId, parentTaskId, taskName, taskStatus, currentAction, assignedId, assignedTo, entityTypeId,
                    entityType, entityId, nextActionUrl, clientData, officeData, configValueMap, description,createdOn);
        }
    }
}