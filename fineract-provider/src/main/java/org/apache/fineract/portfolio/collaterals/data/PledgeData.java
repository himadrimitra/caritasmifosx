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
package org.apache.fineract.portfolio.collaterals.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;

@SuppressWarnings("unused")
public class PledgeData {
    
    private final Long id;
    private final Long clientId;
    private final Long loanId;
    private final String loanAccountNo;
    private final String officeName;
    private final String clientName;
    private final Long sealNumber;
    private final String pledgeNumber;
    private final EnumOptionData status;
    private final BigDecimal systemValue;
    private final BigDecimal userValue;
    private Collection<CollateralsData> collateralData;
    private Collection<QualityStandardsData> qualityStandardsData;
    private Collection<CollateralDetailsData> collateralDetailsData;
    private final Long createdBy;
    private final Date createdDate;
    private final Long updatedBy;
    private final Date updatedDate;
    
    public static PledgeData createNew(final Long id, final Long clientId, final Long loanId, final String loanAccountNo, final String officeName, final String clientName, 
            final Long sealNumber, final String pledgeNumber, final EnumOptionData status, final BigDecimal systemValue, final BigDecimal userValue, final Long createdBy, final Date createdDate,
            final Long updatedBy, final Date updatedDate){      
        return new PledgeData(id, clientId, loanId, loanAccountNo, officeName, clientName, sealNumber, pledgeNumber, status, systemValue, userValue, null, null, null, createdBy, createdDate,
                updatedBy, updatedDate);
        
    }
    
    
    public PledgeData(final Long id, final Long clientId, final Long loanId, final String loanAccountNo, final String officeName, final String clientName, final Long sealNumber, 
            final String pledgeNumber, final EnumOptionData status, final BigDecimal systemValue, final BigDecimal userValue, final Collection<CollateralsData> collateralData, 
            final Collection<QualityStandardsData> qualityStandardsData, final Collection<CollateralDetailsData> collateralDetailsData, final Long createdBy, final Date createdDate,
            final Long updatedBy, final Date updatedDate) {
        this.id = id;
        this.clientId = clientId;
        this.loanId = loanId;
        this.loanAccountNo = loanAccountNo;
        this.officeName = officeName;
        this.clientName = clientName;
        this.sealNumber = sealNumber;
        this.pledgeNumber = pledgeNumber;
        this.status = status;
        this.systemValue = systemValue;
        this.userValue = userValue;
        this.collateralData = collateralData;
        this.qualityStandardsData = qualityStandardsData;
        this.collateralDetailsData = collateralDetailsData;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
    }


    public static PledgeData retrieveTemplateData(BigDecimal systemCalculatedPrice, final Collection<CollateralsData> collateralData, 
            final Collection<QualityStandardsData> qualityStandardsData) {
        final Long id = null;
        final Long clientId = null;
        final Long loanId = null;
        final String loanAccountNo = null;
        final String officeName = null;
        final String clientName = null;
        final Long sealNumber = null;
        final String pledgeNumber = null;
        final EnumOptionData status = null;
        final BigDecimal userValue = null;
        final Collection<CollateralDetailsData> collateralDetailsData = null;
        final Long createdBy = null;
        final Date createdDate = null;
        final Long updatedBy = null;
        final Date updatedDate = null;
        return new PledgeData(id, clientId, loanId, loanAccountNo, officeName, clientName, sealNumber, pledgeNumber, status, systemCalculatedPrice, 
                userValue, collateralData, qualityStandardsData, collateralDetailsData, createdBy, createdDate, updatedBy, updatedDate);
    }
    
    public void updateCollateralDetails(final Collection<CollateralDetailsData> collateralDetailsData){
        this.collateralDetailsData = collateralDetailsData;
    }


    public static PledgeData retrieveLoanPledgesTemplate(final Long id, final String pledgeNumber, final BigDecimal systemValue, final BigDecimal userValue) {
        final Long clientId = null;
        final Long loanId = null;
        final String loanAccountNo = null;
        final String officeName = null;
        final String clientName = null;
        final Long sealNumber = null;
        final EnumOptionData status = null;
        final Collection<CollateralsData> collateralData = null;
        final Collection<QualityStandardsData> qualityStandardsData = null;
        final Collection<CollateralDetailsData> collateralDetailsData = null;
        final Long createdBy = null;
        final Date createdDate = null;
        final Long updatedBy = null;
        final Date updatedDate = null;
        return new PledgeData(id, clientId, loanId, loanAccountNo, officeName, clientName, sealNumber, pledgeNumber, status, systemValue, 
                userValue, collateralData, qualityStandardsData, collateralDetailsData, createdBy, createdDate, updatedBy, updatedDate);
    }
    
    public Long getPledgeId(){
        return this.id;
    }

}
