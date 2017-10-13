package com.finflux.task.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskConfigEntityTypeMappingRepository extends JpaRepository<TaskConfigEntityTypeMapping, Long>,
        JpaSpecificationExecutor<TaskConfigEntityTypeMapping> {

    TaskConfigEntityTypeMapping findOneByEntityTypeAndEntityId(final Integer entityType, final Long entityId);

    @Query("from TaskConfigEntityTypeMapping tem where tem.taskConfig.id = :taskConfigId and tem.entityType = :entityType")
    List<TaskConfigEntityTypeMapping> findByTaskConfigIdAndEntityType(@Param("taskConfigId") final Long taskConfigId,
            @Param("entityType") final Integer entityType);
}