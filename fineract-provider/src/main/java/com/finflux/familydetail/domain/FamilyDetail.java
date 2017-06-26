package com.finflux.familydetail.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.portfolio.cashflow.domain.IncomeExpense;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_family_details")
public class FamilyDetail extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salutation_cv_id", nullable = true)
    private CodeValue salutation;

    @Column(name = "firstname", length = 50, nullable = false)
    private String firstname;

    @Column(name = "middlename", length = 50, nullable = true)
    private String middlename;

    @Column(name = "lastname", length = 50, nullable = true)
    private String lastname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_cv_id", nullable = true)
    private CodeValue relationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_cv_id", nullable = true)
    private CodeValue gender;

    @Column(name = "date_of_birth", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "age", nullable = true)
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occupation_details_id", nullable = true)
    private IncomeExpense occupation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_cv_id", nullable = true)
    private CodeValue education;

    @Column(name = "is_dependent", length = 1, nullable = true)
    private Boolean isDependent;

    @Column(name = "is_serious_illness", length = 1, nullable = true)
    private Boolean isSeriousIllness;

    @Column(name = "is_deceased", length = 1, nullable = true)
    private Boolean isDeceased;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_reference", nullable = true)
    private Client clientReference;

    protected FamilyDetail() {}

    public FamilyDetail(final Client client, final CodeValue salutation, final String firstname, final String middlename,
            final String lastname, final CodeValue relationship, final CodeValue gender, final LocalDate dateOfBirth, final Integer age,
            final IncomeExpense occupation, final CodeValue education, final Boolean isDependent, final Boolean isSeriousIllness,
            final Boolean isDeceased, final Client clientReference) {
        this.client = client;
        this.salutation = salutation;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.relationship = relationship;
        this.gender = gender;
        if (dateOfBirth != null) {
            this.dateOfBirth = dateOfBirth.toDate();
        }
        this.age = age;
        this.occupation = occupation;
        this.education = education;
        this.isDependent = isDependent;
        this.isSeriousIllness = isSeriousIllness;
        this.isDeceased = isDeceased;
        this.clientReference = clientReference;
    }

    public static FamilyDetail create(final Client client, final CodeValue salutation, final String firstname, final String middlename,
            final String lastname, final CodeValue relationship, final CodeValue gender, final LocalDate dateOfBirth, final Integer age,
            final IncomeExpense occupation, final CodeValue education, final Boolean isDependent, final Boolean isSeriousIllness,
            final Boolean isDeceased) {   
    	final Client familyMemberClient = null;
        return new FamilyDetail(client, salutation, firstname, middlename, lastname, relationship, gender, dateOfBirth, age, occupation,
                education, isDependent, isSeriousIllness, isDeceased, familyMemberClient);
    }
    
    public static FamilyDetail create(final Client client, final CodeValue salutation, final String firstname, final String middlename,
            final String lastname, final CodeValue relationship, final CodeValue gender, final LocalDate dateOfBirth, final Integer age,
            final IncomeExpense occupation, final CodeValue education, final Boolean isDependent, final Boolean isSeriousIllness,
            final Boolean isDeceased, final Client clientReference) {   
        return new FamilyDetail(client, salutation, firstname, middlename, lastname, relationship, gender, dateOfBirth, age, occupation,
                education, isDependent, isSeriousIllness, isDeceased, clientReference);
    }

    public Client getClient() {
        return client;
    }

    public CodeValue getSalutation() {
        return salutation;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public CodeValue getRelationship() {
        return relationship;
    }

    public CodeValue getGender() {
        return gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public IncomeExpense getOccupation() {
        return occupation;
    }

    public CodeValue getEducation() {
        return education;
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.salutationIdParamName, salutationId())) {
            final Long newValue = command.longValueOfParameterNamed(FamilyDetailsApiConstants.salutationIdParamName);
            actualChanges.put(FamilyDetailsApiConstants.salutationIdParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(FamilyDetailsApiConstants.firstNameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.firstNameParamName);
            actualChanges.put(FamilyDetailsApiConstants.firstNameParamName, newValue);
            this.firstname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(FamilyDetailsApiConstants.middleNameParamName, this.middlename)) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.middleNameParamName);
            actualChanges.put(FamilyDetailsApiConstants.middleNameParamName, newValue);
            this.middlename = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(FamilyDetailsApiConstants.lastNameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.lastNameParamName);
            actualChanges.put(FamilyDetailsApiConstants.lastNameParamName, newValue);
            this.lastname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.relationshipIdParamName, relationshipId())) {
            final Long newValue = command.longValueOfParameterNamed(FamilyDetailsApiConstants.relationshipIdParamName);
            actualChanges.put(FamilyDetailsApiConstants.relationshipIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.genderIdParamName, genderId())) {
            final Long newValue = command.longValueOfParameterNamed(FamilyDetailsApiConstants.genderIdParamName);
            actualChanges.put(FamilyDetailsApiConstants.genderIdParamName, newValue);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(FamilyDetailsApiConstants.dateOfBirthParamName, dateOfBirthLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.dateOfBirthParamName);
            actualChanges.put(FamilyDetailsApiConstants.dateOfBirthParamName, valueAsInput);
            actualChanges.put(FamilyDetailsApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(FamilyDetailsApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(FamilyDetailsApiConstants.dateOfBirthParamName);
            this.dateOfBirth = newValue.toDate();
        }

        if (command.isChangeInIntegerParameterNamed(FamilyDetailsApiConstants.ageParamName, updateaAge())) {
            final Integer newValue = command.integerValueOfParameterNamed(FamilyDetailsApiConstants.ageParamName);
            actualChanges.put(FamilyDetailsApiConstants.ageParamName, newValue);
            this.age = newValue;
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.occupationDetailsIdParamName, occupationalDetsilsId())) {
            final Long newValue = command.longValueOfParameterNamed(FamilyDetailsApiConstants.occupationDetailsIdParamName);
            actualChanges.put(FamilyDetailsApiConstants.occupationDetailsIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.educationIdParamName, educationId())) {
            final Long newValue = command.longValueOfParameterNamed(FamilyDetailsApiConstants.educationIdParamName);
            actualChanges.put(FamilyDetailsApiConstants.educationIdParamName, newValue);
        }

        if (command.isChangeInBooleanParameterNamed(FamilyDetailsApiConstants.isDependentParamName, this.isDependent)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(FamilyDetailsApiConstants.isDependentParamName);
            actualChanges.put(FamilyDetailsApiConstants.isDependentParamName, newValue);
            this.isDependent = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(FamilyDetailsApiConstants.isDeceasedParamName, this.isDeceased)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(FamilyDetailsApiConstants.isDeceasedParamName);
            actualChanges.put(FamilyDetailsApiConstants.isDeceasedParamName, newValue);
            this.isDeceased = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(FamilyDetailsApiConstants.isSeriousIllnessParamName, this.isSeriousIllness)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(FamilyDetailsApiConstants.isSeriousIllnessParamName);
            actualChanges.put(FamilyDetailsApiConstants.isSeriousIllnessParamName, newValue);
            this.isSeriousIllness = newValue;
        }
        
		if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.ClientReference,
				this.clientReference())) {
			final Long newValue = command
					.longValueOfParameterNamed(FamilyDetailsApiConstants.ClientReference);
			actualChanges.put(FamilyDetailsApiConstants.ClientReference, newValue);
		}
		return actualChanges;
	}

    public LocalDate dateOfBirthLocalDate() {
        LocalDate dateOfBirth = null;
        if (this.dateOfBirth != null) {
            dateOfBirth = LocalDate.fromDateFields(this.dateOfBirth);
        }
        return dateOfBirth;
    }

    private Integer updateaAge() {
        Integer updateAge = null;
        if (this.age != null) {
            updateAge = this.getAge();
        }
        return updateAge;
    }

    public Long salutationId() {
        Long salutationId = null;
        if (this.salutation != null) {
            salutationId = this.salutation.getId();
        }
        return salutationId;
    }

    public Long relationshipId() {
        Long relationshipId = null;
        if (this.relationship != null) {
            relationshipId = this.relationship.getId();
        }
        return relationshipId;
    }

    public Long educationId() {
        Long educationId = null;
        if (this.education != null) {
            educationId = this.education.getId();
        }
        return educationId;
    }

    public Long genderId() {
        Long genderId = null;
        if (this.gender != null) {
            genderId = this.gender.getId();
        }
        return genderId;
    }
    
    public Long clientReference() {
        Long clientReferenceId = null;
        if (this.clientReference != null) {
        	clientReferenceId = this.clientReference.getId();
        }
        return clientReferenceId;
    }

    public Long occupationalDetsilsId() {
        Long occupationalDetsilsId = null;
        if (this.occupation != null) {
            occupationalDetsilsId = this.occupation.getId();
        }
        return occupationalDetsilsId;
    }

    public void updateSalutaion(final CodeValue salutaion) {
        this.salutation = salutaion;
    }

    public void updateRelationship(final CodeValue relationship) {
        this.relationship = relationship;
    }

    public void updateGender(final CodeValue gender) {
        this.gender = gender;
    }

    public void updateOccupation(final IncomeExpense occupation) {
        this.occupation = occupation;
    }

    public void updateEducation(final CodeValue education) {
        this.education = education;
    }
    
    public void updateFamilyMemberClient(final Client clientReference){
    	this.clientReference = clientReference;
    }
    
    public void removeFamilyMemberAssociation(){
    	this.clientReference = null;
    }
}