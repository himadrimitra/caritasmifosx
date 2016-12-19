package com.finflux.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskActionRepository extends JpaRepository<TaskAction, Long>, JpaSpecificationExecutor<TaskAction> {

	TaskAction findOneByActionGroupIdAndAction(Long actionGroupId, Integer action);
}
