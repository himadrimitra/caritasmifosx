package com.finflux.workflow.execution.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.useradministration.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.workflow.execution.api.WorkFlowStepApiConstants;
import com.finflux.workflow.execution.data.LoanProductWorkFlowSummaryData;
import com.finflux.workflow.execution.data.StepAction;
import com.finflux.workflow.execution.data.StepStatus;
import com.finflux.workflow.execution.data.StepSummaryData;
import com.finflux.workflow.execution.data.WorkFlowExecutionEntityType;
import com.finflux.workflow.execution.data.WorkFlowStepActionData;
import com.finflux.workflow.execution.data.WorkFlowSummaryData;
import com.finflux.workflow.execution.service.WorkFlowStepReadService;

@Service
public class WorkFlowStepReadServiceImpl implements WorkFlowStepReadService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public WorkFlowStepReadServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
    }

    @SuppressWarnings({ "unused", "null" })
    @Override
    public List<LoanProductData> retrieveLoanProductWorkFlowSummary(final Long loanProductId, final Long officeId) {
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("SELECT lp.id AS loanProductId,lp.name AS loanProductName,wf.id AS workFlowId, wf.name AS workFlowName ");
        sqlBuilder.append(",co.id AS officeId, co.name AS officeName, wfes.status AS stepStatus ");
        sqlBuilder.append(",wfs.id AS stepId, wfs.name AS stepName,wfs.short_name AS stepShortName ");
        sqlBuilder.append(",SUM(IF(wfes.status BETWEEN 2 AND 6,1,0)) AS noOfCount ");
        sqlBuilder.append("FROM f_workflow wf ");
        sqlBuilder.append("JOIN f_workflow_entity_type_mapping wfm ON wfm.workflow_id = wf.id AND wfm.entity_type = 1 ");
        sqlBuilder.append("JOIN m_product_loan lp ON ");
        if (loanProductId != null) {
            sqlBuilder.append("lp.id = ").append(loanProductId).append(" AND ");
        }
        sqlBuilder.append("lp.id = wfm.entity_id ");
        sqlBuilder.append("JOIN f_workflow_execution wfe ON wfe.workflow_id = wf.id AND wfe.entity_type = 1 ");
        sqlBuilder.append("JOIN f_loan_application_reference lar ON lar.id = wfe.entity_id AND lp.id = lar.loan_product_id ");
        sqlBuilder.append("JOIN m_client c ON c.id = lar.client_id ");
        sqlBuilder.append("JOIN m_office o ");
        sqlBuilder.append("JOIN m_office co ON co.hierarchy LIKE CONCAT(o.hierarchy, '%') AND co.id = c.office_id ");
        sqlBuilder.append("JOIN f_workflow_execution_step wfes ON wfes.workflow_execution_id = wfe.id ");
        sqlBuilder.append("JOIN f_workflow_step wfs ON wfs.id = wfes.workflow_step_id ");
        if (officeId != null) {
            sqlBuilder.append("AND o.id = ").append(officeId).append(" ");
        }
        sqlBuilder.append("GROUP BY lp.id,wf.id,co.id,wfes.status,wfs.name ");
        sqlBuilder.append("ORDER BY lp.name,wf.name,co.id,wfs.step_order,wfes.status ");
        final List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sqlBuilder.toString());
        final List<LoanProductData> loanProducts = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            LoanProductData loanProduct = null;
            LoanProductWorkFlowSummaryData loanProductWorkFlowSummary = null;
            List<OfficeData> offices = null;
            OfficeData office = null;
            List<WorkFlowSummaryData> workFlowSummaries = null;
            WorkFlowSummaryData workFlowSummary = null;
            List<StepSummaryData> stepSummaries = null;
            StepSummaryData stepSummary = null;

            for (final Map<String, Object> l : list) {
                final Integer stepStatusId = Integer.parseInt(l.get("stepStatus").toString());
                if (!(StepStatus.fromInt(stepStatusId).getValue() > 6)) {
                    final Long lpId = (Long) l.get("loanProductId");
                    final String loanProductName = (String) l.get("loanProductName");
                    final Long oId = (Long) l.get("officeId");
                    final String officeName = (String) l.get("officeName");
                    final Long workFlowId = (Long) l.get("workFlowId");
                    final String workFlowName = (String) l.get("workFlowName");
                    final Long stepId = (Long) l.get("stepId");
                    final String stepName = (String) l.get("stepName");
                    final String stepShortName = (String) l.get("stepShortName");
                    final Long noOfCount = Long.parseLong(l.get("noOfCount").toString());
                    final String stepStatus = StepStatus.fromInt(stepStatusId).toString();

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
                        loanProductWorkFlowSummary = new LoanProductWorkFlowSummaryData();
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
                        if (ws.getStepName().equalsIgnoreCase(stepName)) {
                            isWorkFlowData = true;
                            ws.setNoOfCount(ws.getNoOfCount() + noOfCount);
                            break;
                        }
                    }
                    if (!isWorkFlowData) {
                        stepSummaries = new ArrayList<StepSummaryData>();
                        workFlowSummary = new WorkFlowSummaryData(stepName, stepShortName, noOfCount);
                        workFlowSummary.setStepSummaries(stepSummaries);
                        workFlowSummaries.add(workFlowSummary);
                    }
                    stepSummary = new StepSummaryData(stepStatus, noOfCount);
                    stepSummaries.add(stepSummary);
                }
            }
        }
        return loanProducts;
    }

    @Override
    public List<WorkFlowStepActionData> retrieveWorkFlowStepActions(final String filterBy) {
        final WorkFlowStepActionDataMapper dataMapper = new WorkFlowStepActionDataMapper();
        final Set<Role> loggedInUserRoles = this.context.authenticatedUser().getRoles();
        final StringBuilder loggedInUserRoleIds = new StringBuilder(10);
        for (final Role r : loggedInUserRoles) {
            if (loggedInUserRoleIds.length() == 0) {
                loggedInUserRoleIds.append(r.getId());
            } else {
                loggedInUserRoleIds.append(",").append(r.getId());
            }
        }
        if (filterBy != null && filterBy.equalsIgnoreCase(WorkFlowStepApiConstants.ASSIGNEED)) {
            return this.jdbcTemplate.query(dataMapper.assigned(), dataMapper, new Object[] { this.context.authenticatedUser().getId(),
                    loggedInUserRoleIds.toString() });
        } else if (filterBy != null && filterBy.equalsIgnoreCase(WorkFlowStepApiConstants.UNASSIGNEED)) { return this.jdbcTemplate.query(
                dataMapper.unAssigned(), dataMapper,
                new Object[] { this.context.authenticatedUser().getId(), loggedInUserRoleIds.toString() }); }
        return this.jdbcTemplate.query(dataMapper.all(), dataMapper, new Object[] { loggedInUserRoleIds.toString() });
    }

    private static final class WorkFlowStepActionDataMapper implements RowMapper<WorkFlowStepActionData> {

        private String schema;

        public WorkFlowStepActionDataMapper() {

        }

        public String all() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("SELECT wes.id AS stepId,ws.name AS stepName, wes.status AS stepStatusId ");
            sqlBuilder.append(",wes.current_action AS currentActionId, wsar.role_id AS roleId,appuser.id AS assignedId ");
            sqlBuilder.append(",CONCAT(appuser.firstname,' ',appuser.lastname) AS assignedTo ");
            sqlBuilder.append(",wfe.entity_type AS entityTypeId,wfe.entity_id AS entityId ");
            sqlBuilder.append("FROM f_workflow_execution_step wes ");
            sqlBuilder.append("JOIN f_workflow_execution wfe ON wfe.id = wes.workflow_execution_id ");
            sqlBuilder.append("JOIN f_workflow_step ws ON ws.id = wes.workflow_step_id ");
            sqlBuilder
                    .append("LEFT JOIN f_workflow_step_action wsa ON wsa.workflow_step_id = wes.workflow_step_id AND wes.current_action = wsa.action ");
            sqlBuilder.append("LEFT JOIN f_workflow_step_action_role wsar ON wsar.workflow_step_action_id = wsa.id ");
            sqlBuilder.append("LEFT JOIN m_appuser appuser ON appuser.id = wes.assigned_to ");
            sqlBuilder.append("WHERE wes.`status` BETWEEN 2 AND  6 AND wes.current_action IS NOT NULL ");
            sqlBuilder.append("AND (wsar.role_id IN (?) OR wsar.role_id IS NULL) ");
            sqlBuilder.append("GROUP BY stepId ");
            sqlBuilder.append("ORDER BY stepId ");
            this.schema = sqlBuilder.toString();
            return this.schema;
        }

        public String assigned() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("SELECT wes.id AS stepId,ws.name AS stepName, wes.status AS stepStatusId ");
            sqlBuilder.append(",wes.current_action AS currentActionId, wsar.role_id AS roleId,appuser.id AS assignedId ");
            sqlBuilder.append(",CONCAT(appuser.firstname,' ',appuser.lastname) AS assignedTo ");
            sqlBuilder.append(",wfe.entity_type AS entityTypeId,wfe.entity_id AS entityId ");
            sqlBuilder.append("FROM f_workflow_execution_step wes ");
            sqlBuilder.append("JOIN f_workflow_execution wfe ON wfe.id = wes.workflow_execution_id ");
            sqlBuilder.append("JOIN f_workflow_step ws ON ws.id = wes.workflow_step_id ");
            sqlBuilder
                    .append("LEFT JOIN f_workflow_step_action wsa ON wsa.workflow_step_id = wes.workflow_step_id AND wes.current_action = wsa.action ");
            sqlBuilder.append("LEFT JOIN f_workflow_step_action_role wsar ON wsar.workflow_step_action_id = wsa.id ");
            sqlBuilder.append("LEFT JOIN m_appuser appuser ON appuser.id = wes.assigned_to ");
            sqlBuilder.append("WHERE wes.`status` BETWEEN 2 AND  6 AND wes.current_action IS NOT NULL ");
            sqlBuilder.append("AND wes.assigned_to = ? AND (wsar.role_id IN (?) OR wsar.role_id IS NULL) ");
            sqlBuilder.append("GROUP BY stepId ");
            sqlBuilder.append("ORDER BY stepId ");
            this.schema = sqlBuilder.toString();
            return this.schema;
        }

        public String unAssigned() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("SELECT wes.id AS stepId,ws.name AS stepName, wes.status AS stepStatusId ");
            sqlBuilder.append(",wes.current_action AS currentActionId, wsar.role_id AS roleId,appuser.id AS assignedId ");
            sqlBuilder.append(",CONCAT(appuser.firstname,' ',appuser.lastname) AS assignedTo ");
            sqlBuilder.append(",wfe.entity_type AS entityTypeId,wfe.entity_id AS entityId ");
            sqlBuilder.append("FROM f_workflow_execution_step wes ");
            sqlBuilder.append("JOIN f_workflow_execution wfe ON wfe.id = wes.workflow_execution_id ");
            sqlBuilder.append("JOIN f_workflow_step ws ON ws.id = wes.workflow_step_id ");
            sqlBuilder
                    .append("LEFT JOIN f_workflow_step_action wsa ON wsa.workflow_step_id = wes.workflow_step_id AND wes.current_action = wsa.action ");
            sqlBuilder.append("LEFT JOIN f_workflow_step_action_role wsar ON wsar.workflow_step_action_id = wsa.id ");
            sqlBuilder.append("LEFT JOIN m_appuser appuser ON appuser.id = wes.assigned_to ");
            sqlBuilder.append("WHERE wes.`status` BETWEEN 2 AND  6 AND wes.current_action IS NOT NULL ");
            sqlBuilder.append("AND (wes.assigned_to IS NULL OR  wes.assigned_to != ? ) ");
            sqlBuilder.append("AND (wsar.role_id IN (?) OR wsar.role_id IS NULL) ");
            sqlBuilder.append("GROUP BY stepId ");
            sqlBuilder.append("ORDER BY stepId ");
            this.schema = sqlBuilder.toString();
            return this.schema;
        }

        @SuppressWarnings({ "unused" })
        @Override
        public WorkFlowStepActionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long stepId = rs.getLong("stepId");
            final String stepName = rs.getString("stepName");
            final String stepStatus = StepStatus.fromInt(rs.getInt("stepStatusId")).toString();
            final String currentAction = StepAction.fromInt(rs.getInt("currentActionId")).toString();
            final Long assignedId = rs.getLong("assignedId");
            final String assignedTo = rs.getString("assignedTo");
            final Integer entityTypeId = rs.getInt("entityTypeId");
            final String entityType = WorkFlowExecutionEntityType.fromInt(entityTypeId).toString();
            final Long entityId = rs.getLong("entityId");
            String nextActionUrl = "";
            if (entityType != null && entityType.equalsIgnoreCase(WorkFlowExecutionEntityType.LOAN_APPLICATION.toString())) {
                nextActionUrl = "/loanapplication/" + entityId + "/workflow";
            }
            return WorkFlowStepActionData.instance(stepId, stepName, stepStatus, currentAction, assignedId, assignedTo, entityTypeId,
                    entityType, entityId, nextActionUrl);
        }
    }
}