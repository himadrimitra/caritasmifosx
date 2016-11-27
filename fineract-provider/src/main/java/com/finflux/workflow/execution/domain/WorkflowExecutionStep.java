package com.finflux.workflow.execution.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_workflow_execution_step")
public class WorkflowExecutionStep
		extends
			AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "short_name", nullable = true)
	private String shortName;

	@Column(name = "task_id", nullable = false)
	private Long taskId;

	@Column(name = "workflow_execution_id", nullable = false)
	private Long workflowExecutionId;

	@Column(name = "workflow_step_id", nullable = false)
	private Long workflowStepId;

	@Column(name = "status", nullable = false)
	private Integer status;

	@Column(name = "current_action", nullable = true)
	private Integer currentAction;

	@Column(name = "assigned_to", nullable = true)
	private Long assignedTo;

	@Column(name = "client_id", nullable = true)
	private Long clientId;

	@Column(name = "office_id", nullable = true)
	private Long officeId;

	@Column(name = "criteria_action", nullable = true)
	private Integer criteriaAction;

	@Column(name = "criteria_result", nullable = true)
	private String criteriaResult;

	@Column(name = "step_order", length = 3, nullable = true)
	private Integer stepOrder;

	@Column(name = "criteria_id", nullable = true)
	private Long criteriaId;

	@Column(name = "approval_logic", length = 512)
	private String approvalLogic;

	@Column(name = "rejection_logic", length = 512)
	private String rejectionLogic;

	@Column(name = "config_values", nullable = true)
	private String configValues;

	@Column(name = "entity_type", nullable = false)
	private Integer entityType;

	@Column(name = "entity_id", nullable = true)
	private Long entityId;

	@Column(name = "action_group_id", nullable = true)
	private Long actionGroupId;

	protected WorkflowExecutionStep() {
	}

	public WorkflowExecutionStep(String name, String shortName, Long taskId,
			Long workflowExecutionId, Long workflowStepId, Integer status,
			Long clientId, Long officeId, Integer stepOrder, Long criteriaId,
			String approvalLogic, String rejectionLogic, String configValues,
			Integer entityType, Long entityId, Long actionGroupId) {
		this.name = name;
		this.shortName = shortName;
		this.taskId = taskId;
		this.workflowExecutionId = workflowExecutionId;
		this.workflowStepId = workflowStepId;
		this.status = status;
		this.clientId = clientId;
		this.officeId = officeId;
		this.stepOrder = stepOrder;
		this.criteriaId = criteriaId;
		this.approvalLogic = approvalLogic;
		this.rejectionLogic = rejectionLogic;
		this.configValues = configValues;
		this.entityType = entityType;
		this.entityId = entityId;
		this.actionGroupId = actionGroupId;
	}

	public static WorkflowExecutionStep create(String name, String shortName,
			Long taskId, Long workflowExecutionId, Long workflowStepId,
			Integer status, Long clientId, Long officeId, Integer stepOrder,
			Long criteriaId, String approvalLogic, String rejectionLogic,
			String configValues, Integer entityType, Long entityId, Long actionGroupId) {
		return new WorkflowExecutionStep(name, shortName, taskId,
				workflowExecutionId, workflowStepId, status, clientId,
				officeId, stepOrder, criteriaId, approvalLogic, rejectionLogic,
				configValues, entityType, entityId, actionGroupId);
	}

	public Long getWorkflowExecutionId() {
		return workflowExecutionId;
	}

	public void setWorkflowExecutionId(Long workflowExecutionId) {
		this.workflowExecutionId = workflowExecutionId;
	}

	public Long getWorkflowStepId() {
		return workflowStepId;
	}

	public void setWorkflowStepId(Long workflowStepId) {
		this.workflowStepId = workflowStepId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCriteriaAction() {
		return criteriaAction;
	}

	public void setCriteriaAction(Integer criteriaAction) {
		this.criteriaAction = criteriaAction;
	}

	public String getCriteriaResult() {
		return criteriaResult;
	}

	public void setCriteriaResult(String criteriaResult) {
		this.criteriaResult = criteriaResult;
	}

	public Long getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Long assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Integer getCurrentAction() {
		return this.currentAction;
	}

	public void setCurrentAction(final Integer currentAction) {
		this.currentAction = currentAction;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public Long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}

	public String getApprovalLogic() {
		return approvalLogic;
	}

	public void setApprovalLogic(String approvalLogic) {
		this.approvalLogic = approvalLogic;
	}

	public String getRejectionLogic() {
		return rejectionLogic;
	}

	public void setRejectionLogic(String rejectionLogic) {
		this.rejectionLogic = rejectionLogic;
	}

	public String getConfigValues() {
		return configValues;
	}

	public void setConfigValues(String configValues) {
		this.configValues = configValues;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public Integer getEntityType() {
		return entityType;
	}

	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Long getActionGroupId() {
		return actionGroupId;
	}

	public void setActionGroupId(Long actionGroupId) {
		this.actionGroupId = actionGroupId;
	}
}