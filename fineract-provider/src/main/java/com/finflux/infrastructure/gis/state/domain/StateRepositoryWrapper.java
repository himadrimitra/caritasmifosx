package com.finflux.infrastructure.gis.state.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.country.domain.Country;
import com.finflux.infrastructure.gis.state.exception.StateNotFoundException;

@Service
public class StateRepositoryWrapper {

    private final StateRepository repository;

    @Autowired
    public StateRepositoryWrapper(final StateRepository repository) {
        this.repository = repository;
    }

    public State findOneWithNotFoundDetection(final Long stateId) {
        final State state = this.repository.findOne(stateId);
        if (state == null) { throw new StateNotFoundException(stateId); }
        return state;
    }

    public void save(final State state) {
        this.repository.save(state);
    }

    public void saveAndFlush(final State state) {
        this.repository.saveAndFlush(state);
    }

    public void delete(final State state) {
        this.repository.delete(state);
    }
}
