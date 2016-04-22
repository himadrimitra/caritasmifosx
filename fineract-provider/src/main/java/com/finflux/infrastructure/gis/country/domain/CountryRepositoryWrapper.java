package com.finflux.infrastructure.gis.country.domain;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.country.exception.CountryNotFoundException;

@Service
public class CountryRepositoryWrapper {

    private final CountryRepository repository;

    @Autowired
    public CountryRepositoryWrapper(final CountryRepository repository) {
        this.repository = repository;
    }

    public Country findOneWithNotFoundDetection(final Long countryId) {
        final Country country = this.repository.findOne(countryId);
        if (country == null) { throw new CountryNotFoundException(countryId); }
        return country;
    }

    public Collection<Country> findAllWithNotFoundDetection() {
        return this.repository.findAll();
    }

    public void save(final Country country) {
        this.repository.save(country);
    }

    public void saveAndFlush(final Country country) {
        this.repository.saveAndFlush(country);
    }

    public void delete(final Country country) {
        this.repository.delete(country);
    }
}
