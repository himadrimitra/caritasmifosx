/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.client.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_client", uniqueConstraints = { @UniqueConstraint(columnNames = { "account_no" }, name = "account_no_UNIQUE"), //
        @UniqueConstraint(columnNames = { "mobile_no" }, name = "mobile_no_UNIQUE") })
public final class Client extends AbstractPersistable<Long> {

    @Column(name = "account_no", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "national_id", length = 20, unique = true, nullable = false)
    private String nationalId;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "transfer_to_office_id", nullable = true)
    private Long transferToOfficeId;

    @OneToOne(optional = true)
    @LazyToOne(value = LazyToOneOption.PROXY)
    @JoinColumn(name = "image_id", nullable = true)
    private Image image;

    /**
     * A value from {@link ClientStatus}.
     */
    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_status", nullable = true)
    private CodeValue subStatus;

    @Column(name = "activation_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @Column(name = "office_joining_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date officeJoiningDate;

    @Column(name = "firstname", length = 50, nullable = true)
    private String firstname;

    @Column(name = "middlename", length = 50, nullable = true)
    private String middlename;

    @Column(name = "lastname", length = 50, nullable = true)
    private String lastname;

    @Column(name = "fullname", length = 100, nullable = true)
    private String fullname;

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @Column(name = "mobile_no", length = 50, nullable = false, unique = true)
    private String mobileNo;

    @Column(name = "alternate_mobile_no", length = 50, nullable = true)
    private String alternateMobileNo;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @Column(name = "date_of_birth", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_cv_id", nullable = true)
    private CodeValue gender;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name = "m_group_client", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    @Transient
    private boolean accountNumberRequiresAutoGeneration = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closure_reason_cv_id", nullable = true)
    private CodeValue closureReason;

    @Column(name = "closedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date closureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reject_reason_cv_id", nullable = true)
    private CodeValue rejectionReason;

    @Column(name = "rejectedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date rejectionDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "rejectedon_userid", nullable = true)
    private AppUser rejectedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdraw_reason_cv_id", nullable = true)
    private CodeValue withdrawalReason;

    @Column(name = "withdrawn_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date withdrawalDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "withdraw_on_userid", nullable = true)
    private AppUser withdrawnBy;

    @Column(name = "reactivated_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date reactivateDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "reactivated_on_userid", nullable = true)
    private AppUser reactivatedBy;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closedBy;

    @Column(name = "submittedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    private AppUser submittedBy;

    @Column(name = "updated_on", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date updatedOnDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = true)
    private AppUser updatedBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "activatedon_userid", nullable = true)
    private AppUser activatedBy;

    @Column(name = "default_savings_product", nullable = true)
    private Long savingsProductId;

    @Column(name = "default_savings_account", nullable = true)
    private Long savingsAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_type_cv_id", nullable = true)
    private CodeValue clientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_classification_cv_id", nullable = true)
    private CodeValue clientClassification;

    @Column(name = "legal_form_enum", nullable = true)
    private Integer legalForm;

    @Column(name = "reopened_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date reopenedDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "reopened_by_userid", nullable = true)
    private AppUser reopenedBy;

    @Column(name = "is_force_activated", nullable = false)
    private boolean isForceActivated = false;

    @Column(name = "email_id", length = 100, nullable = true)
    private String emailId;

    @Column(name = "is_locked", nullable = false)
    private final boolean isLocked = false;

    public static Client createNew(final AppUser currentUser, final Office clientOffice, final Group clientParentGroup, final Staff staff,
            final SavingsProduct savingsProduct, final CodeValue gender, final CodeValue clientType, final CodeValue clientClassification,
            final Integer legalForm, final JsonCommand command, final boolean isDisplayFirstNameLastLastNameFirstForClient) {

        final String accountNo = command.stringValueOfParameterNamed(ClientApiConstants.accountNoParamName);
        final String externalId = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
        final String mobileNo = command.stringValueOfParameterNamed(ClientApiConstants.mobileNoParamName);
        final String nationalId = command.stringValueOfParameterNamed(ClientApiConstants.nationalId);
        final String alternateMobileNo = command.stringValueOfParameterNamed(ClientApiConstants.alternateMobileNoParamName);
        final String emailId = command.stringValueOfParameterNamed(ClientApiConstants.emailAddress);

        final String firstname = command.stringValueOfParameterNamed(ClientApiConstants.firstNameParamName);
        final String middlename = command.stringValueOfParameterNamed(ClientApiConstants.middleNameParamName);
        final String lastname = command.stringValueOfParameterNamed(ClientApiConstants.lastNameParamName);
        final String fullname = command.stringValueOfParameterNamed(ClientApiConstants.fullnameParamName);

        final LocalDate dataOfBirth = command.localDateValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);

        ClientStatus status = ClientStatus.PENDING;
        boolean active = false;
        if (command.hasParameter("active")) {
            active = command.booleanPrimitiveValueOfParameterNamed(ClientApiConstants.activeParamName);
        }

        LocalDate activationDate = null;
        LocalDate officeJoiningDate = null;
        if (active) {
            status = ClientStatus.ACTIVE;
            activationDate = command.localDateValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            officeJoiningDate = activationDate;
        }

        LocalDate submittedOnDate = DateUtils.getLocalDateOfTenant();
        if (active && submittedOnDate.isAfter(activationDate)) {
            submittedOnDate = activationDate;
        }
        if (command.hasParameter(ClientApiConstants.submittedOnDateParamName)) {
            submittedOnDate = command.localDateValueOfParameterNamed(ClientApiConstants.submittedOnDateParamName);
        }
        final SavingsAccount account = null;
        return new Client(currentUser, status, clientOffice, clientParentGroup, accountNo, firstname, middlename, lastname, fullname,
                activationDate, officeJoiningDate, externalId, mobileNo, alternateMobileNo, staff, submittedOnDate, savingsProduct, account,
                dataOfBirth, gender, clientType, clientClassification, legalForm, emailId, isDisplayFirstNameLastLastNameFirstForClient,
                nationalId);
    }

    protected Client() {
        setLegalForm(null);
    }

    private Client(final AppUser currentUser, final ClientStatus status, final Office office, final Group clientParentGroup,
            final String accountNo, final String firstname, final String middlename, final String lastname, final String fullname,
            final LocalDate activationDate, final LocalDate officeJoiningDate, final String externalId, final String mobileNo,
            final String alternateMobileNo, final Staff staff, final LocalDate submittedOnDate, final SavingsProduct savingsProduct,
            final SavingsAccount savingsAccount, final LocalDate dateOfBirth, final CodeValue gender, final CodeValue clientType,
            final CodeValue clientClassification, final Integer legalForm, final String emailId,
            final boolean isDisplayFirstNameLastLastNameFirstForClient, final String nationalId) {

        if (StringUtils.isBlank(accountNo)) {
            this.accountNumber = new RandomPasswordGenerator(19).generate();
            this.accountNumberRequiresAutoGeneration = true;
        } else {
            this.accountNumber = accountNo;
        }

        this.nationalId = nationalId;
        this.submittedOnDate = submittedOnDate.toDate();
        this.submittedBy = currentUser;

        this.status = status.getValue();
        this.office = office;
        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId.trim();
        } else {
            this.externalId = null;
        }

        if (StringUtils.isNotBlank(mobileNo)) {
            this.mobileNo = mobileNo.trim();
        } else {
            this.mobileNo = null;
        }

        if (StringUtils.isNotBlank(alternateMobileNo)) {
            this.alternateMobileNo = alternateMobileNo.trim();
        } else {
            this.alternateMobileNo = null;
        }

        if (activationDate != null) {
            this.activationDate = activationDate.toDateTimeAtStartOfDay().toDate();
            this.activatedBy = currentUser;
        }
        if (officeJoiningDate != null) {
            this.officeJoiningDate = officeJoiningDate.toDateTimeAtStartOfDay().toDate();
        }
        if (StringUtils.isNotBlank(firstname)) {
            this.firstname = firstname.trim();
        } else {
            this.firstname = null;
        }

        if (StringUtils.isNotBlank(middlename)) {
            this.middlename = middlename.trim();
        } else {
            this.middlename = null;
        }

        if (StringUtils.isNotBlank(lastname)) {
            this.lastname = lastname.trim();
        } else {
            this.lastname = null;
        }

        if (StringUtils.isNotBlank(fullname)) {
            this.fullname = fullname.trim();
        } else {
            this.fullname = null;
        }

        if (clientParentGroup != null) {
            this.groups = new HashSet<>();
            this.groups.add(clientParentGroup);
        }

        this.staff = staff;
        if (savingsProduct != null) {
            this.savingsProductId = savingsProduct.getId();
        }
        if (savingsAccount != null) {
            this.savingsAccountId = savingsAccount.getId();
        }

        if (gender != null) {
            this.gender = gender;
        }
        if (dateOfBirth != null) {
            this.dateOfBirth = dateOfBirth.toDateTimeAtStartOfDay().toDate();
        }

        if (StringUtils.isNotBlank(emailId)) {
            this.emailId = emailId.trim();
        } else {
            this.emailId = null;
        }

        this.clientType = clientType;
        this.clientClassification = clientClassification;
        setLegalForm(legalForm);

        deriveDisplayName(isDisplayFirstNameLastLastNameFirstForClient);
        validate();
    }

    public String getNationalId() {
        return this.nationalId;
    }

    public void setNationalId(final String nationalId) {
        this.nationalId = nationalId;
    }

    private void validate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        validateNameParts(dataValidationErrors);
        validateActivationDate(dataValidationErrors);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    private void validateUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        // Not validating name parts while update request as firstname/lastname
        // can be along with fullname
        // when we change clientType from Individual to Organisation or
        // vice-cersa
        validateActivationDate(dataValidationErrors);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public boolean isAccountNumberRequiresAutoGeneration() {
        return this.accountNumberRequiresAutoGeneration;
    }

    public void setAccountNumberRequiresAutoGeneration(final boolean accountNumberRequiresAutoGeneration) {
        this.accountNumberRequiresAutoGeneration = accountNumberRequiresAutoGeneration;
    }

    public boolean identifiedBy(final String identifier) {
        return identifier.equalsIgnoreCase(this.externalId);
    }

    public boolean identifiedBy(final Long clientId) {
        return getId().equals(clientId);
    }

    public void updateAccountNo(final String accountIdentifier) {
        this.accountNumber = accountIdentifier;
        this.accountNumberRequiresAutoGeneration = false;
    }

    public void activate(final AppUser currentUser, final DateTimeFormatter formatter, final LocalDate activationLocalDate) {

        if (isActive()) {
            final String defaultUserMessage = "Cannot activate client. Client is already active.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.already.active", defaultUserMessage,
                    ClientApiConstants.activationDateParamName, activationLocalDate.toString(formatter));

            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        this.activationDate = activationLocalDate.toDate();
        this.activatedBy = currentUser;
        this.officeJoiningDate = this.activationDate;
        this.status = ClientStatus.ACTIVE.getValue();

        validate();
    }

    public boolean isNotActive() {
        return !isActive();
    }

    public boolean isActive() {
        return ClientStatus.fromInt(this.status).isActive();
    }

    public boolean isClosed() {
        return ClientStatus.fromInt(this.status).isClosed();
    }

    public boolean isTransferInProgress() {
        return ClientStatus.fromInt(this.status).isTransferInProgress();
    }

    public boolean isTransferOnHold() {
        return ClientStatus.fromInt(this.status).isTransferOnHold();
    }

    public boolean isTransferInProgressOrOnHold() {
        return isTransferInProgress() || isTransferOnHold();
    }

    public boolean isNotPending() {
        return !isPending();
    }

    public boolean isPending() {
        return ClientStatus.fromInt(this.status).isPending();
    }

    private boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }

    public boolean isRejected() {
        return ClientStatus.fromInt(this.status).isRejected();
    }

    public boolean isWithdrawn() {
        return ClientStatus.fromInt(this.status).isWithdrawn();
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return this.middlename;
    }

    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public String getFullname() {
        return this.fullname;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getOfficeName() {
        return this.office.getName();
    }

    public Map<String, Object> update(final JsonCommand command, final boolean isDisplayFirstNameLastLastNameFirstForClient) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.statusParamName, this.status)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.statusParamName);
            actualChanges.put(ClientApiConstants.statusParamName, ClientEnumerations.status(newValue));
            this.status = ClientStatus.fromInt(newValue).getValue();
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.accountNoParamName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.accountNoParamName);
            actualChanges.put(ClientApiConstants.accountNoParamName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.nationalId, this.nationalId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nationalId);
            actualChanges.put(ClientApiConstants.nationalId, newValue);
            this.nationalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
            actualChanges.put(ClientApiConstants.externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.mobileNoParamName);
            actualChanges.put(ClientApiConstants.mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.alternateMobileNoParamName, this.alternateMobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.alternateMobileNoParamName);
            actualChanges.put(ClientApiConstants.alternateMobileNoParamName, newValue);
            this.alternateMobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.emailAddress, this.emailId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.emailAddress);
            actualChanges.put(ClientApiConstants.emailAddress, newValue);
            this.emailId = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.firstNameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.firstNameParamName);
            actualChanges.put(ClientApiConstants.firstNameParamName, newValue);
            this.firstname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.middleNameParamName, this.middlename)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.middleNameParamName);
            actualChanges.put(ClientApiConstants.middleNameParamName, newValue);
            this.middlename = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.lastNameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.lastNameParamName);
            actualChanges.put(ClientApiConstants.lastNameParamName, newValue);
            this.lastname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.fullnameParamName, this.fullname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fullnameParamName);
            actualChanges.put(ClientApiConstants.fullnameParamName, newValue);
            this.fullname = newValue;
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.staffIdParamName, staffId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.staffIdParamName);
            actualChanges.put(ClientApiConstants.staffIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.genderIdParamName, genderId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.genderIdParamName);
            actualChanges.put(ClientApiConstants.genderIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.savingsProductIdParamName, savingsProductId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.savingsProductIdParamName);
            actualChanges.put(ClientApiConstants.savingsProductIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.genderIdParamName, genderId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.genderIdParamName);
            actualChanges.put(ClientApiConstants.genderIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.clientTypeIdParamName, clientTypeId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.clientTypeIdParamName);
            actualChanges.put(ClientApiConstants.clientTypeIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.clientClassificationIdParamName, clientClassificationId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.clientClassificationIdParamName);
            actualChanges.put(ClientApiConstants.clientClassificationIdParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.legalFormIdParamName, getLegalForm())) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.legalFormIdParamName);
            if (newValue != null) {
                final LegalForm legalForm = LegalForm.fromInt(newValue);
                if (legalForm != null) {
                    actualChanges.put(ClientApiConstants.legalFormIdParamName, ClientEnumerations.legalForm(newValue));
                    setLegalForm(legalForm.getValue());
                } else {
                    actualChanges.put(ClientApiConstants.legalFormIdParamName, null);
                    setLegalForm(null);
                }
            } else {
                actualChanges.put(ClientApiConstants.legalFormIdParamName, null);
                setLegalForm(null);
            }
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.activationDateParamName, getActivationLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            actualChanges.put(ClientApiConstants.activationDateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            this.activationDate = newValue.toDate();
            this.officeJoiningDate = this.activationDate;
        }

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.dateOfBirthParamName, dateOfBirthLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
            actualChanges.put(ClientApiConstants.dateOfBirthParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
            this.dateOfBirth = newValue.toDate();
        }

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.submittedOnDateParamName, getSubmittedOnDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.submittedOnDateParamName);
            actualChanges.put(ClientApiConstants.submittedOnDateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.submittedOnDateParamName);
            this.submittedOnDate = newValue.toDate();
        }

        validateUpdate();

        deriveDisplayName(isDisplayFirstNameLastLastNameFirstForClient);

        return actualChanges;
    }

    private void validateNameParts(final List<ApiParameterError> dataValidationErrors) {
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("client");

        if (StringUtils.isNotBlank(this.fullname)) {

            baseDataValidator.reset().parameter(ClientApiConstants.firstNameParamName).value(this.firstname)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);

            baseDataValidator.reset().parameter(ClientApiConstants.middleNameParamName).value(this.middlename)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);

            baseDataValidator.reset().parameter(ClientApiConstants.lastNameParamName).value(this.lastname)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);
        } else {

            baseDataValidator.reset().parameter(ClientApiConstants.firstNameParamName).value(this.firstname).notBlank()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(ClientApiConstants.middleNameParamName).value(this.middlename).ignoreIfNull()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(ClientApiConstants.lastNameParamName).value(this.lastname).notBlank()
                    .notExceedingLengthOf(50);
        }
    }

    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors) {

        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {

            final String defaultUserMessage = "submitted date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.in.the.future",
                    defaultUserMessage, ClientApiConstants.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.after.activation.date",
                    defaultUserMessage, ClientApiConstants.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getReopenedDate() != null && getActivationLocalDate() != null && getReopenedDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "reopened date cannot be after the submittedon date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.after.reopened.date",
                    defaultUserMessage, ClientApiConstants.reopenedDateParamName, this.reopenedDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.activationDate.in.the.future",
                    defaultUserMessage, ClientApiConstants.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null) {
            if (this.office.isOpeningDateAfter(getActivationLocalDate())) {
                final String defaultUserMessage = "Client activation date cannot be a date before the office opening date.";
                final ApiParameterError error = ApiParameterError.parameterError(
                        "error.msg.clients.activationDate.cannot.be.before.office.activation.date", defaultUserMessage,
                        ClientApiConstants.activationDateParamName, getActivationLocalDate());
                dataValidationErrors.add(error);
            }
        }
    }

    private void deriveDisplayName(final boolean isDisplayFirstNameLastLastNameFirstForClient) {

        StringBuilder nameBuilder = new StringBuilder();
        final Integer legalForm = getLegalForm();
        if (legalForm == null || LegalForm.fromInt(legalForm).isPerson()) {
            if (isDisplayFirstNameLastLastNameFirstForClient) {
                if (StringUtils.isNotBlank(this.lastname)) {
                    nameBuilder.append(this.lastname).append(' ');
                }

                if (StringUtils.isNotBlank(this.middlename)) {
                    nameBuilder.append(this.middlename).append(' ');
                }

                if (StringUtils.isNotBlank(this.firstname)) {
                    nameBuilder.append(this.firstname);
                }
            } else {
                if (StringUtils.isNotBlank(this.firstname)) {
                    nameBuilder.append(this.firstname).append(' ');
                }

                if (StringUtils.isNotBlank(this.middlename)) {
                    nameBuilder.append(this.middlename).append(' ');
                }

                if (StringUtils.isNotBlank(this.lastname)) {
                    nameBuilder.append(this.lastname);
                }
            }

        } else if (LegalForm.fromInt(legalForm).isEntity()) {
            if (StringUtils.isNotBlank(this.fullname)) {
                nameBuilder = new StringBuilder(this.fullname);
            }
        }

        this.displayName = nameBuilder.toString();
    }

    public LocalDate getSubmittedOnDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);
    }

    public LocalDate getActivationLocalDate() {
        LocalDate activationLocalDate = null;
        if (this.activationDate != null) {
            activationLocalDate = LocalDate.fromDateFields(this.activationDate);
        }
        return activationLocalDate;
    }

    public LocalDate getOfficeJoiningLocalDate() {
        LocalDate officeJoiningLocalDate = null;
        if (this.officeJoiningDate != null) {
            officeJoiningLocalDate = LocalDate.fromDateFields(this.officeJoiningDate);
        }
        return officeJoiningLocalDate;
    }

    public boolean isOfficeIdentifiedBy(final Long officeId) {
        return this.office.identifiedBy(officeId);
    }

    public Long officeId() {
        return this.office.getId();
    }

    public void setImage(final Image image) {
        this.image = image;
    }

    public Image getImage() {
        return this.image;
    }

    public String mobileNo() {
        return this.mobileNo;
    }

    public void setMobileNo(final String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public Office getOffice() {
        return this.office;
    }

    public Long getTransferToOfficeId() {
        return this.transferToOfficeId;
    }

    public void updateOffice(final Office office) {
        this.office = office;
    }

    public void updateTransferToOffice(final Office office) {
        if (office == null) {
            this.transferToOfficeId = null;
        } else {
            this.transferToOfficeId = office.getId();
        }
    }

    public void updateOfficeJoiningDate(final Date date) {
        this.officeJoiningDate = date;
    }

    private Long staffId() {
        Long staffId = null;
        if (this.staff != null) {
            staffId = this.staff.getId();
        }
        return staffId;
    }

    public void updateStaff(final Staff staff) {
        this.staff = staff;
    }

    public Staff getStaff() {
        return this.staff;
    }

    public void unassignStaff() {
        this.staff = null;
    }

    public void assignStaff(final Staff staff) {
        this.staff = staff;
    }

    public Set<Group> getGroups() {
        return this.groups;
    }

    public void close(final AppUser currentUser, final CodeValue closureReason, final Date closureDate) {
        this.closureReason = closureReason;
        this.closureDate = closureDate;
        this.closedBy = currentUser;
        this.status = ClientStatus.CLOSED.getValue();
    }

    public Integer getStatus() {
        return this.status;
    }

    public CodeValue subStatus() {
        return this.subStatus;
    }

    public Long subStatusId() {
        Long subStatusId = null;
        if (this.subStatus != null) {
            subStatusId = this.subStatus.getId();
        }
        return subStatusId;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public boolean isActivatedAfter(final LocalDate submittedOn) {
        return getActivationLocalDate().isAfter(submittedOn);
    }

    public boolean isChildOfGroup(final Long groupId) {
        if (groupId != null && this.groups != null && !this.groups.isEmpty()) {
            for (final Group group : this.groups) {
                if (group.getId().equals(groupId)) { return true; }
            }
        }
        return false;
    }

    public Long savingsProductId() {
        return this.savingsProductId;
    }

    public void updateSavingsProduct(final SavingsProduct savingsProduct) {
        if (savingsProduct == null) {
            this.savingsProductId = null;
        } else {
            this.savingsProductId = savingsProduct.getId();
        }
    }

    public AppUser activatedBy() {
        return this.activatedBy;
    }

    public void updateSavingsAccount(final SavingsAccount savingsAccount) {
        if (savingsAccount == null) {
            this.savingsAccountId = null;
        } else {
            this.savingsAccountId = savingsAccount.getId();
        }
    }

    public Long genderId() {
        Long genderId = null;
        if (this.gender != null) {
            genderId = this.gender.getId();
        }
        return genderId;
    }

    public Long clientTypeId() {
        Long clientTypeId = null;
        if (this.clientType != null) {
            clientTypeId = this.clientType.getId();
        }
        return clientTypeId;
    }

    public Long clientClassificationId() {
        Long clientClassificationId = null;
        if (this.clientClassification != null) {
            clientClassificationId = this.clientClassification.getId();
        }
        return clientClassificationId;
    }

    public LocalDate getClosureDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.closureDate), null);
    }

    public LocalDate getRejectedDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.rejectionDate), null);
    }

    public LocalDate getWithdrawalDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.withdrawalDate), null);
    }

    public LocalDate getReopenedDate() {
        return this.reopenedDate == null ? null : new LocalDate(this.reopenedDate);
    }

    public CodeValue gender() {
        return this.gender;
    }

    public CodeValue clientType() {
        return this.clientType;
    }

    public void updateClientType(final CodeValue clientType) {
        this.clientType = clientType;
    }

    public CodeValue clientClassification() {
        return this.clientClassification;
    }

    public void updateClientClassification(final CodeValue clientClassification) {
        this.clientClassification = clientClassification;
    }

    public void updateGender(final CodeValue gender) {
        this.gender = gender;
    }

    public Date dateOfBirth() {
        return this.dateOfBirth;
    }

    public LocalDate dateOfBirthLocalDate() {
        LocalDate dateOfBirth = null;
        if (this.dateOfBirth != null) {
            dateOfBirth = LocalDate.fromDateFields(this.dateOfBirth);
        }
        return dateOfBirth;
    }

    public void reject(final AppUser currentUser, final CodeValue rejectionReason, final Date rejectionDate) {
        this.rejectionReason = rejectionReason;
        this.rejectionDate = rejectionDate;
        this.rejectedBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = rejectionDate;
        this.status = ClientStatus.REJECTED.getValue();

    }

    public void withdraw(final AppUser currentUser, final CodeValue withdrawalReason, final Date withdrawalDate) {
        this.withdrawalReason = withdrawalReason;
        this.withdrawalDate = withdrawalDate;
        this.withdrawnBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = withdrawalDate;
        this.status = ClientStatus.WITHDRAWN.getValue();

    }

    public void reActivate(final AppUser currentUser, final Date reactivateDate) {
        this.closureDate = null;
        this.closureReason = null;
        this.reactivateDate = reactivateDate;
        this.reactivatedBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = reactivateDate;
        this.status = ClientStatus.PENDING.getValue();

    }

    public void reOpened(final AppUser currentUser, final Date reopenedDate) {
        this.reopenedDate = reopenedDate;
        this.reopenedBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = reopenedDate;
        this.status = ClientStatus.PENDING.getValue();

    }

    public Integer getLegalForm() {
        return this.legalForm;
    }

    public void setLegalForm(final Integer legalForm) {
        this.legalForm = legalForm;
    }

    public void setForceActivate() {
        this.isForceActivated = true;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    public CodeValue getClientType() {
        return this.clientType;
    }

    public CodeValue getClientClassification() {
        return this.clientClassification;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

}