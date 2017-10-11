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
package org.apache.fineract.portfolio.collaterals.domain;

import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants.PLEDGE_STATUS_PARAMS;
import org.apache.fineract.portfolio.collaterals.data.CollateralDetailsData;
import org.apache.fineract.portfolio.collaterals.exception.InvalidPledgeStateTransitionException;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_pledge")
public class Pledges extends AbstractPersistable<Long> {
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;
    
    @OneToOne
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan loan;
    
    @Column(name = "pledge_number")
    private String pledgeNumber;
   
    @Column(name = "seal_number")
    private Long sealNumber;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "system_value")
    private BigDecimal systemValue;
    
    @Column(name = "user_value")
    private BigDecimal userValue;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closedBy;
    
    @Column(name = "closedon_date")
    private Date closureDate;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = true)
    private AppUser updatedBy;

    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date updatedDate;

    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pledge", orphanRemoval = true)
    private Set<CollateralDetails> collateralDetails = new HashSet<>();
    
    
    
    public static Pledges instance(final Client client, final Loan loan, final String pledgeNumber, final Long sealNumber, final Integer status, final BigDecimal systemValue, 
            final BigDecimal userValue, final AppUser closedBy, final Date closureDate, final Set<CollateralDetails> collateralDetails, final AppUser createdBy, final Date createdDate){
        return new Pledges(client, loan, pledgeNumber, sealNumber, status, systemValue, userValue, closedBy, closureDate, collateralDetails,createdBy,createdDate);
    }
    

    public Pledges(final Client client, final Loan loan, final String pledgeNumber, final Long sealNumber, final Integer status, final BigDecimal systemValue, 
            final BigDecimal userValue, final AppUser closedBy, final Date closureDate, final Set<CollateralDetails> collateralDetails, final AppUser createdBy, final Date createdDate) {
        this.client = client;
        this.loan = loan;
        this.pledgeNumber = pledgeNumber;
        this.sealNumber = sealNumber;
        this.status = status;
        this.systemValue = systemValue;
        this.userValue = userValue;
        this.closedBy = closedBy;
        this.closureDate = closureDate;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
        if(collateralDetails != null && !collateralDetails.isEmpty()){
            this.collateralDetails = associateCollateralDetailsWithThisPledge(collateralDetails);
        }
        
    }
    
    private Set<CollateralDetails> associateCollateralDetailsWithThisPledge(Set<CollateralDetails> collateralDetails) {
        for(CollateralDetails collateralDetail : collateralDetails){
            collateralDetail.update(this);
        }
        return collateralDetails;
    }


    protected Pledges(){
        
    }


    public Map<String, Object> update(final JsonCommand command, final Set<CollateralDetails> possiblyModifedCollateralDetails) {
        
        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);
        
        
        if (command.isChangeInLongParameterNamed(PledgeApiConstants.clientIdParamName, getClientId())){
            final Long newClientId = command.longValueOfParameterNamed(PledgeApiConstants.clientIdParamName);
            actualChanges.put(PledgeApiConstants.clientIdParamName, newClientId);
        }
        
        if (command.isChangeInLongParameterNamed(PledgeApiConstants.loanIdParamName, getLoanId())){
            final Long newLoanId = command.longValueOfParameterNamed(PledgeApiConstants.loanIdParamName);
            actualChanges.put(PledgeApiConstants.loanIdParamName, newLoanId);
        }
        
        if (command.isChangeInLongParameterNamed(PledgeApiConstants.sealNumberParamName, this.sealNumber)){
            final Long newSealNumber = command.longValueOfParameterNamed(PledgeApiConstants.sealNumberParamName);
            actualChanges.put(PledgeApiConstants.sealNumberParamName, newSealNumber);
            this.sealNumber = newSealNumber;
        }
        
        if (command.isChangeInIntegerParameterNamed(PledgeApiConstants.statusParamName, this.status)){
            final Integer newStatus = command.integerValueOfParameterNamed(PledgeApiConstants.statusParamName);
            actualChanges.put(PledgeApiConstants.statusParamName, newStatus);
            this.status = newStatus;
        }
        
        if (command.isChangeInBigDecimalParameterNamed(PledgeApiConstants.systemValueParamName, this.systemValue)){
            final BigDecimal systemValue = command.bigDecimalValueOfParameterNamed(PledgeApiConstants.systemValueParamName);
            actualChanges.put(PledgeApiConstants.systemValueParamName, userValue);
            this.systemValue = systemValue;
        }
        
        if (command.isChangeInBigDecimalParameterNamed(PledgeApiConstants.userValueParamName, this.userValue)){
            final BigDecimal userValue = command.bigDecimalValueOfParameterNamed(PledgeApiConstants.userValueParamName);
            actualChanges.put(PledgeApiConstants.userValueParamName, userValue);
            this.userValue = userValue;
        }
        
        if (command.parameterExists(PledgeApiConstants.collateralDetailsParamName)) {

            if (!possiblyModifedCollateralDetails.equals(this.collateralDetails)) {
                actualChanges.put(PledgeApiConstants.collateralDetailsParamName, listOfCollateralDetails(possiblyModifedCollateralDetails));
            }
        }
    
        return actualChanges;
    }
    
    
    private CollateralDetailsData[] listOfCollateralDetails(final Set<CollateralDetails> possiblyModifedCollateralDetails) {

        CollateralDetailsData[] existingCollateralDetails = null;

        final List<CollateralDetailsData> collateralDetailsList = new ArrayList<>();
        for (final CollateralDetails collateralDetail: possiblyModifedCollateralDetails) {

            final CollateralDetailsData data = collateralDetail.toData();

            collateralDetailsList.add(data);
        }

        existingCollateralDetails = collateralDetailsList.toArray(new CollateralDetailsData[collateralDetailsList.size()]);

        return existingCollateralDetails;
    }

    private Long getLoanId() {
        Long loanId = null;
        if(this.loan != null){
            loanId = this.loan.getId();
        }
        return loanId;
    }


    private Long getClientId() {
        Long clientId = null;
        if(this.client != null){
            clientId = this.client.getId();
        }
        return clientId;
    }


    public BigDecimal getSystemValue(){
        return this.systemValue;
    }


    public boolean isAssociatedToLoan() {
        boolean isAssociatedToActiveLoan = false;
        if(this.loan != null && !this.loan.isClosed()){
            isAssociatedToActiveLoan = true;
        }
        return isAssociatedToActiveLoan;
    }
    
    public Long getSealNumber(){
        return this.sealNumber;
    }


    public void close(final AppUser currentUser, final LocalDate closureDate) {
        
        if (closureDate.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a pledge with identifier : " + this.getId()
                    + " is closed cannot be in the future.";
            throw new InvalidPledgeStateTransitionException("pledge", "cannot.be.a.future.date", errorMessage, closureDate);
        }
        
        this.status = PLEDGE_STATUS_PARAMS.CLOSE_PLEDGE.getValue();
        this.closedBy = currentUser;
        this.closureDate = closureDate.toDate();
    }
    
    public boolean isNotInitiated(){
        return !this.status.equals(PLEDGE_STATUS_PARAMS.INITIATE_PLEDGE.getValue());
        
    }
    
    public Loan getloan(){
        return this.loan;
    }

    public void updateClientId(final Client client) {
        this.client = client;
        
    }

    public void updateLoanId(final Loan loan) {
        this.loan = loan;
        
    }
    
    public Set<CollateralDetails> getCollateralDetails(){
        return this.collateralDetails;
    }


    public void removeCollateralDetails(final CollateralDetails collateralDetail) {
        this.collateralDetails.remove(collateralDetail);
        
    }
    
    public void updateSystemValue(final BigDecimal totalSystemValue) {
        this.systemValue = totalSystemValue;
        
    }
    
    public void updateUserValue(final BigDecimal totalUserValue) {
        this.userValue = totalUserValue;
        
    }
    
    public void updatePledgeStatus(){
        this.status = PLEDGE_STATUS_PARAMS.ACTIVE_PLEDGE.getValue();
    }


    public void updateCollateralDetails(Set<CollateralDetails> collateralDetails) {
        if (this.collateralDetails == null) {
            this.collateralDetails = new HashSet<>();
        }
        this.collateralDetails.clear();
        this.collateralDetails.addAll(associateCollateralDetailsWithThisPledge(collateralDetails));
        
    }
    
    public void setUpdatedDate(final Date updatedDate){
		this.updatedDate = updatedDate;
	}
	
	public void setUpdatedBy(final AppUser appUser){
		this.updatedBy = appUser;
	}

}
