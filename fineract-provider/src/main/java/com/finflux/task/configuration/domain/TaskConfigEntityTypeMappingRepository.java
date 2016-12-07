package com.finflux.task.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskConfigEntityTypeMappingRepository extends JpaRepository<TaskConfigEntityTypeMapping, Long>,
        JpaSpecificationExecutor<TaskConfigEntityTypeMapping> {

    TaskConfigEntityTypeMapping findOneByEntityTypeAndEntityId(final Integer entityType, final Long entityId);
}