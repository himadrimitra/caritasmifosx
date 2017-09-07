package com.finflux.fileprocess.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileProcessRepository extends JpaRepository<FileProcess, Long>, JpaSpecificationExecutor<FileProcess> {

}
