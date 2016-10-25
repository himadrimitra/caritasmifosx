package com.finflux.workflow.execution.data;

import java.util.List;
import java.util.Map;

import com.finflux.ruleengine.execution.data.EligibilityResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 15/10/16.
 */
public class WorkflowExecutionStepData {

    private Long id;
    private String name;
    private WorkflowExecutionTaskData task;
    private Map<String, String> configValues;
    private EnumOptionData status;
    private List<EnumOptionData> possibleActions;
    private EligibilityResult criteriaResult;

    private WorkflowExecutionStepData(Long id, String name, WorkflowExecutionTaskData task, Map<String, String> configValues,
            final EnumOptionData status, List<EnumOptionData> possibleActions, EligibilityResult criteriaResult) {
        this.id = id;
        this.name = name;
        this.task = task;
        this.configValues = configValues;
        this.possibleActions = possibleActions;
        this.status = status;
        this.criteriaResult = criteriaResult;

    }

    public static WorkflowExecutionStepData instance(Long id, String name, WorkflowExecutionTaskData task,
                                                     Map<String, String> configValues, final EnumOptionData status, List<EnumOptionData> possibleActions, EligibilityResult criteriaResult) {
        return new WorkflowExecutionStepData(id, name, task, configValues, status, possibleActions,criteriaResult);
    }
}
