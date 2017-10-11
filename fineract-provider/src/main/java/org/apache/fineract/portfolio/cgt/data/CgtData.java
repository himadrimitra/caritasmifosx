package org.apache.fineract.portfolio.cgt.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.cgt.domain.Cgt;
import org.apache.fineract.portfolio.cgt.domain.CgtStatusType;
import org.apache.fineract.portfolio.cgt.domain.CgtTypes;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.joda.time.LocalDate;

public class CgtData {

    private final Long id;
    private final Integer entityId;
    private final CgtTypes entityType;
    private String uniqueId;
    private String location;
    private Collection<NoteData> note;
    private final LocalDate expectedStartDate;
    private final LocalDate expectedEndDate;
    private final LocalDate actualStartDate;
    private final LocalDate actualEndDate;
    private final EnumOptionData status;
    private final StaffData primaryLoanOfficer;
    private final Collection<StaffData> loanOfficerOptions;
    private final Collection<ClientData> clientMembers;
    private final Collection<CgtDayData> dayDatas;

    private CgtData(final Long id, final Integer entityId, final String uniqueId, final String location, final Collection<NoteData> note,
            final LocalDate expectedStartDate, final LocalDate expectedEndDate, final LocalDate actualStartDate,
            final LocalDate actualEndDate, final EnumOptionData status, final StaffData primaryLoanOfficer,
            final Collection<StaffData> loanOfficerOptions, final Collection<ClientData> clientMembers,
            final Collection<CgtDayData> dayDatas, final CgtTypes entityType) {
        this.id = id;
        this.entityId = entityId;
        this.entityType = entityType;
        this.uniqueId = uniqueId;
        this.location = location;
        this.note = note;
        this.status = status;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.primaryLoanOfficer = primaryLoanOfficer;
        this.loanOfficerOptions = loanOfficerOptions;
        this.clientMembers = clientMembers;
        this.dayDatas = dayDatas;
    }

    public static CgtData retriveTemplate(final StaffData primaryLoanOfficer, final Collection<ClientData> clientMembers,
            final Collection<StaffData> loanOfficerOptions) {

        final Long id = null;
        final Integer entityId = null;
        final CgtTypes entityType = null;
        String uniqueId = null;
        String location = null;
        final Collection<NoteData> note = null;
        final LocalDate expectedStartDate = null;
        final LocalDate expectedEndDate = null;
        final LocalDate actualStartDate = null;
        final LocalDate actualEndDate = null;
        final EnumOptionData status = null;
        final Collection<CgtDayData> dayDatas = null;

        return new CgtData(id, entityId, uniqueId, location, note, expectedStartDate, expectedEndDate, actualStartDate, actualEndDate,
                status, primaryLoanOfficer, loanOfficerOptions, clientMembers, dayDatas, entityType);
    }

    public static CgtData retriveCgtData(final Cgt cgt, final Collection<CgtDayData> CgtDayDatas, Collection<NoteData> cgtNoteData) {

        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<ClientData> clientMembers = null;
        final StaffData primaryLoanOfficer = StaffData.lookup(cgt.getLoanOfficer().getId(), cgt.getLoanOfficer().getDisplayName());
        final LocalDate expectedStartDate = new LocalDate(cgt.getExpectedStartDate());
        final LocalDate expectedEndDate = new LocalDate(cgt.getExpectedEndDate());
		LocalDate actualStartDate = cgt.getActualStartLocalDate();
		LocalDate actualEndDate = cgt.getActualEndLocalDate();
		 
       
        return new CgtData(cgt.getId(), cgt.getEntityTypeId(), cgt.getUniqueId(), cgt.getLocation(), cgtNoteData, expectedStartDate,
                expectedEndDate, actualStartDate, actualEndDate, CgtStatusType.CgtStatusTypeEnumDatafromInt(cgt.getCgtStatus()),
                primaryLoanOfficer, loanOfficerOptions, clientMembers, CgtDayDatas, CgtTypes.fromInt(cgt.getEntityType()));
    }

}
