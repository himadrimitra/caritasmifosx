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
package org.apache.fineract.commands.service;

import javax.ws.rs.core.MediaType;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.infrastructure.accountnumberformat.service.AccountNumberFormatConstants;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.paymenttype.api.PaymentTypeApiResourceConstants;
import org.apache.fineract.portfolio.savings.DepositsApiConstants;
import org.apache.fineract.portfolio.village.api.VillageTypeApiConstants;
import org.apache.fineract.useradministration.api.PasswordPreferencesApiConstants;

import com.sun.jersey.multipart.FormDataMultiPart;

public class CommandWrapperBuilder {

    private Long officeId;
    private Long groupId;
    private Long clientId;
    private Long loanId;
    private Long savingsId;
    private String actionName;
    private String entityName;
    private Long entityId;
    private Long subentityId;
    private String href;
    private String json = "{}";
    private String transactionId;
    private Long productId;
    private Long templateId;
    private MediaType file;
    private String option;
    private Integer entityTypeId;
    private FormDataMultiPart formDataMultiPart;

    public CommandWrapperBuilder withMediaType(final MediaType withMediaType) {
        this.file = withMediaType;
        return this;
    }

    public CommandWrapper build() {
        return new CommandWrapper(this.officeId, this.groupId, this.clientId, this.loanId, this.savingsId, this.actionName, this.entityName,
                this.entityId, this.subentityId, this.href, this.json, this.transactionId, this.productId, this.templateId, this.option,
                this.entityTypeId, this.formDataMultiPart);
    }

    public CommandWrapperBuilder withLoanId(final Long withLoanId) {
        this.loanId = withLoanId;
        return this;
    }

    public CommandWrapperBuilder withSavingsId(final Long withSavingsId) {
        this.savingsId = withSavingsId;
        return this;
    }

    public CommandWrapperBuilder withClientId(final Long withClientId) {
        this.clientId = withClientId;
        return this;
    }

    public CommandWrapperBuilder withGroupId(final Long withGroupId) {
        this.groupId = withGroupId;
        return this;
    }

    public CommandWrapperBuilder withEntityName(final String withEntityName) {
        this.entityName = withEntityName;
        return this;
    }

    public CommandWrapperBuilder withSubEntityId(final Long withSubEntityId) {
        this.subentityId = withSubEntityId;
        return this;
    }

    public CommandWrapperBuilder withJson(final String withJson) {
        this.json = withJson;
        return this;
    }

    public CommandWrapperBuilder withNoJsonBody() {
        this.json = null;
        return this;
    }

    public CommandWrapperBuilder updateGlobalConfiguration(final Long configId) {
        this.actionName = "UPDATE";
        this.entityName = "CONFIGURATION";
        this.entityId = configId;

        this.href = "/configurations/" + configId;
        return this;
    }

    public CommandWrapperBuilder updatePermissions() {
        this.actionName = "UPDATE";
        this.entityName = "PERMISSION";
        this.entityId = null;
        this.href = "/permissions";
        return this;
    }

    public CommandWrapperBuilder createRole() {
        this.actionName = "CREATE";
        this.entityName = "ROLE";
        this.href = "/roles/template";
        return this;
    }

    public CommandWrapperBuilder updateRole(final Long roleId) {
        this.actionName = "UPDATE";
        this.entityName = "ROLE";
        this.entityId = roleId;
        this.href = "/roles/" + roleId;
        return this;
    }

    public CommandWrapperBuilder updateRolePermissions(final Long roleId) {
        this.actionName = "PERMISSIONS";
        this.entityName = "ROLE";
        this.entityId = roleId;
        this.href = "/roles/" + roleId + "/permissions";
        return this;
    }

    public CommandWrapperBuilder createUser() {
        this.actionName = "CREATE";
        this.entityName = "USER";
        this.entityId = null;
        this.href = "/users/template";
        return this;
    }

    public CommandWrapperBuilder updateUser(final Long userId) {
        this.actionName = "UPDATE";
        this.entityName = "USER";
        this.entityId = userId;
        this.href = "/users/" + userId;
        return this;
    }

    public CommandWrapperBuilder deleteUser(final Long userId) {
        this.actionName = "DELETE";
        this.entityName = "USER";
        this.entityId = userId;
        this.href = "/users/" + userId;
        return this;
    }

    public CommandWrapperBuilder unlockUser(final Long userId) {
        this.actionName = "UNLOCK";
        this.entityName = "USER";
        this.entityId = userId;
        this.href = "/users/" + userId + "?command=unlock";
        return this;
    }

    public CommandWrapperBuilder createOffice() {
        this.actionName = "CREATE";
        this.entityName = "OFFICE";
        this.entityId = null;
        this.href = "/offices/template";
        return this;
    }

    public CommandWrapperBuilder updateOffice(final Long officeId) {
        this.actionName = "UPDATE";
        this.entityName = "OFFICE";
        this.entityId = officeId;
        this.href = "/offices/" + officeId;
        return this;
    }

    public CommandWrapperBuilder createOfficeTransaction() {
        this.actionName = "CREATE";
        this.entityName = "OFFICETRANSACTION";
        this.href = "/officetransactions/template";
        return this;
    }

    public CommandWrapperBuilder deleteOfficeTransaction(final Long transactionId) {
        this.actionName = "DELETE";
        this.entityName = "OFFICETRANSACTION";
        this.entityId = transactionId;
        this.href = "/officetransactions/" + transactionId;
        return this;
    }

    public CommandWrapperBuilder activateOffice(final Long officeId) {
        this.actionName = "ACTIVATE";
        this.entityName = "OFFICE";
        this.entityId = officeId;
        this.href = "/offices/" + officeId;
        return this;
    }

    public CommandWrapperBuilder rejectOffice(final Long officeId) {
        this.actionName = "REJECT";
        this.entityName = "OFFICE";
        this.entityId = officeId;
        this.href = "/offices/" + officeId;
        return this;
    }

    public CommandWrapperBuilder intiateOfficeWorkflow(final Long officeId) {
        this.actionName = "INITIATEWORKFLOW";
        this.entityName = "OFFICE";
        this.entityId = officeId;
        this.href = "/offices/" + officeId + "?command=initiateWorkflow";
        return this;
    }

    public CommandWrapperBuilder createStaff() {
        this.actionName = "CREATE";
        this.entityName = "STAFF";
        this.entityId = null;
        this.href = "/staff/template";
        return this;
    }

    public CommandWrapperBuilder updateStaff(final Long staffId) {
        this.actionName = "UPDATE";
        this.entityName = "STAFF";
        this.entityId = staffId;
        this.href = "/staff/" + staffId;
        return this;
    }

    public CommandWrapperBuilder createGuarantor(final Long loanId) {
        this.actionName = "CREATE";
        this.entityName = "GUARANTOR";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/guarantors";
        return this;
    }

    public CommandWrapperBuilder recoverFromGuarantor(final Long loanId) {
        this.actionName = "RECOVERGUARANTEES";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "?command=recoverGuarantees";
        return this;
    }

    public CommandWrapperBuilder updateGuarantor(final Long loanId, final Long guarantorId) {
        this.actionName = "UPDATE";
        this.entityName = "GUARANTOR";
        this.entityId = guarantorId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/guarantors/" + guarantorId;
        return this;
    }

    public CommandWrapperBuilder deleteGuarantor(final Long loanId, final Long guarantorId, final Long guarantorFundingId) {
        this.actionName = "DELETE";
        this.entityName = "GUARANTOR";
        this.entityId = guarantorId;
        this.subentityId = guarantorFundingId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/guarantors/" + guarantorId;
        return this;
    }

    public CommandWrapperBuilder createFund() {
        this.actionName = "CREATE";
        this.entityName = "FUND";
        this.entityId = null;
        this.href = "/funds/template";
        return this;
    }

    public CommandWrapperBuilder updateFund(final Long fundId) {
        this.actionName = "UPDATE";
        this.entityName = "FUND";
        this.entityId = fundId;
        this.href = "/funds/" + fundId;
        return this;
    }

    public CommandWrapperBuilder createReport() {
        this.actionName = "CREATE";
        this.entityName = "REPORT";
        this.entityId = null;
        this.href = "/reports/template";
        return this;
    }

    public CommandWrapperBuilder updateReport(final Long id) {
        this.actionName = "UPDATE";
        this.entityName = "REPORT";
        this.entityId = id;
        this.href = "/reports/" + id;
        return this;
    }

    public CommandWrapperBuilder deleteReport(final Long id) {
        this.actionName = "DELETE";
        this.entityName = "REPORT";
        this.entityId = id;
        this.href = "/reports/" + id;
        return this;
    }

    public CommandWrapperBuilder updateCurrencies() {
        this.actionName = "UPDATE";
        this.entityName = "CURRENCY";
        this.href = "/currencies";
        return this;
    }

    public CommandWrapperBuilder createSms() {
        this.actionName = "CREATE";
        this.entityName = "SMS";
        this.entityId = null;
        this.href = "/sms/template";
        return this;
    }

    public CommandWrapperBuilder uploadDetails() {
        this.actionName = "CREATE";
        this.entityName = "UPLOADSHEET";
        this.entityId = null;
        this.href = "/uploadxls";
        return this;
    }

    public CommandWrapperBuilder updateSms(final Long resourceId) {
        this.actionName = "UPDATE";
        this.entityName = "SMS";
        this.entityId = resourceId;
        this.href = "/sms/" + resourceId;
        return this;
    }

    public CommandWrapperBuilder deleteSms(final Long resourceId) {
        this.actionName = "DELETE";
        this.entityName = "SMS";
        this.entityId = resourceId;
        this.href = "/sms/" + resourceId;
        return this;
    }

    public CommandWrapperBuilder createCode() {
        this.actionName = "CREATE";
        this.entityName = "CODE";
        this.entityId = null;
        this.href = "/codes/template";
        return this;
    }

    public CommandWrapperBuilder updateCode(final Long codeId) {
        this.actionName = "UPDATE";
        this.entityName = "CODE";
        this.entityId = codeId;
        this.href = "/codes/" + codeId;
        return this;
    }

    public CommandWrapperBuilder deleteCode(final Long codeId) {
        this.actionName = "DELETE";
        this.entityName = "CODE";
        this.entityId = codeId;
        this.href = "/codes/" + codeId;
        return this;
    }

    public CommandWrapperBuilder createHook() {
        this.actionName = "CREATE";
        this.entityName = "HOOK";
        this.entityId = null;
        this.href = "/hooks/template";
        return this;
    }

    public CommandWrapperBuilder updateHook(final Long hookId) {
        this.actionName = "UPDATE";
        this.entityName = "HOOK";
        this.entityId = hookId;
        this.href = "/hooks/" + hookId;
        return this;
    }

    public CommandWrapperBuilder deleteHook(final Long hookId) {
        this.actionName = "DELETE";
        this.entityName = "HOOK";
        this.entityId = hookId;
        this.href = "/hooks/" + hookId;
        return this;
    }

    public CommandWrapperBuilder createCharge() {
        this.actionName = "CREATE";
        this.entityName = "CHARGE";
        this.entityId = null;
        this.href = "/charges/template";
        return this;
    }

    public CommandWrapperBuilder updateCharge(final Long chargeId) {
        this.actionName = "UPDATE";
        this.entityName = "CHARGE";
        this.entityId = chargeId;
        this.href = "/charges/" + chargeId;
        return this;
    }

    public CommandWrapperBuilder deleteCharge(final Long chargeId) {
        this.actionName = "DELETE";
        this.entityName = "CHARGE";
        this.entityId = chargeId;
        this.href = "/charges/" + chargeId;
        return this;
    }

    public CommandWrapperBuilder createLoanProduct() {
        this.actionName = "CREATE";
        this.entityName = "LOANPRODUCT";
        this.entityId = null;
        this.href = "/loanproducts/template";
        return this;
    }

    public CommandWrapperBuilder updateLoanProduct(final Long productId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANPRODUCT";
        this.entityId = productId;
        this.href = "/loanproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder createClientIdentifier(final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "CLIENTIDENTIFIER";
        this.entityId = null;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/identifiers/template";
        return this;
    }

    public CommandWrapperBuilder updateClientIdentifier(final Long clientId, final Long clientIdentifierId) {
        this.actionName = "UPDATE";
        this.entityName = "CLIENTIDENTIFIER";
        this.entityId = clientIdentifierId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/identifiers/" + clientIdentifierId;
        return this;
    }

    public CommandWrapperBuilder deleteClientIdentifier(final Long clientId, final Long clientIdentifierId) {
        this.actionName = "DELETE";
        this.entityName = "CLIENTIDENTIFIER";
        this.entityId = clientIdentifierId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/identifiers/" + clientIdentifierId;
        return this;
    }

    public CommandWrapperBuilder createClient() {
        this.actionName = "CREATE";
        this.entityName = "CLIENT";
        this.href = "/clients/template";
        return this;
    }

    public CommandWrapperBuilder activateClient(final Long clientId) {
        this.actionName = "ACTIVATE";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=activate&template=true";
        return this;
    }

    public CommandWrapperBuilder forceActivateClient(final Long clientId) {
        this.actionName = "FORCE_ACTIVATE";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=forceActivate&template=true";
        return this;
    }

    public CommandWrapperBuilder closeClient(final Long clientId) {
        this.actionName = "CLOSE";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=close&template=true";
        return this;
    }

    public CommandWrapperBuilder rejectClient(final Long clientId) {
        this.actionName = "REJECT";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=reject&template=true";
        return this;
    }

    public CommandWrapperBuilder withdrawClient(final Long clientId) {
        this.actionName = "WITHDRAW";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=withdraw&template=true";
        return this;
    }

    public CommandWrapperBuilder reActivateClient(final Long clientId) {
        this.actionName = "REACTIVATE";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=reactivate&template=true";
        return this;
    }

    public CommandWrapperBuilder proposeClientTransfer(final Long clientId) {
        this.actionName = "PROPOSETRANSFER";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clientId/" + clientId + "?command=proposeTransfer";
        return this;
    }

    public CommandWrapperBuilder proposeAndAcceptClientTransfer(final Long clientId) {
        this.actionName = "PROPOSEANDACCEPTTRANSFER";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clientId/" + clientId + "?command=proposeAndAcceptTransfer";
        return this;
    }

    public CommandWrapperBuilder withdrawClientTransferRequest(final Long clientId) {
        this.actionName = "WITHDRAWTRANSFER";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clientId/" + clientId + "?command=withdrawTransfer";
        return this;
    }

    public CommandWrapperBuilder acceptClientTransfer(final Long clientId) {
        this.actionName = "ACCEPTTRANSFER";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clientId/" + clientId + "?command=acceptTransfer";
        return this;
    }

    public CommandWrapperBuilder rejectClientTransfer(final Long clientId) {
        this.actionName = "REJECTTRANSFER";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clientId/" + clientId + "?command=rejectTransfer";
        return this;
    }

    public CommandWrapperBuilder updateClient(final Long clientId) {
        this.actionName = "UPDATE";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId;
        return this;
    }

    public CommandWrapperBuilder deleteClient(final Long clientId) {
        this.actionName = "DELETE";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId;
        this.json = "{}";
        return this;
    }

    public CommandWrapperBuilder createDBDatatable(final String json) {
        this.actionName = "CREATE";
        this.entityName = "DATATABLE";
        this.entityId = null;
        this.href = "/datatables/";
        this.json = json;
        return this;
    }

    public CommandWrapperBuilder updateDBDatatable(final String datatable, final String json) {
        this.actionName = "UPDATE";
        this.entityName = "DATATABLE";
        this.entityId = null;
        this.href = "/datatables/" + datatable;
        this.json = json;
        return this;
    }

    public CommandWrapperBuilder deleteDBDatatable(final String datatable, final String json) {
        this.actionName = "DELETE";
        this.entityName = "DATATABLE";
        this.entityId = null;
        this.href = "/datatables/" + datatable;
        this.json = json;
        return this;
    }

