package com.finflux.infrastructure.gis.district.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.district.exception.DistrictNotFoundException;
import com.finflux.infrastructure.gis.state.domain.State;

@Service
public class DistrictRepositoryWrapper {

    private final DistrictRepository repository;

    @Autowired
    public DistrictRepositoryWrapper(final DistrictRepository repository) {
        this.repository = repository;
    }

    public District findOneWithNotFoundDetection(final Long districtId) {
        final District district = this.repository.findOne(districtId);
        if (district == null) { throw new DistrictNotFoundException(districtId); }
        return district;
    }

    public void save(final District district) {
        this.repository.save(district);
    }

    public void saveAndFlush(final District district) {
        this.repository.saveAndFlush(district);
    }

    public void delete(final District district) {
        this.repository.delete(district);
    }

    public List<District> findByEntityTypeIdAndEntityId(final State state) {
        return this.repository.findByState(state);
    }
}
