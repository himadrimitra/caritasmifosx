package com.finflux.fileprocess.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileProcessRepository extends JpaRepository<FileProcess, Long>, JpaSpecificationExecutor<FileProcess> {

    List<FileProcess> findByStatusIn(final List<Integer> statuses);
}