    public CommandWrapperBuilder undoRejection(final Long clientId) {
        this.actionName = "UNDOREJECT";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=undoRejection";
        return this;
    }

    public CommandWrapperBuilder undoWithdrawal(final Long clientId) {
        this.actionName = "UNDOWITHDRAWAL";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=undoWithdrawal";
        return this;
    }

    public CommandWrapperBuilder createDatatable(final String datatable, final String apptableIdentifier, final Long datatableId) {
        this.actionName = "CREATE";
        commonDatatableSettings(datatable, apptableIdentifier, datatableId);
        return this;
    }

    public CommandWrapperBuilder updateDatatable(final String datatable, final String apptableIdentifier, final Long datatableId) {
        this.actionName = "UPDATE";
        commonDatatableSettings(datatable, apptableIdentifier, datatableId);
        return this;
    }

    public CommandWrapperBuilder deleteDatatable(final String datatable, final String apptableIdentifier, final Long datatableId) {
        this.actionName = "DELETE";
        commonDatatableSettings(datatable, apptableIdentifier, datatableId);
        return this;
    }

    private void commonDatatableSettings(final String datatable, final String apptableIdentifier, final Long datatableId) {

        this.entityName = datatable;
        this.entityId = (apptableIdentifier == null) ? null : Long.parseLong(apptableIdentifier);
        this.subentityId = datatableId;
        if (datatableId == null) {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier;
        } else {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier + "/" + datatableId;
        }
    }

    public CommandWrapperBuilder createDataTableForNonPrimaryIdentifier(final String datatable, final String apptableIdentifier,
            final Long datatableId) {
        this.actionName = "CREATE";
        this.entityName = datatable;
        this.entityId = Long.parseLong("0");
        this.transactionId = apptableIdentifier;
        this.subentityId = datatableId;
        if (datatableId == null) {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier;
        } else {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier + "/" + datatableId;
        }
        return this;
    }

    public CommandWrapperBuilder deleteDataTableForNonPrimaryIdentifier(final String datatable, final String apptableIdentifier,
            final Long datatableId) {
        this.actionName = "DELETE";
        this.entityName = datatable;
        this.entityId = Long.parseLong("0");
        this.transactionId = apptableIdentifier;
        this.subentityId = datatableId;
        if (datatableId == null) {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier;
        } else {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier + "/" + datatableId;
        }
        return this;
    }

    public CommandWrapperBuilder updateDataTableForNonPrimaryIdentifier(final String datatable, final String apptableIdentifier,
            final Long datatableId) {
        this.actionName = "UPDATE";
        this.entityName = datatable;
        this.entityId = Long.parseLong("0");
        this.transactionId = apptableIdentifier;
        this.subentityId = datatableId;
        if (datatableId == null) {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier;
        } else {
            this.href = "/datatables/" + datatable + "/" + apptableIdentifier + "/" + datatableId;
        }
        return this;
    }

    public CommandWrapperBuilder createLoanCharge(final Long loanId) {
        this.actionName = "CREATE";
        this.entityName = "LOANCHARGE";
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/charges";
        return this;
    }

    public CommandWrapperBuilder updateLoanCharge(final Long loanId, final Long loanChargeId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANCHARGE";
        this.entityId = loanChargeId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
        return this;
    }

    public CommandWrapperBuilder waiveLoanCharge(final Long loanId, final Long loanChargeId) {
        this.actionName = "WAIVE";
        this.entityName = "LOANCHARGE";
        this.entityId = loanChargeId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
        return this;
    }

    public CommandWrapperBuilder payLoanCharge(final Long loanId, final Long loanChargeId) {
        this.actionName = "PAY";
        this.entityName = "LOANCHARGE";
        this.entityId = loanChargeId;
        this.loanId = loanId;
        if (loanChargeId == null) {
            this.href = "/loans/" + loanId;
        } else {
            this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
        }
        return this;
    }

    public CommandWrapperBuilder deleteLoanCharge(final Long loanId, final Long loanChargeId) {
        this.actionName = "DELETE";
        this.entityName = "LOANCHARGE";
        this.entityId = loanChargeId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
        return this;
    }

    public CommandWrapperBuilder loanRepaymentTransaction(final Long loanId) {
        this.actionName = "REPAYMENT";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=repayment";
        return this;
    }

    public CommandWrapperBuilder loanPrepaymentTransaction(final Long loanId) {
        this.actionName = "PREPAYMENT";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions?command=prepayment";
        return this;
    }

    public CommandWrapperBuilder loanSubsidyAddTransaction(final Long loanId) {
        this.actionName = "SUBSIDYADD";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions?command=addsubsidy";
        return this;
    }

    public CommandWrapperBuilder loanSubsidyRevokeTransaction(final Long loanId) {
        this.actionName = "SUBSIDYREVOKE";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions?command=revokesubsidy";
        return this;
    }

    public CommandWrapperBuilder loanRecoveryPaymentTransaction(final Long loanId) {
        this.actionName = "RECOVERYPAYMENT";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=recoverypayment";
        return this;
    }

    public CommandWrapperBuilder waiveInterestPortionTransaction(final Long loanId) {
        this.actionName = "WAIVEINTERESTPORTION";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=waiveinterest";
        return this;
    }

    public CommandWrapperBuilder writeOffLoanTransaction(final Long loanId) {
        this.actionName = "WRITEOFF";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=writeoff";
        return this;
    }

    public CommandWrapperBuilder undoWriteOffLoanTransaction(final Long loanId) {
        this.actionName = "UNDOWRITEOFF";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=undowriteoff";
        return this;
    }

    public CommandWrapperBuilder closeLoanAsRescheduledTransaction(final Long loanId) {
        this.actionName = "CLOSEASRESCHEDULED";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=close-rescheduled";
        return this;
    }

    public CommandWrapperBuilder closeLoanTransaction(final Long loanId) {
        this.actionName = "CLOSE";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=close";
        return this;
    }

    public CommandWrapperBuilder adjustTransaction(final Long loanId, final Long transactionId) {
        this.actionName = "ADJUST";
        this.entityName = "LOAN";
        this.entityId = transactionId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/" + transactionId;
        return this;
    }

    public CommandWrapperBuilder refundLoanTransactionByCash(final Long loanId) {
        this.actionName = "REFUNDBYCASH";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions/template?command=refundbycash";
        return this;
    }

    public CommandWrapperBuilder loanForeclosure(final Long loanId) {
        this.actionName = "FORECLOSURE";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/transactions?command=foreclosure";
        return this;
    }

    public CommandWrapperBuilder createLoanApplication() {
        this.actionName = "CREATE";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = null;
        this.href = "/loans";
        return this;
    }

