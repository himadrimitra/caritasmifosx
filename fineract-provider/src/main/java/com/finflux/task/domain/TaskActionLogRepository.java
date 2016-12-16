package com.finflux.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskActionLogRepository extends JpaRepository<TaskActionLog, Long>, JpaSpecificationExecutor<TaskActionLog> {

}