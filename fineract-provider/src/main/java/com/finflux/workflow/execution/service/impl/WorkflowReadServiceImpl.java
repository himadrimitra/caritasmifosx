package com.finflux.workflow.execution.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.workflow.execution.data.StepStatus;
import com.finflux.workflow.execution.data.TaskType;
import com.finflux.workflow.execution.data.WorkflowExecutionData;
import com.finflux.workflow.execution.data.WorkflowExecutionStepData;
import com.finflux.workflow.execution.data.WorkflowExecutionTaskData;
import com.finflux.workflow.execution.exception.WorkflowExecutionNotFoundException;
import com.finflux.workflow.execution.service.WorkflowReadService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class WorkflowReadServiceImpl implements WorkflowReadService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final WorkflowExecutionStepIdMapper workflowExecutionStepIdMapper = new WorkflowExecutionStepIdMapper();

    @Autowired
    public WorkflowReadServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Long> getWorkflowStepsIds(final Long workflowId) {
        return this.jdbcTemplate.query("SELECT wfs.id FROM f_workflow_step wfs WHERE wfs.workflow_id = ? ORDER BY wfs.step_order ASC ",
                new ParameterizedRowMapper<Long>() {

                    @SuppressWarnings("unused")
                    @Override
                    public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                        return rs.getLong(1);
                    }
                }, workflowId);
    }

    @Override
    public WorkflowExecutionData getWorkflowExecutionData(final Long workflowExecutionId) {
        final WorkflowExecutionDataExtractor workflowExecutionDataExtractor = new WorkflowExecutionDataExtractor();
        this.context.authenticatedUser();
        String sql = "SELECT " + workflowExecutionDataExtractor.schema() + " WHERE wfe.id = ? ";
        final Collection<WorkflowExecutionData> workflowExecutionDatas = this.jdbcTemplate.query(sql, workflowExecutionDataExtractor,
                new Object[] { workflowExecutionId });
        if (workflowExecutionDatas == null || workflowExecutionDatas.isEmpty()) { throw new WorkflowExecutionNotFoundException(
                workflowExecutionId); }
        return workflowExecutionDatas.iterator().next();
    }

    @Override
    public WorkflowExecutionStepData getWorkflowExecutionStepData(Long workflowExecutionStepId) {
        final WorkflowExecutionStepDataMapper workflowExecutionStepDataMapper = new WorkflowExecutionStepDataMapper();
        this.context.authenticatedUser();
        String sql = "SELECT " + workflowExecutionStepDataMapper.schema() + " WHERE wfes.id = ? ";
        final List<WorkflowExecutionStepData> workflowExecutionStepDatas = this.jdbcTemplate.query(sql, workflowExecutionStepDataMapper,
                new Object[] { workflowExecutionStepId });
        if (workflowExecutionStepDatas == null || workflowExecutionStepDatas.isEmpty()) { throw new WorkflowExecutionNotFoundException(
                workflowExecutionStepId); }
        return workflowExecutionStepDatas.iterator().next();
    }

    @Override
    public List<Long> getExecutionStepsByOrder(Long workflowExecutionId, int orderId) {
        this.context.authenticatedUser();
        String sql = "SELECT " + workflowExecutionStepIdMapper.schema() + "  WHERE wfes.workflow_execution_id = ? and wfs.step_order = ? ";
        return this.jdbcTemplate.query(sql, workflowExecutionStepIdMapper,workflowExecutionId, orderId);
    }

    private static final class WorkflowExecutionDataMapper implements RowMapper<WorkflowExecutionData> {

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("wfe.id AS wfeId, wf.name AS wfName, wfes.id AS wfesId, wfs.name AS wfsName ");
            sqlBuilder.append(",task.id AS taskId, task.identifier as identifier, task.name AS taskName ");
            sqlBuilder.append(", task.`type` AS taskType, wfs.config_values AS wfsConfigValues ");
            sqlBuilder.append(",wfes.`status` AS wfesStatus, wfes.criteria_result as criteriaResult ");
            sqlBuilder.append("FROM f_workflow_execution_step wfes  ");
            sqlBuilder.append("LEFT JOIN f_workflow_execution wfe ON wfes.workflow_execution_id = wfe.id ");
            sqlBuilder.append("JOIN f_workflow wf ON wf.id = wfe.workflow_id ");
            sqlBuilder.append("LEFT JOIN f_workflow_step wfs ON wfs.id = wfes.workflow_step_id ");
            sqlBuilder.append("LEFT JOIN f_task task ON task.id = wfs.task_id ");
            return sqlBuilder.toString();
        }

        @Override
        public WorkflowExecutionData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "wfeId");
            if (id == null) { return null; }
            final String name = rs.getString("wfName");
            return WorkflowExecutionData.instance(id, name);
        }
    }

    private static final class WorkflowExecutionDataExtractor implements ResultSetExtractor<Collection<WorkflowExecutionData>> {

        final WorkflowExecutionDataMapper workflowExecutionDataMapper = new WorkflowExecutionDataMapper();
        final WorkflowExecutionStepDataMapper workflowExecutionStepDataMapper = new WorkflowExecutionStepDataMapper();

        private String schemaSql;

        private WorkflowExecutionDataExtractor() {
            this.schemaSql = this.workflowExecutionDataMapper.schema();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public Collection<WorkflowExecutionData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            final List<WorkflowExecutionData> workflowExecutionDataList = new ArrayList<>();
            WorkflowExecutionData workflowExecutionData = null;
            Long workflowExecutionId = null;
            int wfeIndex = 0;// Work flow execution index
            int wfesIndex = 0;// Work flow execution step index
            while (rs.next()) {
                final Long tempWfeId = rs.getLong("wfeId");
                if (workflowExecutionData == null || (workflowExecutionId != null && !workflowExecutionId.equals(tempWfeId))) {
                    workflowExecutionId = tempWfeId;
                    workflowExecutionData = this.workflowExecutionDataMapper.mapRow(rs, wfeIndex++);
                    workflowExecutionDataList.add(workflowExecutionData);
                }
                final WorkflowExecutionStepData workflowExecutionStepData = this.workflowExecutionStepDataMapper.mapRow(rs, wfesIndex++);
                if (workflowExecutionStepData != null) {
                    workflowExecutionData.addStep(workflowExecutionStepData);
                }
            }
            return workflowExecutionDataList;
        }
    }

    private static final class WorkflowExecutionStepDataMapper implements RowMapper<WorkflowExecutionStepData> {

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("wfe.id AS wfeId, wf.name AS wfName, wfes.id AS wfesId, wfs.name AS wfsName ");
            sqlBuilder.append(",task.id AS taskId, task.identifier as identifier, task.name AS taskName ");
            sqlBuilder.append(", task.`type` AS taskType, wfs.config_values AS wfsConfigValues ");
            sqlBuilder.append(",wfes.`status` AS wfesStatus, wfes.criteria_result as criteriaResult ");
            sqlBuilder.append("FROM f_workflow_execution_step wfes  ");
            sqlBuilder.append("LEFT JOIN f_workflow_execution wfe ON wfes.workflow_execution_id = wfe.id ");
            sqlBuilder.append("JOIN f_workflow wf ON wf.id = wfe.workflow_id ");
            sqlBuilder.append("LEFT JOIN f_workflow_step wfs ON wfs.id = wfes.workflow_step_id ");
            sqlBuilder.append("LEFT JOIN f_task task ON task.id = wfs.task_id ");
            return sqlBuilder.toString();
        }

        @Override
        public WorkflowExecutionStepData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "wfesId");
            if (id == null) { return null; }
            final String name = rs.getString("wfsName");
            final Long taskId = JdbcSupport.getLongDefaultToNullIfZero(rs, "taskId");
            WorkflowExecutionTaskData task = null;
            if (taskId != null) {
                final String taskName = rs.getString("taskName");
                final Integer taskType = JdbcSupport.getIntegeActualValue(rs, "taskType");
                final String taskIdentifier = rs.getString("identifier");
                EnumOptionData type = null;
                if (taskType != null) {
                    type = TaskType.fromInt(taskType).getEnumOptionData();
                }
                task = WorkflowExecutionTaskData.instance(taskId, taskName, type,taskIdentifier);
            }
            final String wfsConfigValues = rs.getString("wfsConfigValues");
            Map<String, String> configValues = null;
            if (wfsConfigValues != null) {
                configValues = new Gson().fromJson(wfsConfigValues, new TypeToken<HashMap<String, String>>() {}.getType());
            }
            final String criteriaResultStr = rs.getString("criteriaResult");
            EligibilityResult criteriaResult = null;
            if (criteriaResultStr != null) {
                criteriaResult = new Gson().fromJson(criteriaResultStr, new TypeToken<EligibilityResult>() {}.getType());
            }
            final Integer wfesStatus = JdbcSupport.getIntegeActualValue(rs, "wfesStatus");
            EnumOptionData status = null;
            List<EnumOptionData> possibleActions = null;
            if (wfesStatus != null) {
                final StepStatus stepStatus = StepStatus.fromInt(wfesStatus);
                status = stepStatus.getEnumOptionData();
                possibleActions = stepStatus.getPossibleActionsEnumOption();

            }
            return WorkflowExecutionStepData.instance(id, name, task, configValues, status, possibleActions,criteriaResult);
        }
    }

    private static final class WorkflowExecutionStepIdMapper implements RowMapper<Long> {

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("wfes.id AS wfesId ");
            sqlBuilder.append("FROM f_workflow_execution_step wfes  ");
            sqlBuilder.append("LEFT JOIN f_workflow_step wfs ON wfs.id = wfes.workflow_step_id ");

            return sqlBuilder.toString();
        }

        @Override
        public Long mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "wfesId");
            return id;
        }
    }
}
