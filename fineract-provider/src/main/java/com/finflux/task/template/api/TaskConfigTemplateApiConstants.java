package com.finflux.task.template.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class TaskConfigTemplateApiConstants 
{
    public static final String TEMPLATE_CONFIG_NAME = "taskTemplateConfiguration";
    
    public static final String taskNameParamName = "taskName";
    public static final String shortNameParamName = "shortName";
    public static final String selectedEntityParamName = "entity_id";
    public static final String selectedActivityParamName = "activity_id";
    
    public static final Set<String> CREATE_TASK_TEMPLATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            taskNameParamName, shortNameParamName,selectedEntityParamName,selectedActivityParamName));

}
