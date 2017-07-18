package com.finflux.kyc.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientKycDetailsRepository extends JpaRepository<ClientKycDetails, Long>, JpaSpecificationExecutor<ClientKycDetails>{

}
