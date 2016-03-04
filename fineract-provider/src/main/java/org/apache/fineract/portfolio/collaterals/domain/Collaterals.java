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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_collateral_type")
public class Collaterals extends AbstractPersistable<Long> {
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "base_unit_price")
    private BigDecimal baseUnitPrice;
    
    @Column(name = "type_classifier")
    private Integer typeClassifier;
    
    
    public static Collaterals createCollaterals(final String name, final String description, final BigDecimal baseUnitPrice, final Integer typeClassifier){
        return new Collaterals(name, description, baseUnitPrice, typeClassifier);
    }
    
    private Collaterals(final String name, final String description, final BigDecimal baseUnitPrice, final Integer typeClassifier) {
        this.name = name;
        this.description = description;
        this.baseUnitPrice = baseUnitPrice;
        this.typeClassifier = typeClassifier;
    }
    
    protected Collaterals(){
        //
    }

    public Map<String, Object> update(JsonCommand command) {
        
        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);
        
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
        
        if (command.isChangeInBigDecimalParameterNamed(CollateralsApiConstants.baseUnitPriceParamName, this.baseUnitPrice)){
            final BigDecimal newBaseUnitPrice = command.bigDecimalValueOfParameterNamed(CollateralsApiConstants.baseUnitPriceParamName);
            actualChanges.put(CollateralsApiConstants.baseUnitPriceParamName, newBaseUnitPrice);
            this.baseUnitPrice = newBaseUnitPrice;
        }
        
        if (command.isChangeInIntegerParameterNamed(CollateralsApiConstants.typeClassifierParamName, this.typeClassifier)){
            final Integer newTypeClassifier = command.integerValueOfParameterNamed(CollateralsApiConstants.typeClassifierParamName);
            actualChanges.put(CollateralsApiConstants.typeClassifierParamName, newTypeClassifier);
            this.typeClassifier = newTypeClassifier;
        }
        
        return actualChanges;
    }
    
    public BigDecimal getBaseUnitPrice(){
        return this.baseUnitPrice;
    }

}
