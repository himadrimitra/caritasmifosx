package com.finflux.risk.profilerating.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRatingRunRepository extends JpaRepository<ProfileRatingRun, Long>, JpaSpecificationExecutor<ProfileRatingRun> {

}