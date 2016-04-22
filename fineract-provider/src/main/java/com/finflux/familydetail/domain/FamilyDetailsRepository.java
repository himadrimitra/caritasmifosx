package com.finflux.familydetail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FamilyDetailsRepository extends JpaRepository<FamilyDetail, Long>, JpaSpecificationExecutor<FamilyDetail> {

    FamilyDetail findByIdAndClientId(final Long id, final Long clientId);

}
