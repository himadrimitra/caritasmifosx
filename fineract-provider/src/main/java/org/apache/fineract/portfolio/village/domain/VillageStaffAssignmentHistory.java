/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.village.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;

@Entity
@Table(name = "f_village_staff_assignment_history")
public class VillageStaffAssignmentHistory extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    public static VillageStaffAssignmentHistory createNew(final Village village, final Staff staff, final LocalDate startDate) {
        return new VillageStaffAssignmentHistory(village, staff, startDate.toDate(), null);
    }

    protected VillageStaffAssignmentHistory() {
        //
    }

    private VillageStaffAssignmentHistory(final Village village, final Staff staff, final Date startDate, final Date endDate) {
        this.village = village;
        this.staff = staff;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateStaff(final Staff staff) {
        this.staff = staff;
    }

    public void updateStartDate(final LocalDate startDate) {
        this.startDate = startDate.toDate();
    }

    public void updateEndDate(final LocalDate endDate) {
        this.endDate = endDate.toDate();
    }

    public boolean matchesStartDateOf(final LocalDate matchingDate) {
        return getStartDate().isEqual(matchingDate);
    }

    public LocalDate getStartDate() {
        return new LocalDate(this.startDate);
    }

    public boolean hasStartDateBefore(final LocalDate matchingDate) {
        return matchingDate.isBefore(getStartDate());
    }

    public boolean isCurrentRecord() {
        return this.endDate == null;
    }

    /**
     * If endDate is null then return false.
     * 
     * @param compareDate
     * @return
     */
    public boolean isEndDateAfter(final LocalDate compareDate) {
        return this.endDate == null ? false : new LocalDate(this.endDate).isAfter(compareDate);
    }

    public LocalDate getEndDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.endDate), null);
    }

    public boolean isSameStaff(final Staff staff) {
        return this.staff.identifiedBy(staff);
    }
}