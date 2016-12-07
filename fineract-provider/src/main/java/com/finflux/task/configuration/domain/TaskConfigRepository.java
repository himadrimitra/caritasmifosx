package com.finflux.task.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskConfigRepository extends JpaRepository<TaskConfig, Long>, JpaSpecificationExecutor<TaskConfig> {

}