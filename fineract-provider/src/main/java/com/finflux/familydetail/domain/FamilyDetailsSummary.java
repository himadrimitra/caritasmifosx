package com.finflux.familydetail.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.client.domain.Client;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.familydetail.FamilyDetailsSummaryApiConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_family_details_summary")
public class FamilyDetailsSummary extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "no_of_family_members", nullable = false)
    private Integer noOfFamilyMembers;

    @Column(name = "no_of_dependent_minors", nullable = true)
    private Integer noOfDependentMinors;

    @Column(name = "no_of_dependent_adults", nullable = true)
    private Integer noOfDependentAdults;

    @Column(name = "no_of_dependent_seniors", nullable = true)
    private Integer noOfDependentSeniors;

    @Column(name = "no_of_dependents_with_serious_illness", nullable = true)
    private Integer noOfDependentsWithSeriousIllness;

    protected FamilyDetailsSummary() {}

    private FamilyDetailsSummary(final Client client, final Integer noOfFamilyMembers, final Integer noOfDependentMinors,
            final Integer noOfDependentAdults, final Integer noOfDependentSeniors, final Integer noOfDependentsWithSeriousIllness) {
        this.client = client;
        this.noOfFamilyMembers = noOfFamilyMembers;
        this.noOfDependentMinors = noOfDependentMinors;
        this.noOfDependentAdults = noOfDependentAdults;
        this.noOfDependentSeniors = noOfDependentSeniors;
        this.noOfDependentsWithSeriousIllness = noOfDependentsWithSeriousIllness;
    }

    public static FamilyDetailsSummary instance(final Client client, final Integer noOfFamilyMembers, final Integer noOfDependentMinors,
            final Integer noOfDependentAdults, final Integer noOfDependentSeniors, final Integer noOfDependentsWithSeriousIllness) {
        return new FamilyDetailsSummary(client, noOfFamilyMembers, noOfDependentMinors, noOfDependentAdults, noOfDependentSeniors,
                noOfDependentsWithSeriousIllness);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInIntegerParameterNamed(FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName, this.noOfFamilyMembers)) {
            final Integer newValue = command.integerValueOfParameterNamed(FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName);
            actualChanges.put(FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName, newValue);
            this.noOfFamilyMembers = newValue;
        }

        if (command
                .isChangeInIntegerParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName, this.noOfDependentMinors)) {
            final Integer newValue = command.integerValueOfParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName);
            actualChanges.put(FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName, newValue);
            this.noOfDependentMinors = newValue;
        }

        if (command
                .isChangeInIntegerParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName, this.noOfDependentAdults)) {
            final Integer newValue = command.integerValueOfParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName);
            actualChanges.put(FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName, newValue);
            this.noOfDependentAdults = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName,
                this.noOfDependentSeniors)) {
            final Integer newValue = command.integerValueOfParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName);
            actualChanges.put(FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName, newValue);
            this.noOfDependentSeniors = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName,
                this.noOfDependentsWithSeriousIllness)) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName);
            actualChanges.put(FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName, newValue);
            this.noOfDependentsWithSeriousIllness = newValue;
        }

        return actualChanges;
    }
}