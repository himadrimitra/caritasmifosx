package com.finflux.task.configuration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.task.configuration.api.TaskConfigurationApiConstants;
import com.finflux.task.data.TaskType;
import com.finflux.task.domain.TaskActivity;
import com.finflux.task.domain.TaskActivityRepository;
import com.finflux.task.domain.TaskConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class TaskConfigurationDataAssembler {

    private final JdbcTemplate jdbcTemplate;
    private final FromJsonHelper fromApiJsonHelper;
    private final TaskActivityRepository taskActivityRepository;

    @Autowired
    public TaskConfigurationDataAssembler(final RoutingDataSource dataSource, final FromJsonHelper fromApiJsonHelper,
            final TaskActivityRepository taskActivityRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.taskActivityRepository = taskActivityRepository;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<TaskConfig> assembleCreateLoanProductWorkflowTasksForm(final Long entityId, final JsonCommand command) {
        final JsonElement element = this.fromApiJsonHelper.parse(command.json());
        final List<TaskConfig> taskConfigs = new ArrayList();
        final TaskConfig parentTaskConfig = new TaskConfig();

        final Map<String, Object> entityMap = this.jdbcTemplate
                .queryForMap("SELECT pl.name entityName,pl.short_name AS shortName FROM m_product_loan pl WHERE pl.id = " + entityId
                        + "");
        parentTaskConfig.setName((String) entityMap.get("entityName")+" Workflow");
        parentTaskConfig.setShortName((String) entityMap.get("shortName"));
        parentTaskConfig.setTaskType(TaskType.WORKFLOW.getValue());
        taskConfigs.add(parentTaskConfig);
        final JsonArray tasks = this.fromApiJsonHelper.extractJsonArrayNamed(TaskConfigurationApiConstants.tasksParamName, element);
        if (tasks != null && tasks.size() > 0) {
            for (int i = 0; i < tasks.size(); i++) {
                final JsonObject e = tasks.get(i).getAsJsonObject();
                final TaskConfig taskConfig = assembleCreateLoanProductWorkflowTaskFormEachObject(e, parentTaskConfig);
                taskConfigs.add(taskConfig);
            }
        }
        return taskConfigs;
    }

    private TaskConfig assembleCreateLoanProductWorkflowTaskFormEachObject(final JsonObject element, final TaskConfig parentTaskConfig) {
        final Long taskActivityId = this.fromApiJsonHelper.extractLongNamed(TaskConfigurationApiConstants.taskActivityIdParamName, element);
        final TaskActivity taskActivity = this.taskActivityRepository.findOne(taskActivityId);
        final String name = this.fromApiJsonHelper.extractStringNamed(TaskConfigurationApiConstants.nameParamName, element);
        final String shortName = this.fromApiJsonHelper.extractStringNamed(TaskConfigurationApiConstants.shortNameParamName, element);
        final TaskConfig taskConfig = new TaskConfig();
        taskConfig.setParent(parentTaskConfig);
        taskConfig.setName(name);
        taskConfig.setShortName(shortName);
        taskConfig.setTaskActivity(taskActivity);
        taskConfig.setTaskType(TaskType.SINGLE.getValue());
        return taskConfig;
    }
}