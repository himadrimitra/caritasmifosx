package com.finflux.infrastructure.gis.state.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.finflux.infrastructure.gis.country.domain.Country;

public interface StateRepository extends JpaRepository<State, Long>, JpaSpecificationExecutor<State> {

    List<State> findByCountry(final Country country);
}