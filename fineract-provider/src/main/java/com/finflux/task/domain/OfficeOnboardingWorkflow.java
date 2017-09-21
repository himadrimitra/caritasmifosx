/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.task.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;

@Component
public class OfficeOnboardingWorkflow implements WorkflowCreator {

    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final TaskPlatformWriteService taskPlatformWriteService;

    @Autowired
    public OfficeOnboardingWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final TaskPlatformWriteService taskPlatformWriteService) {
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.taskPlatformWriteService = taskPlatformWriteService;
    }

    @Override
    public Boolean createWorkFlow(WorkflowDTO workflowDTO) {
        boolean isSuccess = false;
        final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.OFFICEONBOARDING.getValue(), -1L);
        final Office office = workflowDTO.getOffice();
        if (taskConfigEntityTypeMapping != null) {
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.OFFICE_ID, String.valueOf(office.getId()));
            final Client client = null;
            final AppUser assignedTo = null;
            final Date dueDate = null;
            final Date dueTime = null;
            final String description, shortDescription;
            description = shortDescription = office.getId() + "- New Office " + WordUtils.capitalizeFully(office.getName());
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(), TaskEntityType.OFFICE,
                    office.getId(), client, assignedTo, dueDate, office, map, description, shortDescription.toString(), dueTime);
            isSuccess = true;
        }
        return isSuccess;
    }

}
