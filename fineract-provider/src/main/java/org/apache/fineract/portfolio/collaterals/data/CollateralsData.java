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

@SuppressWarnings("unused")
public class CollateralsData {
       
    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal baseUnitPrice;
    private final Integer typeClassifier;
    private Collection<QualityStandardsData> qualityStandards;
    
    public static CollateralsData instance(final Long id, final String name, final String description, final BigDecimal baseUnitPrice,
            final Integer typeClassifier, final Collection<QualityStandardsData> qualityStandards) {
        return new CollateralsData(id, name, description, baseUnitPrice, typeClassifier, qualityStandards);
    }
    
    private CollateralsData(final Long id, final String name, final String description, final BigDecimal baseUnitPrice, final Integer typeClassifier, 
            final Collection<QualityStandardsData> qualityStandards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseUnitPrice = baseUnitPrice;
        this.typeClassifier = typeClassifier;
        this.qualityStandards = qualityStandards;
    }
    
    public void updateQualityStandards(Collection<QualityStandardsData> qualityStandards){
        this.qualityStandards = qualityStandards;
    }

    public static CollateralsData lookUp(final Long id, final String name, final BigDecimal baseUnitPrice) {
        final String description = null;
        final Integer typeClassifier = null;
        final Boolean isActive = null;
        final Collection<QualityStandardsData> qualityStandards = null;
        
        return new CollateralsData(id, name, description, baseUnitPrice, typeClassifier, qualityStandards);
    }

}