package com.finflux.workflow.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long>,
        JpaSpecificationExecutor<WorkflowStep> {

}
