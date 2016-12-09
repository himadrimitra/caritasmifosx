package org.apache.fineract.portfolio.cgt.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.cgt.api.CgtDayApiConstants;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.useradministration.domain.AppUser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Entity
@Table(name = "f_cgt_Day")
public class CgtDay extends AbstractAuditableCustom<AppUser, Long> {

    @OneToMany(mappedBy = "cgtDay", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CgtDayClient> cgtDayClient = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cgt_id")
    private Cgt cgt;

    @Column(name = "scheduled_date")
    @Temporal(TemporalType.DATE)
    private Date scheduledDate;

    @Column(name = "completed_date")
    @Temporal(TemporalType.DATE)
    private Date completedDate;

    @Column(name = "cgt_day_name", nullable = false)
    private String cgtDayName;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_officer_id")
    private Staff loanOfficer;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "cgt_Day_status", nullable = false)
    private Integer cgtDayStatus;

    @OneToMany(mappedBy = "cgtDay", fetch = FetchType.EAGER)
    private Collection<Note> notes;

    protected CgtDay() {
        super();
    }

    private CgtDay(final Cgt cgt, final Date scheduledDate, final Date completedDate, final String cgtDayName, final Staff loanOfficer,
            final String location, final Integer cgtDayStatus, final Collection<Note> notes) {
        this.cgt = cgt;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.cgtDayName = cgtDayName;
        this.loanOfficer = loanOfficer;
        this.location = location;
        this.cgtDayStatus = cgtDayStatus;
        this.notes = notes;
    }

    public static CgtDay newCgtDay(final Cgt cgt, final Date scheduledDate, final Date completedDate, final String cgtDayName,
            final Staff loanOfficer, final String location, final Integer cgtDayStatus, final Collection<Note> notes) {

        return new CgtDay(cgt, scheduledDate, completedDate, cgtDayName, loanOfficer, location, cgtDayStatus, notes);
    }

    public Map<String, Object> updateCgtDay(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        if (command.isChangeInStringParameterNamed(CgtDayApiConstants.locationParamName, this.location)) {
            final String newValue = command.stringValueOfParameterNamed(CgtDayApiConstants.locationParamName);
            actualChanges.put(CgtDayApiConstants.locationParamName, newValue);
            this.location = newValue;
        }

        if (this.loanOfficer == null
                || command.isChangeInLongParameterNamed(CgtDayApiConstants.loanOfficerIdParamName, this.loanOfficer.getId())) {
            final Long newValue = command.longValueOfParameterNamed(CgtDayApiConstants.loanOfficerIdParamName);
            actualChanges.put(CgtDayApiConstants.loanOfficerIdParamName, newValue);
        }

        if (command.isChangeInDateParameterNamed(CgtDayApiConstants.scheduledDateParamName, this.scheduledDate)) {
            final Date newValue = command.DateValueOfParameterNamed(CgtDayApiConstants.scheduledDateParamName);
            actualChanges.put(CgtDayApiConstants.scheduledDateParamName, newValue);
            this.scheduledDate = newValue;
        }

        return actualChanges;
    }

    public Map<String, Object> completeCgtDay(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        if (this.cgtDayStatus.compareTo(CgtDayStatusType.COMPLETE.getValue()) != 0) {
            this.cgtDayStatus = CgtDayStatusType.COMPLETE.getValue();
            actualChanges.put(CgtDayApiConstants.cgtDayStatusParamName, CgtDayStatusType.COMPLETE.getCode());
        }
        if (command.isChangeInDateParameterNamed(CgtDayApiConstants.completedDateParamName, this.completedDate)) {
            final Date newValue = command.DateValueOfParameterNamed(CgtDayApiConstants.completedDateParamName);
            actualChanges.put(CgtDayApiConstants.completedDateParamName, newValue);
            this.completedDate = newValue;
        }

        if (command.hasParameter(CgtDayApiConstants.clientIdsParamName)) {
            JsonArray clientIds = command.arrayOfParameterNamed(CgtDayApiConstants.clientIdsParamName);
            Set<Long> absentClients = new HashSet<>(clientIds.size());
            for (JsonElement client : clientIds) {
                final Long clientAttendance = client.getAsJsonObject().get(CgtDayApiConstants.attendanceTypeParamName).getAsLong();
                if (CgtDayClientAttendanceStatusType.fromInt(clientAttendance.intValue()).isAbsent()) {
                    absentClients.add(client.getAsJsonObject().get(CgtDayApiConstants.idParamName).getAsLong());
                }
            }
            for (CgtDayClient absent : this.cgtDayClient) {
                if (absentClients.contains(absent.getClient().getId())) {
                    absent.updateAttendance(CgtDayClientAttendanceStatusType.ABSENT.getValue());
                }
            }
        }

        return actualChanges;
    }

    public Set<CgtDayClient> getCgtDayClient() {
        return this.cgtDayClient;
    }

    public Cgt getCgt() {
        return this.cgt;
    }

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public Date getCompletedDate() {
        return this.completedDate;
    }

    public String getCgtDayName() {
        return this.cgtDayName;
    }

    public Staff getLoanOfficer() {
        return this.loanOfficer;
    }

    public String getLocation() {
        return this.location;
    }

    public Integer getCgtDayStatus() {
        return this.cgtDayStatus;
    }

    public void updateLoanOfficer(final Staff loanOStaff) {
        this.loanOfficer = loanOStaff;
    }

    public Collection<Note> getNotes() {
        return this.notes;
    }

    public void updateCgtDayClient(Set<CgtDayClient> cgtDayClient) {
        this.cgtDayClient = cgtDayClient;
    }

    public boolean isCompleted() {
        boolean isCompleted = false;
        if (CgtDayStatusType.fromInt(this.cgtDayStatus.intValue()).isComplete()) {
            isCompleted = true;
        }
        return isCompleted;
    }

}
