package com.finflux.infrastructure.external.requestreponse.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ThirdPartyRequestResponseLogRepository extends JpaRepository<ThirdPartyRequestResponseLog, Long>,
        JpaSpecificationExecutor<ThirdPartyRequestResponseLog> {

}