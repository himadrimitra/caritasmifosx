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
package org.apache.fineract.accounting.financialactivityaccount.api;

import java.util.HashSet;
import java.util.Set;

/***
 * Enum of all parameters passed in while creating/updating a GL Account
 ***/
public enum FinancialActivityAccountsJsonInputParams {
    FINANCIAL_ACTIVITY_ID("financialActivityId"), GL_ACCOUNT_ID("glAccountId"), ADVANCED_FINANCIAL_ACTIVITY_MAPPING(
            "advancedFinacialActivityMapping"), PAYMENT_TYPE_ACCOUNT_MAPPING("paymentTypeAccountMapping"), PAYMENT_TYPE_ID(
                    "paymentTypeId"), DELETE("delete"), ID("id"), FINANCIAL_ACTIVITY_ACCOUNT_MAPPING("financialActivityaAccountmapping");

    private final String value;

    private FinancialActivityAccountsJsonInputParams(final String value) {
        this.value = value;
    }

    private static final Set<String> values = new HashSet<>();
    static {
        for (final FinancialActivityAccountsJsonInputParams type : FinancialActivityAccountsJsonInputParams.values()) {
            values.add(type.value);
        }
    }

    public static Set<String> getAllValues() {
        return values;
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public String getValue() {
        return this.value;
    }
}