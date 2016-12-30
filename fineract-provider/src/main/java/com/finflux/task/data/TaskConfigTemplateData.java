package com.finflux.task.data;

import java.util.Collection;

import org.apache.fineract.organisation.office.data.OfficeData;

public class TaskConfigTemplateData {

    private final Collection<OfficeData> officeOptions;
    private final Collection<TaskConfigData> taskConfigs;

    public TaskConfigTemplateData(final Collection<OfficeData> officeOptions, final Collection<TaskConfigData> taskConfigs) {
        this.officeOptions = officeOptions;
        this.taskConfigs = taskConfigs;
    }

    public static TaskConfigTemplateData template(final Collection<OfficeData> officeOptions,
            final Collection<TaskConfigData> taskConfigs) {
        return new TaskConfigTemplateData(officeOptions, taskConfigs);
    }

    /**
     * @return the officeOptions
     */
    public Collection<OfficeData> getOfficeOptions() {
        return this.officeOptions;
    }

    /**
     * @return the taskConfigs
     */
    public Collection<TaskConfigData> getTaskConfigs() {
        return this.taskConfigs;
    }

}
