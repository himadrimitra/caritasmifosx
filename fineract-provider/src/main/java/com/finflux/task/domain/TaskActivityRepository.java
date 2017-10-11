package com.finflux.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long>, JpaSpecificationExecutor<TaskActivity> {

	TaskActivity findOneByIdentifier(final String identifier);
}