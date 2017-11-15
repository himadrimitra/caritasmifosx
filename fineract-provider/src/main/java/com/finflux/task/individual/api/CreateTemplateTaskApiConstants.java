package com.finflux.task.individual.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CreateTemplateTaskApiConstants 
{
 
    public static final String TASK_ASSIGN = "taskAssign";
    public static final String TemplateIdParamName = "templateId";
    public static final String UserIdParamName = "userId";
    public static final String EntityIdParamName = "entityId";
    public static final String DueDateParamName = "duedate";
    public static final String DueTimeParamName = "dueTime";
    public static final String DateFormat = "dateFormat";
    public static final String descriptionParamName = "description";
    public static final String Locale = "locale";
    public static final String TIME_FORMAT = "timeFormat";
    
    public static final Set<String> ASSIGN_TASK_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            TemplateIdParamName,descriptionParamName, UserIdParamName,EntityIdParamName,DueDateParamName,Locale,DateFormat,DueTimeParamName, TIME_FORMAT));
}