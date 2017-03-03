package com.finflux.familydetail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.domain.FamilyDetail;
import com.finflux.portfolio.cashflow.domain.IncomeExpense;
import com.finflux.portfolio.cashflow.domain.IncomeExpenseRepositoryWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class FamilyDetailDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final IncomeExpenseRepositoryWrapper incomeExpenseRepository;
    private final ClientRepositoryWrapper clientRepository;

    @Autowired
    public FamilyDetailDataAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
            final IncomeExpenseRepositoryWrapper incomeExpenseRepository,
            final ClientRepositoryWrapper clientRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.incomeExpenseRepository = incomeExpenseRepository;
        this.clientRepository = clientRepository;
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

        final Long salutationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.salutationIdParamName, element);
        CodeValue salutaion = null;
        if (salutationId != null) {
            salutaion = this.codeValueRepository.findOneWithNotFoundDetection(salutationId);
        }

        final String firstname = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.firstnameParamName, element);

        final String middlename = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.middlenameParamName, element);

        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);

        final Long relationshipId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.relationshipIdParamName, element);
        CodeValue relationship = null;
        if (relationshipId != null) {
            relationship = this.codeValueRepository.findOneWithNotFoundDetection(relationshipId);
        }

        final Long genderId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.genderIdParamName, element);
        CodeValue gender = null;
        if (genderId != null) {
            gender = this.codeValueRepository.findOneWithNotFoundDetection(genderId);
        }

        final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(FamilyDetailsApiConstants.dateOfBirthParamName, element);

        final Integer age = this.fromApiJsonHelper.extractIntegerNamed(FamilyDetailsApiConstants.ageParamName, element, locale);

        final Long occupationalDetailsId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.occupationDetailsIdParamName,
                element);
        IncomeExpense occupation = null;
        if (occupationalDetailsId != null) {
            occupation = this.incomeExpenseRepository.findOneWithNotFoundDetection(occupationalDetailsId);
        }

        final Long educationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.educationIdParamName, element);
        CodeValue education = null;
        if (educationId != null) {
            education = this.codeValueRepository.findOneWithNotFoundDetection(educationId);
        }

        final Boolean isDependent = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isDependentParamName, element);

        final Boolean isSeriousIllness = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isSeriousIllnessParamName,
                element);

        final Boolean isDeceased = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isDeceasedParamName, element);
        
		final Long clientReferenceId = this.fromApiJsonHelper
				.extractLongNamed(FamilyDetailsApiConstants.ClientReference, element);
		
		/*
		 * familyMemberClient : this family member is an existing customer of this 
		 * financial institution.
		 * */
		Client clientReference = null;
		if (clientReferenceId != null) {
			clientReference = this.clientRepository.findOneWithNotFoundDetection(clientReferenceId);
		}
		
        return FamilyDetail.create(client, salutaion, firstname, middlename, lastname, relationship, gender, dateOfBirth, age, occupation,
                education, isDependent, isSeriousIllness, isDeceased, clientReference);
    }

    public Map<String, Object> assembleUpdateForm(final FamilyDetail familyDetail, final JsonCommand command) {
        final Map<String, Object> changes = familyDetail.update(command);
        if (!CollectionUtils.isEmpty(changes)) {
            if (changes.containsKey(FamilyDetailsApiConstants.salutationIdParamName)) {
                final Long salutationId = (Long) changes.get(FamilyDetailsApiConstants.salutationIdParamName);
                if (salutationId != null) {
                    final CodeValue salutaion = this.codeValueRepository.findOneWithNotFoundDetection(salutationId);
                    familyDetail.updateSalutaion(salutaion);
                }
            }
            if (changes.containsKey(FamilyDetailsApiConstants.relationshipIdParamName)) {
                final Long relationshipId = (Long) changes.get(FamilyDetailsApiConstants.relationshipIdParamName);
                if (relationshipId != null) {
                    final CodeValue relationship = this.codeValueRepository.findOneWithNotFoundDetection(relationshipId);
                    familyDetail.updateRelationship(relationship);
                }
            }
            if (changes.containsKey(FamilyDetailsApiConstants.genderIdParamName)) {
                final Long genderId = (Long) changes.get(FamilyDetailsApiConstants.genderIdParamName);
                if (genderId != null) {
                    final CodeValue gender = this.codeValueRepository.findOneWithNotFoundDetection(genderId);
                    familyDetail.updateGender(gender);
                }
            }
            if (changes.containsKey(FamilyDetailsApiConstants.occupationDetailsIdParamName)) {
                final Long occupationDetailsId = (Long) changes.get(FamilyDetailsApiConstants.occupationDetailsIdParamName);
                if (occupationDetailsId != null) {
                    final IncomeExpense occupationDetails = this.incomeExpenseRepository.findOneWithNotFoundDetection(occupationDetailsId);
                    familyDetail.updateOccupation(occupationDetails);
                }
            }
            if (changes.containsKey(FamilyDetailsApiConstants.educationIdParamName)) {
                final Long educationId = (Long) changes.get(FamilyDetailsApiConstants.educationIdParamName);
                if (educationId != null) {
                    final CodeValue education = this.codeValueRepository.findOneWithNotFoundDetection(educationId);
                    familyDetail.updateEducation(education);
                }
            }
            
			if (changes.containsKey(FamilyDetailsApiConstants.ClientReference)) {
				final Long clientReferenceId = (Long) changes.get(FamilyDetailsApiConstants.ClientReference);
				final Client familyMemberClient = this.clientRepository
						.findOneWithNotFoundDetection(clientReferenceId);
				familyDetail.updateFamilyMemberClient(familyMemberClient);
			}
		}
        return changes;
    }
}