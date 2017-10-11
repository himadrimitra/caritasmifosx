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
package org.apache.fineract.portfolio.tax.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

public class TaxComponentData {

    private final Long id;
    private final String name;
    private final BigDecimal percentage;
    private final EnumOptionData debitAccountType;
    private final GLAccountData debitAccount;
    private final EnumOptionData creditAccountType;
    private final GLAccountData creditAccount;
    private final LocalDate startDate;
    private final Collection<TaxComponentHistoryData> taxComponentHistories;

    // template options
    private final Map<String, List<GLAccountData>> glAccountOptions;
    private final Collection<EnumOptionData> glAccountTypeOptions;

    public static TaxComponentData instance(final Long id, final String name, final BigDecimal percentage,
            final EnumOptionData debitAccountType, final GLAccountData debitAcount, final EnumOptionData creditAccountType,
            final GLAccountData creditAcount, final LocalDate startDate, final Collection<TaxComponentHistoryData> taxComponentHistories) {
        final Map<String, List<GLAccountData>> glAccountOptions = null;
        final Collection<EnumOptionData> glAccountTypeOptions = null;
        return new TaxComponentData(id, name, percentage, debitAccountType, debitAcount, creditAccountType, creditAcount, startDate,
                taxComponentHistories, glAccountOptions, glAccountTypeOptions);
    }

    public static TaxComponentData lookup(final Long id, final String name) {
        final BigDecimal percentage = null;
        final EnumOptionData debitAccountType = null;
        final GLAccountData debitAcount = null;
        final EnumOptionData creditAccountType = null;
        final GLAccountData creditAcount = null;
        final LocalDate startDate = null;
        final Collection<TaxComponentHistoryData> taxComponentHistories = null;
        final Map<String, List<GLAccountData>> glAccountOptions = null;
        final Collection<EnumOptionData> glAccountTypeOptions = null;
        return new TaxComponentData(id, name, percentage, debitAccountType, debitAcount, creditAccountType, creditAcount, startDate,
                taxComponentHistories, glAccountOptions, glAccountTypeOptions);
    }

    public static TaxComponentData template(final Map<String, List<GLAccountData>> glAccountOptions,
            final Collection<EnumOptionData> glAccountTypeOptions) {
        final Long id = null;
        final String name = null;
        final BigDecimal percentage = null;
        final EnumOptionData debitAccountType = null;
        final GLAccountData debitAcount = null;
        final EnumOptionData creditAccountType = null;
        final GLAccountData creditAcount = null;
        final LocalDate startDate = null;
        final Collection<TaxComponentHistoryData> taxComponentHistories = null;
        return new TaxComponentData(id, name, percentage, debitAccountType, debitAcount, creditAccountType, creditAcount, startDate,
                taxComponentHistories, glAccountOptions, glAccountTypeOptions);
    }

    private TaxComponentData(final Long id, final String name, final BigDecimal percentage, final EnumOptionData debitAccountType,
            final GLAccountData debitAcount, final EnumOptionData creditAccountType, final GLAccountData creditAcount,
            final LocalDate startDate, final Collection<TaxComponentHistoryData> taxComponentHistories,
            final Map<String, List<GLAccountData>> glAccountOptions, final Collection<EnumOptionData> glAccountTypeOptions) {
        this.id = id;
        this.percentage = percentage;
        this.name = name;
        this.debitAccountType = debitAccountType;
        this.debitAccount = debitAcount;
        this.creditAccountType = creditAccountType;
        this.creditAccount = creditAcount;
        this.startDate = startDate;
        this.taxComponentHistories = taxComponentHistories;
        this.glAccountOptions = glAccountOptions;
        this.glAccountTypeOptions = glAccountTypeOptions;
    }

    public BigDecimal getApplicablePercentage(final LocalDate date) {
        BigDecimal percentage = null;
        if (occursOnDayFrom(date)) {
            percentage = getPercentage();
        } else {
            for (final TaxComponentHistoryData componentHistory : this.taxComponentHistories) {
                if (componentHistory.occursOnDayFromAndUpToAndIncluding(date)) {
                    percentage = componentHistory.getPercentage();
                    break;
                }
            }
        }
        return percentage;
    }

    public BigDecimal getPercentage() {
        return this.percentage;
    }

    private boolean occursOnDayFrom(final LocalDate target) {
        return target != null && target.isAfter(this.startDate);
    }

    public Long getId() {
        return this.id;
    }

    public static TaxComponentData lookup(final Long id, final String name, final LocalDate startDate, final BigDecimal percentage) {
        final EnumOptionData debitAccountType = null;
        final GLAccountData debitAcount = null;
        final EnumOptionData creditAccountType = null;
        final GLAccountData creditAcount = null;
        final Collection<TaxComponentHistoryData> taxComponentHistories = null;
        final Map<String, List<GLAccountData>> glAccountOptions = null;
        final Collection<EnumOptionData> glAccountTypeOptions = null;
        return new TaxComponentData(id, name, percentage, debitAccountType, debitAcount, creditAccountType, creditAcount, startDate,
                taxComponentHistories, glAccountOptions, glAccountTypeOptions);
    }

    public String getName() {
        return this.name;
    }

    public EnumOptionData getDebitAccountType() {
        return this.debitAccountType;
    }

    public GLAccountData getDebitAccount() {
        return this.debitAccount;
    }

    public EnumOptionData getCreditAccountType() {
        return this.creditAccountType;
    }

    public GLAccountData getCreditAccount() {
        return this.creditAccount;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Collection<TaxComponentHistoryData> getTaxComponentHistories() {
        return this.taxComponentHistories;
    }

    public Map<String, List<GLAccountData>> getGlAccountOptions() {
        return this.glAccountOptions;
    }

    public Collection<EnumOptionData> getGlAccountTypeOptions() {
        return this.glAccountTypeOptions;
    }

}
