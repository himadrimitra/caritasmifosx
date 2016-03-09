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
package org.apache.fineract.portfolio.collaterals.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;



public class CollateralsApiConstants {
    
    public static final String COLLATERALS_RESOURCE_NAME = "collaterals";
    public static final String COLLATERALS_QUALITY_STANDARDS_RESOURCE_NAME = "collateralQualityStandards";
    public static final String nameParamName = "name";
    public static final String descriptionParamName = "description";
    public static final String typeClassifierParamName = "typeClassifier";
    public static final String baseUnitPriceParamName = "baseUnitPrice";
    public static final String isActiveParamName = "isActive"; 
    public static final String absolutePriceParamName = "absolutePrice";
    public static final String percentagePriceParamName = "percentagePrice";
    public static final String localeParamName = "locale";
    public static final String collateralIdParamName = "collateralId";
    public static final String createdByParamName = "createdBy";
    public static final String createdDateParamName = "createdDate";
    public static final String updatedByParamName = "updatedBy";
    public static final String updatedDateParamName = "updatedDate";
    public static final String idParamName = "id";
    public static final String productIdParamName = "loanProductId";
    public static final String PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME = "PRODUCTCOLLATERALSMAPPING";
    
    
    
    public static final Set<String> COLLATERALS_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName, descriptionParamName, typeClassifierParamName,
    		baseUnitPriceParamName));
    
    public static final Set<String> QUALITY_STANDARDS_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,collateralIdParamName, nameParamName, descriptionParamName, percentagePriceParamName,
    		absolutePriceParamName, createdByParamName, createdDateParamName, updatedByParamName, updatedDateParamName));
    
    public static final Set<String> PRODUCT_COLLATERAL_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, productIdParamName, collateralIdParamName));
       
    public static final Set<String> COLLATERALS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName, descriptionParamName, 
            typeClassifierParamName, baseUnitPriceParamName, localeParamName));
    
    public static final Set<String> QUALITY_STANDARDS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(collateralIdParamName, nameParamName, descriptionParamName, 
    		absolutePriceParamName, percentagePriceParamName, localeParamName));
    
    public static final Set<String> QUALITY_STANDARDS_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(collateralIdParamName, nameParamName, descriptionParamName, 
    		absolutePriceParamName, percentagePriceParamName, localeParamName, idParamName));
    
    public static final Set<String> PRODUCT_COLLATERAL_MAPPING_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(productIdParamName, collateralIdParamName));
    
    public static enum COLLATERALS_TYPE_CLASSIFIER_PARAMS {
        PRECIOUS_STONE(1), PRECIOUS_METAL(2), OTHERS(3);
       
        private final Integer value;

        private COLLATERALS_TYPE_CLASSIFIER_PARAMS(final Integer value) {
            this.value = value;
        }
        public Integer getValue() {
            return this.value;
        }
    }
}
