package com.finflux.task.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.village.domain.Village;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;

@Component
public class VillageOnboardingWorkflow implements WorkflowCreator {

    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final TaskPlatformWriteService taskPlatformWriteService;

    @Autowired
    public VillageOnboardingWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final TaskPlatformWriteService taskPlatformWriteService) {
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.taskPlatformWriteService = taskPlatformWriteService;
    }

    @Override
    public Boolean createWorkFlow(WorkflowDTO workflowDTO) {
        boolean isSuccess = false ;
        final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.VILLAGEONBOARDING.getValue(), -1L);
        Village village = workflowDTO.getVillage();
        if (taskConfigEntityTypeMapping != null) {
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.VILLAGE_ID, String.valueOf(village.getId()));
            Client client = null ;
            AppUser assignedTo = null;
            Date dueDate = null;
            /*String description = village.getId() + "- New Village "
                    + WordUtils.capitalizeFully(village.getVillageName()) + " for office - "
                    + WordUtils.capitalizeFully(village.getOfficeName());*/
            String description = village.getId() + "- New Village "
                    + WordUtils.capitalizeFully(village.getVillageName());
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
                    TaskEntityType.VILLAGE, village.getId(), client, assignedTo, dueDate, village.getOffice(), map,
                    description);
            isSuccess = true ;
        }
        return isSuccess ;
    }

}
