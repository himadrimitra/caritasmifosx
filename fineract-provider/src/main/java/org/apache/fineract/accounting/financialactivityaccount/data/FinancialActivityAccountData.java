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
package org.apache.fineract.accounting.financialactivityaccount.data;

import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;

public class FinancialActivityAccountData {

    private final Long id;
    private final FinancialActivityData financialActivityData;
    private final GLAccountData glAccountData;
    private Map<String, List<GLAccountData>> glAccountOptions;
    private List<FinancialActivityData> financialActivityOptions;
    private final List<FinancialActivityAccountPaymentTypeMappingData> financialActivityAccountPaymentTypeMappingData;

    public FinancialActivityAccountData() {
        this.id = null;
        this.glAccountData = null;
        this.financialActivityData = null;
        this.glAccountOptions = null;
        this.financialActivityOptions = null;
        this.financialActivityAccountPaymentTypeMappingData = null;
    }

    public FinancialActivityAccountData(final Long id, final FinancialActivityData financialActivityData, final GLAccountData glAccountData,
            final List<FinancialActivityAccountPaymentTypeMappingData> financialActivityAccountPaymentTypeMappingData) {
        this.id = id;
        this.glAccountData = glAccountData;
        this.financialActivityData = financialActivityData;
        this.financialActivityAccountPaymentTypeMappingData = financialActivityAccountPaymentTypeMappingData;
    }

    public FinancialActivityAccountData(final Map<String, List<GLAccountData>> glAccountOptions,
            final List<FinancialActivityData> financialActivityOptions) {
        this.id = null;
        this.glAccountData = null;
        this.financialActivityData = null;
        this.glAccountOptions = glAccountOptions;
        this.financialActivityOptions = financialActivityOptions;
        this.financialActivityAccountPaymentTypeMappingData = null;

    }

    public List<FinancialActivityData> getFinancialActivityOptions() {
        return this.financialActivityOptions;
    }

    public FinancialActivityAccountData(final FinancialActivityAccountData financialActivityAccountData,
            final List<FinancialActivityAccountPaymentTypeMappingData> financialActivityAccountPaymentTypeMappingData) {
        this.id = financialActivityAccountData.getId();
        this.glAccountData = financialActivityAccountData.glAccountData;
        this.financialActivityData = financialActivityAccountData.getFinancialActivityData();
        this.financialActivityAccountPaymentTypeMappingData = financialActivityAccountPaymentTypeMappingData;
    }

    public void setFinancialActivityOptions(final List<FinancialActivityData> financialActivityOptions) {
        this.financialActivityOptions = financialActivityOptions;
    }

    public Map<String, List<GLAccountData>> getAccountingMappingOptions() {
        return this.glAccountOptions;
    }

    public void setAccountingMappingOptions(final Map<String, List<GLAccountData>> accountingMappingOptions) {
        this.glAccountOptions = accountingMappingOptions;
    }

    public GLAccountData getGlAccountData() {
        return this.glAccountData;
    }

    public FinancialActivityData getFinancialActivityData() {
        return this.financialActivityData;
    }

    public Long getId() {
        return this.id;
    }

}
