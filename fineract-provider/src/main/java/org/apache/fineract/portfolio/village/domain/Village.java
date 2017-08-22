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
package org.apache.fineract.portfolio.village.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.village.api.VillageTypeApiConstants;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "chai_villages")
public class Village extends AbstractPersistable<Long> {

    @Column(name = "external_id")
    private String externalId;
    
    @ManyToOne
    @JoinColumn(name= "office_id")
    private Office officeId;
    
    @Column(name = "village_code")
    private String villageCode;
    
    @Column(name="village_name")
    private String villageName;
    
    @Column(name="counter")
    private Long count;
  
    @Column(name="activatedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;
    
    @ManyToOne(optional = true,fetch=FetchType.LAZY)
    @JoinColumn(name="activatedon_userid", nullable = true)
    private AppUser activedBy;
    
    @Column(name = "submitedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;
    
    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name="submitedon_userid", nullable = true)
    private AppUser submitedBy;
    
    @Column(name="status")
    private Integer status;
    
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinTable(name="chai_village_center", joinColumns= @JoinColumn(name="village_id"), inverseJoinColumns = @JoinColumn(name="center_id"))
    private Set<Group> centerMembers; 
    
    @ManyToOne
    @JoinColumn(name = "staff_id", nullable=true)
    private Staff staff;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "village", fetch=FetchType.LAZY)
    private Set<VillageStaffAssignmentHistory> villageStaffHistory = new HashSet<>();
    
    public Village() {
    }

    public static Village newVillage(final Office office, final String villageName, final Long count, final AppUser currentUser,
            final boolean active, final LocalDate activationDate, final LocalDate submittedOnDate, final JsonCommand command){
        
        final String externalId = command.stringValueOfParameterNamed(VillageTypeApiConstants.externalIdParamName);
        final String villageCode = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageCodeParamName);
       
        VillageTypeStatus status = VillageTypeStatus.PENDING;
        LocalDate villageActivaionDate = null;
        if (active) {
            status = VillageTypeStatus.ACTIVE;
            villageActivaionDate = activationDate;
        }
        
