package com.finflux.familydetail.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.exception.FamilyDetailsSummaryNotFoundException;

@Service
public class FamilyDetailsSummaryRepositoryWrapers {

    private final FamilyDetailsSummaryRepository repository;

    @Autowired
    public FamilyDetailsSummaryRepositoryWrapers(final FamilyDetailsSummaryRepository repository) {
        this.repository = repository;
    }

    public FamilyDetailsSummary findOneWithNotFoundDetection(final Long id) {
        final FamilyDetailsSummary familyDetailsSummary = this.repository.findOne(id);
        if (familyDetailsSummary == null) { throw new FamilyDetailsSummaryNotFoundException(id); }
        return familyDetailsSummary;
    }

    public void save(final FamilyDetailsSummary familyDetailsSummary) {
        this.repository.save(familyDetailsSummary);
    }

    public void save(final List<FamilyDetailsSummary> familyDetailsSummary) {
        this.repository.save(familyDetailsSummary);
    }

    public void saveAndFlush(final FamilyDetailsSummary familyDetailsSummary) {
        this.repository.saveAndFlush(familyDetailsSummary);
    }

    public void delete(final FamilyDetailsSummary familyDetailsSummary) {
        this.repository.delete(familyDetailsSummary);
    }

}
