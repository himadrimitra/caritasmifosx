package com.finflux.workflow.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkflowEntityTypeMappingRepository extends JpaRepository<WorkflowEntityTypeMapping, Long>,
        JpaSpecificationExecutor<WorkflowEntityTypeMapping> {

    WorkflowEntityTypeMapping findOneByEntityTypeAndEntityId(final Integer entityType, final Long entityId);
}