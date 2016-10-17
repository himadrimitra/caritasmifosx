package com.finflux.familydetail.service;

import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.FamilyDetailsSummaryApiConstants;
import com.finflux.familydetail.domain.FamilyDetailsSummary;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class FamilyDetailsSummaryDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public FamilyDetailsSummaryDataAssembler(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public FamilyDetailsSummary assembleCreateForm(final Client client, final JsonCommand command) {

        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Integer noOfFamilyMembers = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName, element, locale);

        final Integer noOfDependentMinors = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName, element, locale);

        final Integer noOfDependentAdults = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName, element, locale);

        final Integer noOfDependentSeniors = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName, element, locale);

        final Integer noOfDependentsWithSeriousIllness = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName, element, locale);

        return FamilyDetailsSummary.instance(client, noOfFamilyMembers, noOfDependentMinors, noOfDependentAdults, noOfDependentSeniors,
                noOfDependentsWithSeriousIllness);
    }
}