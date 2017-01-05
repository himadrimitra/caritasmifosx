package com.finflux.risk.profilerating.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.exception.ProfileRatingScoreNotFoundException;

@Service
public class ProfileRatingScoreRepositoryWrapper {

    private final ProfileRatingScoreRepository repository;

    @Autowired
    public ProfileRatingScoreRepositoryWrapper(final ProfileRatingScoreRepository repository) {
        this.repository = repository;
    }

    public ProfileRatingScore findOneWithNotFoundDetection(final Long profileRatingScoreId) {
        final ProfileRatingScore profileRatingScore = this.repository.findOne(profileRatingScoreId);
        if (profileRatingScore == null) { throw new ProfileRatingScoreNotFoundException(profileRatingScoreId); }
        return profileRatingScore;
    }

    public void save(final ProfileRatingScore profileRatingScore) {
        this.repository.save(profileRatingScore);
    }

    public void save(final List<ProfileRatingScore> profileRatingScore) {
        this.repository.save(profileRatingScore);
    }

    public void saveAndFlush(final ProfileRatingScore profileRatingScore) {
        this.repository.saveAndFlush(profileRatingScore);
    }

    public void delete(final ProfileRatingScore profileRatingScore) {
        this.repository.delete(profileRatingScore);
    }

    public ProfileRatingScore findOneByEntityTypeAndEntityId(final Integer entityType, final Long entityId) {
        return this.repository.findOneByEntityTypeAndEntityId(entityType, entityId);
    }
}
