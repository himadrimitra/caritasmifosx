/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.commands.service;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;

import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.reconcilation.ReconciliationApiConstants;

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
    private String option;

    public CommandWrapper build() {
        return new CommandWrapper(this.officeId, this.groupId, this.clientId, this.loanId, this.savingsId, this.actionName,
                this.entityName, this.entityId, this.subentityId, this.href, this.json, this.transactionId, this.productId,
                this.templateId, this.option);
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

    public CommandWrapperBuilder deleteBankStatement(final Long bankStatementId) {
        this.actionName = "DELETE";
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId;
        return this;
    }

    public CommandWrapperBuilder reconcileBankStatementDetails(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.RECONCILE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId + "/details";
        return this;
    }

    public CommandWrapperBuilder undoReconcileBankStatementDetails(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.UNDO_RECONCILE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId + "/details";
        return this;
    }

    public CommandWrapperBuilder reconcileBankStatement(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.RECONCILE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId + "?command=reconcile";
        return this;
    }
    
    public CommandWrapperBuilder generatePortfolioTransactions(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.CREATE_ACTION;
        this.entityName = ReconciliationApiConstants.PORTFOLIO_TRANSACTIONS;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/"+ bankStatementId+"/generatetransactions";
        return this;
    }

    public CommandWrapperBuilder createBank() {
        this.actionName = ReconciliationApiConstants.CREATE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_RESOURCE_NAME;
        this.entityId = null;
        this.href = "/bank";
        return this;
    }

    public CommandWrapperBuilder updateBank(final Long bankId) {
        this.actionName = ReconciliationApiConstants.UPDATE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_RESOURCE_NAME;
        this.entityId = bankId;
        this.href = "/bank/" + bankId;
        return this;
    }

    public CommandWrapperBuilder deleteBank(final Long bankId) {
        this.actionName = ReconciliationApiConstants.DELETE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_RESOURCE_NAME;
        this.entityId = bankId;
        this.href = "/bank/" + bankId;
        return this;
    }
    
    public CommandWrapperBuilder inactivateClientRecurringCharge(final Long clientId, final Long recurringChargeId) {
        this.actionName = ClientApiConstants.CLIENT_RECURRING_CHARGE_ACTION_INACTIVATE;
        this.entityName = ClientApiConstants.CLIENT_RECURRING_CHARGES_RESOURCE_NAME;
        this.clientId = clientId;
        this.entityId = recurringChargeId;
        this.href = "/clients/" + clientId + "/recurringcharges/" + recurringChargeId;
        return this;
    }

    public CommandWrapperBuilder updateSecondaryAuthenticationService(final Long authenticationServiceId) {
        this.actionName = "UPDATE";
        this.entityName = "AUTHENTICATIONSERVICE";
        this.entityId = authenticationServiceId;
        this.href = "/external/authentications/services/" + authenticationServiceId;
        return this;
    }

    public CommandWrapperBuilder createTransactionAuthenticationService() {
        this.actionName = TransactionAuthenticationApiConstants.CREATE_ACTION;
        this.entityName = TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_SERVICE;
        this.entityId = null;
        this.href = "/transaction/authentications";
        return this;
    }

    public CommandWrapperBuilder updateTransactionAuthenticationService(final Long transactionAuthenticationServiceId) {
        this.actionName = TransactionAuthenticationApiConstants.UPDATE_ACTION;
        this.entityName = TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_SERVICE;
        this.entityId = transactionAuthenticationServiceId;
        this.href = "/transaction/authentications/" + transactionAuthenticationServiceId;
        return this;
    }

    public CommandWrapperBuilder deleteTransactionAuthenticationService(final Long transactionAuthenticationServiceId) {
        this.actionName = TransactionAuthenticationApiConstants.DELETE_ACTION;
        this.entityName = TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_SERVICE;
        this.entityId = transactionAuthenticationServiceId;
        this.href = "/transaction/authentications/" + transactionAuthenticationServiceId;
        return this;
    }
    
    public CommandWrapperBuilder activateCreditBureau(Long creditBureauId) {
        this.actionName = "ACTIVATE";
        this.entityName = "CREDITBUREAU";
        this.entityId = creditBureauId;
        this.href = "/creditbureau/" + creditBureauId + "?command=activate&template=true";
        return this;
    }

    public CommandWrapperBuilder deactivateCreditBureau(Long creditBureauId) {
        this.actionName = "DEACTIVATE";
        this.entityName = "CREDITBUREAU";
        this.entityId = creditBureauId;
        this.href = "/creditbureau/" + creditBureauId + "?command=deactivate&template=true";
        return this;
    }
    
    public CommandWrapperBuilder createCreditBureauLoanProductMapping(final Long productId) {
        this.actionName = "CREATE";
        this.entityName = "CREDIT_BUREAU_LOANPRODUCT_MAPPING";
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/creditbureau";
        return this;
    }

    public CommandWrapperBuilder updateCreditBureauLoanProductMapping(final Long productId, final Long bureauId) {
        this.actionName = "UPDATE";
        this.entityName = "CREDIT_BUREAU_LOANPRODUCT_MAPPING";
        this.entityId = bureauId;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/creditbureau/"+bureauId;
        return this;
    }
    
    public CommandWrapperBuilder activateLoanProductCreditBureauMapping(final Long productId, final Long bureauId) {
        this.actionName = "ACTIVE";
        this.entityName = "CREDIT_BUREAU_LOANPRODUCT_MAPPING";
        this.entityId = bureauId;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/creditbureau/"+bureauId;
        return this;
    }

    public CommandWrapperBuilder inActivateLoanProductCreditBureauMapping(final Long productId, final Long bureauId) {
        this.actionName = "INACTIVE";
        this.entityName = "CREDIT_BUREAU_LOANPRODUCT_MAPPING";
        this.entityId = bureauId;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/creditbureau/"+bureauId;
        return this;
    }
    
    public CommandWrapperBuilder deleteLoanProductCreditBureauMapping(final Long productId, final Long bureauId) {
        this.actionName = "DELETE";
        this.entityName = "CREDIT_BUREAU_LOANPRODUCT_MAPPING";
        this.entityId = bureauId;
        this.productId = productId;
        this.href = "/loanproducts/" + productId + "/creditbureau/" + bureauId ;
        return this;
    }
}