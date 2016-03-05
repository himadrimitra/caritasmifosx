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


@SuppressWarnings("unused")
public class ProductCollateralsMappingData {
    private final Long id;
    private final Long product;
    private final String productName;
    private final String productShortName;
    private final Long collateral;
    private final String collateralName;
        
    private ProductCollateralsMappingData(final Long id, final Long product, final String productName, final String productShortName, final Long collateral,
            final String collateralName) {
        this.id = id;
        this.product = product;
        this.productName = productName;
        this.productShortName = productShortName;
        this.collateral = collateral;
        this.collateralName = collateralName;
    }

    public static ProductCollateralsMappingData instance(final Long id, final Long product, final String productName, final String productShortName, final Long collateral,
            final String collateralName) {
        return new ProductCollateralsMappingData(id,product,productName, productShortName, collateral, collateralName);        
    }

    
    public String getProductName() {
        return this.productName;
    }

    
    public String getCollateralName() {
        return this.collateralName;
    }
        
}
