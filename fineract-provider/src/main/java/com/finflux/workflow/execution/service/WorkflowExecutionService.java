package com.finflux.workflow.execution.service;

import com.finflux.workflow.execution.data.StepAction;
import com.finflux.workflow.execution.data.WorkflowExecutionData;
import com.finflux.workflow.execution.data.WorkflowExecutionStepData;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.List;

/**
 * Created by dhirendra on 22/09/16.
 */
public interface WorkflowExecutionService {

    Long getOrCreateWorkflowExecution(final Integer entityTypeId, final Long entityId);

    public WorkflowExecutionData getWorkflowExecutionData(Long workflowExecutionId);

    WorkflowExecutionStepData getWorkflowExecutionStepData(Long workflowExecutionStepId);

    public void doActionOnWorkflowExecutionStep(Long workflowExecutionStepId, StepAction stepAction);

    public void addNoteToWorkflowExecution(Long workflowExecutionId);

    public void addNoteToWorkflowExecutionStep(Long workflowExecutionId);

    List<EnumOptionData> getClickableActionsForUser(Long workflowExecutionStepId, Long id);
}
