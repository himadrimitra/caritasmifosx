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

@SuppressWarnings("unused")
public class CollateralDetailsData {
    
    private final Long id;
    private final Long pledgeId;
    private final Long collateralId;
    private final String collateralName;
    private final Long qualityStandardId;
    private final String description;
    private final String name;
    private final BigDecimal grossWeight;
    private final BigDecimal netWeight;
    private final BigDecimal systemPrice;
    private final BigDecimal userPrice;
    
    
    public static CollateralDetailsData instance(final Long id, final Long pledgeId, final Long collateralId, final String collateralName, final Long qualityStandardId, 
            final String name, final String description, final BigDecimal grossWeight, final BigDecimal netWeight, final BigDecimal systemPrice, final BigDecimal userPrice){
        return new CollateralDetailsData(id, pledgeId, collateralId, collateralName, qualityStandardId, name, description, grossWeight, netWeight, systemPrice, userPrice);
        
    }
    
    public CollateralDetailsData(final Long id, final Long pledgeId, final Long collateralId, final String collateralName, final Long qualityStandardId, 
            final String name, final String description, final BigDecimal grossWeight, final BigDecimal netWeight, final BigDecimal systemPrice, final BigDecimal userPrice) {
        this.id = id;
        this.pledgeId = pledgeId;
        this.collateralId = collateralId;
        this.collateralName = collateralName;
        this.qualityStandardId = qualityStandardId;
        this.name = name;
        this.description = description;
        this.grossWeight = grossWeight;
        this.netWeight = netWeight;
        this.systemPrice = systemPrice;
        this.userPrice = userPrice;
    }
    
    public static CollateralDetailsData toData(final Long id, final Long pledgeId, final Long collateralId, final Long qualityStandardId, 
            final String name, final String description, final BigDecimal grossWeight, final BigDecimal netWeight, final BigDecimal systemPrice, final BigDecimal userPrice){
        return new CollateralDetailsData(id, pledgeId, collateralId, null, qualityStandardId, name, description, grossWeight, netWeight, systemPrice, userPrice);
        
    }

}
