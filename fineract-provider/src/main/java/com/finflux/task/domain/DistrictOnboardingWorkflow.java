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

import com.finflux.infrastructure.gis.district.domain.District;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;

@Component
public class DistrictOnboardingWorkflow implements WorkflowCreator {

    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final TaskPlatformWriteService taskPlatformWriteService;

    @Autowired
    public DistrictOnboardingWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final TaskPlatformWriteService taskPlatformWriteService) {
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.taskPlatformWriteService = taskPlatformWriteService;
    }

    @Override
    public Boolean createWorkFlow(final WorkflowDTO workflowDTO) {
        boolean isSuccess = false;
        final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.DISTRICTONBOARDING.getValue(), -1L);
        final District district = workflowDTO.getDistrict();
        if (taskConfigEntityTypeMapping != null) {
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.DISTRICT_ID, String.valueOf(district.getId()));
            final Client client = null;
            final Office office = null;
            final AppUser assignedTo = null;
            final Date dueDate = null;
            final Date dueTime = null;
            final String description, shortDescription;
            description = shortDescription = district.getId() + "- New District " + WordUtils.capitalizeFully(district.getDistrictName());
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(), TaskEntityType.DISTRICT,
                    district.getId(), client, assignedTo, dueDate, office, map, description, shortDescription.toString(), dueTime);
            isSuccess = true;
        }
        return isSuccess;
    }

}
