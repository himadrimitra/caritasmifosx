package com.finflux.task.domain;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;
import org.apache.commons.lang.WordUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClientBankAccountWorkflow implements WorkflowCreator {
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final TaskPlatformWriteService taskPlatformWriteService;

    @Autowired
    public ClientBankAccountWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final TaskPlatformWriteService taskPlatformWriteService) {
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.taskPlatformWriteService = taskPlatformWriteService;
    }

    @Override
    public Boolean createWorkFlow(WorkflowDTO workflowDTO) {
        TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.CLIENTBANKACCOUNT.getValue(), -1L);
        Client client = workflowDTO.getClient();
        if (taskConfigEntityTypeMapping != null) {
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.CLIENT_ID, String.valueOf(client.getId()));
            AppUser assignedTo = null;
            Date dueDate = null;
            Date dueTime = null;
            String description = "Bank Account Creation for Client #" + client.getId()
                    + WordUtils.capitalizeFully(client.getFirstname() + " " + client.getLastname()) + " for office - "
                    + WordUtils.capitalizeFully(client.getOfficeName());
            final StringBuilder shortDescription = new StringBuilder();
            shortDescription.append("Bank Account Creation for Client ");
            shortDescription.append(WordUtils.capitalizeFully(client.getFirstname() + " " + client.getLastname()));
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
                    TaskEntityType.CLIENT_BANKACCOUNT, client.getId(), client, assignedTo, dueDate, client.getOffice(),
                    map, description, shortDescription.toString(), dueTime);
            return true;
        }
        return false;// TODO Auto-generated method stub
    }

}
