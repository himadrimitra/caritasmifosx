package com.finflux.task.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;

public class TaskConfigTemplateData {

    private final Collection<OfficeData> officeOptions;
    private final Collection<TaskConfigData> taskConfigs;
    private final Collection<EnumOptionData> loanAccoutTypeOptions;

    public TaskConfigTemplateData(final Collection<OfficeData> officeOptions, final Collection<TaskConfigData> taskConfigs,
            final Collection<EnumOptionData> loanAccoutTypeOptions) {
        this.officeOptions = officeOptions;
        this.taskConfigs = taskConfigs;
        this.loanAccoutTypeOptions = loanAccoutTypeOptions;
    }

    public static TaskConfigTemplateData template(final Collection<OfficeData> officeOptions,
            final Collection<TaskConfigData> taskConfigs, final Collection<EnumOptionData> loanAccoutTypeOptions) {
        return new TaskConfigTemplateData(officeOptions, taskConfigs, loanAccoutTypeOptions);
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
