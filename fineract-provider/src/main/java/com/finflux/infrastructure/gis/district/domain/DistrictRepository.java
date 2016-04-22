package com.finflux.infrastructure.gis.district.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.finflux.infrastructure.gis.state.domain.State;

public interface DistrictRepository extends JpaRepository<District, Long>, JpaSpecificationExecutor<District> {

    List<District> findByState(final State state);

}