    public CommandWrapperBuilder updateLoanApplication(final Long loanId) {
        this.actionName = "UPDATE";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder updateDisbusementDate(final Long loanId, final Long disbursementId) {
        this.actionName = "UPDATE";
        this.entityName = "DISBURSEMENTDETAIL";
        this.entityId = disbursementId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/disbursementdetail/" + disbursementId;
        return this;
    }

    public CommandWrapperBuilder addAndDeleteDisbursementDetails(final Long loanId) {
        this.actionName = "UPDATE";
        this.entityName = "DISBURSEMENTDETAIL";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/editdisbursementdetails/";
        return this;
    }

    public CommandWrapperBuilder deleteLoanApplication(final Long loanId) {
        this.actionName = "DELETE";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder rejectLoanApplication(final Long loanId) {
        this.actionName = "REJECT";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder withdrawLoanApplication(final Long loanId) {
        this.actionName = "WITHDRAW";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder approveLoanApplication(final Long loanId) {
        this.actionName = "APPROVE";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder disburseLoanApplication(final Long loanId) {
        this.actionName = "DISBURSE";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder disburseLoanToSavingsApplication(final Long loanId) {
        this.actionName = "DISBURSETOSAVINGS";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder undoLoanApplicationApproval(final Long loanId) {
        this.actionName = "APPROVALUNDO";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder undoLoanApplicationDisbursal(final Long loanId) {
        this.actionName = "DISBURSALUNDO";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder undoLastDisbursalLoanApplication(final Long loanId) {
        this.actionName = "DISBURSALLASTUNDO";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder assignLoanOfficer(final Long loanId) {
        this.actionName = "UPDATELOANOFFICER";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder unassignLoanOfficer(final Long loanId) {
        this.actionName = "REMOVELOANOFFICER";
        this.entityName = "LOAN";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId;
        return this;
    }

    public CommandWrapperBuilder assignLoanOfficersInBulk() {
        this.actionName = "BULKREASSIGN";
        this.entityName = "LOAN";
        this.href = "/loans/loanreassignment";
        return this;
    }

    public CommandWrapperBuilder createCodeValue(final Long codeId) {
        this.actionName = "CREATE";
        this.entityName = "CODEVALUE";
        this.entityId = codeId;
        this.href = "/codes/" + codeId + "/codevalues/template";
        return this;
    }

    public CommandWrapperBuilder updateCodeValue(final Long codeId, final Long codeValueId) {
        this.actionName = "UPDATE";
        this.entityName = "CODEVALUE";
        this.subentityId = codeValueId;
        this.entityId = codeId;
        this.href = "/codes/" + codeId + "/codevalues/" + codeValueId;
        return this;
    }

    public CommandWrapperBuilder deleteCodeValue(final Long codeId, final Long codeValueId) {
        this.actionName = "DELETE";
        this.entityName = "CODEVALUE";
        this.subentityId = codeValueId;
        this.entityId = codeId;
        this.href = "/codes/" + codeId + "/codevalues/" + codeValueId;
        return this;
    }

    public CommandWrapperBuilder createGLClosure() {
        this.actionName = "CREATE";
        this.entityName = "GLCLOSURE";
        this.entityId = null;
        this.href = "/glclosures/template";
        return this;
    }

    public CommandWrapperBuilder updateGLClosure(final Long glClosureId) {
        this.actionName = "UPDATE";
        this.entityName = "GLCLOSURE";
        this.entityId = glClosureId;
        this.href = "/glclosures/" + glClosureId;
        return this;
    }

    public CommandWrapperBuilder deleteGLClosure(final Long glClosureId) {
        this.actionName = "DELETE";
        this.entityName = "GLCLOSURE";
        this.entityId = glClosureId;
        this.href = "/glclosures/" + glClosureId;
        return this;
    }

    public CommandWrapperBuilder excuteAccrualAccounting() {
        this.actionName = "EXECUTE";
        this.entityName = "PERIODICACCRUALACCOUNTING";
        this.entityId = null;
        this.href = "/accrualaccounting";
        return this;
    }

    public CommandWrapperBuilder createGLAccount() {
        this.actionName = "CREATE";
        this.entityName = "GLACCOUNT";
        this.entityId = null;
        this.href = "/glaccounts/template";
        return this;
    }

    public CommandWrapperBuilder updateGLAccount(final Long glAccountId) {
        this.actionName = "UPDATE";
        this.entityName = "GLACCOUNT";
        this.entityId = glAccountId;
        this.href = "/glaccounts/" + glAccountId;
        return this;
    }

    public CommandWrapperBuilder deleteGLAccount(final Long glAccountId) {
        this.actionName = "DELETE";
        this.entityName = "GLACCOUNT";
        this.entityId = glAccountId;
        this.href = "/glaccounts/" + glAccountId;
        return this;
    }

    public CommandWrapperBuilder createJournalEntry() {
        this.actionName = "CREATE";
        this.entityName = "JOURNALENTRY";
        this.entityId = null;
        this.href = "/journalentries/template";
        return this;
    }

    public CommandWrapperBuilder reverseJournalEntry(final String transactionId) {
        this.actionName = "REVERSE";
        this.entityName = "JOURNALENTRY";
        this.entityId = null;
        this.transactionId = transactionId;
        this.href = "/journalentries/" + transactionId;
        return this;
    }

    public CommandWrapperBuilder updateRunningBalanceForJournalEntry() {
        this.actionName = "UPDATERUNNINGBALANCE";
        this.entityName = "JOURNALENTRY";
        this.entityId = null;
        this.href = "/journalentries/update";
        return this;
    }

    public CommandWrapperBuilder defineOpeningBalanceForJournalEntry() {
        this.actionName = "DEFINEOPENINGBALANCE";
        this.entityName = "JOURNALENTRY";
        this.entityId = null;
        this.href = "/journalentries/update";
        return this;
    }

    public CommandWrapperBuilder updateOpeningBalanceForJournalEntry() {
        this.actionName = "UPDATEOPENINGBALANCE";
        this.entityName = "JOURNALENTRY";
        this.entityId = null;
        this.href = "/journalentries/update";
        return this;
    }

    public CommandWrapperBuilder createSavingProduct() {
        this.actionName = "CREATE";
        this.entityName = "SAVINGSPRODUCT";
        this.entityId = null;
        this.href = "/savingsproducts/template";
        return this;
    }

    public CommandWrapperBuilder updateSavingProduct(final Long productId) {
        this.actionName = "UPDATE";
        this.entityName = "SAVINGSPRODUCT";
        this.entityId = productId;
        this.href = "/savingsproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder deleteSavingProduct(final Long productId) {
        this.actionName = "DELETE";
        this.entityName = "SAVINGSPRODUCT";
        this.entityId = productId;
        this.href = "/savingsproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder createSavingsAccount() {
        this.actionName = "CREATE";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = null;
        this.href = "/savingsaccounts/template";
        return this;
    }

    public CommandWrapperBuilder createOrActivateSavings() {
        this.actionName = "CREATE";
        this.entityName = "CREATEORACTIVATESAVINGSACCOUNT";
        this.entityId = null;
        this.href = "/savingsaccounts/createOrActivateSavingsAccount";
        return this;
    }

    public CommandWrapperBuilder updateSavingsAccount(final Long accountId) {
        this.actionName = "UPDATE";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId;
        return this;
    }

    public CommandWrapperBuilder deleteSavingsAccount(final Long accountId) {
        this.actionName = "DELETE";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId;
        return this;
    }

    public CommandWrapperBuilder rejectSavingsAccountApplication(final Long accountId) {
        this.actionName = "REJECT";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=reject";
        return this;
    }

    public CommandWrapperBuilder withdrawSavingsAccountApplication(final Long accountId) {
        this.actionName = "WITHDRAW";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=withdrawnByApplicant";
        return this;
    }

    public CommandWrapperBuilder approveSavingsAccountApplication(final Long accountId) {
        this.actionName = "APPROVE";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=approve";
        return this;
    }

    public CommandWrapperBuilder undoSavingsAccountApplication(final Long accountId) {
        this.actionName = "APPROVALUNDO";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=undoapproval";
        return this;
    }

    public CommandWrapperBuilder savingsAccountActivation(final Long accountId) {
        this.actionName = "ACTIVATE";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder closeSavingsAccountApplication(final Long accountId) {
        this.actionName = "CLOSE";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder createAccountTransfer() {
        this.actionName = "CREATE";
        this.entityName = "ACCOUNTTRANSFER";
        this.entityId = null;
        this.href = "/accounttransfers";
        return this;
    }

    public CommandWrapperBuilder createStandingInstruction() {
        this.actionName = "CREATE";
        this.entityName = "STANDINGINSTRUCTION";
        this.entityId = null;
        this.href = "/standinginstructions";
        return this;
    }

    public CommandWrapperBuilder updateStandingInstruction(final Long standingInstructionId) {
        this.actionName = "UPDATE";
        this.entityName = "STANDINGINSTRUCTION";
        this.entityId = standingInstructionId;
        this.href = "/standinginstructions";
        return this;
    }

    public CommandWrapperBuilder deleteStandingInstruction(final Long standingInstructionId) {
        this.actionName = "DELETE";
        this.entityName = "STANDINGINSTRUCTION";
        this.entityId = standingInstructionId;
        this.href = "/standinginstructions";
        return this;
    }

    public CommandWrapperBuilder savingsAccountDeposit(final Long accountId) {
        this.actionName = "DEPOSIT";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "/transactions";
        return this;
    }

    public CommandWrapperBuilder savingsAccountWithdrawal(final Long accountId) {
        this.actionName = "WITHDRAWAL";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "/transactions";
        return this;
    }

    public CommandWrapperBuilder undoSavingsAccountTransaction(final Long accountId, final Long transactionId) {
        this.actionName = "UNDOTRANSACTION";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.subentityId = transactionId;
        this.transactionId = transactionId.toString();
        this.href = "/savingsaccounts/" + accountId + "/transactions/" + transactionId + "?command=undo";
        return this;
    }

    public CommandWrapperBuilder adjustSavingsAccountTransaction(final Long accountId, final Long transactionId) {
        this.actionName = "ADJUSTTRANSACTION";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.subentityId = transactionId;
        this.transactionId = transactionId.toString();
        this.href = "/savingsaccounts/" + accountId + "/transactions/" + transactionId + "?command=modify";
        return this;
    }

    public CommandWrapperBuilder savingsAccountInterestCalculation(final Long accountId) {
        this.actionName = "CALCULATEINTEREST";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=calculateInterest";
        return this;
    }

    public CommandWrapperBuilder savingsAccountInterestPosting(final Long accountId) {
        this.actionName = "POSTINTEREST";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=postInterest";
        return this;
    }

    public CommandWrapperBuilder createSavingInvestment(final Long savingsAccountId) {
        this.actionName = "CREATE";
        this.entityName = "SAVINGINVESTMENT";
        this.savingsId = savingsAccountId;
        this.entityId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/savingInvestment";
        return this;
    }

    public CommandWrapperBuilder updateSavingInvestment(final Long savingsAccountId) {
        this.actionName = "UPDATE";
        this.entityName = "SAVINGINVESTMENT";
        this.savingsId = savingsAccountId;
        this.entityId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/savingInvestment";
        return this;
    }

    public CommandWrapperBuilder closeSavingInvestment(final Long savingsAccountId) {
        this.actionName = "CLOSE";
        this.entityName = "SAVINGINVESTMENT";
        this.savingsId = savingsAccountId;
        this.entityId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/savingInvestment/close";
        return this;
    }

    public CommandWrapperBuilder closeLoanInvestment(final Long loanId) {
        this.actionName = "CLOSE";
        this.entityName = "LOANINVESTMENT";
        this.loanId = loanId;
        this.entityId = loanId;
        this.href = "/loans/" + loanId + "/loanInvestment/close";
        return this;
    }

    public CommandWrapperBuilder deleteInvestmentBasedOnMapping(final Long savingsAccountId) {

        this.actionName = "DELETE";
        this.entityName = "SAVINGINVESTMENT";
        this.savingsId = savingsAccountId;
        this.entityId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/savingInvestment";
        return this;

    }

    public CommandWrapperBuilder deleteLoanInvestment(final Long loanId) {
        this.actionName = "DELETE";
        this.entityName = "LOANINVESTMENT";
        this.loanId = loanId;
        this.entityId = loanId;
        this.href = "/loans/" + loanId + "/loanInvestment/delete";
        return this;
    }

    public CommandWrapperBuilder createLoanInvestment(final Long loanId) {
        this.actionName = "CREATE";
        this.entityName = "LOANINVESTMENT";
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/loanInvestment";
        return this;
    }

    public CommandWrapperBuilder updateLoanInvestment(final Long loanId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANINVESTMENT";
        this.loanId = loanId;
        this.entityId = loanId;
        this.href = "/loans/" + loanId + "/loanInvestments";
        return this;
    }

    public CommandWrapperBuilder investmentBatchJob() {
        this.actionName = "RUNBATCHJOB";
        this.entityName = "INVESTMENT";
        this.href = "/investmentBatchJob/";

        return this;
    }

    public CommandWrapperBuilder savingsAccountApplyAnnualFees(final Long accountId) {
        this.actionName = "APPLYANNUALFEE";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=applyAnnualFees";
        return this;
    }

    public CommandWrapperBuilder createSavingsAccountCharge(final Long savingsAccountId) {
        this.actionName = "CREATE";
        this.entityName = "SAVINGSACCOUNTCHARGE";
        this.savingsId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/charges";
        return this;
    }

    public CommandWrapperBuilder updateSavingsAccountCharge(final Long savingsAccountId, final Long savingsAccountChargeId) {
        this.actionName = "UPDATE";
        this.entityName = "SAVINGSACCOUNTCHARGE";
        this.entityId = savingsAccountChargeId;
        this.savingsId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/charges/" + savingsAccountChargeId;
        return this;
    }

    public CommandWrapperBuilder waiveSavingsAccountCharge(final Long savingsAccountId, final Long savingsAccountChargeId) {
        this.actionName = "WAIVE";
        this.entityName = "SAVINGSACCOUNTCHARGE";
        this.entityId = savingsAccountChargeId;
        this.savingsId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/charges/" + savingsAccountChargeId;
        return this;

    }

    public CommandWrapperBuilder paySavingsAccountCharge(final Long savingsAccountId, final Long savingsAccountChargeId) {
        this.actionName = "PAY";
        this.entityName = "SAVINGSACCOUNTCHARGE";
        this.entityId = savingsAccountChargeId;
        this.savingsId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/charges/" + savingsAccountChargeId;
        return this;

    }

    public CommandWrapperBuilder inactivateSavingsAccountCharge(final Long savingsAccountId, final Long savingsAccountChargeId) {
        this.actionName = "INACTIVATE";
        this.entityName = "SAVINGSACCOUNTCHARGE";
        this.entityId = savingsAccountChargeId;
        this.savingsId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/charges/" + savingsAccountChargeId;
        return this;

    }

    public CommandWrapperBuilder deleteSavingsAccountCharge(final Long savingsAccountId, final Long savingsAccountChargeId) {
        this.actionName = "DELETE";
        this.entityName = "SAVINGSACCOUNTCHARGE";
        this.entityId = savingsAccountChargeId;
        this.savingsId = savingsAccountId;
        this.href = "/savingsaccounts/" + savingsAccountId + "/charges/" + savingsAccountChargeId;
        return this;
    }

    public CommandWrapperBuilder createFixedDepositProduct() {
        this.actionName = "CREATE";
        this.entityName = "FIXEDDEPOSITPRODUCT";
        this.entityId = null;
        this.href = "/fixeddepositproducts/template";
        return this;
    }

    public CommandWrapperBuilder updateFixedDepositProduct(final Long productId) {
        this.actionName = "UPDATE";
        this.entityName = "FIXEDDEPOSITPRODUCT";
        this.entityId = productId;
        this.href = "/fixeddepositproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder deleteFixedDepositProduct(final Long productId) {
        this.actionName = "DELETE";
        this.entityName = "FIXEDDEPOSITPRODUCT";
        this.entityId = productId;
        this.href = "/fixeddepositproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder createRecurringDepositProduct() {
        this.actionName = "CREATE";
        this.entityName = "RECURRINGDEPOSITPRODUCT";
        this.entityId = null;
        this.href = "/recurringdepositproducts/template";
        return this;
    }

    public CommandWrapperBuilder updateRecurringDepositProduct(final Long productId) {
        this.actionName = "UPDATE";
        this.entityName = "RECURRINGDEPOSITPRODUCT";
        this.entityId = productId;
        this.href = "/recurringdepositproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder deleteRecurringDepositProduct(final Long productId) {
        this.actionName = "DELETE";
        this.entityName = "RECURRINGDEPOSITPRODUCT";
        this.entityId = productId;
        this.href = "/recurringdepositproducts/" + productId;
        return this;
    }

    public CommandWrapperBuilder createInterestRateChart() {
        this.actionName = "CREATE";
        this.entityName = "INTERESTRATECHART";
        this.entityId = null;
        this.href = "/interestratechart/template";
        return this;
    }

    public CommandWrapperBuilder updateInterestRateChart(final Long interestRateChartId) {
        this.actionName = "UPDATE";
        this.entityName = "INTERESTRATECHART";
        this.entityId = interestRateChartId;
        this.href = "/interestratechart/" + interestRateChartId;
        return this;
    }

    public CommandWrapperBuilder deleteInterestRateChart(final Long interestRateChartId) {
        this.actionName = "DELETE";
        this.entityName = "INTERESTRATECHART";
        this.entityId = interestRateChartId;
        this.href = "/interestratechart/" + interestRateChartId;
        return this;
    }

    public CommandWrapperBuilder createInterestRateChartSlab(final Long chartId) {
        this.actionName = "CREATE";
        this.entityName = "CHARTSLAB";
        this.entityId = null;
        this.subentityId = chartId; // refer to chart id
        this.href = "/interestratechart/" + chartId + "/chartdetails/template";
        return this;
    }

    public CommandWrapperBuilder updateInterestRateChartSlab(final Long chartId, final Long chartSlabId) {
        this.actionName = "UPDATE";
        this.entityName = "CHARTSLAB";
        this.entityId = chartSlabId;
        this.subentityId = chartId;// refers parent chart
        this.href = "/interestratechart/" + chartId + "/chartdetails/" + chartSlabId;
        return this;
    }

    public CommandWrapperBuilder deleteInterestRateChartSlab(final Long chartId, final Long chartSlabId) {
        this.actionName = "DELETE";
        this.entityName = "CHARTSLAB";
        this.entityId = chartSlabId;
        this.subentityId = chartId;// refers parent chart
        this.href = "/interestratechart/" + chartId + "/chartdetails/" + chartSlabId;
        return this;
    }

    public CommandWrapperBuilder createCalendar(final CommandWrapper resourceDetails, final String supportedEntityType,
            final Long supportedEntityId) {
        this.actionName = "CREATE";
        this.entityName = "CALENDAR";
        this.clientId = resourceDetails.getClientId();
        this.loanId = resourceDetails.getLoanId();
        this.groupId = resourceDetails.getGroupId();
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/calendars/template";
        return this;
    }

    public CommandWrapperBuilder updateCalendar(final String supportedEntityType, final Long supportedEntityId, final Long calendarId) {
        this.actionName = "UPDATE";
        this.entityName = "CALENDAR";
        this.entityId = calendarId;
        this.groupId = supportedEntityId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/calendars/" + calendarId;
        return this;
    }

    public CommandWrapperBuilder deleteCalendar(final String supportedEntityType, final Long supportedEntityId, final Long calendarId) {
        this.actionName = "DELETE";
        this.entityName = "CALENDAR";
        this.entityId = calendarId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/calendars/" + calendarId;
        return this;
    }

    public CommandWrapperBuilder createNote(final CommandWrapper resourceDetails, final String resourceType, final Long resourceId) {
        this.actionName = "CREATE";
        this.entityName = resourceDetails.entityName();// Note supports multiple
                                                       // resources. Note
                                                       // Permissions are set
                                                       // for each resource.
        this.clientId = resourceDetails.getClientId();
        this.loanId = resourceDetails.getLoanId();
        this.savingsId = resourceDetails.getSavingsId();
        this.groupId = resourceDetails.getGroupId();
        this.subentityId = resourceDetails.subresourceId();
        this.href = "/" + resourceType + "/" + resourceId + "/notes/template";
        return this;
    }

    public CommandWrapperBuilder updateNote(final CommandWrapper resourceDetails, final String resourceType, final Long resourceId,
            final Long noteId) {
        this.actionName = "UPDATE";
        this.entityName = resourceDetails.entityName();// Note supports multiple
        // resources. Note
        // Permissions are set
        // for each resource.
        this.entityId = noteId;
        this.clientId = resourceDetails.getClientId();
        this.loanId = resourceDetails.getLoanId();
        this.savingsId = resourceDetails.getSavingsId();
        this.groupId = resourceDetails.getGroupId();
        this.subentityId = resourceDetails.subresourceId();
        this.href = "/" + resourceType + "/" + resourceId + "/notes";
        return this;
    }

    public CommandWrapperBuilder deleteNote(final CommandWrapper resourceDetails, final String resourceType, final Long resourceId,
            final Long noteId) {
        this.actionName = "DELETE";
        this.entityName = resourceDetails.entityName();// Note supports multiple
        // resources. Note
        // Permissions are set
        // for each resource.
        this.entityId = noteId;
        this.clientId = resourceDetails.getClientId();
        this.loanId = resourceDetails.getLoanId();
        this.savingsId = resourceDetails.getSavingsId();
        this.groupId = resourceDetails.getGroupId();
        this.subentityId = resourceDetails.subresourceId();
        this.href = "/" + resourceType + "/" + resourceId + "/calendars/" + noteId;
        return this;
    }

    public CommandWrapperBuilder createGroup() {
        this.actionName = "CREATE";
        this.entityName = "GROUP";
        this.href = "/groups/template";
        return this;
    }

    public CommandWrapperBuilder updateGroup(final Long groupId) {
        this.actionName = "UPDATE";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId;
        return this;
    }

    public CommandWrapperBuilder activateGroup(final Long groupId) {
        this.actionName = "ACTIVATE";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder saveGroupCollectionSheet(final Long groupId) {
        this.actionName = "SAVECOLLECTIONSHEET";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId + "?command=saveCollectionSheet";
        return this;
    }

    public CommandWrapperBuilder saveIndividualCollectionSheet() {
        this.actionName = "SAVE";
        this.entityName = "COLLECTIONSHEET";
        this.href = "/collectionsheet?command=saveCollectionSheet";
        return this;
    }

    public CommandWrapperBuilder deleteGroup(final Long groupId) {
        this.actionName = "DELETE";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId;
        return this;
    }

    public CommandWrapperBuilder associateClientsToGroup(final Long groupId) {
        this.actionName = "ASSOCIATECLIENTS";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId + "?command=associateClients";
        return this;
    }

    public CommandWrapperBuilder disassociateClientsFromGroup(final Long groupId) {
        this.actionName = "DISASSOCIATECLIENTS";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId + "?command=disassociateClients";
        return this;
    }

    public CommandWrapperBuilder transferClientsBetweenGroups(final Long sourceGroupId) {
        this.actionName = "TRANSFERCLIENTS";
        this.entityName = "GROUP";
        this.entityId = sourceGroupId;
        this.groupId = sourceGroupId;
        this.href = "/groups/" + sourceGroupId + "?command=transferClients";
        return this;
    }

    public CommandWrapperBuilder unassignGroupStaff(final Long groupId) {
        this.actionName = "UNASSIGNSTAFF";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId;
        return this;
    }

    public CommandWrapperBuilder assignGroupStaff(final Long groupId) {
        this.actionName = "ASSIGNSTAFF";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId + "?command=assignStaff";
        return this;
    }

    public CommandWrapperBuilder closeGroup(final Long groupId) {
        this.actionName = "CLOSE";
        this.entityName = "GROUP";
        this.entityId = groupId;
        this.groupId = groupId;
        this.href = "/groups/" + groupId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder createCollateral(final Long loanId) {
        this.actionName = "CREATE";
        this.entityName = "COLLATERAL";
        this.entityId = null;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/collaterals/template";
        return this;
    }

    public CommandWrapperBuilder updateCollateral(final Long loanId, final Long collateralId) {
        this.actionName = "UPDATE";
        this.entityName = "COLLATERAL";
        this.entityId = collateralId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/collaterals/" + collateralId;
        return this;
    }

    public CommandWrapperBuilder deleteCollateral(final Long loanId, final Long collateralId) {
        this.actionName = "DELETE";
        this.entityName = "COLLATERAL";
        this.entityId = collateralId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/collaterals/" + collateralId;
        return this;
    }

    public CommandWrapperBuilder updateCollectionSheet(final Long groupId) {
        this.actionName = "UPDATE";
        this.entityName = "COLLECTIONSHEET";
        this.entityId = groupId;
        this.href = "/groups/" + groupId + "/collectionsheet";
        return this;
    }

    public CommandWrapperBuilder createCgt() {
        this.actionName = "CREATE";
        this.entityName = "CGT";
        this.entityId = null;
        this.href = "/cgt";
        return this;
    }

    public CommandWrapperBuilder rejectCgt(final Long cgtId) {
        this.actionName = "REJECT";
        this.entityName = "CGT";
        this.entityId = cgtId;
        this.href = "/cgt/" + cgtId + "?action=reject";
        return this;
    }

    public CommandWrapperBuilder completeCgt(final Long cgtId) {
        this.actionName = "COMPLETE";
        this.entityName = "CGT";
        this.entityId = cgtId;
        this.href = "/cgt/" + cgtId + "?action=complete";
        return this;
    }

    public CommandWrapperBuilder updateCgt(final Long cgtId) {
        this.actionName = "UPDATE";
        this.entityName = "CGT";
        this.entityId = cgtId;
        this.href = "/cgt/" + cgtId;
        return this;
    }

    public CommandWrapperBuilder createCgtDay(final Long cgtId) {
        this.actionName = "CREATE";
        this.entityName = "CGTDAY";
        this.entityId = null;
        this.subentityId = cgtId;
        this.href = "/cgt/" + cgtId + "/cgtSub";
        return this;
    }

    public CommandWrapperBuilder updateCgtDay(final Long cgtId, final Long cgtDayId) {
        this.actionName = "UPDATE";
        this.entityName = "CGTDAY";
        this.entityId = cgtDayId;
        this.subentityId = cgtId;
        this.href = "/cgt/" + cgtId + "/cgtSub" + cgtDayId;
        return this;
    }

    public CommandWrapperBuilder completeCgtDay(final Long cgtId, final Long cgtDayId) {
        this.actionName = "COMPLETE";
        this.entityName = "CGTDAY";
        this.entityId = cgtDayId;
        this.subentityId = cgtId;
        this.href = "/cgt/" + cgtId + "/cgtSub" + cgtDayId + "?action=complete";
        return this;
    }

    public CommandWrapperBuilder createCenter() {
        this.actionName = "CREATE";
        this.entityName = "CENTER";
        this.href = "/centers/template";
        return this;
    }

    public CommandWrapperBuilder updateCenter(final Long centerId) {
        this.actionName = "UPDATE";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.href = "/centers/" + centerId;
        return this;
    }

    public CommandWrapperBuilder deleteCenter(final Long centerId) {
        this.actionName = "DELETE";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.href = "/centers/" + centerId;
        return this;
    }

    public CommandWrapperBuilder activateCenter(final Long centerId) {
        this.actionName = "ACTIVATE";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.groupId = centerId;
        this.href = "/centers/" + centerId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder saveCenterCollectionSheet(final Long centerId) {
        this.actionName = "SAVECOLLECTIONSHEET";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.groupId = centerId;
        this.href = "/centers/" + centerId + "?command=saveCollectionSheet";
        return this;
    }

    public CommandWrapperBuilder closeCenter(final Long centerId) {
        this.actionName = "CLOSE";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.groupId = centerId;
        this.href = "/centers/" + centerId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder associateGroupsToCenter(final Long centerId) {
        this.actionName = "ASSOCIATEGROUPS";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.groupId = centerId;
        this.href = "/groups/" + centerId + "?command=associateGroups";
        return this;
    }

    public CommandWrapperBuilder disassociateGroupsFromCenter(final Long centerId) {
        this.actionName = "DISASSOCIATEGROUPS";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.groupId = centerId;
        this.href = "/groups/" + centerId + "?command=disassociateGroups";
        return this;
    }

    public CommandWrapperBuilder createAccountingRule() {
        this.actionName = "CREATE";
        this.entityName = "ACCOUNTINGRULE";
        this.entityId = null;
        this.href = "/accountingrules/template";
        return this;
    }

    public CommandWrapperBuilder updateAccountingRule(final Long accountingRuleId) {
        this.actionName = "UPDATE";
        this.entityName = "ACCOUNTINGRULE";
        this.entityId = accountingRuleId;
        this.href = "/accountingrules/" + accountingRuleId;
        return this;
    }

    public CommandWrapperBuilder deleteAccountingRule(final Long accountingRuleId) {
        this.actionName = "DELETE";
        this.entityName = "ACCOUNTINGRULE";
        this.entityId = accountingRuleId;
        this.href = "/accountingrules/" + accountingRuleId;
        return this;
    }

    public CommandWrapperBuilder updateTaxonomyMapping(final Long mappingId) {
        this.actionName = "UPDATE";
        this.entityName = "XBRLMAPPING";
        this.entityId = mappingId;
        this.href = "/xbrlmapping";
        return this;
    }

    public CommandWrapperBuilder createHoliday() {
        this.actionName = "CREATE";
        this.entityName = "HOLIDAY";
        this.entityId = null;
        this.href = "/holidays/template";
        return this;
    }

    public CommandWrapperBuilder activateHoliday(final Long holidayId) {
        this.actionName = "ACTIVATE";
        this.entityName = "HOLIDAY";
        this.entityId = holidayId;
        this.href = "/holidays/" + holidayId + "command=activate";
        return this;
    }

    public CommandWrapperBuilder updateHoliday(final Long holidayId) {
        this.actionName = "UPDATE";
        this.entityName = "HOLIDAY";
        this.entityId = holidayId;
        this.href = "/holidays/" + holidayId;
        return this;
    }

    public CommandWrapperBuilder deleteHoliday(final Long holidayId) {
        this.actionName = "DELETE";
        this.entityName = "HOLIDAY";
        this.entityId = holidayId;
        this.href = "/holidays/" + holidayId + "command=delete";
        return this;
    }

    public CommandWrapperBuilder assignRole(final Long groupId) {
        this.actionName = "ASSIGNROLE";
        this.entityName = "GROUP";
        this.groupId = groupId;
        this.entityId = null;
        this.href = "/groups/" + groupId + "?command=assignRole";
        return this;
    }

    public CommandWrapperBuilder unassignRole(final Long groupId, final Long roleId) {
        this.actionName = "UNASSIGNROLE";
        this.entityName = "GROUP";
        this.groupId = groupId;
        this.entityId = roleId;
        this.href = "/groups/" + groupId + "?command=unassignRole";
        return this;
    }

    public CommandWrapperBuilder updateRole(final Long groupId, final Long roleId) {
        this.actionName = "UPDATEROLE";
        this.entityName = "GROUP";
        this.groupId = groupId;
        this.entityId = roleId;
        this.href = "/groups/" + groupId + "?command=updateRole";
        return this;
    }

    public CommandWrapperBuilder unassignClientStaff(final Long clientId) {
        this.actionName = "UNASSIGNSTAFF";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=unassignStaff";
        return this;
    }

    public CommandWrapperBuilder createTemplate() {
        this.actionName = "CREATE";
        this.entityName = "TEMPLATE";
        this.entityId = null;
        this.href = "/templates";
        return this;
    }

    public CommandWrapperBuilder updateTemplate(final Long templateId) {
        this.actionName = "UPDATE";
        this.entityName = "TEMPLATE";
        this.entityId = templateId;
        this.href = "/templates/" + templateId;
        return this;
    }

    public CommandWrapperBuilder deleteTemplate(final Long templateId) {
        this.actionName = "DELETE";
        this.entityName = "TEMPLATE";
        this.entityId = templateId;
        this.href = "/templates/" + templateId;
        return this;
    }

    public CommandWrapperBuilder assignClientStaff(final Long clientId) {
        this.actionName = "ASSIGNSTAFF";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=assignStaff";
        return this;
    }

    public CommandWrapperBuilder updateClientSavingsAccount(final Long clientId) {
        this.actionName = "UPDATESAVINGSACCOUNT";
        this.entityName = "CLIENT";
        this.entityId = clientId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "?command=updateSavingsAccount";
        return this;
    }

    public CommandWrapperBuilder createProductMix(final Long productId) {
        this.actionName = "CREATE";
        this.entityName = "PRODUCTMIX";
        this.entityId = null;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/productmix";
        return this;
    }

    public CommandWrapperBuilder updateProductMix(final Long productId) {
        this.actionName = "UPDATE";
        this.entityName = "PRODUCTMIX";
        this.entityId = null;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/productmix";
        return this;
    }

    public CommandWrapperBuilder deleteProductMix(final Long productId) {
        this.actionName = "DELETE";
        this.entityName = "PRODUCTMIX";
        this.entityId = null;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/productmix";
        return this;
    }

    public CommandWrapperBuilder withProduct(final Long productId) {
        this.productId = productId;
        return this;
    }

    public CommandWrapperBuilder updateJobDetail(final Long jobId) {
        this.actionName = "UPDATE";
        this.entityName = "SCHEDULER";
        this.entityId = jobId;
        this.href = "/updateJobDetail/" + jobId + "/updateJobDetail";
        return this;
    }

    public CommandWrapperBuilder createMeeting(final CommandWrapper resourceDetails, final String supportedEntityType,
            final Long supportedEntityId) {
        this.actionName = "CREATE";
        this.entityName = "MEETING";
        this.clientId = resourceDetails.getClientId();
        this.loanId = resourceDetails.getLoanId();
        this.groupId = resourceDetails.getGroupId();
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/meetings";
        return this;
    }

    public CommandWrapperBuilder updateMeeting(final String supportedEntityType, final Long supportedEntityId, final Long meetingId) {
        this.actionName = "UPDATE";
        this.entityName = "MEETING";
        this.entityId = meetingId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/meetings/" + meetingId;
        return this;
    }

    public CommandWrapperBuilder deleteMeeting(final String supportedEntityType, final Long supportedEntityId, final Long meetingId) {
        this.actionName = "DELETE";
        this.entityName = "MEETING";
        this.entityId = meetingId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/meetings/" + meetingId;
        return this;
    }

    public CommandWrapperBuilder saveOrUpdateAttendance(final Long entityId, final String supportedEntityType,
            final Long supportedEntityId) {
        this.actionName = "SAVEORUPDATEATTENDANCE";
        this.entityName = "MEETING";
        this.entityId = entityId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/meetings/" + entityId + "?command=saveOrUpdateAttendance";
        return this;
    }

    public CommandWrapperBuilder updateCache() {
        this.actionName = "UPDATE";
        this.entityName = "CACHE";
        this.href = "/cache";
        return this;
    }

    /**
     * Deposit account mappings
     */

    public CommandWrapperBuilder createFixedDepositAccount() {
        this.actionName = "CREATE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = null;
        this.href = "/fixeddepositaccounts/template";
        return this;
    }

    public CommandWrapperBuilder updateFixedDepositAccount(final Long accountId) {
        this.actionName = "UPDATE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId;
        return this;
    }

    public CommandWrapperBuilder deleteFixedDepositAccount(final Long accountId) {
        this.actionName = "DELETE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId;
        return this;
    }

    public CommandWrapperBuilder rejectFixedDepositAccountApplication(final Long accountId) {
        this.actionName = "REJECT";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=reject";
        return this;
    }

    public CommandWrapperBuilder withdrawFixedDepositAccountApplication(final Long accountId) {
        this.actionName = "WITHDRAW";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=withdrawnByApplicant";
        return this;
    }

    public CommandWrapperBuilder approveFixedDepositAccountApplication(final Long accountId) {
        this.actionName = "APPROVE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=approve";
        return this;
    }

    public CommandWrapperBuilder undoFixedDepositAccountApplication(final Long accountId) {
        this.actionName = "APPROVALUNDO";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=undoapproval";
        return this;
    }

    public CommandWrapperBuilder fixedDepositAccountActivation(final Long accountId) {
        this.actionName = "ACTIVATE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder closeFixedDepositAccount(final Long accountId) {
        this.actionName = "CLOSE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder prematureCloseFixedDepositAccount(final Long accountId) {
        this.actionName = "PREMATURECLOSE";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=prematureClose";
        return this;
    }

    public CommandWrapperBuilder fixedDepositAccountInterestCalculation(final Long accountId) {
        this.actionName = "CALCULATEINTEREST";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=calculateInterest";
        return this;
    }

    public CommandWrapperBuilder fixedDepositAccountInterestPosting(final Long accountId) {
        this.actionName = "POSTINTEREST";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "?command=postInterest";
        return this;
    }

    public CommandWrapperBuilder fixedDepositAccountDeposit(final Long accountId) {
        this.actionName = "DEPOSIT";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "/transactions?command=deposit";
        return this;
    }

    public CommandWrapperBuilder fixedDepositAccountWithdrawal(final Long accountId) {
        this.actionName = "WITHDRAWAL";
        this.entityName = "FIXEDDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/fixeddepositaccounts/" + accountId + "/transactions?command=withdrawal";
        return this;
    }

    public CommandWrapperBuilder createRecurringDepositAccount() {
        this.actionName = "CREATE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = null;
        this.href = "/recurringdepositaccounts/template";
        return this;
    }

    public CommandWrapperBuilder updateRecurringDepositAccount(final Long accountId) {
        this.actionName = "UPDATE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId;
        return this;
    }

    public CommandWrapperBuilder recurringAccountDeposit(final Long accountId) {
        this.actionName = "DEPOSIT";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "/transactions?command=deposit";
        return this;
    }

    public CommandWrapperBuilder recurringAccountWithdrawal(final Long accountId) {
        this.actionName = "WITHDRAWAL";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "/transactions?command=withdrawal";
        return this;
    }

    public CommandWrapperBuilder adjustRecurringAccountTransaction(final Long accountId, final Long transactionId) {
        this.actionName = "ADJUSTTRANSACTION";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.subentityId = transactionId;
        this.transactionId = transactionId.toString();
        this.href = "/recurringdepositaccounts/" + accountId + "/transactions/" + transactionId + "?command=modify";
        return this;
    }

    public CommandWrapperBuilder undoRecurringAccountTransaction(final Long accountId, final Long transactionId) {
        this.actionName = "UNDOTRANSACTION";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.subentityId = transactionId;
        this.transactionId = transactionId.toString();
        this.href = "/recurringdepositaccounts/" + accountId + "/transactions/" + transactionId + "?command=undo";
        return this;
    }

    public CommandWrapperBuilder deleteRecurringDepositAccount(final Long accountId) {
        this.actionName = "DELETE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId;
        return this;
    }

    public CommandWrapperBuilder rejectRecurringDepositAccountApplication(final Long accountId) {
        this.actionName = "REJECT";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=reject";
        return this;
    }

    public CommandWrapperBuilder withdrawRecurringDepositAccountApplication(final Long accountId) {
        this.actionName = "WITHDRAW";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=withdrawnByApplicant";
        return this;
    }

    public CommandWrapperBuilder approveRecurringDepositAccountApplication(final Long accountId) {
        this.actionName = "APPROVE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=approve";
        return this;
    }

    public CommandWrapperBuilder undoRecurringDepositAccountApplication(final Long accountId) {
        this.actionName = "APPROVALUNDO";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=undoapproval";
        return this;
    }

    public CommandWrapperBuilder recurringDepositAccountActivation(final Long accountId) {
        this.actionName = "ACTIVATE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder closeRecurringDepositAccount(final Long accountId) {
        this.actionName = "CLOSE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder updateDepositAmountForRecurringDepositAccount(final Long accountId) {
        this.actionName = DepositsApiConstants.UPDATE_DEPOSIT_AMOUNT.toUpperCase();
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=" + DepositsApiConstants.UPDATE_DEPOSIT_AMOUNT;
        return this;
    }

    public CommandWrapperBuilder prematureCloseRecurringDepositAccount(final Long accountId) {
        this.actionName = "PREMATURECLOSE";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.entityId = accountId;
        this.savingsId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=prematureClose";
        return this;
    }

    public CommandWrapperBuilder recurringDepositAccountInterestCalculation(final Long accountId) {
        this.actionName = "CALCULATEINTEREST";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=calculateInterest";
        return this;
    }

    public CommandWrapperBuilder recurringDepositAccountInterestPosting(final Long accountId) {
        this.actionName = "POSTINTEREST";
        this.entityName = "RECURRINGDEPOSITACCOUNT";
        this.savingsId = accountId;
        this.entityId = accountId;
        this.href = "/recurringdepositaccounts/" + accountId + "?command=postInterest";
        return this;
    }

    public CommandWrapperBuilder createOfficeToGLAccountMapping() {
        this.actionName = "CREATE";
        this.entityName = "FINANCIALACTIVITYACCOUNT";
        this.entityId = null;
        this.href = "/organizationglaccounts/template";
        return this;
    }

    public CommandWrapperBuilder updateOfficeToGLAccountMapping(final Long mappingId) {
        this.actionName = "UPDATE";
        this.entityName = "FINANCIALACTIVITYACCOUNT";
        this.entityId = mappingId;
        this.href = "/organizationglaccounts/" + mappingId;
        return this;
    }

    public CommandWrapperBuilder deleteOfficeToGLAccountMapping(final Long mappingId) {
        this.actionName = "DELETE";
        this.entityName = "FINANCIALACTIVITYACCOUNT";
        this.entityId = mappingId;
        this.href = "/organizationglaccounts/" + mappingId;
        return this;
    }

    public CommandWrapperBuilder registerDBDatatable(final String datatable, final String apptable) {
        this.actionName = "REGISTER";
        this.entityName = "DATATABLE";
        this.entityId = null;
        this.href = "/datatables/register/" + datatable + "/" + apptable;
        return this;
    }

    public CommandWrapperBuilder registerSurvey(final String datatable, final String apptable) {
        this.actionName = "REGISTER";
        this.entityName = "SURVEY";
        this.entityId = null;
        this.href = "/survey/register/" + datatable + "/" + apptable;
        return this;
    }

    public CommandWrapperBuilder fullFilSurvey(final String datatable, final Long apptableId) {
        this.entityName = datatable;
        this.entityId = apptableId;
        this.actionName = "CREATE";
        this.href = "/survey/" + datatable + "/" + apptableId;
        return this;
    }

    public CommandWrapperBuilder updateLikelihood(final Long entityId) {
        this.actionName = "UPDATE";
        this.entityName = "LIKELIHOOD";
        this.href = "/likelihood/" + entityId;
        this.entityId = entityId;
        return this;
    }

    public CommandWrapperBuilder assignSavingsOfficer(final Long accountId) {
        this.actionName = "UPDATESAVINGSOFFICER";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?command=assignSavingsOfficer";
        return this;
    }

    public CommandWrapperBuilder unassignSavingsOfficer(final Long accountId) {
        this.actionName = "REMOVESAVINGSOFFICER";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?commad=unassignSavingsOfficer";
        return this;
    }

    public CommandWrapperBuilder savingsAccountInterestPostingAsOnDate(final Long accountId) {
        this.actionName = "POSTINTERESTASON";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=postInterestAsOn";
        return this;
    }

    public CommandWrapperBuilder createLoanRescheduleRequest(final String entityName) {
        this.actionName = "CREATE";
        this.entityName = entityName;
        this.entityId = null;
        this.href = "/rescheduleloans";
        return this;
    }

    public CommandWrapperBuilder approveLoanRescheduleRequest(final String entityName, final Long requestId) {
        this.actionName = "APPROVE";
        this.entityName = entityName;
        this.entityId = requestId;
        this.href = "/rescheduleloans/" + requestId + "?command=approve";
        return this;
    }

    public CommandWrapperBuilder rejectLoanRescheduleRequest(final String entityName, final Long requestId) {
        this.actionName = "REJECT";
        this.entityName = entityName;
        this.entityId = requestId;
        this.href = "/rescheduleloans/" + requestId + "?command=reject";
        return this;
    }

    public CommandWrapperBuilder createAccountNumberFormat() {
        this.actionName = "CREATE";
        this.entityName = AccountNumberFormatConstants.ENTITY_NAME.toUpperCase();
        this.href = AccountNumberFormatConstants.resourceRelativeURL;
        return this;
    }

    public CommandWrapperBuilder updateAccountNumberFormat(final Long accountNumberFormatId) {
        this.actionName = "UPDATE";
        this.entityName = AccountNumberFormatConstants.ENTITY_NAME.toUpperCase();
        this.entityId = accountNumberFormatId;
        this.href = AccountNumberFormatConstants.resourceRelativeURL + "/" + accountNumberFormatId;
        return this;
    }

    public CommandWrapperBuilder deleteAccountNumberFormat(final Long accountNumberFormatId) {
        this.actionName = "DELETE";
        this.entityName = AccountNumberFormatConstants.ENTITY_NAME.toUpperCase();
        this.entityId = accountNumberFormatId;
        this.href = "AccountNumberFormatConstants.resourceRelativeURL" + "/" + accountNumberFormatId;
        this.json = "{}";
        return this;
    }

    public CommandWrapperBuilder refundByTransfer() {
        this.actionName = "REFUNDBYTRANSFER";
        this.entityName = "ACCOUNTTRANSFER";
        this.entityId = null;
        this.href = "/refundByTransfer";
        return this;
    }

    public CommandWrapperBuilder refundOverPaidLoan(final Long loanId) {
        this.actionName = "REFUND";
        this.entityName = "LOAN";
        this.entityId = null;
        this.href = "/refund";
        this.loanId = loanId;
        this.entityId = loanId;
        return this;
    }

    public CommandWrapperBuilder createTeller() {
        this.actionName = "CREATE";
        this.entityName = "TELLER";
        this.entityId = null;
        this.href = "/tellers/templates";
        return this;
    }

    public CommandWrapperBuilder updateTeller(final Long tellerId) {
        this.actionName = "UPDATE";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.href = "/tellers/" + tellerId;
        return this;
    }

    public CommandWrapperBuilder deleteTeller(final Long tellerId) {
        this.actionName = "DELETE";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.href = "/tellers/" + tellerId;
        return this;
    }

    public CommandWrapperBuilder allocateTeller(final long tellerId) {
        this.actionName = "ALLOCATECASHIER";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.href = "/tellers/" + tellerId + "/cashiers/templates";
        return this;
    }

    public CommandWrapperBuilder updateAllocationTeller(final Long tellerId, final Long cashierId) {
        this.actionName = "UPDATECASHIERALLOCATION";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.subentityId = cashierId;
        this.href = "/tellers/" + tellerId + "/cashiers/" + cashierId;
        return this;
    }

    public CommandWrapperBuilder deleteAllocationTeller(final Long tellerId, final Long cashierId) {
        this.actionName = "DELETECASHIERALLOCATION";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.subentityId = cashierId;
        this.href = "/tellers/" + tellerId + "/cashiers/" + cashierId;
        return this;
    }

    public CommandWrapperBuilder allocateCashToCashier(final Long tellerId, final Long cashierId) {
        this.actionName = "ALLOCATECASHTOCASHIER";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.subentityId = cashierId;
        this.href = "/tellers/" + tellerId + "/cashiers/" + cashierId + "/allocate";
        return this;
    }

    public CommandWrapperBuilder settleCashFromCashier(final Long tellerId, final Long cashierId) {
        this.actionName = "SETTLECASHFROMCASHIER";
        this.entityName = "TELLER";
        this.entityId = tellerId;
        this.subentityId = cashierId;
        this.href = "/tellers/" + tellerId + "/cashiers/" + cashierId + "/settle";
        return this;
    }

    public CommandWrapperBuilder deleteRole(final Long roleId) {
        this.actionName = "DELETE";
        this.entityName = "ROLE";
        this.entityId = roleId;
        this.href = "/roles/" + roleId;
        this.json = "{}";
        return this;
    }

    public CommandWrapperBuilder disableRole(final Long roleId) {
        this.actionName = "DISABLE";
        this.entityName = "ROLE";
        this.entityId = roleId;
        this.href = "/roles/" + roleId + "/disbales";
        this.json = "{}";
        return this;
    }

    public CommandWrapperBuilder enableRole(final Long roleId) {
        this.actionName = "ENABLE";
        this.entityName = "ROLE";
        this.entityId = roleId;
        this.href = "/roles/" + roleId + "/enable";
        this.json = "{}";
        return this;
    }

    public CommandWrapperBuilder createMap(final Long relId) {
        this.actionName = "CREATE";
        this.entityName = "ENTITYMAPPING";
        this.entityId = relId;
        this.href = "/entitytoentitymapping/" + relId;
        return this;
    }

    public CommandWrapperBuilder updateMap(final Long mapId) {
        this.actionName = "UPDATE";
        this.entityName = "ENTITYMAPPING";
        this.entityId = mapId;
        this.href = "/entitytoentitymapping" + mapId;
        return this;
    }

    public CommandWrapperBuilder deleteMap(final Long mapId) {
        this.actionName = "DELETE";
        this.entityName = "ENTITYMAPPING";
        this.entityId = mapId;
        this.href = "/entitytoentitymapping/" + mapId;
        return this;
    }

    public CommandWrapperBuilder updateWorkingDays() {
        this.actionName = "UPDATE";
        this.entityName = "WORKINGDAYS";
        this.href = "/workingdays/";
        return this;
    }

    public CommandWrapperBuilder updatePasswordPreferences() {
        this.actionName = "UPDATE";
        this.entityName = PasswordPreferencesApiConstants.ENTITY_NAME;
        this.href = "/" + PasswordPreferencesApiConstants.RESOURCE_NAME;
        return this;
    }

    public CommandWrapperBuilder createPaymentType() {
        this.actionName = "CREATE";
        this.entityName = PaymentTypeApiResourceConstants.ENTITY_NAME;
        this.entityId = null;
        this.href = "/" + PaymentTypeApiResourceConstants.RESOURCE_NAME;
        return this;
    }

    public CommandWrapperBuilder updatePaymentType(final Long paymentTypeId) {
        this.actionName = "UPDATE";
        this.entityName = PaymentTypeApiResourceConstants.ENTITY_NAME;
        this.entityId = paymentTypeId;
        this.href = "/" + PaymentTypeApiResourceConstants.RESOURCE_NAME + paymentTypeId;
        return this;
    }

    public CommandWrapperBuilder deletePaymentType(final Long paymentTypeId) {
        this.actionName = "DELETE";
        this.entityName = "PAYMENTTYPE";
        this.entityId = paymentTypeId;
        this.href = "/" + PaymentTypeApiResourceConstants.RESOURCE_NAME + paymentTypeId;
        return this;
    }

    public CommandWrapperBuilder sendNotification() {
        this.actionName = "SEND";
        this.entityName = "NOTIFICATION";
        // this.entityId = transactionId;
        // this.clientId = clientId;
        this.href = "/notification";
        return this;
    }

    public CommandWrapperBuilder updateExternalServiceProperties(final String externalServiceName) {
        this.actionName = "UPDATE";
        this.entityName = "EXTERNALSERVICES";
        this.transactionId = externalServiceName;
        this.href = "/externalservices/" + externalServiceName;
        return this;
    }

    public CommandWrapperBuilder createClientCharge(final Long clientId) {
        this.actionName = ClientApiConstants.CLIENT_CHARGE_ACTION_CREATE;
        this.entityName = ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/charges";
        return this;
    }

    public CommandWrapperBuilder deleteClientCharge(final Long clientId, final Long chargeId) {
        this.actionName = ClientApiConstants.CLIENT_CHARGE_ACTION_DELETE;
        this.entityName = ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME;
        this.clientId = clientId;
        this.entityId = chargeId;
        this.href = "/clients/" + clientId + "/charges/" + chargeId;
        return this;
    }

    public CommandWrapperBuilder waiveClientCharge(final Long clientId, final Long chargeId) {
        this.actionName = ClientApiConstants.CLIENT_CHARGE_ACTION_WAIVE;
        this.entityName = ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME;
        this.entityId = chargeId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/charges/" + chargeId + "?command=waive";
        return this;
    }

    public CommandWrapperBuilder payClientCharge(final Long clientId, final Long chargeId) {
        this.actionName = ClientApiConstants.CLIENT_CHARGE_ACTION_PAY;
        this.entityName = ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME;
        this.entityId = chargeId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/charges/" + chargeId + "?command=paycharge";
        return this;
    }

    public CommandWrapperBuilder inactivateClientCharge(final Long clientId, final Long chargeId) {
        this.actionName = "INACTIVATE";
        this.entityName = ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME;
        this.entityId = chargeId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/charges/" + chargeId + "?command=inactivate";
        return this;
    }

    public CommandWrapperBuilder undoClientTransaction(final Long clientId, final Long transactionId) {
        this.actionName = ClientApiConstants.CLIENT_TRANSACTION_ACTION_UNDO;
        this.entityName = ClientApiConstants.CLIENT_RESOURCE_NAME;
        this.entityId = transactionId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/transactions/" + transactionId + "?command=undo";
        return this;
    }

    public CommandWrapperBuilder createProvisioningCategory() {
        this.actionName = "CREATE";
        this.entityName = "PROVISIONCATEGORY";
        this.entityId = null;
        this.href = "/provisioningcategory";
        return this;
    }

    public CommandWrapperBuilder updateProvisioningCategory(final Long cateoryId) {
        this.actionName = "UPDATE";
        this.entityName = "PROVISIONCATEGORY";
        this.entityId = cateoryId;
        this.href = "/provisioningcategory/" + cateoryId;
        return this;
    }

    public CommandWrapperBuilder deleteProvisioningCategory(final Long categoryId) {
        this.actionName = "DELETE";
        this.entityName = "PROVISIONCATEGORY";
        this.entityId = categoryId;
        this.href = "/provisioningcategory/" + categoryId;
        return this;
    }

    public CommandWrapperBuilder createProvisioningCriteria() {
        this.actionName = "CREATE";
        this.entityName = "PROVISIONCRITERIA";
        this.entityId = null;
        this.href = "/provisioningcriteria";
        return this;
    }

    public CommandWrapperBuilder updateProvisioningCriteria(final Long criteriaId) {
        this.actionName = "UPDATE";
        this.entityName = "PROVISIONCRITERIA";
        this.entityId = criteriaId;
        this.href = "/provisioningcriteria/" + criteriaId;
        return this;
    }

    public CommandWrapperBuilder deleteProvisioningCriteria(final Long criteriaId) {
        this.actionName = "DELETE";
        this.entityName = "PROVISIONCRITERIA";
        this.entityId = criteriaId;
        this.href = "/provisioningcriteria/" + criteriaId;
        return this;
    }

    public CommandWrapperBuilder createProvisioningEntries() {
        this.actionName = "CREATE";
        this.entityName = "PROVISIONENTRIES";
        this.entityId = null;
        this.href = "/provisioningentries";
        return this;
    }

    public CommandWrapperBuilder createProvisioningJournalEntries(final Long entryId) {
        this.actionName = "CREATE";
        this.entityName = "PROVISIONJOURNALENTRIES";
        this.entityId = entryId;
        this.href = "/provisioningentries/" + entryId;
        return this;
    }

    public CommandWrapperBuilder reCreateProvisioningEntries(final Long entryId) {
        this.actionName = "RECREATE";
        this.entityName = "PROVISIONENTRIES";
        this.entityId = entryId;
        this.href = "/provisioningentries/" + entryId;
        return this;
    }

    public CommandWrapperBuilder createFloatingRate() {
        this.actionName = "CREATE";
        this.entityName = "FLOATINGRATE";
        this.entityId = null;
        this.href = "/floatingrates";
        return this;
    }

    public CommandWrapperBuilder updateFloatingRate(final Long floatingRateId) {
        this.actionName = "UPDATE";
        this.entityName = "FLOATINGRATE";
        this.entityId = floatingRateId;
        this.href = "/floatingrates/" + floatingRateId;
        return this;
    }

    public CommandWrapperBuilder createScheduleExceptions(final Long loanId) {
        this.actionName = "CREATESCHEDULEEXCEPTIONS";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/schedule";
        return this;
    }

    public CommandWrapperBuilder deleteScheduleExceptions(final Long loanId) {
        this.actionName = "DELETESCHEDULEEXCEPTIONS";
        this.entityName = "LOAN";
        this.entityId = loanId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/schedule";
        return this;
    }

    public CommandWrapperBuilder createProduct(final String productType) {
        this.entityName = productType.toUpperCase() + "PRODUCT"; // To Support
        // different
        // type of
        // products
        this.actionName = "CREATE";
        this.entityId = null;
        this.href = "/products/" + productType;
        return this;
    }

    public CommandWrapperBuilder updateProduct(final String productType, final Long productId) {
        this.entityName = productType.toUpperCase() + "PRODUCT";
        this.actionName = "UPDATE";
        this.entityId = productId;
        this.href = "/products/" + productType + "/" + productId;
        return this;
    }

    public CommandWrapperBuilder createAccount(final String accountType) {
        this.entityName = accountType.toUpperCase() + "ACCOUNT"; // To Support
        // different
        // type of
        // Accounts
        this.actionName = "CREATE";
        this.entityId = null;
        this.href = "/accounts/" + accountType;
        return this;
    }

    public CommandWrapperBuilder updateAccount(final String accountType, final Long accountId) {
        this.entityName = accountType.toUpperCase() + "ACCOUNT";
        this.actionName = "UPDATE";
        this.entityId = accountId;
        this.href = "/accounts/" + accountType + "/" + accountId;
        return this;
    }

    public CommandWrapperBuilder createProductCommand(final String productType, final String command, final Long productId) {
        this.entityName = productType.toUpperCase() + "PRODUCT";
        this.actionName = "CREATE" + "_" + command.toUpperCase();
        this.entityId = productId;
        this.href = "/products/" + productType + "/" + productId + "?command=" + command;
        return this;
    }

    public CommandWrapperBuilder createShareProductDividendPayoutCommand(final Long productId) {
        this.entityName = "SHAREPRODUCT";
        this.actionName = "CREATE_DIVIDEND";
        this.entityId = productId;
        this.href = "/shareproduct/" + productId + "/dividend";
        return this;
    }

    public CommandWrapperBuilder approveShareProductDividendPayoutCommand(final Long productId, final Long dividendId) {
        this.entityName = "SHAREPRODUCT";
        this.actionName = "APPROVE_DIVIDEND";
        this.entityId = dividendId;
        this.href = "/shareproduct/" + productId + "/dividend/" + dividendId;
        return this;
    }

    public CommandWrapperBuilder deleteShareProductDividendPayoutCommand(final Long productId, final Long dividendId) {
        this.entityName = "SHAREPRODUCT";
        this.actionName = "DELETE_DIVIDEND";
        this.entityId = dividendId;
        this.href = "/shareproduct/" + productId + "/dividend/" + dividendId;
        return this;
    }

    public CommandWrapperBuilder createAccountCommand(final String accountType, final Long accountId, final String command) {
        this.entityName = accountType.toUpperCase() + "ACCOUNT";
        this.actionName = command.toUpperCase();
        this.entityId = accountId;
        this.href = "/accounts/" + accountType + "/" + accountId + "?command=" + command;
        return this;
    }

    public CommandWrapperBuilder createTaxComponent() {
        this.actionName = "CREATE";
        this.entityName = "TAXCOMPONENT";
        this.entityId = null;
        this.href = "/taxes/component";
        return this;
    }

    public CommandWrapperBuilder updateTaxComponent(final Long taxComponentId) {
        this.actionName = "UPDATE";
        this.entityName = "TAXCOMPONENT";
        this.entityId = taxComponentId;
        this.href = "/taxes/component/" + taxComponentId;
        return this;
    }

    public CommandWrapperBuilder createTaxGroup() {
        this.actionName = "CREATE";
        this.entityName = "TAXGROUP";
        this.entityId = null;
        this.href = "/taxes/group";
        return this;
    }

    public CommandWrapperBuilder updateTaxGroup(final Long taxGroupId) {
        this.actionName = "UPDATE";
        this.entityName = "TAXGROUP";
        this.entityId = taxGroupId;
        this.href = "/taxes/group/" + taxGroupId;
        return this;
    }

    public CommandWrapperBuilder updateWithHoldTax(final Long accountId) {
        this.actionName = "UPDATEWITHHOLDTAX";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = accountId;
        this.href = "/savingsaccounts/" + accountId + "?commad=updateTaxWithHoldTax";
        return this;
    }

    public CommandWrapperBuilder createCollaterals() {
        this.actionName = "CREATE";
        this.entityName = "COLLATERALS";
        this.entityId = null;
        this.href = "/collaterals";
        return this;
    }

    public CommandWrapperBuilder updateCollaterals(final Long collateralId) {
        this.actionName = "UPDATE";
        this.entityName = "COLLATERALS";
        this.entityId = collateralId;
        this.href = "/collaterals/" + collateralId;
        return this;
    }

    public CommandWrapperBuilder createQualityStandards(final Long collateralId) {
        this.actionName = "CREATE";
        this.entityName = "COLLATERALQUALITYSTANDARDS";
        this.entityId = collateralId;
        this.subentityId = null;
        this.href = "/collaterals/" + collateralId + "/qualityStandards";
        return this;
    }

    public CommandWrapperBuilder updateQualityStandards(final Long collateralId, final Long qualityStandardsId) {
        this.actionName = "UPDATE";
        this.entityName = "COLLATERALQUALITYSTANDARDS";
        this.entityId = collateralId;
        this.subentityId = qualityStandardsId;
        this.href = "/collaterals/" + collateralId + "/qualityStandards" + qualityStandardsId;
        return this;
    }

    public CommandWrapperBuilder createPledge() {
        this.actionName = "CREATE";
        this.entityName = "COLLATERALPLEDGE";
        this.entityId = null;
        this.href = "/pledge/";
        return this;
    }

    public CommandWrapperBuilder updatePledges(final Long pledgeId) {
        this.actionName = "UPDATE";
        this.entityName = "COLLATERALPLEDGE";
        this.entityId = pledgeId;
        this.href = "/pledge/" + pledgeId;
        return this;
    }

    public CommandWrapperBuilder closePledge(final Long pledgeId) {
        this.actionName = "CLOSE";
        this.entityName = "COLLATERALPLEDGE";
        this.entityId = pledgeId;
        this.href = "/pledge/" + pledgeId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder deletePledge(final Long pledgeId) {
        this.actionName = "DELETE";
        this.entityName = "COLLATERALPLEDGE";
        this.entityId = pledgeId;
        this.href = "/pledge/" + pledgeId;
        return this;
    }

    public CommandWrapperBuilder activePledge(final Long pledgeId) {
        this.actionName = "ACTIVE";
        this.entityName = "COLLATERALPLEDGE";
        this.entityId = pledgeId;
        this.href = "/pledge/" + pledgeId + "?command=active";
        return this;
    }

    public CommandWrapperBuilder deleteQualityStandards(final Long collateralId, final Long qualityStandardsId) {
        this.actionName = "DELETE";
        this.entityName = "COLLATERALQUALITYSTANDARDS";
        this.entityId = collateralId;
        this.subentityId = qualityStandardsId;
        this.href = "/collaterals/" + collateralId + "/qualityStandards" + qualityStandardsId;
        return this;
    }

    public CommandWrapperBuilder createProductCollateralMapping(final Long loanProductId) {
        this.actionName = "CREATE";
        this.entityName = CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME;
        this.entityId = loanProductId;
        this.subentityId = null;
        this.href = "/loanProducts/" + loanProductId + "/collaterals";
        return this;
    }

    public CommandWrapperBuilder updateProductCollateralMapping(final Long loanProductId, final Long productCollateralMappingId) {
        this.actionName = "UPDATE";
        this.entityName = CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME;
        this.entityId = loanProductId;
        this.subentityId = productCollateralMappingId;
        this.href = "/loanProducts/" + loanProductId + "/collaterals/" + productCollateralMappingId;
        return this;
    }

    public CommandWrapperBuilder deleteProductCollateralMapping(final Long loanProductId, final Long productCollateralMappingId) {
        this.actionName = "DELETE";
        this.entityName = CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME;
        this.entityId = loanProductId;
        this.subentityId = productCollateralMappingId;
        this.href = "/loanProducts/" + loanProductId + "/collaterals/" + productCollateralMappingId;
        return this;
    }

    public CommandWrapperBuilder updateCollateralDetails(final Long pledgeId, final Long collateraldetailId) {
        this.actionName = "UPDATE";
        this.entityName = "COLLATERALDETAILS";
        this.entityId = pledgeId;
        this.subentityId = collateraldetailId;
        this.href = "/pledges/" + pledgeId + "/collateraldetails/" + collateraldetailId;
        return this;
    }

    public CommandWrapperBuilder deleteCollateralDetails(final Long pledgeId, final Long collateraldetailId) {
        this.actionName = "DELETE";
        this.entityName = "COLLATERALDETAILS";
        this.entityId = pledgeId;
        this.subentityId = collateraldetailId;
        this.href = "/pledges/" + pledgeId + "/collateraldetails/" + collateraldetailId;
        return this;
    }

    public CommandWrapperBuilder createVillage() {
        this.actionName = "CREATE";
        this.entityName = "VILLAGE";
        this.href = "/villages/template";
        return this;
    }

    public CommandWrapperBuilder activateVillage(final Long villageId) {
        this.actionName = "ACTIVATE";
        this.entityName = "VILLAGE";
        this.entityId = villageId;
        this.href = "/villages/" + villageId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder intiateVillageWorkflow(final Long villageId) {
        this.actionName = "INITIATEWORKFLOW";
        this.entityName = "VILLAGE";
        this.entityId = villageId;
        this.href = "/villages/" + villageId + "?command=initiateWorkflow";
        return this;
    }

    public CommandWrapperBuilder updateVillage(final Long villageId) {
        this.actionName = "UPDATE";
        this.entityName = "VILLAGE";
        this.entityId = villageId;
        this.href = "/villages/" + villageId;
        return this;
    }

    public CommandWrapperBuilder deleteVillage(final Long villageId) {
        this.actionName = "DELETE";
        this.entityName = "VILLAGE";
        this.entityId = villageId;
        this.href = "/villages/" + villageId;
        return this;
    }

    public CommandWrapperBuilder rejectMultipleVillages() {
        this.actionName = "REJECT";
        this.entityName = "VILLAGE";
        this.option = "MULTIPLE";
        this.href = "/villages/bulk?command=reject";
        return this;
    }

    public CommandWrapperBuilder assignStaffToVillage(final Long villageId) {
        this.actionName = "ASSIGNSTAFF";
        this.entityName = "VILLAGE";
        this.entityId = villageId;
        this.href = "/villages/" + villageId + "?command=" + VillageTypeApiConstants.ASSIGN_STAFF_COMMAND;
        return this;
    }

    public CommandWrapperBuilder unassignStaffFromVillage(final Long villageId) {
        this.actionName = "UNASSIGNSTAFF";
        this.entityName = "VILLAGE";
        this.entityId = villageId;
        this.href = "/villages/" + villageId + "?command=" + VillageTypeApiConstants.UNASSIGN_STAFF_COMMAND;
        return this;
    }

    public CommandWrapperBuilder associateCentersToVillage(final Long centerId) {
        this.actionName = "ASSOCIATECENTERS";
        this.entityName = "CENTER";
        this.entityId = centerId;
        this.href = "/centers/" + centerId + "?command=associateCenters";
        return this;
    }

    public CommandWrapperBuilder createFamilyDetails(final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "FAMILYDETAIL";
        this.clientId = clientId;
        this.href = "/clients" + "/" + clientId + "/familydetails";
        return this;
    }

    public CommandWrapperBuilder updateFamilyDetails(final Long clientId, final Long familyDetailsId) {
        this.actionName = "UPDATE";
        this.entityName = "FAMILYDETAIL";
        this.entityId = familyDetailsId;
        this.clientId = clientId;
        this.href = "/clients" + "/" + clientId + "/familydetails/" + familyDetailsId;
        return this;
    }

    public CommandWrapperBuilder removeFamilyMemberAssociation(final Long clientId, final Long familyDetailsId) {
        this.actionName = "DELETE";
        this.entityName = "FAMILYMEMBERASSOCIATION";
        this.entityId = familyDetailsId;
        this.clientId = clientId;
        this.href = "/clients" + "/" + clientId + "/familydetails/" + familyDetailsId;
        return this;
    }

    public CommandWrapperBuilder deleteFamilyDetails(final Long familyDetailsId, final Long clientId) {
        this.actionName = "DELETE";
        this.entityName = "FAMILYDETAIL";
        this.entityId = familyDetailsId;
        this.clientId = clientId;
        this.href = "/clients" + "/" + clientId + "/familydetails/" + familyDetailsId;
        return this;
    }

    public CommandWrapperBuilder storeExistingLoan(final Long clientId) {
        this.actionName = "SAVE";
        this.entityName = "EXISTINGLOAN";
        this.clientId = clientId;
        this.href = "clients/" + clientId + "/existingLoans/";
        return this;
    }

    public CommandWrapperBuilder updateExistingLoan(final Long clientId, final Long existingLoanId) {
        this.actionName = "UPDATE";
        this.entityName = "EXISTINGLOAN";
        this.clientId = clientId;
        this.entityId = existingLoanId;
        this.href = "clients/" + clientId + "/existingLoans/" + existingLoanId;
        return this;
    }

    public CommandWrapperBuilder deleteExistingLoan(final Long clientId, final Long existingLoanId) {
        this.actionName = "DELETE";
        this.entityName = "EXISTINGLOAN";
        this.entityId = existingLoanId;
        this.clientId = clientId;
        this.href = "clients/" + clientId + "/existingLoans/" + existingLoanId;
        return this;
    }

    public CommandWrapperBuilder addSelfServiceBeneficiaryTPT() {
        this.actionName = "CREATE";
        this.entityName = "SSBENEFICIARYTPT";
        this.entityId = null;
        this.href = "/self/beneficiaries/tpt";
        return this;
    }

    public CommandWrapperBuilder updateSelfServiceBeneficiaryTPT(final Long beneficiaryId) {
        this.actionName = "UPDATE";
        this.entityName = "SSBENEFICIARYTPT";
        this.entityId = beneficiaryId;
        this.href = "/self/beneficiaries/tpt/" + beneficiaryId;
        return this;
    }

    public CommandWrapperBuilder deleteSelfServiceBeneficiaryTPT(final Long beneficiaryId) {
        this.actionName = "DELETE";
        this.entityName = "SSBENEFICIARYTPT";
        this.entityId = beneficiaryId;
        this.href = "/self/beneficiaries/tpt/" + beneficiaryId;
        return this;
    }

    public CommandWrapperBuilder createSmsCampaign() {
        this.actionName = "CREATE";
        this.entityName = "SMS_CAMPAIGN";
        this.entityId = null;
        this.href = "/sms/campaign";
        return this;
    }

    public CommandWrapperBuilder updateSmsCampaign(final Long resourceId) {
        this.actionName = "UPDATE";
        this.entityName = "SMS_CAMPAIGN";
        this.entityId = resourceId;
        this.href = "/sms/campaign/" + resourceId;
        return this;
    }

    public CommandWrapperBuilder deleteSmsCampaign(final Long resourceId) {
        this.actionName = "DELETE";
        this.entityName = "SMS_CAMPAIGN";
        this.entityId = resourceId;
        this.href = "/sms/campaign/" + resourceId;
        return this;
    }

    public CommandWrapperBuilder activateSmsCampaign(final Long resourceId) {
        this.actionName = "ACTIVATE";
        this.entityName = "SMS_CAMPAIGN";
        this.entityId = resourceId;
        this.href = "/sms/campaign/" + resourceId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder closeSmsCampaign(final Long resourceId) {
        this.actionName = "CLOSE";
        this.entityName = "SMS_CAMPAIGN";
        this.entityId = resourceId;
        this.href = "/sms/campaign/" + resourceId + "?command=close";
        return this;
    }

    public CommandWrapperBuilder reactivateSmsCampaign(final Long resourceId) {
        this.actionName = "REACTIVATE";
        this.entityName = "SMS_CAMPAIGN";
        this.entityId = resourceId;
        this.href = "/sms/campaign/" + resourceId + "?command=reactivate";
        return this;
    }

    public CommandWrapperBuilder createReportMailingJob(final String entityName) {
        this.actionName = "CREATE";
        this.entityName = entityName;
        this.entityId = null;
        this.href = "/reportmailingjobs";
        return this;
    }

    public CommandWrapperBuilder updateReportMailingJob(final String entityName, final Long entityId) {
        this.actionName = "UPDATE";
        this.entityName = entityName;
        this.entityId = entityId;
        this.href = "/reportmailingjobs/" + entityId;
        return this;
    }

    public CommandWrapperBuilder deleteReportMailingJob(final String entityName, final Long entityId) {
        this.actionName = "DELETE";
        this.entityName = entityName;
        this.entityId = entityId;
        this.href = "/reportmailingjobs/" + entityId;
        return this;
    }

    /**
     * Create Address
     * 
     * @param entityTypeId
     *            : This parameter is setting to entityId
     * @param entityType
     * @param entityId
     *            : This parameter is setting to subentityId
     * @return
     */
    public CommandWrapperBuilder createAddress(final Long entityTypeId, final String entityType, final Long entityId) {
        this.actionName = "CREATE";
        this.entityName = "ADDRESSES";
        this.entityId = entityTypeId;
        this.subentityId = entityId;
        this.href = entityType + "/" + entityId + "/addresses";
        return this;
    }

    /**
     * Update Address
     * 
     * @param entityType
     * @param entityId
     *            : This parameter is setting to subentityId
     * @param addressId
     *            : This parameter is setting to entityId
     * @return
     */
    public CommandWrapperBuilder updateAddress(final String entityType, final Long entityId, final Long addressId) {
        this.actionName = "UPDATE";
        this.entityName = "ADDRESSES";
        this.entityId = addressId;
        this.subentityId = entityId;
        this.href = entityType + "/" + entityId + "/addresses/" + addressId;
        return this;
    }

    /**
     * Delete Address
     * 
     * @param entityType
     * @param entityId
     *            : This parameter is setting to subentityId
     * @param addressId
     *            : This parameter is setting to entityId
     * @return
     */
    public CommandWrapperBuilder daleteAddress(final String entityType, final Long entityId, final Long addressId) {
        this.actionName = "DELETE";
        this.entityName = "ADDRESSES";
        this.entityId = addressId;
        this.subentityId = entityId;
        this.href = entityType + "/" + entityId + "/addresses/" + addressId;
        return this;
    }

    public CommandWrapperBuilder createLoanApplicationReference() {
        this.actionName = "CREATE";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.href = "/loanapplicationreferences";
        return this;
    }

    public CommandWrapperBuilder updateLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder deleteLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "DELETE";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder requestForApprovalLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "REQUESTFORAPPROVAL";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder rejectLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "REJECT";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder approveLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "APPROVE";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder undoApproveLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "UNDOAPPROVE";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder disburseLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "DISBURSE";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder updateFutureMeetingCalendar(final String entityType, final Long entityId, final Long calendarId) {
        this.actionName = "UPDATEFUTUREMEETINGS";
        this.entityName = "CALENDAR";
        this.entityId = calendarId;
        this.groupId = entityId;
        this.href = "/" + entityType + "/" + entityId + "/calendars/" + calendarId;
        return this;
    }

    public CommandWrapperBuilder createTaluka(final String entityType, final Long entityId) {
        this.actionName = "CREATE";
        this.entityName = "TALUKA";
        this.entityId = entityId;
        this.href = entityType + "/" + entityId + "/talukas";
        return this;

    }

    public CommandWrapperBuilder createFingerPrint(final String entityType, final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "FINGERPRINT";
        this.clientId = clientId;
        this.href = entityType + "/" + clientId + "/fingerprint";
        return this;
    }

    public CommandWrapperBuilder createLoanPurposeGroup() {
        this.actionName = "CREATE";
        this.entityName = "LOANPURPOSEGROUP";
        this.href = "/loanpurposegroups";
        return this;
    }

    public CommandWrapperBuilder updateLoanPurposeGroup(final Long loanPurposeGroupId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANPURPOSEGROUP";
        this.entityId = loanPurposeGroupId;
        this.href = "/loanpurposegroups/" + loanPurposeGroupId;
        return this;
    }

    public CommandWrapperBuilder activateLoanPurposeGroup(final Long loanPurposeGroupId) {
        this.actionName = "ACTIVATE";
        this.entityName = "LOANPURPOSEGROUP";
        this.entityId = loanPurposeGroupId;
        this.href = "/loanpurposegroups/" + loanPurposeGroupId;
        return this;
    }

    public CommandWrapperBuilder inActivateLoanPurposeGroup(final Long loanPurposeGroupId) {
        this.actionName = "INACTIVATE";
        this.entityName = "LOANPURPOSEGROUP";
        this.entityId = loanPurposeGroupId;
        this.href = "/loanpurposegroups/" + loanPurposeGroupId;
        return this;
    }

    public CommandWrapperBuilder createLoanPurpose() {
        this.actionName = "CREATE";
        this.entityName = "LOANPURPOSE";
        this.href = "/loanpurposes";
        return this;
    }

    public CommandWrapperBuilder updateLoanPurpose(final Long loanPurposeId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANPURPOSE";
        this.entityId = loanPurposeId;
        this.href = "/loanpurposes/" + loanPurposeId;
        return this;
    }

    public CommandWrapperBuilder activateLoanPurpose(final Long loanPurposeId) {
        this.actionName = "ACTIVATE";
        this.entityName = "LOANPURPOSE";
        this.entityId = loanPurposeId;
        this.href = "/loanpurposes/" + loanPurposeId;
        return this;
    }

    public CommandWrapperBuilder inActivateLoanPurpose(final Long loanPurposeId) {
        this.actionName = "INACTIVATE";
        this.entityName = "LOANPURPOSE";
        this.entityId = loanPurposeId;
        this.href = "/loanpurposes/" + loanPurposeId;
        return this;
    }

    public CommandWrapperBuilder createLoanUtilizationCheck(final Long loanId) {
        this.actionName = "CREATE";
        this.entityName = "LOANUTILIZATIONCHECK";
        this.entityId = loanId;
        this.href = "/loans/" + loanId + "/utilizationchecks";
        return this;
    }

    public CommandWrapperBuilder updateLoanUtilizationCheck(final Long loanId, final Long utilizationCheckId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANUTILIZATIONCHECK";
        this.entityId = loanId;
        this.subentityId = utilizationCheckId;
        this.href = "/loans/" + loanId + "/utilizationchecks/" + utilizationCheckId;
        return this;
    }

    public CommandWrapperBuilder createCashFlow() {
        this.actionName = "CREATE";
        this.entityName = "CASHFLOW";
        this.href = "/cashflowcategories";
        return this;
    }

    public CommandWrapperBuilder updateCashFlow(final Long cashFlowCategoryId) {
        this.actionName = "UPDATE";
        this.entityName = "CASHFLOW";
        this.entityId = cashFlowCategoryId;
        this.href = "/cashflowcategories/" + cashFlowCategoryId;
        return this;
    }

    public CommandWrapperBuilder activateCashFlow(final Long cashFlowCategoryId) {
        this.actionName = "ACTIVATE";
        this.entityName = "CASHFLOW";
        this.entityId = cashFlowCategoryId;
        this.href = "/cashflowcategories/" + cashFlowCategoryId;
        return this;
    }

    public CommandWrapperBuilder inActivateCashFlow(final Long cashFlowCategoryId) {
        this.actionName = "INACTIVATE";
        this.entityName = "CASHFLOW";
        this.entityId = cashFlowCategoryId;
        this.href = "/cashflowcategories/" + cashFlowCategoryId;
        return this;
    }

    public CommandWrapperBuilder createIncomeExpense() {
        this.actionName = "CREATE";
        this.entityName = "INCOMEEXPENSE";
        this.href = "/incomesorexpenses";
        return this;
    }

    public CommandWrapperBuilder updateIncomeExpense(final Long incomeExpenseId) {
        this.actionName = "UPDATE";
        this.entityName = "INCOMEEXPENSE";
        this.entityId = incomeExpenseId;
        this.href = "/incomesorexpenses/" + incomeExpenseId;
        return this;
    }

    public CommandWrapperBuilder activateIncomeExpense(final Long incomeExpenseId) {
        this.actionName = "ACTIVATE";
        this.entityName = "INCOMEEXPENSE";
        this.entityId = incomeExpenseId;
        this.href = "/incomesorexpenses/" + incomeExpenseId;
        return this;
    }

    public CommandWrapperBuilder inActivateIncomeExpense(final Long incomeExpenseId) {
        this.actionName = "INACTIVATE";
        this.entityName = "INCOMEEXPENSE";
        this.entityId = incomeExpenseId;
        this.href = "/incomesorexpenses/" + incomeExpenseId;
        return this;
    }

    public CommandWrapperBuilder createFamilyDetailsSummary(final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "FAMILYDETAILSSUMMARY";
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/familydetailssummary";
        return this;
    }

    public CommandWrapperBuilder updateFamilyDetailsSummary(final Long clientId, final Long familyDetailsSummaryId) {
        this.actionName = "UPDATE";
        this.entityName = "FAMILYDETAILSSUMMARY";
        this.clientId = clientId;
        this.entityId = familyDetailsSummaryId;
        this.href = "/clients/" + clientId + "/familydetailssummary/" + familyDetailsSummaryId;
        return this;
    }

    public CommandWrapperBuilder deleteFamilyDetailsSummary(final Long clientId, final Long familyDetailsSummaryId) {
        this.actionName = "DELETE";
        this.entityName = "FAMILYDETAILSSUMMARY";
        this.clientId = clientId;
        this.entityId = familyDetailsSummaryId;
        this.href = "/clients/" + clientId + "/familydetailssummary/" + familyDetailsSummaryId;
        return this;
    }

    public CommandWrapperBuilder createCenterLoanUtilizationCheck(final Long centerId) {
        this.actionName = "CREATE";
        this.entityName = "LOANUTILIZATIONCHECK";
        this.entityId = centerId;
        this.href = "/centers/" + centerId + "/utilizationchecks";
        return this;
    }

    public CommandWrapperBuilder createGroupLoanUtilizationCheck(final Long groupId) {
        this.actionName = "CREATE";
        this.entityName = "LOANUTILIZATIONCHECK";
        this.entityId = groupId;
        this.href = "/groups/" + groupId + "/utilizationchecks";
        return this;
    }

    public CommandWrapperBuilder createClientIncomeExpense(final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "CLIENTINCOMEEXPENSE";
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/incomesandexpenses";
        return this;
    }

    public CommandWrapperBuilder updateClientIncomeExpense(final Long clientId, final Long clientIncomeExpenseId) {
        this.actionName = "UPDATE";
        this.entityName = "CLIENTINCOMEEXPENSE";
        this.entityId = clientIncomeExpenseId;
        this.href = "/clients/" + clientId + "/incomesandexpenses/" + clientIncomeExpenseId;
        return this;
    }

    public CommandWrapperBuilder deleteClientIncomeExpense(final Long clientId, final Long clientIncomeExpenseId) {
        this.actionName = "DELETE";
        this.entityName = "CLIENTINCOMEEXPENSE";
        this.entityId = clientIncomeExpenseId;
        this.href = "/clients/" + clientId + "/incomesandexpenses/" + clientIncomeExpenseId;
        return this;
    }

    public CommandWrapperBuilder createRiskFactor() {
        this.actionName = "CREATE";
        this.entityName = "RISKFACTOR";
        this.href = "/risk/factor";
        return this;
    }

    public CommandWrapperBuilder createRiskDimension() {
        this.actionName = "CREATE";
        this.entityName = "RISKDIMENSION";
        this.href = "/risk/dimension";
        return this;
    }

    public CommandWrapperBuilder createRiskCriteria() {
        this.actionName = "CREATE";
        this.entityName = "RISKCRITERIA";
        this.href = "/risk/criteria";
        return this;
    }

    public CommandWrapperBuilder updateRiskFactor(final Long factorId) {
        this.actionName = "UPDATE";
        this.entityName = "RISKFACTOR";
        this.entityId = factorId;
        this.href = "/risk/factor/" + factorId;
        return this;
    }

    public CommandWrapperBuilder updateRiskDimension(final Long dimensionId) {
        this.actionName = "UPDATE";
        this.entityName = "RISKDIMENSION";
        this.entityId = dimensionId;
        this.href = "/risk/dimension/" + dimensionId;
        return this;
    }

    public CommandWrapperBuilder updateRiskCriteria(final Long criteriaId) {
        this.actionName = "UPDATE";
        this.entityName = "RISKCRITERIA";
        this.entityId = criteriaId;
        this.href = "/risk/criteria/" + criteriaId;
        return this;
    }

    public CommandWrapperBuilder createLoanProductEligibility(final Long loanProductId) {
        this.actionName = "CREATE";
        this.entityName = "LOANPRODUCTELIGIBILITY";
        this.productId = loanProductId;
        this.href = "/loanproduct/" + loanProductId + "/eligibility";
        return this;
    }

    public CommandWrapperBuilder updateLoanProductEligibility(final Long loanProductId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANPRODUCTELIGIBILITY";
        this.productId = loanProductId;
        this.href = "/loanproduct/" + loanProductId + "/eligibility";
        return this;
    }

    public CommandWrapperBuilder createGlimRepaymentTransaction(final Long loanId) {
        this.actionName = "REPAYMENT";
        this.entityName = "LOAN";
        this.href = "/glim/" + loanId + "/transactions?command=repayment";
        this.option = "GLIM";
        this.loanId = loanId;
        return this;
    }

    public CommandWrapperBuilder waiveInterestTransactionForGlim(final Long loanId) {
        this.actionName = "WAIVEINTERESTPORTION";
        this.entityName = "LOAN";
        this.href = "/glim/" + loanId + "/transactions?command=waiveinterest";
        this.option = "GLIM";
        this.loanId = loanId;
        return this;
    }

    public CommandWrapperBuilder waiveChargeTransactionForGlim(final Long loanId) {
        this.actionName = "WAIVE";
        this.entityName = "LOANCHARGE";
        this.href = "/glim/" + loanId + "/transactions?command=waivecharge";
        this.option = "GLIM";
        this.loanId = loanId;
        return this;
    }

    public CommandWrapperBuilder writeOffTransactionForGlim(final Long loanId) {
        this.actionName = "WRITEOFF";
        this.entityName = "LOAN";
        this.href = "/glim/" + loanId + "/transactions?command=writeoff";
        this.option = "GLIM";
        this.loanId = loanId;
        return this;
    }

    public CommandWrapperBuilder recoveryPaymentForGlim(final Long loanId) {
        this.actionName = "RECOVERYPAYMENT";
        this.entityName = "LOAN";
        this.href = "/glim/" + loanId + "/transactions?command=recoverypayment";
        this.option = "GLIM";
        this.loanId = loanId;
        return this;
    }

    public CommandWrapperBuilder createSurvey() {
        this.actionName = "CREATE";
        this.entityName = "SURVEY";
        this.href = "/surveys";
        return this;
    }

    public CommandWrapperBuilder activateSmartCard(final Long entityTypeId, final String entityType, final Long clientId) {
        this.actionName = "ACTIVATE";
        this.entityName = "SMARTCARD";
        this.clientId = clientId;
        this.subentityId = entityTypeId;
        this.href = "/clients/" + clientId + "/" + entityType + "/smartcard";
        return this;
    }

    public CommandWrapperBuilder inActivateSmartCard(final Long entityTypeId, final String entityType, final Long clientId) {
        this.actionName = "INACTIVATE";
        this.entityName = "SMARTCARD";
        this.clientId = clientId;
        this.subentityId = entityTypeId;
        this.href = "/clients/" + clientId + "/" + entityType + "/smartcard";
        return this;
    }

    public CommandWrapperBuilder createAdhocTask() {
        this.actionName = "CREATE";
        this.entityName = "ADHOCTASK";
        this.href = "/task/adhoc";
        return this;
    }

    public CommandWrapperBuilder createTaskConfigTemplate() {
        this.actionName = "CREATE";
        this.entityName = "TASKCONFIGTEMPLATE";
        this.href = "/tasktemplate";
        return this;
    }

    public CommandWrapperBuilder assignTask() {
        this.actionName = "CREATE";
        this.entityName = "TEMPLATETASK";
        this.href = "/taskassign";
        return this;
    }

    public CommandWrapperBuilder createBankAccountDetail(final String supportedEntityType, final Long supportedEntityId,
            final Integer entityTypeId) {
        this.actionName = "CREATE";
        this.entityName = "BANKACCOUNTDETAIL";
        this.entityId = supportedEntityId;
        this.entityTypeId = entityTypeId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/bankaccountdetail";
        return this;
    }

    public CommandWrapperBuilder updateBankAccountDetail(final String supportedEntityType, final Long supportedEntityId,
            final Integer entityTypeId) {
        this.actionName = "UPDATE";
        this.entityName = "BANKACCOUNTDETAIL";
        this.entityId = supportedEntityId;
        this.entityTypeId = entityTypeId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/bankaccountdetail";
        return this;
    }

    public CommandWrapperBuilder deleteBankAccountDetail(final String supportedEntityType, final Long supportedEntityId,
            final Integer entityTypeId) {
        this.actionName = "DELETE";
        this.entityName = "BANKACCOUNTDETAIL";
        this.entityId = supportedEntityId;
        this.entityTypeId = entityTypeId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/bankaccountdetail";
        return this;
    }

    public CommandWrapperBuilder activateBankAccountDetail(final String supportedEntityType, final Long supportedEntityId,
            final Integer entityTypeId) {
        this.actionName = "ACTIVATE";
        this.entityName = "BANKACCOUNTDETAIL";
        this.entityId = supportedEntityId;
        this.entityTypeId = entityTypeId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/bankaccountdetail/action";
        return this;
    }

    public CommandWrapperBuilder updateCheckerInfoBankAccountDetail(final String supportedEntityType, final Long supportedEntityId,
            final Integer entityTypeId) {
        this.actionName = "CHECKERINFO";
        this.entityName = "BANKACCOUNTDETAIL";
        this.entityId = supportedEntityId;
        this.entityTypeId = entityTypeId;
        this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/bankaccountdetail/action";
        return this;
    }

    public CommandWrapperBuilder createProfileRatingConfig() {
        this.actionName = "CREATE";
        this.entityName = "PROFILE_RATING_CONFIG";
        this.href = "/profileratingconfigs";
        return this;
    }

    public CommandWrapperBuilder updateProfileRatingConfig(final Long profileRatingConfigId) {
        this.actionName = "UPDATE";
        this.entityName = "PROFILE_RATING_CONFIG";
        this.entityId = profileRatingConfigId;
        this.href = "/profileratingconfigs/" + profileRatingConfigId;
        return this;
    }

    public CommandWrapperBuilder activateProfileRatingConfig(final Long profileRatingConfigId) {
        this.actionName = "ACTIVATE";
        this.entityName = "PROFILE_RATING_CONFIG";
        this.entityId = profileRatingConfigId;
        this.href = "/profileratingconfigs/" + profileRatingConfigId;
        return this;
    }

    public CommandWrapperBuilder inActivateProfileRatingConfig(final Long profileRatingConfigId) {
        this.actionName = "INACTIVATE";
        this.entityName = "PROFILE_RATING_CONFIG";
        this.entityId = profileRatingConfigId;
        this.href = "/profileratingconfigs/" + profileRatingConfigId;
        return this;
    }

    public CommandWrapperBuilder computeProfileRating() {
        this.actionName = "COMPUTE";
        this.entityName = "PROFILE_RATING";
        this.href = "/computeprofileratings";
        return this;
    }

    public CommandWrapperBuilder createLoanEMIPack(final Long loanProductId) {
        this.actionName = "CREATE";
        this.entityName = "LOANEMIPACKS";
        this.productId = loanProductId;
        this.href = "/loanemipacks/" + loanProductId;
        return this;
    }

    public CommandWrapperBuilder updateLoanEMIPack(final Long loanProductId, final Long loanEMIPackId) {
        this.actionName = "UPDATE";
        this.entityName = "LOANEMIPACKS";
        this.productId = loanProductId;
        this.entityId = loanEMIPackId;
        this.href = "/loanemipacks/" + loanProductId + "/" + loanEMIPackId;
        return this;
    }

    public CommandWrapperBuilder deleteLoanEMIPack(final Long loanProductId, final Long loanEMIPackId) {
        this.actionName = "DELETE";
        this.entityName = "LOANEMIPACKS";
        this.productId = loanProductId;
        this.entityId = loanEMIPackId;
        this.href = "/loanemipacks/" + loanProductId + "/" + loanEMIPackId;
        return this;
    }

    public CommandWrapperBuilder createTaskConfig(final String entityType, final Long entityId) {
        this.actionName = "CREATE";
        this.entityName = "TASK_CONFIG";
        this.entityId = entityId;
        this.href = "/tasksconfigurations/" + entityType + "/" + entityId;
        return this;
    }

    public CommandWrapperBuilder updateDedupWeightage(final Long legalForm) {
        this.actionName = "UPDATE";
        this.entityName = "CLIENTDEDUPWEIGHTAGES";
        this.entityId = legalForm;
        this.href = "/clientdedup/weightages/" + legalForm;
        return this;
    }

    public CommandWrapperBuilder assignFund(final Long fundId, final String options) {
        this.actionName = "ASSIGN";
        this.entityName = "FUND";
        this.entityId = fundId;
        this.href = (options.equalsIgnoreCase("fromcsv")) ? "/funds/" + fundId + "/assign" : "/funds/" + fundId;
        return this;
    }

    public CommandWrapperBuilder activateFund(final Long fundId) {
        this.actionName = "ACTIVATE";
        this.entityName = "FUND";
        this.entityId = fundId;
        this.href = "/funds/" + fundId + "?command=activate";
        return this;
    }

    public CommandWrapperBuilder deactivateFund(final Long fundId) {
        this.actionName = "DEACTIVATE";
        this.entityName = "FUND";
        this.entityId = fundId;
        this.href = "/funds/" + fundId + "?command=deactivate";
        return this;
    }

    public CommandWrapperBuilder withFormDataMultiPart(final FormDataMultiPart formDataMultiPart) {
        this.formDataMultiPart = formDataMultiPart;
        return this;
    }

    public CommandWrapperBuilder createMandate(final Long loanId) {
        this.actionName = "CREATE";
        this.entityName = "MANDATE";
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/mandates?command=CREATE";
        return this;
    }

    public CommandWrapperBuilder updateMandate(final Long loanId) {
        this.actionName = "UPDATE";
        this.entityName = "MANDATE";
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/mandates?command=UPDATE";
        return this;
    }

    public CommandWrapperBuilder cancelMandate(final Long loanId) {
        this.actionName = "CANCEL";
        this.entityName = "MANDATE";
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/mandates?command=CANCEL";
        return this;
    }

    public CommandWrapperBuilder editMandate(final Long loanId, final Long mandateId) {
        this.actionName = "EDIT";
        this.entityName = "MANDATE";
        this.entityId = mandateId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/mandates/" + mandateId;
        return this;
    }

    public CommandWrapperBuilder deleteMandate(final Long loanId, final Long mandateId) {
        this.actionName = "DELETE";
        this.entityName = "MANDATE";
        this.entityId = mandateId;
        this.loanId = loanId;
        this.href = "/loans/" + loanId + "/mandates/" + mandateId;
        return this;
    }

    public CommandWrapperBuilder mandatesDownload() {
        this.actionName = "MANDATES_DOWNLOAD";
        this.entityName = "MANDATES";
        this.href = "/mandates/command=MANDATES_DOWNLOAD";
        return this;
    }

    public CommandWrapperBuilder mandatesUpload() {
        this.actionName = "MANDATES_UPLOAD";
        this.entityName = "MANDATES";
        this.href = "/mandates/command=MANDATES_UPLOAD";
        return this;
    }

    public CommandWrapperBuilder transactionsDownload() {
        this.actionName = "TRANSACTIONS_DOWNLOAD";
        this.entityName = "MANDATES";
        this.href = "/mandates/command=TRANSACTIONS_DOWNLOAD";
        return this;
    }

    public CommandWrapperBuilder transactionsUpload() {
        this.actionName = "TRANSACTIONS_UPLOAD";
        this.entityName = "MANDATES";
        this.href = "/mandates/command=TRANSACTIONS_UPLOAD";
        return this;
    }

    public CommandWrapperBuilder addCoApplicant(final Long loanApplicationReferenceId, final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "COAPPLICANTS";
        this.entityId = loanApplicationReferenceId;
        this.clientId = clientId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId + "/coapplicants?clientId=" + clientId;
        return this;
    }

    public CommandWrapperBuilder deleteCoApplicant(final Long loanApplicationReferenceId, final Long coapplicantId) {
        this.actionName = "DELETE";
        this.entityName = "COAPPLICANTS";
        this.entityId = coapplicantId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId + "/coapplicants/" + coapplicantId;
        return this;
    }

    public CommandWrapperBuilder updateTaskConfigTemplate(final Long templateId) {
        this.actionName = "UPDATE";
        this.entityName = "TASKCONFIGTEMPLATE";
        this.entityId = templateId;
        this.href = "/tasktemplate/" + templateId;
        return this;
    }

    public CommandWrapperBuilder createAndApproveLoanRescheduleRequest() {
        this.actionName = "BULKCREATEANDAPPROVE";
        this.entityName = "RESCHEDULELOAN";
        this.entityId = null;
        this.href = "/rescheduleloans";
        return this;
    }

    public CommandWrapperBuilder createVoucher(final String voucherType) {
        this.entityName = voucherType.toUpperCase() + "_VOUCHER";
        // To Support different type of vouchers
        this.actionName = "CREATE";
        this.entityId = null;
        this.href = "/vouchers?voucherType=" + voucherType;
        return this;
    }

    public CommandWrapperBuilder updateVoucher(final String voucherType, final Long voucherId) {
        // To Support different type of vouchers
        this.entityName = voucherType.toUpperCase() + "_VOUCHER";
        this.actionName = "UPDATE";
        this.entityId = voucherId;
        this.href = "/vouchers?voucherType=" + voucherType;
        return this;
    }

    public CommandWrapperBuilder reverseVoucher(final String voucherType, final Long voucherId) {
        // To Support different type of vouchers
        this.entityName = voucherType.toUpperCase() + "_VOUCHER";
        this.actionName = "REVERSE";
        this.entityId = voucherId;
        this.href = "/vouchers?voucherType=" + voucherType;
        return this;
    }

    public CommandWrapperBuilder submitForApprovalLoanApplicationReference(final Long loanApplicationReferenceId) {
        this.actionName = "SUBMITFORAPPROVAL";
        this.entityName = "LOANAPPLICATIONREFERENCE";
        this.entityId = loanApplicationReferenceId;
        this.href = "/loanapplicationreferences/" + loanApplicationReferenceId;
        return this;
    }

    public CommandWrapperBuilder createAccountLimits(final Long clientId) {
        this.actionName = "CREATE";
        this.entityName = "CUSTOMERACCOUNTLIMITS";
        this.entityId = null;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/accountlimits";
        return this;
    }

    public CommandWrapperBuilder updateAccountLimits(final Long accountLimitId, final Long clientId) {
        this.actionName = "UPDATE";
        this.entityName = "CUSTOMERACCOUNTLIMITS";
        this.entityId = accountLimitId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/accountlimits/" + accountLimitId;
        return this;
    }

    public CommandWrapperBuilder deleteAccountLimits(final Long accountLimitId, final Long clientId) {
        this.actionName = "DELETE";
        this.entityName = "CUSTOMERACCOUNTLIMITS";
        this.entityId = accountLimitId;
        this.clientId = clientId;
        this.href = "/clients/" + clientId + "/accountlimits/" + accountLimitId;
        return this;
    }

    public CommandWrapperBuilder holdAmount(final Long accountId) {
        this.actionName = "HOLDAMOUNT";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "/transactions?command=holdAmount";
        return this;
    }

    public CommandWrapperBuilder releaseAmount(final Long accountId, final Long transactionId) {
        this.actionName = "RELEASEAMOUNT";
        this.entityName = "SAVINGSACCOUNT";
        this.entityId = null;
        this.savingsId = accountId;
        this.transactionId = transactionId.toString();
        this.href = "/savingsaccounts/" + accountId + "/transactions/" + transactionId + "?command=releaseAmount";
        return this;
    }

    public CommandWrapperBuilder blockDebitsFromSavingsAccount(final Long accountId) {
        this.actionName = "BLOCKDEBIT";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=blockDebit";
        return this;
    }

    public CommandWrapperBuilder unblockDebitsFromSavingsAccount(final Long accountId) {
        this.actionName = "UNBLOCKDEBIT";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=unblockDebit";
        return this;
    }

    public CommandWrapperBuilder blockCreditsToSavingsAccount(final Long accountId) {
        this.actionName = "BLOCKCREDIT";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=blockCredit";
        return this;
    }

    public CommandWrapperBuilder unblockCreditsToSavingsAccount(final Long accountId) {
        this.actionName = "UNBLOCKCREDIT";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=unblockCredit";
        return this;
    }

    public CommandWrapperBuilder blockSavingsAccount(final Long accountId) {
        this.actionName = "BLOCK";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=block";
        return this;
    }

    public CommandWrapperBuilder unblockSavingsAccount(final Long accountId) {
        this.actionName = "UNBLOCK";
        this.entityName = "SAVINGSACCOUNT";
        this.savingsId = accountId;
        this.entityId = null;
        this.href = "/savingsaccounts/" + accountId + "?command=unblock";
        return this;
    }

    public CommandWrapperBuilder createDistrict() {
        this.actionName = "CREATE";
        this.entityName = "DISTRICT";
        this.entityId = null;
        this.href = "/districts/template";
        return this;
    }

    public CommandWrapperBuilder updateDistrict(final Long districtId) {
        this.actionName = "UPDATE";
        this.entityName = "DISTRICT";
        this.entityId = districtId;
        this.href = "/districts/" + districtId;
        return this;
    }

    public CommandWrapperBuilder activateDistrict(final Long districtId) {
        this.actionName = "ACTIVATE";
        this.entityName = "DISTRICT";
        this.entityId = districtId;
        this.href = "/offices/" + districtId;
        return this;
    }

    public CommandWrapperBuilder rejectDistrict(final Long districtId) {
        this.actionName = "REJECT";
        this.entityName = "DISTRICT";
        this.entityId = districtId;
        this.href = "/offices/" + districtId;
        return this;
    }

    public CommandWrapperBuilder intiateDistirctWorkflow(final Long districtId) {
        this.actionName = "INITIATEWORKFLOW";
        this.entityName = "DISTRICT";
        this.entityId = districtId;
        this.href = "/districts/" + districtId + "?command=initiateWorkflow";
        return this;
    }

    public CommandWrapperBuilder executeOverdueCharges() {
        this.actionName = "EXECUTE";
        this.entityName = "OVERDUECHARGE";
        this.entityId = null;
        this.href = "/runoverduecharges";
        return this;
    }
    
    public CommandWrapperBuilder createTaskEntityMapping(final Long taskConfigId) {
        this.actionName = "CREATE";
        this.entityName = "TASK_CONFIG_ENTITYMAPPING";
        this.entityId = taskConfigId;
        this.href = "/taskconfigs/" + taskConfigId + "/mapping";
        return this;
    }

    public CommandWrapperBuilder inActivateTaskConfigEntityMapping(final Long taskConfigId, final Integer taskConfigEntityTypeValue) {
        this.actionName = "INACTIVATE";
        this.entityName = "TASK_CONFIG_ENTITYMAPPING";
        this.entityId = taskConfigId;
        this.entityTypeId = taskConfigEntityTypeValue;
        this.href = "/taskconfigs/mappings/" + taskConfigId + "/" + taskConfigEntityTypeValue;
        return this;
    }
}