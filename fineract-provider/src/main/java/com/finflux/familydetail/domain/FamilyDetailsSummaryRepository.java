package com.finflux.familydetail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FamilyDetailsSummaryRepository extends JpaRepository<FamilyDetailsSummary, Long>, JpaSpecificationExecutor<FamilyDetailsSummary> {

}
