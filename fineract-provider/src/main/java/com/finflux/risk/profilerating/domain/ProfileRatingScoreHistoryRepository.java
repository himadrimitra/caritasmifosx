package com.finflux.risk.profilerating.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRatingScoreHistoryRepository extends JpaRepository<ProfileRatingScoreHistory, Long>,
        JpaSpecificationExecutor<ProfileRatingScoreHistory> {

}