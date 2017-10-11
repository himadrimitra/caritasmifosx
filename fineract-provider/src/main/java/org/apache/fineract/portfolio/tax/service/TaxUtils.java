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
package org.apache.fineract.portfolio.tax.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.tax.data.TaxComponentData;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.apache.fineract.portfolio.tax.data.TaxGroupMappingsData;
import org.apache.fineract.portfolio.tax.domain.TaxComponent;
import org.apache.fineract.portfolio.tax.domain.TaxGroupMappings;
import org.joda.time.LocalDate;

public class TaxUtils {

    public static Map<TaxComponent, BigDecimal> splitTax(final BigDecimal amount, final LocalDate date,
            final List<TaxGroupMappings> taxGroupMappings, final int scale) {
        Map<TaxComponent, BigDecimal> map = new HashMap<>(3);
        if (amount != null) {
            final double amountVal = amount.doubleValue();
            double cent_percentage = Double.valueOf("100.0");
            for (TaxGroupMappings groupMappings : taxGroupMappings) {
                if (groupMappings.occursOnDayFromAndUpToAndIncluding(date)) {
                    TaxComponent component = groupMappings.getTaxComponent();
                    BigDecimal percentage = component.getApplicablePercentage(date);
                    if (percentage != null) {
                        double percentageVal = percentage.doubleValue();
                        double tax = amountVal * percentageVal / cent_percentage;
                        map.put(component, BigDecimal.valueOf(tax).setScale(scale, MoneyHelper.getRoundingMode()));
                    }
                }
            }
        }
        return map;
    }

    public static BigDecimal incomeAmount(final BigDecimal amount, final LocalDate date, final List<TaxGroupMappings> taxGroupMappings,
            final int scale) {
        Map<TaxComponent, BigDecimal> map = splitTax(amount, date, taxGroupMappings, scale);
        return incomeAmount(amount, map);
    }

    public static BigDecimal incomeAmount(final BigDecimal amount, final Map<TaxComponent, BigDecimal> map) {
        BigDecimal totalTax = totalTaxAmount(map);
        return amount.subtract(totalTax);
    }

    public static BigDecimal totalTaxAmount(final Map<TaxComponent, BigDecimal> map) {
        BigDecimal totalTax = BigDecimal.ZERO;
        for (final BigDecimal tax : map.values()) {
            totalTax = totalTax.add(tax);
        }
        return totalTax;
    }

    public static BigDecimal addTax(final BigDecimal amount, final LocalDate date, final List<TaxGroupMappings> taxGroupMappings,
            final int scale) {
        BigDecimal totalAmount = null;
        if (amount != null && amount.compareTo(BigDecimal.ZERO) == 1) {
            double percentageVal = 0;
            double amountVal = amount.doubleValue();
            double cent_percentage = Double.valueOf("100.0");
            for (TaxGroupMappings groupMappings : taxGroupMappings) {
                if (groupMappings.occursOnDayFromAndUpToAndIncluding(date)) {
                    TaxComponent component = groupMappings.getTaxComponent();
                    BigDecimal percentage = component.getApplicablePercentage(date);
                    if (percentage != null) {
                        percentageVal = percentageVal + percentage.doubleValue();
                    }
                }
            }
            double total = amountVal * cent_percentage / (cent_percentage - percentageVal);
            totalAmount = BigDecimal.valueOf(total).setScale(scale, MoneyHelper.getRoundingMode());
        }
        return totalAmount;
    }
    
    public static Map<TaxComponentData, BigDecimal> splitTaxForLoanCharge(final BigDecimal amount, final LocalDate date,
            final TaxGroupData taxGroupData, final int scale) {
        final Map<TaxComponentData, BigDecimal> map = new HashMap<>(3);
        if (amount != null) {
            final double cent_percentage = Double.valueOf("100.0");
            final double chargeWithoutTax = getChargeWithoutTax(amount, date, taxGroupData.getTaxAssociations());
            for (final TaxGroupMappingsData groupMappings : taxGroupData.getTaxAssociations()) {
                if (groupMappings.occursOnDayFromAndUpToAndIncluding(date)) {
                    final TaxComponentData component = groupMappings.getTaxComponent();
                    final BigDecimal percentage = component.getApplicablePercentage(date);
                    if (percentage != null) {
                        final double percentageVal = percentage.doubleValue();
                        final double tax = chargeWithoutTax * (percentageVal / cent_percentage);
                        map.put(component, BigDecimal.valueOf(tax).setScale(scale, MoneyHelper.getRoundingMode()));
                    }
                }
            }
        }
        return map;
    }

    private static double getChargeWithoutTax(final BigDecimal amount, final LocalDate date,
            final Collection<TaxGroupMappingsData> taxGroupMappingsData) {
        double cent_percentage = Double.valueOf("100.0");
        BigDecimal percent = new BigDecimal("100.0");
        final double amountVal = amount.doubleValue();
        double feeAmount = 0;
        for (final TaxGroupMappingsData groupMappings : taxGroupMappingsData) {
            if (groupMappings.occursOnDayFromAndUpToAndIncluding(date)) {
                final TaxComponentData component = groupMappings.getTaxComponent();
                final BigDecimal percentage = component.getApplicablePercentage(date);
                if (percentage != null) {
                    percent = percent.add(percentage);
                }
            }
        }
        feeAmount = (amountVal * cent_percentage) / percent.doubleValue();

        return feeAmount;
    }

    public static Map<TaxComponent, BigDecimal> splitTaxForLoanCharge(final BigDecimal amount, final LocalDate date,
            final List<TaxGroupMappings> taxGroupMappings, final int scale) {
        final Map<TaxComponent, BigDecimal> map = new HashMap<>(3);
        if (amount != null) {
            final double cent_percentage = Double.valueOf("100.0");
            final double chargeWithoutTax = getChargeWithoutTax(amount, date, taxGroupMappings);
            for (final TaxGroupMappings groupMappings : taxGroupMappings) {
                if (groupMappings.occursOnDayFromAndUpToAndIncluding(date)) {
                    final TaxComponent component = groupMappings.getTaxComponent();
                    final BigDecimal percentage = component.getApplicablePercentage(date);
                    if (percentage != null) {
                        final double percentageVal = percentage.doubleValue();
                        final double tax = chargeWithoutTax * (percentageVal / cent_percentage);
                        map.put(component, BigDecimal.valueOf(tax).setScale(scale, MoneyHelper.getRoundingMode()));
                    }
                }
            }
        }
        return map;
    }
    
    private static double getChargeWithoutTax(final BigDecimal amount, final LocalDate date, final List<TaxGroupMappings> taxGroupMappings) {
        double cent_percentage = Double.valueOf("100.0");
        BigDecimal percent = new BigDecimal("100.0");
        final double amountVal = amount.doubleValue();
        double feeAmount = 0;
        for (final TaxGroupMappings groupMappings : taxGroupMappings) {
            if (groupMappings.occursOnDayFromAndUpToAndIncluding(date)) {
                final TaxComponent component = groupMappings.getTaxComponent();
                final BigDecimal percentage = component.getApplicablePercentage(date);
                if (percentage != null) {
                    percent = percent.add(percentage);
                }
            }
        }
        feeAmount = (amountVal * cent_percentage) / percent.doubleValue();

        return feeAmount;
    }

    public static BigDecimal totalTaxAmountData(final Map<TaxComponentData, BigDecimal> map) {
        BigDecimal totalTax = BigDecimal.ZERO;
        for (final BigDecimal tax : map.values()) {
            totalTax = totalTax.add(tax);
        }
        return totalTax;
    }
    
}
