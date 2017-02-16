package com.finflux.task.template.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface TaskConfigTemplateRepository extends JpaRepository<TaskConfigTemplate,Long>,JpaSpecificationExecutor<TaskConfigTemplate>{

}
