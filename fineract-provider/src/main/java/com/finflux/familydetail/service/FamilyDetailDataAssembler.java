package com.finflux.familydetail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.domain.FamilyDetail;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class FamilyDetailDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    
    private final CodeValueRepositoryWrapper codeValueRepository;

    @Autowired
    public FamilyDetailDataAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<FamilyDetail> assembleCreateForm(final Client client, final JsonCommand command) {
        final List<FamilyDetail> familyDetails = new ArrayList();
        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        if (parentElement.isJsonObject() && !command.parameterExists(FamilyDetailsApiConstants.familyMembersParamName)) {
            final FamilyDetail familyDetail = assembleCreateFormEachObject(client, parentElement.getAsJsonObject());
            familyDetails.add(familyDetail);
        } else if (command.parameterExists(FamilyDetailsApiConstants.familyMembersParamName)) {
            final JsonArray array = parentElementObj.get(FamilyDetailsApiConstants.familyMembersParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    final FamilyDetail familyDetail = assembleCreateFormEachObject(client, element);
                    familyDetails.add(familyDetail);
                }
            }
        }
        return familyDetails;
    }

    private FamilyDetail assembleCreateFormEachObject(final Client client, final JsonObject element) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String firstname = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.firstnameParamName, element);

        final String middlename = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.middlenameParamName, element);

        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);

        final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(FamilyDetailsApiConstants.dobParamName, element);

        final Integer age = this.fromApiJsonHelper.extractIntegerNamed(FamilyDetailsApiConstants.ageParamName, element, locale);

        final Long genderId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.genderIdParamName, element);
        CodeValue gender = null;
        if (genderId != null) {
            gender = this.codeValueRepository.findOneWithNotFoundDetection(genderId);
        }

        final Long relationshipId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.relationshipParamName, element);
        CodeValue relationship = null;
        if (relationshipId != null) {
            relationship = this.codeValueRepository.findOneWithNotFoundDetection(relationshipId);
        }

        final Long salutationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.salutationParamName, element);
        CodeValue salutaion = null;
        if (salutationId != null) {
            salutaion = this.codeValueRepository.findOneWithNotFoundDetection(salutationId);
        }

        final Long occupationalDetailsId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.occupationalDetailsParamName,
                element);
        CodeValue occupationDetails = null;
        if (occupationalDetailsId != null) {
            occupationDetails = this.codeValueRepository.findOneWithNotFoundDetection(occupationalDetailsId);
        }

        final Long educationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.educationIdParamName, element);
        CodeValue education = null;
        if (educationId != null) {
            education = this.codeValueRepository.findOneWithNotFoundDetection(educationId);
        }

        return FamilyDetail.create(client, salutaion, firstname, middlename, lastname, relationship, gender, dateOfBirth, age,
                occupationDetails, education);
    }
}
