package com.finflux.risk.profilerating.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.exception.ProfileRatingConfigNotFoundException;

@Service
public class ProfileRatingConfigRepositoryWrapper {

    private final ProfileRatingConfigRepository repository;

    @Autowired
    public ProfileRatingConfigRepositoryWrapper(final ProfileRatingConfigRepository repository) {
        this.repository = repository;
    }

    public ProfileRatingConfig findOneWithNotFoundDetection(final Long profileRatingConfigId) {
        final ProfileRatingConfig profileRatingConfig = this.repository.findOne(profileRatingConfigId);
        if (profileRatingConfig == null) { throw new ProfileRatingConfigNotFoundException(profileRatingConfigId); }
        return profileRatingConfig;
    }

    public void save(final ProfileRatingConfig profileRatingConfig) {
        this.repository.save(profileRatingConfig);
    }

    public void save(final List<ProfileRatingConfig> profileRatingConfiges) {
        this.repository.save(profileRatingConfiges);
    }

    public void saveAndFlush(final ProfileRatingConfig profileRatingConfig) {
        this.repository.saveAndFlush(profileRatingConfig);
    }

    public void delete(final ProfileRatingConfig profileRatingConfig) {
        this.repository.delete(profileRatingConfig);
    }
}
