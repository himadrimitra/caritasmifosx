package com.finflux.task.configuration.data;

import java.util.Collection;

import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.task.data.TaskActivityData;

public class LoanProdcutTasksConfigTemplateData {

    private final Collection<TaskActivityData> taskActivityDatas;
    private final Collection<RuleData> criteriaOptions;

    private LoanProdcutTasksConfigTemplateData(final Collection<TaskActivityData> taskActivityDatas,
            final Collection<RuleData> criteriaOptions) {
        this.taskActivityDatas = taskActivityDatas;
        this.criteriaOptions = criteriaOptions;
    }

    public static LoanProdcutTasksConfigTemplateData template(final Collection<TaskActivityData> taskActivityDatas,
            final Collection<RuleData> criteriaOptions) {
        return new LoanProdcutTasksConfigTemplateData(taskActivityDatas, criteriaOptions);
    }
}
