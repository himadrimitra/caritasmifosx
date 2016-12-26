/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.exception.ClientSharesNotEqualToPrincipalAmountException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidClientShareInGroupLoanException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class GroupLoanIndividualMonitoringDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public GroupLoanIndividualMonitoringDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public static void validateForGroupLoanIndividualMonitoring(final JsonCommand command, String totalAmountType) {
        JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        if (isClientsAmountValid(clientMembers)) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                if (member.has(LoanApiConstants.isClientSelectedParamName)
                        && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean()) {
                    totalAmount = totalAmount.add(member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal());
                }
            }
            if (command.bigDecimalValueOfParameterNamed(totalAmountType).doubleValue() != totalAmount.doubleValue()) { throw new ClientSharesNotEqualToPrincipalAmountException(); }
        } else {
            throw new InvalidClientShareInGroupLoanException();
        }

    }
    
    public static void validateForGroupLoanIndividualMonitoringTransaction(final JsonCommand command, String totalAmountType) {
        JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (JsonElement clientMember : clientMembers) {
            JsonObject member = clientMember.getAsJsonObject();
            if (member.has(LoanApiConstants.transactionAmountParamName)) {
                totalAmount = totalAmount.add(member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal());
            }
        }
        if (command.bigDecimalValueOfParameterNamed(totalAmountType).doubleValue() != totalAmount.doubleValue()) { throw new ClientSharesNotEqualToPrincipalAmountException(); }

    }

    public static boolean isClientsAmountValid(JsonArray clientMembers) {
        boolean isValidAmount = true;
        for (JsonElement clientMember : clientMembers) {
            JsonObject member = clientMember.getAsJsonObject();
            if (member.has(LoanApiConstants.isClientSelectedParamName)
                    && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean()) {
                if (!(member.has(LoanApiConstants.transactionAmountParamName) && member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal()
                        .doubleValue() > 0)) {
                    isValidAmount = false;
                }
            }
        }
        return isValidAmount;
    }

    public static boolean isClientsDoNotHaveAmount(JsonArray clientMembers) {
        boolean isAmountNotPresent = true;
        for (JsonElement clientMember : clientMembers) {
            JsonObject member = clientMember.getAsJsonObject();
            if (member.has(LoanApiConstants.isClientSelectedParamName)
                    && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean()) {
                if (member.has(LoanApiConstants.transactionAmountParamName)) {
                    isAmountNotPresent = false;
                    break;
                }
            }
        }
        return isAmountNotPresent;
    }
    
    public static void validateGlimClientActivationDate(LocalDate submittedOnDate, List<GroupLoanIndividualMonitoring> glimMembers) {
        List<String> clientNames = new ArrayList<>();
        for (GroupLoanIndividualMonitoring glimMember : glimMembers) {
            if (submittedOnDate.isBefore(glimMember.getClient().getActivationLocalDate())) {
                clientNames.add(glimMember.getClient().getDisplayName());
            }
        }
        if (!clientNames.isEmpty()) {
            String defaultUserMessage = "Submitted on date cannot be before the client activation date.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.client.activation.date", defaultUserMessage,
                    submittedOnDate.toString(), clientNames);
        }
    }

}
