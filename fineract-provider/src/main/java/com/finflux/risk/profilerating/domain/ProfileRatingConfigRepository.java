package com.finflux.risk.profilerating.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRatingConfigRepository extends JpaRepository<ProfileRatingConfig, Long>,
        JpaSpecificationExecutor<ProfileRatingConfig> {

    ProfileRatingConfig findByType(final Integer type);
}