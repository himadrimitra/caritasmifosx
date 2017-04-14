/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.data;

import org.joda.time.LocalDate;

public class AdjustedDateDetailsDTO {

    /**
     * Variable tracks the current schedule date that has been changed
     */
    LocalDate changedScheduleDate;
    /**
     * Variable tracks If the meeting has been changed , i.e future schedule
     * also changes along with the current repayments date.
     */
    LocalDate changedActualRepaymentDate;

    /**
     * Variable tracks the next repayment period due date
     */
    LocalDate nextRepaymentPeriodDueDate;

    public AdjustedDateDetailsDTO(final LocalDate changedScheduleDate, final LocalDate changedActualRepaymentDate) {
        this.changedScheduleDate = changedScheduleDate;
        this.changedActualRepaymentDate = changedActualRepaymentDate;
    }

    public AdjustedDateDetailsDTO(final LocalDate changedScheduleDate, final LocalDate changedActualRepaymentDate,
            final LocalDate nextRepaymentPeriodDueDate) {
        this.changedScheduleDate = changedScheduleDate;
        this.changedActualRepaymentDate = changedActualRepaymentDate;
        this.nextRepaymentPeriodDueDate = nextRepaymentPeriodDueDate;
    }

    public LocalDate getChangedScheduleDate() {
        return this.changedScheduleDate;
    }

    public LocalDate getChangedActualRepaymentDate() {
        return this.changedActualRepaymentDate;
    }

    public void setChangedScheduleDate(final LocalDate changedScheduleDate) {
        this.changedScheduleDate = changedScheduleDate;
    }

    public void setChangedActualRepaymentDate(final LocalDate changedActualRepaymentDate) {
        this.changedActualRepaymentDate = changedActualRepaymentDate;
    }

    public LocalDate getNextRepaymentPeriodDueDate() {
        return this.nextRepaymentPeriodDueDate;
    }

    public void setNextRepaymentPeriodDueDate(final LocalDate nextRepaymentPeriodDueDate) {
        this.nextRepaymentPeriodDueDate = nextRepaymentPeriodDueDate;
    }
}