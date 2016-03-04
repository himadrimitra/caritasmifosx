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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.collaterals.data.CollateralDetailsData;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_collateral_details")
public class CollateralDetails extends AbstractPersistable<Long> {
    
    @ManyToOne
    @JoinColumn(name = "collateral_id", nullable = false)
    private Collaterals collateral;
    
    @ManyToOne
    @JoinColumn(name = "quality_standard_id", nullable = false)
    private QualityStandards qualityStandards;
    
    @ManyToOne
    @JoinColumn(name = "pledge_id", nullable = false)
    private Pledges pledge;
    
    @Column(name = "description", nullable = true)
    private String description;
    
    @Column(name = "gross_weight", nullable = false)
    private BigDecimal grossWeight;
    
    @Column(name = "net_weight", nullable = false)
    private BigDecimal netWeight;
    
    @Column(name = "system_price", nullable = false)
    private BigDecimal systemPrice;
    
    @Column(name = "user_price", nullable = false)
    private BigDecimal userPrice;
    
    
    public static CollateralDetails instance(final Collaterals collateral, final QualityStandards qualityStandards, final Pledges pledge, final String description,
            final BigDecimal grossWeight, final BigDecimal netWeight, final BigDecimal systemPrice, final BigDecimal userPrice){
        
        return new CollateralDetails(collateral, qualityStandards, pledge, description, grossWeight, netWeight, systemPrice, userPrice);
    }
    
    public static CollateralDetails createNewWithoutLoan(final Collaterals collateral, final QualityStandards qualityStandards, final String description,
            final BigDecimal grossWeight, final BigDecimal netWeight, final BigDecimal systemPrice, final BigDecimal userPrice){
        
        return new CollateralDetails(collateral, qualityStandards, null, description, grossWeight, netWeight, systemPrice, userPrice);
    }

    private CollateralDetails(final Collaterals collateral, final QualityStandards qualityStandards, final Pledges pledge, final String description,
            final BigDecimal grossWeight, final BigDecimal netWeight, final BigDecimal systemPrice, final BigDecimal userPrice) {
        this.collateral = collateral;
        this.qualityStandards = qualityStandards;
        this.pledge = pledge;
        this.description = description;
        this.grossWeight = grossWeight;
        this.netWeight = netWeight;
        this.systemPrice = systemPrice;
        this.userPrice = userPrice;
    }
    
    protected CollateralDetails(){
        
    }

    public void update(Pledges pledges) {
        this.pledge = pledges;
        
    }
    
    public BigDecimal getSystemPrice(){
        return this.systemPrice;
    }
    
    public BigDecimal getUserPrice(){
        return this.userPrice;
    }

    public CollateralDetailsData toData() {
        return CollateralDetailsData.toData(getId(), null, this.collateral.getId(), this.qualityStandards.getId(), null, 
                this.description, this.grossWeight, this.netWeight, this.systemPrice, this.userPrice);
    }

}
