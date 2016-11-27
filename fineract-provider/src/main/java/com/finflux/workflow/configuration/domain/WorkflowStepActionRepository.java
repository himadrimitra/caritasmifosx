package com.finflux.workflow.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkflowStepActionRepository extends JpaRepository<WorkflowStepAction, Long>, JpaSpecificationExecutor<WorkflowStepAction> {

    WorkflowStepAction findOneByActionGroupIdAndAction(Long actionGroupId, Integer action);
}