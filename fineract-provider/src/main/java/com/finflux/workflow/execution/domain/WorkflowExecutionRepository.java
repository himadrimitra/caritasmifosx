package com.finflux.workflow.execution.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long>, JpaSpecificationExecutor<WorkflowExecution> {

    WorkflowExecution findByEntityTypeAndEntityId(final Integer entityType, final Long entityId);
}