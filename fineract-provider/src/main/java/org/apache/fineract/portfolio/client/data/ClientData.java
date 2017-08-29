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
package org.apache.fineract.portfolio.client.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.data.SavingsAccountSummaryData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.joda.time.LocalDate;

import com.finflux.kyc.address.data.AddressData;

/**
 * Immutable data object representing client data.
 */
final public class ClientData implements Comparable<ClientData> {

    private final Long id;
    private final String accountNo;
    private final String externalId;

    private final EnumOptionData status;
    private final CodeValueData subStatus;

    @SuppressWarnings("unused")
    private final Boolean active;
    private final LocalDate activationDate;

    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final String fullname;
    private final String displayName;
    private final String mobileNo;
    private final String alternateMobileNo;
    private final LocalDate dateOfBirth;
    private final CodeValueData gender;
    private final CodeValueData clientType;
    private final CodeValueData clientClassification;
    private final String emailId;

    private final Long officeId;
    private final String officeName;
    private final Long transferToOfficeId;
    private final String transferToOfficeName;

    private final Long imageId;
    private final Boolean imagePresent;
    private final Long staffId;
    private final String staffName;
    private final ClientTimelineData timeline;
    private final CodeValueData closureReason;
    private final Long savingsProductId;
    private final String savingsProductName;

    private final Long savingsAccountId;
    private final EnumOptionData legalForm;
    // associations
    private final Collection<GroupGeneralData> groups;

    // template
    private final Collection<OfficeData> officeOptions;
    private final Collection<StaffData> staffOptions;
    private final Collection<CodeValueData> narrations;
    private final Collection<SavingsProductData> savingProductOptions;
    private final Collection<SavingsAccountData> savingAccountOptions;
    private final Collection<CodeValueData> genderOptions;
    private final Collection<CodeValueData> clientTypeOptions;
    private final Collection<CodeValueData> clientClassificationOptions;    
    private final Collection<CodeValueData> clientNonPersonConstitutionOptions;
    private final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions;
    private final List<EnumOptionData> clientLegalFormOptions;
    
    private final ClientNonPersonData clientNonPersonDetails;

    // lookup
    private Collection<LoanAccountSummaryData> loanAccountSummaryDatas;
    private Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas;
    private final Collection<CodeValueData> closureReasons;

    private Collection<ClientData> possibleClientMatches = null;
    
    private Collection<AddressData> addressData;
    private final Boolean isWorkflowEnabled;
    private final Long workflowId;

