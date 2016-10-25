package com.finflux.workflow.execution.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkflowExecutionStepRepository extends JpaRepository<WorkflowExecutionStep, Long>,
        JpaSpecificationExecutor<WorkflowExecutionStep> {

}
