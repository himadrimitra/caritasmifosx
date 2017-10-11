package com.finflux.familydetail.data;

public class FamilyDetailsSummaryData {

    private final Long id;
    private final Long clientId;
    private final Integer noOfFamilyMembers;
    private final Integer noOfDependentMinors;
    private final Integer noOfDependentAdults;
    private final Integer noOfDependentSeniors;
    private final Integer noOfDependentsWithSeriousIllness;

    public FamilyDetailsSummaryData(final Long id, final Long clientId, final Integer noOfFamilyMembers, final Integer noOfDependentMinors,
            final Integer noOfDependentAdults, final Integer noOfDependentSeniors, final Integer noOfDependentsWithSeriousIllness) {
        this.id = id;
        this.clientId = clientId;
        this.noOfFamilyMembers = noOfFamilyMembers;
        this.noOfDependentMinors = noOfDependentMinors;
        this.noOfDependentAdults = noOfDependentAdults;
        this.noOfDependentSeniors = noOfDependentSeniors;
        this.noOfDependentsWithSeriousIllness = noOfDependentsWithSeriousIllness;
    }
}