package org.apache.fineract.portfolio.cgt.data;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.cgt.domain.CgtDay;
import org.apache.fineract.portfolio.cgt.domain.CgtDayStatusType;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.LocalDate;

public class CgtDayData {

    private final Long id;
    private final Long cgtId;
    private final String cgtDayName;
    private String location;
    private Collection<NoteData> note;
    private final LocalDate scheduledDate;
    private final LocalDate completedDate;
    private final CgtDayStatusType status;
    private final LocalDate createdDate;
    private final LocalDate updatedDate;
    private final AppUserData createdBy;
    private final AppUserData updatedBy;
    private final StaffData loanOfficer;
    private final Collection<StaffData> loanOfficerOptions;
    private final Collection<ClientData> clientMembers;
    private final List<EnumOptionData> attendanceTypeOptions;
    private final Collection<CgtDayClientData> cgtDayClientData;

    public CgtDayData(final Long id, final Long cgtId, final String cgtDayName, final String location, final Collection<NoteData> note,
            final LocalDate scheduledDate, final LocalDate completedDate, final CgtDayStatusType status, final LocalDate createdDate,
            final LocalDate updatedDate, final AppUserData createdBy, final AppUserData updatedBy, final StaffData loanOfficer,
            final Collection<StaffData> loanOfficerOptions, final Collection<ClientData> clientMembers,
            final List<EnumOptionData> attendanceTypeOptions, final Collection<CgtDayClientData> cgtDayClientData) {
        this.id = id;
        this.cgtId = cgtId;
        this.cgtDayName = cgtDayName;
        this.location = location;
        this.note = note;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.loanOfficer = loanOfficer;
        this.loanOfficerOptions = loanOfficerOptions;
        this.clientMembers = clientMembers;
        this.attendanceTypeOptions = attendanceTypeOptions;
        this.cgtDayClientData = cgtDayClientData;
    }

    public static CgtDayData retriveTemplate(final String location, final LocalDate scheduledDate, final StaffData loanOfficer,
            final Collection<ClientData> clientMembers, final List<EnumOptionData> attendanceTypeOptions,
            final Collection<StaffData> loanOfficerOptions) {

        final Long id = null;
        final Long cgtId = null;
        String cgtDayName = null;
        Collection<NoteData> note = null;
        final LocalDate completedDate = null;
        final CgtDayStatusType status = null;
        final LocalDate createdDate = null;
        final LocalDate updatedDate = null;
        final AppUserData createdBy = null;
        final AppUserData updatedBy = null;
        final Collection<CgtDayClientData> cgtDayClientData = null;

        return new CgtDayData(id, cgtId, cgtDayName, location, note, scheduledDate, completedDate, status, createdDate, updatedDate,
                createdBy, updatedBy, loanOfficer, loanOfficerOptions, clientMembers, attendanceTypeOptions, cgtDayClientData);

    }

    public static CgtDayData retriveCgtRelatedDays(final String cgtDayName, final LocalDate scheduledDate, final LocalDate completedDate,
            final StaffData loanOfficer, final String location, final CgtDayStatusType status) {

        final Long id = null;
        final Long cgtId = null;
        Collection<NoteData> note = null;
        final LocalDate createdDate = null;
        final LocalDate updatedDate = null;
        final AppUserData createdBy = null;
        final AppUserData updatedBy = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<ClientData> clientMembers = null;
        final List<EnumOptionData> attendanceTypeOptions = null;
        final Collection<CgtDayClientData> cgtDayClientData = null;

        return new CgtDayData(id, cgtId, cgtDayName, location, note, scheduledDate, completedDate, status, createdDate, updatedDate,
                createdBy, updatedBy, loanOfficer, loanOfficerOptions, clientMembers, attendanceTypeOptions, cgtDayClientData);

    }

    public static CgtDayData retriveCgtDayDataFromEntity(final CgtDay cgtDay, Collection<NoteData> cgtDayNoteData) {

        final Long id = cgtDay.getId();
        final Long cgtId = null;
        final LocalDate createdDate = null;
        final LocalDate updatedDate = null;
        final AppUserData createdBy = null;
        final AppUserData updatedBy = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<ClientData> clientMembers = null;
        final List<EnumOptionData> attendanceTypeOptions = null;
        final StaffData cgtDayprimaryLoanOfficer = StaffData.lookup(cgtDay.getLoanOfficer().getId(), cgtDay.getLoanOfficer()
                .getDisplayName());
        final LocalDate scheduledDate = new LocalDate(cgtDay.getScheduledDate());
        LocalDate completedDate = null;
        if (cgtDay.isCompleted()) {
            completedDate = new LocalDate(cgtDay.getCompletedDate());
        }
        final Collection<CgtDayClientData> cgtDayClientData = null;

        return new CgtDayData(id, cgtId, cgtDay.getCgtDayName(), cgtDay.getLocation(), cgtDayNoteData, scheduledDate, completedDate,
                CgtDayStatusType.fromInt(cgtDay.getCgtDayStatus()), createdDate, updatedDate, createdBy, updatedBy,
                cgtDayprimaryLoanOfficer, loanOfficerOptions, clientMembers, attendanceTypeOptions, cgtDayClientData);

    }

}
