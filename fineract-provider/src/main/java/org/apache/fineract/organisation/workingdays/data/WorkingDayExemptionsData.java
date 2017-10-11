/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.data;

import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.workingdays.domain.ApplicableProperty;
import org.apache.fineract.organisation.workingdays.domain.RepaymentRescheduleType;
import org.apache.fineract.organisation.workingdays.domain.RepaymentScheduleUpdationType;

public class WorkingDayExemptionsData {

    // EntityAccountType.java
    private EnumOptionData protfolioType;

    // ApplicableProperty.java
    private EnumOptionData applicableProperty;

    private String expression;

    // RepaymentRescheduleType.java
    private EnumOptionData repaymentRescheduleType;

    // RepaymentScheduleUpdationType.java
    private EnumOptionData repaymentScheduleUpdationType;

    public WorkingDayExemptionsData(final EnumOptionData protfolioType, final EnumOptionData applicableProperty, final String expression,
            final EnumOptionData repaymentRescheduleType, final EnumOptionData repaymentScheduleUpdationType) {
        this.protfolioType = protfolioType;
        this.applicableProperty = applicableProperty;
        this.expression = expression;
        this.repaymentRescheduleType = repaymentRescheduleType;
        this.repaymentScheduleUpdationType = repaymentScheduleUpdationType;
    }

    public EntityAccountType getProtfolioType() {
        if (this.protfolioType != null) { return EntityAccountType.fromInt(this.protfolioType.getId().intValue()); }
        return null;
    }

    public void setProtfolioType(final EnumOptionData protfolioType) {
        this.protfolioType = protfolioType;
    }

    public ApplicableProperty getApplicableProperty() {
        if (this.applicableProperty != null) { return ApplicableProperty.fromInt(this.applicableProperty.getId().intValue()); }
        return null;
    }

    public void setApplicableProperty(final EnumOptionData applicableProperty) {
        this.applicableProperty = applicableProperty;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public RepaymentRescheduleType getRepaymentRescheduleType() {
        if (this.repaymentRescheduleType != null) { return RepaymentRescheduleType.fromInt(this.repaymentRescheduleType.getId().intValue()); }
        return null;
    }

    public void setRepaymentRescheduleType(final EnumOptionData repaymentRescheduleType) {
        this.repaymentRescheduleType = repaymentRescheduleType;
    }

    public RepaymentScheduleUpdationType getRepaymentScheduleUpdationType() {
        if (this.repaymentScheduleUpdationType != null) { return RepaymentScheduleUpdationType.fromInt(this.repaymentScheduleUpdationType
                .getId().intValue()); }
        return null;
    }

    public void setRepaymentScheduleUpdationType(final EnumOptionData repaymentScheduleUpdationType) {
        this.repaymentScheduleUpdationType = repaymentScheduleUpdationType;
    }
}