package com.finflux.infrastructure.gis.taluka.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TalukaRepository extends JpaRepository<Taluka, Long>, JpaSpecificationExecutor<Taluka> {

}
