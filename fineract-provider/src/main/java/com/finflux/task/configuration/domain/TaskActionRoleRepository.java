package com.finflux.task.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface TaskActionRoleRepository extends JpaRepository<TaskActionRole, Long>, JpaSpecificationExecutor<TaskActionRole> {

	List<TaskActionRole> findByTaskActionId(Long taskActionId);
}