        return new Village(externalId, office, villageCode, villageName, count, currentUser, status, villageActivaionDate, submittedOnDate);
    }
    
    private Village(final String externalId, final Office office,final String villageCode, final String villageName, final Long count,
             final AppUser currentUser, final VillageTypeStatus status,
            final LocalDate activationDate, final LocalDate submittedOnDate){
       
        final List<ApiParameterError> dataValidationErorrs = new ArrayList<>();

        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId;
        }else {
            this.externalId = null;
        }
        this.officeId = office;
        this.villageCode = villageCode;
        this.villageName = villageName;
        this.count = count;
        this.villageStaffHistory = new HashSet<>();
        this.activedBy = currentUser;
        this.submittedOnDate = submittedOnDate.toDate();
        this.submitedBy = currentUser;
        
        setStatus(activationDate, currentUser, status, dataValidationErorrs);
        
        throwExceptionIfErrors(dataValidationErorrs);
    }
    
    public void setCenter(final Group centerDetails){
        this.centerMembers = new HashSet<>();
        this.centerMembers.add(centerDetails);
    }
    
    private void setStatus(final LocalDate activationDate, final AppUser loginUser, final VillageTypeStatus status, List<ApiParameterError> dataValidationErrors){
        
        if (status.isActive()) {
            activate(loginUser, activationDate, dataValidationErrors);
        }else{
            this.status = status.getValue();
        }
    }
    
    private void activate(final AppUser currentUser, final LocalDate activationLocalDate, final List<ApiParameterError> dataValidationErrors){
        validateStatusNotEqualToActiveAndLogError(dataValidationErrors);
        if (dataValidationErrors.isEmpty()) {
            this.status = VillageTypeStatus.ACTIVE.getValue();
            setActivationDate(activationLocalDate.toDate(), currentUser, dataValidationErrors);
        }
    }
    
    public void activate(final AppUser currentUser, final LocalDate activationLocalDate){
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        activate(currentUser, activationLocalDate, dataValidationErrors);
        
        throwExceptionIfErrors(dataValidationErrors);
    }
    
    private void setActivationDate(final Date activationDate, final AppUser loginUser, final List<ApiParameterError> dataValidationErrors){
        if (activationDate != null) {
            this.activationDate = activationDate;
            this.activedBy = loginUser;
        }
        
        validateActivationDate(dataValidationErrors);
    }
    
    public LocalDate getSubmittedOnDate(){
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);
    }
    
    public LocalDate getActivationLocalDate(){
        LocalDate activationLocalDate = null;
        if (this.activationDate != null) {
            activationLocalDate = new LocalDate(this.activationDate);
        }
        return activationLocalDate;
    }
    
    private boolean isDateInTheFuture(final LocalDate localDate){
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }
    
    public boolean isActive() {
        return this.status != null ? VillageTypeStatus.fromInt(this.status).isActive() : false; 
    }
    
    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors){
        
        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {
            
            final String defaultUserMessage = "Submitted on date cannot be in the future.";
            final String globalisationMessageCode = "error.message.village.submittedOnDate.in.the.future";
            final ApiParameterError error = ApiParameterError.parameterError(globalisationMessageCode, 
                    defaultUserMessage, VillageTypeApiConstants.submittedOnDateParamName, this.submittedOnDate);
            
            dataValidationErrors.add(error);
        }
        
        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {
            
            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.village.submittedOnDate.after.activation.date", 
                    defaultUserMessage, VillageTypeApiConstants.submittedOnDateParamName, this.submittedOnDate);
            dataValidationErrors.add(error);
        }
        
        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {
            
            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.group.activationDate.in.the.future",
                    defaultUserMessage, VillageTypeApiConstants.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }
        
        if (getActivationLocalDate() != null) {
            if (this.officeId.isOpeningDateAfter(getActivationLocalDate())) {
                final String defaultUserMessage = "Activation date cannot be a date before the office opening date.";
                final ApiParameterError error = ApiParameterError.parameterError(
                        "error.msg.group.activationDate.cannot.be.before.office.activation.date", defaultUserMessage,
                        VillageTypeApiConstants.activationDateParamName, getActivationLocalDate());
                dataValidationErrors.add(error);
            }
        }
    }
    
    private void validateStatusNotEqualToActiveAndLogError(final List<ApiParameterError> dataValidationErrors) {
      
        if (isActive()) {
            final String defaultUserMessage = "Cannot activate group. Group is already active.";
            final String globalisationMessageCode = "error.msg.group.already.active";
            final ApiParameterError error = ApiParameterError.parameterError(globalisationMessageCode, 
                    defaultUserMessage, VillageTypeApiConstants.activeParamName, true);
            dataValidationErrors.add(error);
        }
    }
    
    private void throwExceptionIfErrors(final List<ApiParameterError> dataValidationErrors) {
        
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
    
    
    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInIntegerParameterNamed(VillageTypeApiConstants.statusParamName, this.status)) {
            final Integer newValue = command.integerValueOfParameterNamed(VillageTypeApiConstants.statusParamName);
            actualChanges.put(VillageTypeApiConstants.statusParamName, VillageTypeEnumerations.status(newValue));
            this.status = VillageTypeStatus.fromInt(newValue).getValue();
        }

        if (command.isChangeInStringParameterNamed(VillageTypeApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(VillageTypeApiConstants.externalIdParamName);
            actualChanges.put(VillageTypeApiConstants.externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInLongParameterNamed(VillageTypeApiConstants.officeIdParamName, this.officeId.getId())) {
            final Long newValue = command.longValueOfParameterNamed(VillageTypeApiConstants.officeIdParamName);
            actualChanges.put(VillageTypeApiConstants.officeIdParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(VillageTypeApiConstants.villageNameParamName, this.villageName)) {
            final String newValue = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageNameParamName);
            actualChanges.put(VillageTypeApiConstants.villageNameParamName, newValue);
            this.villageName = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(VillageTypeApiConstants.villageCodeParamName, this.villageCode)) {
            final String newValue = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageCodeParamName);
            actualChanges.put(VillageTypeApiConstants.villageCodeParamName, newValue);
            this.villageCode = StringUtils.defaultIfEmpty(newValue, null);
        }
    
        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(VillageTypeApiConstants.activationDateParamName, getActivationLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(VillageTypeApiConstants.activationDateParamName);
            actualChanges.put(VillageTypeApiConstants.activationDateParamName, valueAsInput);
            actualChanges.put(VillageTypeApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(VillageTypeApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(VillageTypeApiConstants.activationDateParamName);
            this.activationDate = newValue.toDate();
        }

        return actualChanges;
    }
    
    public Long officeId() {
       return this.officeId.getId();
    }
    
    public Office getOffice() {
        return this.officeId;
    }
    
    public boolean isOfficeIdentifiedBy(final Long officeId) {
        return this.officeId.identifiedBy(officeId);
    }
    
    public void incrementCount() {
        this.count += 1;
    }

    public boolean isNotPending() {
        return !isPending();
    }
    
    public boolean isPending() {
        return VillageTypeStatus.fromInt(this.status).isPending();
    }
    
    public String getVillageName() {
        return this.villageName ;
    }
    
    public String getExternalId() {
        return this.externalId ;
    }
    
    public String getVillageCode() {
        return this.villageCode ;
    }
    
    public String getOfficeName() {
        return this.getOffice().getName() ;
    }
    
    public void assignStaff(final Staff staff) {
        this.staff = staff;
    }
    
    public void unassignStaff() {
        this.staff = null;
    }
    
    public VillageStaffAssignmentHistory findLatestIncompleteHistoryRecord() {

        VillageStaffAssignmentHistory latestRecordWithNoEndDate = null;
        for (final VillageStaffAssignmentHistory historyRecord : this.villageStaffHistory) {
            if (historyRecord.isCurrentRecord()) {
                latestRecordWithNoEndDate = historyRecord;
                break;
            }
        }
        return latestRecordWithNoEndDate;
    }

    
    public Set<VillageStaffAssignmentHistory> getVillageStaffHistory() {
        return this.villageStaffHistory;
    }

    
    public void setVillageStaffHistory(Set<VillageStaffAssignmentHistory> villageStaffHistory) {
        this.villageStaffHistory = villageStaffHistory;
    }
    
    
    
}
