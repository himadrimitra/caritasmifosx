package com.finflux.task.configuration.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.task.data.TaskConfigData;

public class TaskMappingTemplateData {

    private Collection<TaskConfigData> parentTasks;
    private Collection<EnumOptionData> taskConfigEntityTypeOptions;

    public TaskMappingTemplateData(Collection<TaskConfigData> parentTasks, Collection<EnumOptionData> taskConfigEntityTypeOptions) {
        this.parentTasks = parentTasks;
        this.taskConfigEntityTypeOptions = taskConfigEntityTypeOptions;
    }

    public Collection<TaskConfigData> getParentTasks() {
        return this.parentTasks;
    }

    public Collection<EnumOptionData> getTaskConfigEntityTypeOptions() {
        return this.taskConfigEntityTypeOptions;
    }

}
