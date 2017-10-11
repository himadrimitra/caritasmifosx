package com.finflux.task.configuration.service;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;

@Component
@Scope("singleton")
public class TaskConfigurationUtils {

    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public TaskConfigurationUtils(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final ConfigurationDomainService configurationDomainService) {
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.configurationDomainService = configurationDomainService;
    }

    public boolean isWorkflowEnabled(final TaskConfigEntityType taskConfigEntityType) {
        boolean isEnabled = false;
        if (this.configurationDomainService.isWorkFlowEnabled() && taskConfigEntityType != null) {
            final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                    .findOneByEntityTypeAndEntityId(taskConfigEntityType.getValue(), -1L);
            if (taskConfigEntityTypeMapping != null) {
                isEnabled = true;
            }
        }
        return isEnabled;
    }
}
