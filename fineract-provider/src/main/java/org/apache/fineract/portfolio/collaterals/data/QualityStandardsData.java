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
import java.util.Date;

@SuppressWarnings("unused")
public class QualityStandardsData {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal percentagePrice;
    private final BigDecimal absolutePrice;
    private final Date createdDate;
    private final Date updatedDate;
    private final Long collateralId;
    private final Long createdBy;
    private final Long updatedBy;

    public static QualityStandardsData instance(final Long id, final Long collateralId, final String name, final String description,
            final BigDecimal percentagePrice, final BigDecimal absolutePrice, final Long createdBy, final Date createdDate,
            final Long updatedBy, final Date updatedDate) {
        return new QualityStandardsData(id, collateralId, name, description, percentagePrice, absolutePrice, createdBy, createdDate,
                updatedBy, updatedDate);
    }

    public QualityStandardsData(final Long id, final Long collateralId, final String name, final String description,
            final BigDecimal percentagePrice, final BigDecimal absolutePrice, final Long createdBy, final Date createdDate,
            final Long updatedBy, final Date updatedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.percentagePrice = percentagePrice;
        this.absolutePrice = absolutePrice;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.collateralId = collateralId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static QualityStandardsData lookUp(final Long id, final String name, final Long collateralId, final BigDecimal percentagePrice, 
            final BigDecimal absolutePrice) { 
        return new QualityStandardsData(id, collateralId, name, null, percentagePrice, absolutePrice, null, null, null, null);
    }
    
    public BigDecimal getPercentagePrice(){
        return this.percentagePrice;
    }
    
    public BigDecimal getAbsolutePrice(){
        return this.absolutePrice;
    }
    
}
