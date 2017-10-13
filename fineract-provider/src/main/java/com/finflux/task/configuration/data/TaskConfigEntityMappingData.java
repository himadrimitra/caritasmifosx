package com.finflux.task.configuration.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.common.data.EntityData;
import com.finflux.task.data.TaskConfigData;

public class TaskConfigEntityMappingData {

    private TaskConfigData taskConfig;
    private EnumOptionData entityType;
    private Collection<EntityData> entityDetails;
    private boolean isActive;

    public TaskConfigEntityMappingData(final TaskConfigData taskConfig, final EnumOptionData entityType,
            final Collection<EntityData> entityDetails, final boolean isActive) {
        this.taskConfig = taskConfig;
        this.entityType = entityType;
        this.entityDetails = entityDetails;
        this.isActive = isActive;
    }

    public TaskConfigData getTaskConfig() {
        return this.taskConfig;
    }

    public EnumOptionData getEntityType() {
        return this.entityType;
    }

    public Collection<EntityData> getEntityDetails() {
        return this.entityDetails;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

}
