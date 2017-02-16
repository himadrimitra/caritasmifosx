package com.finflux.task.template.service;

import java.util.List;

import com.finflux.task.template.data.TaskConfigTemplateEntityData;
import com.finflux.task.template.data.TaskConfigTemplateObject;


public interface TaskConfigTemplateReadService {

    List<TaskConfigTemplateObject> retrieveTemplateData();
    TaskConfigTemplateObject readOneTask(Long templateId);
    TaskConfigTemplateEntityData retrieveTemplateEntities(Long templateId);

}
