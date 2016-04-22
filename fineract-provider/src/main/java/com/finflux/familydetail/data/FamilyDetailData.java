package com.finflux.familydetail.data;

import java.util.Date;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

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
    private final CodeValueData occupation;

    public FamilyDetailData(Long id, String firstname, String middlename, String lastname, CodeValueData salutation,
            CodeValueData relationship, CodeValueData gender, Date dateOfBirth, Integer age, CodeValueData education,
            CodeValueData occupation) {
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

    public CodeValueData getOccupation() {
        return occupation;
    }

}
