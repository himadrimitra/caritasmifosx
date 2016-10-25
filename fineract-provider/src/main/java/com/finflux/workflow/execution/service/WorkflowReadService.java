package com.finflux.workflow.execution.service;

import java.util.List;

import com.finflux.workflow.execution.data.WorkflowExecutionData;
import com.finflux.workflow.execution.data.WorkflowExecutionStepData;

public interface WorkflowReadService {

    List<Long> getWorkflowStepsIds(final Long workflowId);

    WorkflowExecutionData getWorkflowExecutionData(final Long workflowExecutionId);

    WorkflowExecutionStepData getWorkflowExecutionStepData(Long workflowExecutionStepId);

    List<Long> getExecutionStepsByOrder(Long workflowExecutionId, int orderId);
}