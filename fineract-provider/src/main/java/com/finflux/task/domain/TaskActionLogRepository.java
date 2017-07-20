package com.finflux.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

public interface TaskActionLogRepository extends JpaRepository<TaskActionLog, Long>, JpaSpecificationExecutor<TaskActionLog> {

    List<TaskActionLog> findByTaskIdAndActionOrderByIdDesc(Long taskId, Integer action);

}