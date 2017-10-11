package org.apache.fineract.portfolio.cgt.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.cgt.api.CgtApiConstants;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;

import com.google.gson.JsonArray;

@Entity
@Table(name = "f_cgt")
public class Cgt extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "unique_id", length = 50, unique = true)
    private String uniqueId;

    @Column(name = "expected_start_date")
    @Temporal(TemporalType.DATE)
    private Date expectedStartDate;

    @Column(name = "expected_end_date")
    @Temporal(TemporalType.DATE)
    private Date expectedEndDate;

    @Column(name = "actual_start_date")
    @Temporal(TemporalType.DATE)
    private Date actualStartDate;

    @Column(name = "actual_end_date")
    @Temporal(TemporalType.DATE)
    private Date actualEndDate;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name = "f_cgt_client", joinColumns = @JoinColumn(name = "cgt_id"), inverseJoinColumns = @JoinColumn(name = "client_id"))
    private Set<Client> clientMembers = new HashSet<>();

    @Column(name = "entity_type")
    private Integer entityType;

    @Column(name = "entity_type_Id")
    private Integer entityTypeId;

    @Column(name = "cgt_status", nullable = false)
    private Integer cgtStatus;

    @Column(name = "location", length = 50)
    private String location;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_officer_id")
    private Staff loanOfficer;

    @OneToMany(mappedBy = "cgt", cascade = CascadeType.ALL)
    private Set<Note> notes;

    protected Cgt() {
        super();
    }

    private Cgt(final String uniqueId, final Date expectedStartDate, final Date expectedEndDate, final Date actualStartDate,
            final Date actualEndDate, final Set<Client> clientMembers, final Integer entityType, final Integer entityTypeId,
            final Integer cgtStatus, final String location, final Staff loanOfficer, final Set<Note> notes) {
        this.uniqueId = uniqueId;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.clientMembers = clientMembers;
        this.entityType = entityType;
        this.entityTypeId = entityTypeId;
        this.cgtStatus = cgtStatus;
        this.location = location;
        this.loanOfficer = loanOfficer;
        this.notes = notes;
        validate();
    }
    
	private void validate() {
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		validateExpectedStartDate(dataValidationErrors);
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}

	}

	private void validateExpectedStartDate(final List<ApiParameterError> dataValidationErrors) {
		if (getExpectedEndDate() != null && getExpectedStartDate() != null
				&& getExpectedStartDate().after(getExpectedEndDate())) {

			final String defaultUserMessage = "expected end date cannot be before the expected start date";
			final ApiParameterError error = ApiParameterError.parameterError(
					"error.msg.cgt.expected.start.date.cannot.be.after.the.expected.end.date", defaultUserMessage,
					CgtApiConstants.expectedEndDateParamName, this.expectedEndDate);

			dataValidationErrors.add(error);
		}
	}

    public static Cgt newCgt(final String uniqueId, final Date expectedStartDate, final Date expectedEndDate, final Date actualStartDate,
            final Set<Client> clientMembers, final Integer entityType, final Integer entityTypeId, final Integer cgtStatus,
            final String location, final Staff loanOfficer, Set<Note> notes) {

        final Date actualEndDate = null;
        return new Cgt(uniqueId, expectedStartDate, expectedEndDate, actualStartDate, actualEndDate, clientMembers, entityType,
                entityTypeId, cgtStatus, location, loanOfficer, notes);

    }

    public Map<String, Object> updateCgt(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        if (command.isChangeInStringParameterNamed(CgtApiConstants.locationParamName, this.location)) {
            final String newValue = command.stringValueOfParameterNamed(CgtApiConstants.locationParamName);
            actualChanges.put(CgtApiConstants.locationParamName, newValue);
            this.location = newValue;
        }

        if (command.isChangeInLongParameterNamed(CgtApiConstants.loanOfficerIdParamName, this.loanOfficer.getId())) {
            final Long newValue = command.longValueOfParameterNamed(CgtApiConstants.loanOfficerIdParamName);
            actualChanges.put(CgtApiConstants.loanOfficerIdParamName, newValue);
        }

        final JsonArray clientIds = command.arrayOfParameterNamed(CgtApiConstants.clientIdsParamName);
        if (clientIds.size() != 0) {
            final boolean isChangeInClients = command.isChangeInArrayParameterNamed(CgtApiConstants.clientIdsParamName, this
                    .getAssociatedClientIds().toArray(new String[this.getAssociatedClientIds().size()]));
            if (isChangeInClients) actualChanges.put(CgtApiConstants.clientIdsParamName, clientIds);
        }

        if (command.isChangeInDateParameterNamed(CgtApiConstants.expectedStartDateParamName, this.expectedStartDate)) {
            final Date newValue = command.DateValueOfParameterNamed(CgtApiConstants.expectedStartDateParamName);
            actualChanges.put(CgtApiConstants.expectedStartDateParamName, newValue);
            this.expectedStartDate = newValue;
        }

        if (command.isChangeInDateParameterNamed(CgtApiConstants.expectedEndDateParamName, this.expectedEndDate)) {
            final Date newValue = command.DateValueOfParameterNamed(CgtApiConstants.expectedEndDateParamName);
            actualChanges.put(CgtApiConstants.expectedEndDateParamName, newValue);
            this.expectedEndDate = newValue;
        }

        return actualChanges;
    }

    public void updateLoanOfficer(final Staff loanOStaff) {
        this.loanOfficer = loanOStaff;
    }

    public void updateAssociatedClients(final Set<Client> newClients) {
        this.clientMembers = newClients;
    }

    private Set<String> getAssociatedClientIds() {
        Set<String> associatedClientIds = new HashSet<>(this.clientMembers.size());
        for (Client client : this.clientMembers) {
            associatedClientIds.add(client.getId().toString());
        }
        return associatedClientIds;
    }

    public Map<String, Object> rejectCgt(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>();
        if (this.cgtStatus != CgtStatusType.REJECT.getValue()) {
            this.cgtStatus = CgtStatusType.REJECT.getValue();
            actualChanges.put(CgtApiConstants.cgtStatusParamName, CgtStatusType.REJECT.getCode());
        }
        if (command.isChangeInDateParameterNamed(CgtApiConstants.rejectedDateParamName, this.actualEndDate)) {
            final Date newValue = command.DateValueOfParameterNamed(CgtApiConstants.rejectedDateParamName);
            actualChanges.put(CgtApiConstants.rejectedDateParamName, newValue);
            this.actualEndDate = newValue;
        }
        return actualChanges;
    }

    public Map<String, Object> completeCgt(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>();
        if (this.cgtStatus.compareTo(CgtStatusType.COMPLETE.getValue()) != 0) {
            this.cgtStatus = CgtStatusType.COMPLETE.getValue();
            actualChanges.put(CgtApiConstants.cgtStatusParamName, CgtStatusType.COMPLETE.getCode());
        }
        if (command.isChangeInDateParameterNamed(CgtApiConstants.completedDateParamName, this.actualEndDate)) {
            final Date newValue = command.DateValueOfParameterNamed(CgtApiConstants.completedDateParamName);
            actualChanges.put(CgtApiConstants.completedDateParamName, newValue);
            this.actualEndDate = newValue;
        }
        return actualChanges;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public Date getExpectedStartDate() {
        return this.expectedStartDate;
    }

    public Date getExpectedEndDate() {
        return this.expectedEndDate;
    }

	public LocalDate getActualStartLocalDate() {
		if (this.actualStartDate != null) {
			return new LocalDate(this.actualStartDate);
		}
		return null;
	}

	public LocalDate getActualEndLocalDate() {
		if (this.actualEndDate != null) {
			return new LocalDate(this.actualEndDate);
		}
		return null;
	}

    public Set<Client> getClientMembers() {
        return this.clientMembers;
    }

    public Integer getEntityType() {
        return this.entityType;
    }

    public Integer getEntityTypeId() {
        return this.entityTypeId;
    }

    public Integer getCgtStatus() {
        return this.cgtStatus;
    }

    public String getLocation() {
        return this.location;
    }

    public Staff getLoanOfficer() {
        return this.loanOfficer;
    }

    public Set<Note> getNotes() {
        return this.notes;
    }

    public void updateNotes(final Note note) {
        this.notes.add(note);
    }

    public void updateCgtStatus(final Integer cgtStatus) {
        this.cgtStatus = cgtStatus;
    }
    
    public void updateActualStartDate(final Date actualStartDate){
    	this.actualStartDate = actualStartDate;
    }

}
