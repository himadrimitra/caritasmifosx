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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatEnumerations.AccountNumberPrefixType;
import org.apache.fineract.infrastructure.accountnumberformat.service.CustomAccountNumberGeneratorFactory;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Example {@link AccountNumberGenerator} for clients that takes an entities
 * auto generated database id and zero fills it ensuring the identifier is
 * always of a given <code>maxLength</code>.
 */
@Component
public class AccountNumberGenerator {

    private final static int maxLength = 9;

    private final static String ID = "id";
    private final static String CLIENT_TYPE = "clientType";
    private final static String OFFICE_CODE = "officeCode";
    private final static String LOAN_PRODUCT_SHORT_NAME = "loanProductShortName";
    private final static String SAVINGS_PRODUCT_SHORT_NAME = "savingsProductShortName";
    private final static String SHARE_PRODUCT_SHORT_NAME = "sharesProductShortName";

    private final CustomAccountNumberGeneratorFactory customAccountNumberGeneratorFactory;

    @Autowired
    public AccountNumberGenerator(final CustomAccountNumberGeneratorFactory customAccountNumberGeneratorFactory) {
        this.customAccountNumberGeneratorFactory = customAccountNumberGeneratorFactory;
    }

    public String generate(final Client client, final AccountNumberFormat accountNumberFormat) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, client.getId().toString());
        propertyMap.put(OFFICE_CODE, client.getOffice().getOfficeCodeId());
        final CodeValue clientType = client.clientType();
        if (clientType != null) {
            propertyMap.put(CLIENT_TYPE, clientType.label());
        }
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    public String generate(final Loan loan, final AccountNumberFormat accountNumberFormat) {
        if (accountNumberFormat != null && accountNumberFormat.getPrefixEnum() != null) {
            final AccountNumberPrefixType accountNumberPrefixType = AccountNumberPrefixType.fromInt(accountNumberFormat.getPrefixEnum());
            if (accountNumberPrefixType.getValue()
                    .equals(AccountNumberPrefixType.CUSTOM.getValue())) { return this.customAccountNumberGeneratorFactory
                            .determineGenerator(accountNumberFormat.getCustomTypeEnum())
                            .generateAccountNumberForLoans(loan, accountNumberFormat); }
        }
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, loan.getId().toString());
        propertyMap.put(OFFICE_CODE, loan.getOffice().getOfficeCodeId());
        propertyMap.put(LOAN_PRODUCT_SHORT_NAME, loan.loanProduct().getShortName());
        return generateAccountNumber(propertyMap, accountNumberFormat);

    }

    public String generate(final SavingsAccount savingsAccount, final AccountNumberFormat accountNumberFormat) {
        if (accountNumberFormat != null && accountNumberFormat.getPrefixEnum() != null) {
            final AccountNumberPrefixType accountNumberPrefixType = AccountNumberPrefixType.fromInt(accountNumberFormat.getPrefixEnum());
            if (accountNumberPrefixType.getValue()
                    .equals(AccountNumberPrefixType.CUSTOM.getValue())) { return this.customAccountNumberGeneratorFactory
                            .determineGenerator(accountNumberFormat.getCustomTypeEnum())
                            .generateAccountNumberForSavings(savingsAccount, accountNumberFormat); }
        }
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, savingsAccount.getId().toString());
        propertyMap.put(OFFICE_CODE, savingsAccount.office().getOfficeCodeId());
        propertyMap.put(SAVINGS_PRODUCT_SHORT_NAME, savingsAccount.savingsProduct().getShortName());
        return generateAccountNumber(propertyMap, accountNumberFormat);

    }

    public String generate(final ShareAccount shareaccount, final AccountNumberFormat accountNumberFormat) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, shareaccount.getId().toString());
        propertyMap.put(SHARE_PRODUCT_SHORT_NAME, shareaccount.getShareProduct().getShortName());
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    private String generateAccountNumber(final Map<String, String> propertyMap, final AccountNumberFormat accountNumberFormat) {
        String accountNumber = StringUtils.leftPad(propertyMap.get(ID), AccountNumberGenerator.maxLength, '0');
        if (accountNumberFormat != null && accountNumberFormat.getPrefixEnum() != null) {
            final AccountNumberPrefixType accountNumberPrefixType = AccountNumberPrefixType.fromInt(accountNumberFormat.getPrefixEnum());
            String prefix = null;
            switch (accountNumberPrefixType) {
                case CLIENT_TYPE:
                    prefix = propertyMap.get(CLIENT_TYPE);
                break;

                case OFFICE_CODE:
                    prefix = propertyMap.get(OFFICE_CODE);
                break;

                case LOAN_PRODUCT_SHORT_NAME:
                    prefix = propertyMap.get(LOAN_PRODUCT_SHORT_NAME);
                break;

                case SAVINGS_PRODUCT_SHORT_NAME:
                    prefix = propertyMap.get(SAVINGS_PRODUCT_SHORT_NAME);
                break;

                default:
                break;

            }
            accountNumber = StringUtils.overlay(accountNumber, prefix, 0, 0);
        }
        return accountNumber;
    }

    public String generateGroupAccountNumber(final Group group, final AccountNumberFormat accountNumberFormat) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, group.getId().toString());
        propertyMap.put(OFFICE_CODE, group.getOffice().getOfficeCodeId());
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    public String generateCenterAccountNumber(final Group group, final AccountNumberFormat accountNumberFormat) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, group.getId().toString());
        propertyMap.put(OFFICE_CODE, group.getOffice().getOfficeCodeId());
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

}