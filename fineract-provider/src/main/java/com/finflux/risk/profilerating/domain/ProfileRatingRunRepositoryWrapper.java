package com.finflux.risk.profilerating.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.exception.ProfileRatingRunNotFoundException;

@Service
public class ProfileRatingRunRepositoryWrapper {

    private final ProfileRatingRunRepository repository;

    @Autowired
    public ProfileRatingRunRepositoryWrapper(final ProfileRatingRunRepository repository) {
        this.repository = repository;
    }

    public ProfileRatingRun findOneWithNotFoundDetection(final Long profileRatingRunId) {
        final ProfileRatingRun profileRatingRun = this.repository.findOne(profileRatingRunId);
        if (profileRatingRun == null) { throw new ProfileRatingRunNotFoundException(profileRatingRunId); }
        return profileRatingRun;
    }

    public void save(final ProfileRatingRun profileRatingRun) {
        this.repository.save(profileRatingRun);
    }

    public void save(final List<ProfileRatingRun> profileRatingRun) {
        this.repository.save(profileRatingRun);
    }

    public void saveAndFlush(final ProfileRatingRun profileRatingRun) {
        this.repository.saveAndFlush(profileRatingRun);
    }

    public void delete(final ProfileRatingRun profileRatingRun) {
        this.repository.delete(profileRatingRun);
    }
}
