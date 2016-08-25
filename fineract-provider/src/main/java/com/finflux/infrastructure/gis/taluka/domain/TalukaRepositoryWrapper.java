package com.finflux.infrastructure.gis.taluka.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.taluka.exception.TalukaNotFoundException;

@Service
public class TalukaRepositoryWrapper {
    
    private final TalukaRepository repository;
    
    @Autowired
    public TalukaRepositoryWrapper(final TalukaRepository repository) {
        this.repository = repository;
    }

    public Taluka findOneWithNotFoundDetection(final Long talukaId) {
        final Taluka taluka = this.repository.findOne(talukaId);
        if (taluka == null) { throw new TalukaNotFoundException(talukaId); }
        return taluka;
    }

    public void save(final Taluka taluka) {
        this.repository.save(taluka);
    }

    public void saveAndFlush(final Taluka taluka) {
        this.repository.saveAndFlush(taluka);
    }

    public void delete(final Taluka taluka) {
        this.repository.delete(taluka);
    }

}
