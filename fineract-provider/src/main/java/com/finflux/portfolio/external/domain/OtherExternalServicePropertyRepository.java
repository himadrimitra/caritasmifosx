package com.finflux.portfolio.external.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OtherExternalServicePropertyRepository extends JpaRepository<OtherExternalServiceProperty, Long>, JpaSpecificationExecutor<OtherExternalServiceProperty> {

	OtherExternalServiceProperty findByExternalServiceIdAndName(final Long externalServiceId, final String name);

}
