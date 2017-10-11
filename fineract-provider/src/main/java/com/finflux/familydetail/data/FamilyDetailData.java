package com.finflux.familydetail.data;

import java.util.Date;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

import com.finflux.portfolio.cashflow.data.IncomeExpenseData;

public class FamilyDetailData {

    private final Long id;
    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final Date dateOfBirth;
    private final Integer age;
    private final CodeValueData salutation;
    private final CodeValueData relationship;
    private final CodeValueData gender;
    private final CodeValueData education;
    private final IncomeExpenseData occupation;
    private final Boolean isDependent;
    private final Boolean isSeriousIllness;
    private final Boolean isDeceased;
    private final Long clientReference;
    private final Long memberClientId;
    private final String displayName;
    private final String accountNo;

    public FamilyDetailData(Long id, String firstname, String middlename, String lastname, CodeValueData salutation,
            CodeValueData relationship, CodeValueData gender, Date dateOfBirth, Integer age, CodeValueData education,
            IncomeExpenseData occupation, final Boolean isDependent, final Boolean isSeriousIllness, final Boolean isDeceased,
            final Long memberClientId, final String displayName, final String accountNo) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.salutation = salutation;
        this.relationship = relationship;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.education = education;
        this.occupation = occupation;
        this.isDependent = isDependent;
        this.isSeriousIllness = isSeriousIllness;
        this.isDeceased = isDeceased;
        this.clientReference = null;
        this.memberClientId = memberClientId;
        this.displayName = displayName;
        this.accountNo = accountNo;
    }

    public FamilyDetailData(Long id, String firstname, String middlename, String lastname, CodeValueData salutation,
            CodeValueData relationship, CodeValueData gender, Date dateOfBirth, Integer age, CodeValueData education,
            IncomeExpenseData occupation, final Boolean isDependent, final Boolean isSeriousIllness, final Boolean isDeceased,
            final Long clientReference, final Long memberClientId, final String displayName, final String accountNo) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.salutation = salutation;
        this.relationship = relationship;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.education = education;
        this.occupation = occupation;
        this.isDependent = isDependent;
        this.isSeriousIllness = isSeriousIllness;
        this.isDeceased = isDeceased;
        this.clientReference = clientReference;
        this.memberClientId = memberClientId;
        this.displayName = displayName;
        this.accountNo = accountNo;
    }

    public Long getId() {
        return id;
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

    public CodeValueData getSalutation() {
        return salutation;
    }

    public CodeValueData getRelationship() {
        return relationship;
    }

    public CodeValueData getGender() {
        return gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public CodeValueData getEducation() {
        return education;
    }

    public IncomeExpenseData getOccupation() {
        return occupation;
    }

    public Boolean getIsDependent() {
        return this.isDependent;
    }

    public Boolean getIsSeriousIllness() {
        return this.isSeriousIllness;
    }

    public Boolean getIsDeceased() {
        return this.isDeceased;
    }

    public Long getClientReference() {
        return this.clientReference;
    }

    public Long getMemberClientId() {
        return this.memberClientId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getAccountNo() {
        return this.accountNo;
    }
}