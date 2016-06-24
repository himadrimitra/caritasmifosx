package com.finflux.infrastructure.gis.state.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StateRepository extends JpaRepository<State, Long>, JpaSpecificationExecutor<State> {

}