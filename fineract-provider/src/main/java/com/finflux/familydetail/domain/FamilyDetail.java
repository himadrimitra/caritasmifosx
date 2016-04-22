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
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.client.domain.Client;
import org.joda.time.LocalDate;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.familydetail.FamilyDetailsApiConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_family_details", uniqueConstraints = { @UniqueConstraint(columnNames = { "id" }, name = "id"),
        @UniqueConstraint(columnNames = { "client_id" }, name = "FK1_client_id") })
public class FamilyDetail extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", unique = true, nullable = false)
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
    @JoinColumn(name = "occupation_details_cv_id")
    private CodeValue occupation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_cv_id")
    private CodeValue education;

    protected FamilyDetail() {

    }

    public FamilyDetail(Client client, CodeValue salutation, String firstname, String middlename, String lastname, CodeValue relationship,
            CodeValue gender, LocalDate dateOfBirth, Integer age, CodeValue occupation, CodeValue education) {
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
    }

    public static FamilyDetail create(final Client client, final CodeValue salutation, final String firstname, final String middlename,
            final String lastname, final CodeValue relationship, final CodeValue gender, final LocalDate dateOfBirth, final Integer age,
            final CodeValue occupation, final CodeValue education) {

        return new FamilyDetail(client, salutation, firstname, middlename, lastname, relationship, gender, dateOfBirth, age, occupation,
                education);
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

    public CodeValue getOccupation() {
        return occupation;
    }

    public CodeValue getEducation() {
        return education;
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        if (command.isChangeInLongParameterNamed("salutation", salutationId())) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.salutationParamName);
            actualChanges.put(FamilyDetailsApiConstants.salutationParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(FamilyDetailsApiConstants.firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.firstnameParamName);
            actualChanges.put(FamilyDetailsApiConstants.firstnameParamName, newValue);
            this.firstname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(FamilyDetailsApiConstants.middlenameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.middlenameParamName);
            actualChanges.put(FamilyDetailsApiConstants.middlenameParamName, newValue);
            this.middlename = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(FamilyDetailsApiConstants.lastnameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.lastnameParamName);
            actualChanges.put(FamilyDetailsApiConstants.lastnameParamName, newValue);
            this.lastname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.relationshipParamName, relationshipId())) {
            final String newValue = command.stringValueOfParameterNamed("relationship");
            actualChanges.put(FamilyDetailsApiConstants.relationshipParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.genderIdParamName, genderId())) {
            final String newValue = command.stringValueOfParameterNamed("Gender");
            actualChanges.put(FamilyDetailsApiConstants.genderIdParamName, newValue);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(FamilyDetailsApiConstants.dobParamName, dateOfBirthLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.dobParamName);
            actualChanges.put(FamilyDetailsApiConstants.dobParamName, valueAsInput);
            actualChanges.put(FamilyDetailsApiConstants.dobParamName, dateFormatAsInput);
            actualChanges.put(FamilyDetailsApiConstants.dobParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(FamilyDetailsApiConstants.dobParamName);
            this.dateOfBirth = newValue.toDate();
        }

        if (command.isChangeInIntegerParameterNamed(FamilyDetailsApiConstants.ageParamName, updateaAge())) {
            final Integer newValue = command.integerValueOfParameterNamed(FamilyDetailsApiConstants.ageParamName);
            actualChanges.put(FamilyDetailsApiConstants.ageParamName, newValue);
            this.age = newValue;
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.occupationalDetailsParamName, occupationalDetsilsId())) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.occupationalDetailsParamName);
            actualChanges.put(FamilyDetailsApiConstants.occupationalDetailsParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(FamilyDetailsApiConstants.educationParamName, educationId())) {
            final String newValue = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.educationParamName);
            actualChanges.put(FamilyDetailsApiConstants.educationParamName, newValue);
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
            salutationId = this.gender.getId();
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

    public Long occupationalDetsilsId() {
        Long occupationalDetsilsId = null;
        if (this.occupation != null) {
            occupationalDetsilsId = this.occupation.getId();
        }
        return occupationalDetsilsId;
    }

}
