package com.finflux.infrastructure.external.authentication.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondaryAuthenticationServiceRepository extends JpaRepository<SecondaryAuthenticationService, Long> {

}
