package com.finflux.risk.profilerating.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;
import com.finflux.ruleengine.configuration.domain.RuleModel;

@Entity
@Table(name = "f_profile_rating_config", uniqueConstraints = { @UniqueConstraint(columnNames = { "type" }, name = "f_profile_rating_config_UNIQUE") })
public class ProfileRatingConfig extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "type", length = 3, nullable = false)
    private Integer type;

    @ManyToOne
    @JoinColumn(name = "criteria_id")
    private RuleModel criteria;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    protected ProfileRatingConfig() {}

    private ProfileRatingConfig(final Integer type, final RuleModel criteria, final boolean isActive) {
        this.type = type;
        this.criteria = criteria;
        this.isActive = isActive;
    }

    public static ProfileRatingConfig create(final Integer type, final RuleModel criteria, final boolean isActive) {
        return new ProfileRatingConfig(type, criteria, isActive);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInIntegerParameterNamed(ProfileRatingConfigApiConstants.typeParamName, this.type)) {
            final Integer newValue = command.integerValueOfParameterNamed(ProfileRatingConfigApiConstants.typeParamName);
            actualChanges.put(ProfileRatingConfigApiConstants.typeParamName, newValue);
            this.type = newValue;
        }

        if (command.isChangeInLongParameterNamed(ProfileRatingConfigApiConstants.criteriaIdParamName, this.criteria.getId())) {
            final Long newValue = command.longValueOfParameterNamed(ProfileRatingConfigApiConstants.criteriaIdParamName);
            actualChanges.put(ProfileRatingConfigApiConstants.criteriaIdParamName, newValue);
        }

        if (command.isChangeInBooleanParameterNamed(ProfileRatingConfigApiConstants.isActiveParamName, this.isActive)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(ProfileRatingConfigApiConstants.isActiveParamName);
            actualChanges.put(ProfileRatingConfigApiConstants.isActiveParamName, newValue);
            this.isActive = newValue;
        }
        return actualChanges;
    }

    public void updateCriteria(final RuleModel criteria) {
        this.criteria = criteria;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void inActivate() {
        this.isActive = false;
    }

    public RuleModel getCriteria() {
        return this.criteria;
    }
}