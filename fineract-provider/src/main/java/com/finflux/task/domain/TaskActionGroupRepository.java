package com.finflux.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskActionGroupRepository extends JpaRepository<TaskActionGroup, Long>, JpaSpecificationExecutor<TaskActionGroup> {

    TaskActionGroup findOneByIdentifier(final String identifier);
}
