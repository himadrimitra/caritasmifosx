package com.finflux.workflow.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface WorkflowStepActionRoleRepository extends JpaRepository<WorkflowStepActionRole, Long>,
        JpaSpecificationExecutor<WorkflowStepActionRole> {

    List<WorkflowStepActionRole> findByWorkflowStepActionId(Long workflowStepActionId);
}
