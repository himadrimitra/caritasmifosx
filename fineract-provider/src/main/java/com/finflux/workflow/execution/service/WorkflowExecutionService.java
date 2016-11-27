package com.finflux.workflow.execution.service;

import com.finflux.workflow.execution.data.*;

import com.finflux.workflow.execution.domain.WorkflowExecution;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 22/09/16.
 */
public interface WorkflowExecutionService {

    Long createWorkflowExecutionForWorkflow(Long workflowId, WorkFlowExecutionEntityType entityType, Long entityId,
            Long clientId, Long officeId, Map<WorkflowConfigKey, String> configValues);

    public WorkflowExecutionData getWorkflowExecutionData(Long workflowExecutionId);

    WorkflowExecutionStepData getWorkflowExecutionStepData(Long workflowExecutionStepId);

    public void doActionOnWorkflowExecutionStep(Long workflowExecutionStepId, StepAction stepAction);

    public void addNoteToWorkflowExecution(Long workflowExecutionId);

    public void addNoteToWorkflowExecutionStep(Long workflowExecutionId);

    List<EnumOptionData> getClickableActionsForUser(Long workflowExecutionStepId, Long id);

    Long getWorkflowExecution(WorkFlowExecutionEntityType entityType, Long entityId);
}
