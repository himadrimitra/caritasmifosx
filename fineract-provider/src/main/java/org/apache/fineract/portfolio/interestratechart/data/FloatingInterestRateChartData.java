/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.interestratechart.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

/**
 * Immutable data object representing a InterestRateChart.
 */
public class FloatingInterestRateChartData {

    private final Long id;
    private final LocalDate effectiveFromDate;
    private final BigDecimal interestRate;

    public static FloatingInterestRateChartData instance(Long id, final LocalDate effectiveFromDate, final BigDecimal interestRate) {

        return new FloatingInterestRateChartData(id, effectiveFromDate, interestRate);
    }

    private FloatingInterestRateChartData(Long id, LocalDate effectiveFromDate, BigDecimal interestRate) {
        this.id = id;
        this.effectiveFromDate = effectiveFromDate;
        this.interestRate = interestRate;

    }

}