package com.finflux.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskNoteRepository extends JpaRepository<TaskNote, Long>, JpaSpecificationExecutor<TaskNote> {

}