package com.finflux.ruleengine.configuration.service;

import com.finflux.ruleengine.configuration.data.FieldData;
import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.lib.data.Rule;

import java.util.List;

/**
 * Created by dhirendra on 15/09/16.
 */
public interface RiskConfigReadPlatformService {

    List<FieldData> getAllFields();

    List<RuleData> getAllFactors();

    RuleData retrieveOneFactor(Long factorId);

    List<RuleData> getAllDimensions();

    RuleData retrieveOneDimension(Long dimensionId);

    List<RuleData> getAllCriterias();

    RuleData retrieveOneCriteria(Long criteriaId);

    List<RuleData> getAllRules();

	RuleData retrieveRuleByUname(String uname);

	Rule getRuleById(Long ruleId);
}
