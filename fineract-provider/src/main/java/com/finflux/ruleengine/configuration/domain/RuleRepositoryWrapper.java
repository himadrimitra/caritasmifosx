package com.finflux.ruleengine.configuration.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.configuration.exception.RuleNotFoundException;

@Service
public class RuleRepositoryWrapper {

    private final RuleRepository repository;

    @Autowired
    public RuleRepositoryWrapper(final RuleRepository repository) {
        this.repository = repository;
    }

    public RuleModel findOneWithNotFoundDetection(final Long id) {
        final RuleModel ruleModel = this.repository.findOne(id);
        if (ruleModel == null) { throw new RuleNotFoundException(id); }
        return ruleModel;
    }

    public void save(final RuleModel ruleModel) {
        this.repository.save(ruleModel);
    }

    public void save(final List<RuleModel> ruleModel) {
        this.repository.save(ruleModel);
    }

    public void saveAndFlush(final RuleModel ruleModel) {
        this.repository.saveAndFlush(ruleModel);
    }

    public void delete(final RuleModel ruleModel) {
        this.repository.delete(ruleModel);
    }
}