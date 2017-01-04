package com.finflux.risk.profilerating.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileRatingScoreHistoryRepositoryWrapper {

    private final ProfileRatingScoreHistoryRepository repository;

    @Autowired
    public ProfileRatingScoreHistoryRepositoryWrapper(final ProfileRatingScoreHistoryRepository repository) {
        this.repository = repository;
    }

    public void save(final ProfileRatingScoreHistory profileRatingScoreHistory) {
        this.repository.save(profileRatingScoreHistory);
    }

    public void save(final List<ProfileRatingScoreHistory> profileRatingScoreHistory) {
        this.repository.save(profileRatingScoreHistory);
    }

    public void saveAndFlush(final ProfileRatingScoreHistory profileRatingScoreHistory) {
        this.repository.saveAndFlush(profileRatingScoreHistory);
    }

    public void delete(final ProfileRatingScoreHistory profileRatingScoreHistory) {
        this.repository.delete(profileRatingScoreHistory);
    }
}