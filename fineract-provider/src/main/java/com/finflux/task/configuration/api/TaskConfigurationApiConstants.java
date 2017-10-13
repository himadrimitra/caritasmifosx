package com.finflux.task.configuration.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TaskConfigurationApiConstants {

    public static final String TASK_CONFIG_RESOURCE_NAME = "taskconfig";
    public static final String TASK_CONFIG_ENTITYMAPPING_RESOURCE_NAME = "taskconfigentitymapping";

    public static final String INVALID = "INVALID";
    public static final String LOANPRODUCT = "LOANPRODUCT";
    public static final String entityTypeParamName = "entityType";
    public static final String entityIdParamName = "entityId";
    public static final String tasksParamName = "tasks";
    public static final String taskActivityIdParamName = "taskActivityId";
    public static final String nameParamName = "name";
    public static final String shortNameParamName = "shortName";
    public static final String taskConfigIdParamName = "taskConfigId";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String entityIdsParamName = "entityIds";
    public static final String isActiveParamName = "isActive";

    public static final Set<String> CREATE_LOAN_PRODUCT_WORKFLOW_TASK_CONFIG_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            entityTypeParamName, entityIdParamName, tasksParamName, localeParamName, dateFormatParamName));

    public static final Set<String> CREATE_LOAN_PRODUCT_WORKFLOW_EACH_TASK_CONFIG_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            taskActivityIdParamName, nameParamName, shortNameParamName, localeParamName, dateFormatParamName));

    public static final Set<String> CREATE_TASK_CONFIG_ENTITYMAPPING_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            entityTypeParamName, entityIdsParamName, isActiveParamName, localeParamName, taskConfigIdParamName));
}