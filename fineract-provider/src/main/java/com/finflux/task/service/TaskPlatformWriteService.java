package com.finflux.task.service;

import java.util.Map;

import com.finflux.task.domain.TaskActivity;

import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;

import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;

public interface TaskPlatformWriteService {

    void createTaskFromConfig(final Long taskConfigId, final TaskEntityType entityType, final Long entityId, final Client client,
							  final Office office, final Map<TaskConfigKey, String> configValues, String description);

    Long createSingleTask(TaskActivity taskActivity, String title, Office office, Map<TaskConfigKey, String> map,
						  Long actionGroupId);
}
