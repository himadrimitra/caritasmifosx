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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_collateral_quality_standards")
public class QualityStandards  extends AbstractPersistable<Long> {
	
	@OneToOne
    @JoinColumn(name = "collateral_id", nullable = false)
	private Collaterals collateral;
	
	@Column(name = "name", length = 50, nullable = false)
	private String name;
	
	@Column(name = "description", length = 250, nullable = true)
	private String description;
	
	@Column(name = "percentage_price", scale = 6, precision = 19, nullable = true)
        private BigDecimal percentagePrice;
	
	@Column(name = "absolute_price", scale = 6, precision = 19, nullable = true)
        private BigDecimal absolutePrice;
	
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

	public QualityStandards(final Collaterals collateral, final String name, final String description, final BigDecimal percentagePrice,
			final BigDecimal absolutePrice, final AppUser createdBy, final Date createdDate) {
		this.collateral = collateral;
		this.name = name;
		this.description = description;
		this.percentagePrice = percentagePrice;
		this.absolutePrice = absolutePrice;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		
	}
	
	protected QualityStandards(){
		
	}
	
	public static QualityStandards createQualityStandards(final Collaterals collateral,final String name, final String description, final BigDecimal percentagePrice,
			final BigDecimal absolutePrice, final AppUser createdBy, final Date createdDate){
            return new QualityStandards(collateral,name, description, percentagePrice, absolutePrice,createdBy,createdDate);
        }
	
	public Map<String, Object> update(JsonCommand command) {
        
        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
        
        if (command.isChangeInStringParameterNamed(CollateralsApiConstants.nameParamName, this.name)){
            final String newName = command.stringValueOfParameterNamed(CollateralsApiConstants.nameParamName);
            actualChanges.put(CollateralsApiConstants.nameParamName, newName);
            this.name = newName;
        }
        
        if (command.isChangeInStringParameterNamed(CollateralsApiConstants.descriptionParamName, this.description)){
            final String newDescription = command.stringValueOfParameterNamed(CollateralsApiConstants.descriptionParamName);
            actualChanges.put(CollateralsApiConstants.descriptionParamName, newDescription);
            this.description = newDescription;
        }
        
        if (command.isChangeInBigDecimalParameterNamed(CollateralsApiConstants.percentagePriceParamName, this.percentagePrice)){
            final BigDecimal newBaseUnitPrice = command.bigDecimalValueOfParameterNamed(CollateralsApiConstants.percentagePriceParamName);
            actualChanges.put(CollateralsApiConstants.percentagePriceParamName, newBaseUnitPrice);
            this.percentagePrice = newBaseUnitPrice;
        }
        
        if (command.isChangeInBigDecimalParameterNamed(CollateralsApiConstants.absolutePriceParamName, this.absolutePrice)){
            final BigDecimal newBaseUnitPrice = command.bigDecimalValueOfParameterNamed(CollateralsApiConstants.absolutePriceParamName);
            actualChanges.put(CollateralsApiConstants.absolutePriceParamName, newBaseUnitPrice);
            this.absolutePrice = newBaseUnitPrice;
        }
        
            return actualChanges;
        }
	
	public void setUpdatedDate(final Date updatedDate){
		this.updatedDate = updatedDate;
	}
	
	public void setUpdatedBy(final AppUser appUser){
		this.updatedBy = appUser;
	}
	
}
