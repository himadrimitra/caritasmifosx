package com.finflux.task.service;

import com.finflux.task.data.TaskConfigData;
import com.finflux.task.data.TaskConfigTemplateData;

public interface TaskConfigReadService {

    TaskConfigTemplateData retrieveForLookUp(final Long parentConfigId);

    TaskConfigTemplateData retrieveTemplate();

    TaskConfigData retrieveOne(Long configId);
}
