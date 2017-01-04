package com.finflux.risk.profilerating.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRatingScoreRepository extends JpaRepository<ProfileRatingScore, Long>, JpaSpecificationExecutor<ProfileRatingScore> {

    ProfileRatingScore findOneByEntityTypeAndEntityId(Integer entityType, Long entityId);

}