    public static ClientData template(final Long officeId, final LocalDate joinedDate, final Collection<OfficeData> officeOptions,
            final Collection<StaffData> staffOptions, final Collection<CodeValueData> narrations,
            final Collection<CodeValueData> genderOptions, final Collection<SavingsProductData> savingProductOptions,
            final Collection<CodeValueData> clientTypeOptions, final Collection<CodeValueData> clientClassificationOptions,
            final Collection<CodeValueData> clientNonPersonConstitutionOptions,
            final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions, final List<EnumOptionData> clientLegalFormOptions, 
            final Collection<CodeValueData> closureReasons, final Boolean isWorkflowEnabled) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final String officeName = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final Long id = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String displayName = null;
        final String externalId = null;
        final String mobileNo = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final CodeValueData closureReason = null;
        final Collection<GroupGeneralData> groups = null;
        final ClientTimelineData timeline = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final EnumOptionData legalForm = null;
        final ClientNonPersonData clientNonPersonDetails = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<AddressData> addressData = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, joinedDate,
                imageId, staffId, staffName, officeOptions, groups, staffOptions, narrations, genderOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions,
                clientNonPersonDetails, clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas,
                closureReasons, closureReason, emailId, addressData, isWorkflowEnabled, workflowId);

    }

    public static ClientData templateOnTop(final ClientData clientData, final ClientData templateData) {

        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName, clientData.externalId, clientData.mobileNo, clientData.alternateMobileNo,
                clientData.dateOfBirth, clientData.gender, clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, templateData.officeOptions, clientData.groups, templateData.staffOptions, templateData.narrations,
                templateData.genderOptions, clientData.timeline, templateData.savingProductOptions, clientData.savingsProductId,
                clientData.savingsProductName, clientData.savingsAccountId, clientData.savingAccountOptions, clientData.clientType,
                clientData.clientClassification, templateData.clientTypeOptions, templateData.clientClassificationOptions,
                templateData.clientNonPersonConstitutionOptions, templateData.clientNonPersonMainBusinessLineOptions,
                clientData.clientNonPersonDetails, templateData.clientLegalFormOptions, clientData.legalForm,
                clientData.loanAccountSummaryDatas, clientData.savingsAccountSummaryDatas, templateData.closureReasons,
                clientData.closureReason, clientData.emailId, clientData.addressData, templateData.isWorkflowEnabled, clientData.workflowId);

    }

    public static ClientData templateWithSavingAccountOptions(final ClientData clientData,
            final Collection<SavingsAccountData> savingAccountOptions) {

        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName, clientData.externalId, clientData.mobileNo, clientData.alternateMobileNo,
                clientData.dateOfBirth, clientData.gender, clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions, clientData.groups, clientData.staffOptions, clientData.narrations,
                clientData.genderOptions, clientData.timeline, clientData.savingProductOptions, clientData.savingsProductId,
                clientData.savingsProductName, clientData.savingsAccountId, savingAccountOptions, clientData.clientType,
                clientData.clientClassification, clientData.clientTypeOptions, clientData.clientClassificationOptions,
                clientData.clientNonPersonConstitutionOptions, clientData.clientNonPersonMainBusinessLineOptions,
                clientData.clientNonPersonDetails, clientData.clientLegalFormOptions, clientData.legalForm,
                clientData.loanAccountSummaryDatas, clientData.savingsAccountSummaryDatas, clientData.closureReasons,
                clientData.closureReason, clientData.emailId, clientData.addressData, clientData.isWorkflowEnabled, clientData.workflowId);

    }

    public static ClientData setParentGroups(final ClientData clientData, final Collection<GroupGeneralData> parentGroups) {
        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName, clientData.externalId, clientData.mobileNo, clientData.alternateMobileNo,
                clientData.dateOfBirth, clientData.gender, clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions, parentGroups, clientData.staffOptions, null, null, clientData.timeline,
                clientData.savingProductOptions, clientData.savingsProductId, clientData.savingsProductName, clientData.savingsAccountId,
                clientData.savingAccountOptions, clientData.clientType, clientData.clientClassification, clientData.clientTypeOptions,
                clientData.clientClassificationOptions, clientData.clientNonPersonConstitutionOptions,
                clientData.clientNonPersonMainBusinessLineOptions, clientData.clientNonPersonDetails, clientData.clientLegalFormOptions,
                clientData.legalForm, clientData.loanAccountSummaryDatas, clientData.savingsAccountSummaryDatas, clientData.closureReasons,
                clientData.closureReason, clientData.emailId, clientData.addressData, clientData.isWorkflowEnabled, clientData.workflowId);

    }
    
    public static ClientData lookupforhierarchy(final ClientData clientData, final Collection<GroupGeneralData> parentGroups, final Long officeId, final String officeName) {
        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, officeId, officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName, clientData.externalId, clientData.mobileNo, clientData.alternateMobileNo,
                clientData.dateOfBirth, clientData.gender, clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions, parentGroups, clientData.staffOptions, null, null, clientData.timeline,
                clientData.savingProductOptions, clientData.savingsProductId, clientData.savingsProductName, clientData.savingsAccountId,
                clientData.savingAccountOptions, clientData.clientType, clientData.clientClassification, clientData.clientTypeOptions,
                clientData.clientClassificationOptions, clientData.clientNonPersonConstitutionOptions,
                clientData.clientNonPersonMainBusinessLineOptions, clientData.clientNonPersonDetails, clientData.clientLegalFormOptions,
                clientData.legalForm, clientData.loanAccountSummaryDatas, clientData.savingsAccountSummaryDatas, clientData.closureReasons,
                clientData.closureReason, clientData.emailId, clientData.addressData, clientData.isWorkflowEnabled, clientData.workflowId);

    }

    public static ClientData clientIdentifier(final Long id, final String accountNo, final String firstname, final String middlename,
            final String lastname, final String fullname, final String displayName, final Long officeId, final String officeName) {

        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String externalId = null;
        final String mobileNo = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final EnumOptionData legalForm = null;
        final CodeValueData closureReason = null;
        final ClientNonPersonData clientNonPerson = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions,
                clientNonPerson, clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas, closureReasons,
                closureReason, emailId, addressData, isWorkflowEnabled, workflowId);
    }

    public static ClientData lookup(final Long id, final String displayName, final Long officeId, final String officeName) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String externalId = null;
        final String mobileNo = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final EnumOptionData legalForm = null;
        final CodeValueData closureReason = null;
        final ClientNonPersonData clientNonPerson = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, 
                clientNonPerson, clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas, closureReasons,
                closureReason, emailId, addressData, isWorkflowEnabled, workflowId);

    }
    
    public static ClientData lookup(final Long id, final String displayName, final String accountNo,final Long officeId,final String officeName,final Long staffId,final String staffName) {
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String externalId = null;
        final String mobileNo = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final EnumOptionData legalForm = null;
        final CodeValueData closureReason = null;
        final ClientNonPersonData clientNonPerson = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, 
                clientNonPerson, clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas, closureReasons,
                closureReason, emailId, addressData, isWorkflowEnabled, workflowId);

    }


    public static ClientData instance(final String accountNo, final EnumOptionData status, final CodeValueData subStatus,
            final Long officeId, final String officeName, final Long transferToOfficeId, final String transferToOfficeName, final Long id,
            final String firstname, final String middlename, final String lastname, final String fullname, final String displayName,
            final String externalId, final String mobileNo, final String alternateMobileNo, final LocalDate dateOfBirth, final CodeValueData gender,
            final LocalDate activationDate, final Long imageId, final Long staffId, final String staffName,
            final ClientTimelineData timeline, final Long savingsProductId, final String savingsProductName, final Long savingsAccountId,
            final CodeValueData clientType, final CodeValueData clientClassification, final EnumOptionData legalForm,
            final ClientNonPersonData clientNonPerson, CodeValueData closureReason, final String emailId, final Boolean isWorkflowEnabled,
            final Long workflowId) {

        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<AddressData> addressData = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, null, clientType, clientClassification, clientTypeOptions,
                clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, clientNonPerson,
                clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas, closureReasons, closureReason,
                emailId, addressData, isWorkflowEnabled, workflowId);

    }

    public static ClientData instanceDedup(final Long id, final String firstname, final String middlename, final String lastname,
            final String fullname, final String displayName, final String mobileNo, final LocalDate dateOfBirth, final CodeValueData gender,
            final EnumOptionData legalForm, final ClientNonPersonData clientNonPerson) {
        
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long officeId = null;
        final String officeName = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String externalId = null;
        final String alternateMobileNo = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final ClientTimelineData timeline = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final CodeValueData closureReason = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final String emailId = null;
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;

        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate,
                imageId, staffId,staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline,
                savingProductOptions,savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType,
                clientClassification, clientTypeOptions,clientClassificationOptions, clientNonPersonConstitutionOptions,
                clientNonPersonMainBusinessLineOptions, clientNonPerson,clientLegalFormOptions, legalForm, loanAccountSummaryDatas,
                savingsAccountSummaryDatas, closureReasons, closureReason, emailId, addressData, isWorkflowEnabled, workflowId);

    }

    public static ClientData instance(final Long id, final String accountNo, final String displayName, final EnumOptionData status) {
        final CodeValueData subStatus = null;
        final Long officeId = null;
        final String officeName = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String externalId = null;
        final String mobileNo = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final ClientTimelineData timeline = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final EnumOptionData legalForm = null;
        final ClientNonPersonData clientNonPerson = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final CodeValueData closureReason = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate,
                imageId, staffId, staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline,
                savingProductOptions, savingsProductId, savingsProductName, savingsAccountId, null, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions,
                clientNonPerson, clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas, closureReasons,
                closureReason, emailId, addressData, isWorkflowEnabled, workflowId);
    }
    
    public static ClientData formClientData(final Long id, final String displayName) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String externalId = null;
        final String mobileNo = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final EnumOptionData legalForm = null;
        final ClientNonPersonData clientNonPerson = null;
        final Long officeId = null;
        final CodeValueData closureReason = null;
        final String officeName = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = new ArrayList<>();
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, 
                clientNonPerson, clientLegalFormOptions, legalForm, loanAccountSummaryDatas, savingsAccountSummaryDatas, closureReasons, closureReason, emailId, addressData, isWorkflowEnabled, workflowId);

    }

    private ClientData(final String accountNo, final EnumOptionData status, final CodeValueData subStatus, final Long officeId,
            final String officeName, final Long transferToOfficeId, final String transferToOfficeName, final Long id,
            final String firstname, final String middlename, final String lastname, final String fullname, final String displayName,
            final String externalId, final String mobileNo, final String alternateMobileNo, final LocalDate dateOfBirth, final CodeValueData gender,
            final LocalDate activationDate, final Long imageId, final Long staffId, final String staffName,
            final Collection<OfficeData> allowedOffices, final Collection<GroupGeneralData> groups,
            final Collection<StaffData> staffOptions, final Collection<CodeValueData> narrations,
            final Collection<CodeValueData> genderOptions, final ClientTimelineData timeline,
            final Collection<SavingsProductData> savingProductOptions, final Long savingsProductId, final String savingsProductName,
            final Long savingsAccountId, final Collection<SavingsAccountData> savingAccountOptions, final CodeValueData clientType,
            final CodeValueData clientClassification, final Collection<CodeValueData> clientTypeOptions,
            final Collection<CodeValueData> clientClassificationOptions,
            final Collection<CodeValueData> clientNonPersonConstitutionOptions,
            final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions, final ClientNonPersonData clientNonPerson,
            final List<EnumOptionData> clientLegalFormOptions, final EnumOptionData legalForm,
            final Collection<LoanAccountSummaryData> loanAccountSummaryDatas,
            final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas, Collection<CodeValueData> closureReasons, CodeValueData closureReason, String emailId,
            final Collection<AddressData> addressData, final Boolean isWorkflowEnabled, final Long workflowId) {
        this.accountNo = accountNo;
        this.status = status;
        if (status != null) {
            this.active = status.getId().equals(300L);
        } else {
            this.active = null;
        }
        this.subStatus = subStatus;
        this.officeId = officeId;
        this.officeName = officeName;
        this.transferToOfficeId = transferToOfficeId;
        this.transferToOfficeName = transferToOfficeName;
        this.id = id;
        this.firstname = StringUtils.defaultIfEmpty(firstname, null);
        this.middlename = StringUtils.defaultIfEmpty(middlename, null);
        this.lastname = StringUtils.defaultIfEmpty(lastname, null);
        this.fullname = StringUtils.defaultIfEmpty(fullname, null);
        this.displayName = StringUtils.defaultIfEmpty(displayName, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.closureReason = closureReason;
        this.mobileNo = StringUtils.defaultIfEmpty(mobileNo, null);
        this.alternateMobileNo = StringUtils.defaultIfEmpty(alternateMobileNo, null);
        this.emailId = StringUtils.defaultIfEmpty(emailId, null);
        this.activationDate = activationDate;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.clientClassification = clientClassification;
        this.clientType = clientType;
        this.imageId = imageId;
        if (imageId != null) {
            this.imagePresent = Boolean.TRUE;
        } else {
            this.imagePresent = null;
        }
        this.staffId = staffId;
        this.staffName = staffName;
        
        // associations
        this.groups = groups;

        // template
        this.officeOptions = allowedOffices;
        this.staffOptions = staffOptions;
        this.narrations = narrations;

        this.genderOptions = genderOptions;
        this.clientClassificationOptions = clientClassificationOptions;
        this.clientTypeOptions = clientTypeOptions;

        this.clientNonPersonConstitutionOptions = clientNonPersonConstitutionOptions;
        this.clientNonPersonMainBusinessLineOptions = clientNonPersonMainBusinessLineOptions;
        this.clientLegalFormOptions = clientLegalFormOptions;

        this.timeline = timeline;
        this.savingProductOptions = savingProductOptions;
        this.savingsProductId = savingsProductId;
        this.savingsProductName = savingsProductName;
        this.savingsAccountId = savingsAccountId;
        this.savingAccountOptions = savingAccountOptions;
        this.legalForm = legalForm;
        this.clientNonPersonDetails = clientNonPerson;
        this.loanAccountSummaryDatas = loanAccountSummaryDatas;
        this.savingsAccountSummaryDatas = savingsAccountSummaryDatas;
        this.closureReasons = closureReasons;
        this.addressData = addressData;
        this.isWorkflowEnabled = isWorkflowEnabled;
        this.workflowId = workflowId;
    }
    
    public static ClientData formClientData(final Long id, final String displayName, final LocalDate dateOfBirth,
            final CodeValueData gender, final String mobileNo) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String externalId = null;
        final String alternateMobileNo = null;
        final String emailId = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final EnumOptionData legalForm = null;
        final ClientNonPersonData clientNonPerson = null;
        final Long officeId = null;
        final CodeValueData closureReason = null;
        final String officeName = null;
        final Collection<SavingsAccountSummaryData> savingsAccountSummaryDatas = null;
        final Collection<LoanAccountSummaryData> loanAccountSummaryDatas = new ArrayList<>();
        final Collection<AddressData> addressData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName, externalId, mobileNo, alternateMobileNo, dateOfBirth, gender, activationDate,
                imageId, staffId, staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions, timeline,
                savingProductOptions, savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType,
                clientClassification, clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions,
                clientNonPersonMainBusinessLineOptions, clientNonPerson, clientLegalFormOptions, legalForm, loanAccountSummaryDatas,
                savingsAccountSummaryDatas, closureReasons, closureReason, emailId, addressData, isWorkflowEnabled, workflowId);
    }

    public Long id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }
    
    public String accountNo() {
    	return this.accountNo;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String officeName() {
        return this.officeName;
    }

    public Long getImageId() {
        return this.imageId;
    }

    public Boolean getImagePresent() {
        return this.imagePresent;
    }

    public ClientTimelineData getTimeline() {
        return this.timeline;
    }

    @Override
    public int compareTo(final ClientData obj) {
        if (obj == null) { return -1; }
        return new CompareToBuilder() //
                .append(this.id, obj.id) //
                .append(this.displayName, obj.displayName) //
                .append(this.mobileNo, obj.mobileNo) //
                .append(this.alternateMobileNo, obj.alternateMobileNo) //
                .toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final ClientData rhs = (ClientData) obj;
        return new EqualsBuilder() //
                .append(this.id, rhs.id) //
                .append(this.displayName, rhs.displayName) //
                .append(this.mobileNo, rhs.mobileNo) //
                .append(this.alternateMobileNo, rhs.alternateMobileNo) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37) //
                .append(this.id) //
                .append(this.displayName) //
                .toHashCode();
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }

    public void addLoanAccountSummaryData(final LoanAccountSummaryData loanAccountSummaryData) {

        if (this.loanAccountSummaryDatas == null) {
            this.loanAccountSummaryDatas = new ArrayList<>();
        }
        this.loanAccountSummaryDatas.add(loanAccountSummaryData);
    }
    
    public Collection<GroupGeneralData> getGroups() {
        return this.groups;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setPossibleClientMatches(final Collection<ClientData> possibleClientMatches) {
        this.possibleClientMatches = possibleClientMatches;
    }

    public EnumOptionData getLegalForm() {
        return this.legalForm;
    }

    public CodeValueData getClientType() {
        return this.clientType;
    }

    public CodeValueData getClientClassification() {
        return this.clientClassification;
    }
    
    public void updateAddressData(Collection<AddressData> addressData){
        this.addressData = addressData;
    }
}