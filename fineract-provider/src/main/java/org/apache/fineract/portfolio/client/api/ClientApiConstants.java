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
package org.apache.fineract.portfolio.client.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.fineract.portfolio.client.data.ClientData;

public class ClientApiConstants {

    public static final String CLIENT_RESOURCE_NAME = "client";
    public static final String CLIENT_CLOSURE_REASON = "ClientClosureReason";
    public static final String CLIENT_ACTION_REASON = "ClientActionReason";
    public static final String CLIENT_REJECT_REASON = "ClientRejectReason";
    public static final String CLIENT_WITHDRAW_REASON = "ClientWithdrawReason";

	
    public static final String GENDER = "Gender";
    public static final String CLIENT_TYPE = "ClientType";
    public static final String CLIENT_CLASSIFICATION = "ClientClassification";
    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    // request parameters
    public static final String idParamName = "id";
    public static final String groupIdParamName = "groupId";
    public static final String accountNoParamName = "accountNo";
    public static final String externalIdParamName = "externalId";
    public static final String mobileNoParamName = "mobileNo";
    public static final String firstnameParamName = "firstname";
    public static final String middlenameParamName = "middlename";
    public static final String lastnameParamName = "lastname";
    public static final String fullnameParamName = "fullname";
    public static final String officeIdParamName = "officeId";
    public static final String transferOfficeIdParamName = "transferOfficeIdParamName";
    public static final String activeParamName = "active";
    public static final String activationDateParamName = "activationDate";
    public static final String reactivationDateParamName = "reactivationDate";
    public static final String staffIdParamName = "staffId";
    public static final String closureDateParamName = "closureDate";
    public static final String closureReasonIdParamName = "closureReasonId";
    public static final String nationalId = "nationalId";
    
    public static final String rejectionDateParamName = "rejectionDate";
    public static final String rejectionReasonIdParamName ="rejectionReasonId";
    public static final String withdrawalDateParamName = "withdrawalDate";
    public static final String withdrawalReasonIdParamName ="withdrawalReasonId";
        
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String savingsProductIdParamName = "savingsProductId";
    public static final String savingsAccountIdParamName = "savingsAccountId";
    public static final String dateOfBirthParamName = "dateOfBirth";
    public static final String genderIdParamName = "genderId";
    public static final String genderParamName = "gender";
    public static final String clientTypeIdParamName = "clientTypeId";
    public static final String clientTypeParamName = "clientType";
    public static final String clientClassificationIdParamName = "clientClassificationId";
    public static final String clientClassificationParamName = "clientClassification";
    // response parameters
    public static final String statusParamName = "status";
    public static final String hierarchyParamName = "hierarchy";
    public static final String displayNameParamName = "displayName";
    public static final String officeNameParamName = "officeName";
    public static final String staffNameParamName = "staffName";
    public static final String trasnferOfficeNameParamName = "transferOfficeName";
    public static final String transferToOfficeNameParamName = "transferToOfficeName";
    public static final String transferToOfficeIdParamName = "transferToOfficeId";
    public static final String imageKeyParamName = "imageKey";
    public static final String imageIdParamName = "imageId";
    public static final String imagePresentParamName = "imagePresent";
    public static final String timelineParamName = "timeline";

    // associations related part of response
    public static final String groupsParamName = "groups";

    // template related part of response
    public static final String officeOptionsParamName = "officeOptions";
    public static final String staffOptionsParamName = "staffOptions";

    public static final Set<String> CLIENT_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, groupIdParamName, accountNoParamName, externalIdParamName, mobileNoParamName, firstnameParamName,
            middlenameParamName, lastnameParamName, fullnameParamName, officeIdParamName, activeParamName, activationDateParamName,
            staffIdParamName, submittedOnDateParamName, savingsProductIdParamName, dateOfBirthParamName, genderIdParamName,
            clientTypeIdParamName, clientClassificationIdParamName,nationalId));

    public static final Set<String> CLIENT_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, accountNoParamName, externalIdParamName, mobileNoParamName, firstnameParamName, middlenameParamName,

            lastnameParamName, fullnameParamName, activeParamName, activationDateParamName, staffIdParamName, savingsProductIdParamName,
            dateOfBirthParamName, genderIdParamName, clientTypeIdParamName, clientClassificationIdParamName,submittedOnDateParamName,nationalId));


    /**
     * These parameters will match the class level parameters of
     * {@link ClientData}. Where possible, we try to get response parameters to
     * match those of request parameters.
     */
    public static final Set<String> CLIENT_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, accountNoParamName,
            externalIdParamName, statusParamName, activeParamName, activationDateParamName, firstnameParamName, middlenameParamName,
            lastnameParamName, fullnameParamName, displayNameParamName, mobileNoParamName, officeIdParamName, officeNameParamName,
            transferToOfficeIdParamName, transferToOfficeNameParamName, hierarchyParamName, imageIdParamName, imagePresentParamName,
            staffIdParamName, staffNameParamName, timelineParamName, groupsParamName, officeOptionsParamName, staffOptionsParamName,
            dateOfBirthParamName, genderParamName,clientTypeParamName,clientClassificationParamName, nationalId));

    public static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, activationDateParamName));
    public static final Set<String> REACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, reactivationDateParamName));

    public static final Set<String> CLIENT_CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, closureDateParamName, closureReasonIdParamName));
	
    public static final Set<String> CLIENT_REJECT_DATA_PARAMETERS =	new HashSet<>(Arrays.asList(localeParamName,dateFormatParamName,rejectionDateParamName,rejectionReasonIdParamName));

    public static final Set<String> CLIENT_WITHDRAW_DATA_PARAMETERS =	new HashSet<>(Arrays.asList(localeParamName,dateFormatParamName,withdrawalDateParamName,withdrawalReasonIdParamName));

}