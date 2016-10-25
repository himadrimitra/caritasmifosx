package com.finflux.workflow.execution.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhirendra on 15/10/16.
 */
public class WorkflowExecutionData {

    private Long id;
    private String name;
    private List<WorkflowExecutionStepData> steps;

    public WorkflowExecutionData(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static WorkflowExecutionData instance(final Long id, final String name) {
        return new WorkflowExecutionData(id, name);
    }

    @SuppressWarnings("unused")
    public void addStep(final WorkflowExecutionStepData workflowExecutionStepData) {
        if (this.steps == null) {
            this.steps = new ArrayList<WorkflowExecutionStepData>();
        }
        this.steps.add(workflowExecutionStepData);
    }
}
