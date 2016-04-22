package com.finflux.familydetail.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FamilyDetailsRepositoryWrapers {

    private final FamilyDetailsRepository repository;

    @Autowired
    public FamilyDetailsRepositoryWrapers(final FamilyDetailsRepository repository) {
        this.repository = repository;
    }

    public FamilyDetail findByIdAndClientId(final Long familyDeatailId, final Long clientId) {
        return this.repository.findByIdAndClientId(clientId, familyDeatailId);
    }

    public void save(final FamilyDetail familyDetails) {
        this.repository.save(familyDetails);
    }

    public void delete(final FamilyDetail familyDetails) {
        this.repository.delete(familyDetails);
    }

}
