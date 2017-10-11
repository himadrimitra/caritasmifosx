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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.collaterals.exception.ProductCollateralsMappinDuplicateMappingException;
import org.apache.fineract.portfolio.collaterals.service.ProductCollateralsMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class ProductCollateralsMappingDataValidator {
    
    private final FromJsonHelper fromApiJsonHelper;
    private final ProductCollateralsMappingReadPlatformService productCollateralsMappingReadPlatformService;
    
    @Autowired
    public ProductCollateralsMappingDataValidator(final FromJsonHelper fromApiJsonHelper, final ProductCollateralsMappingReadPlatformService productCollateralsMappingReadPlatformService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.productCollateralsMappingReadPlatformService = productCollateralsMappingReadPlatformService;
    }
    
    public void validateForCreate(JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CollateralsApiConstants.PRODUCT_COLLATERAL_MAPPING_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        
        final Long collateralId = this.fromApiJsonHelper.extractLongNamed(CollateralsApiConstants.collateralIdParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.collateralIdParamName).value(collateralId).notNull();
        
        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(CollateralsApiConstants.productIdParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.productIdParamName).value(loanProductId).notNull();
        
        List<ProductCollateralsMappingData> productCollateralsMappingDataList = this.productCollateralsMappingReadPlatformService.retrieveOneWithProductAndCollateral(loanProductId, collateralId);
        
        if(productCollateralsMappingDataList.size()>0){
            throw  new ProductCollateralsMappinDuplicateMappingException(productCollateralsMappingDataList.get(0).getProductName(), productCollateralsMappingDataList.get(0).getCollateralName());
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }
    

    public void validateForUpdate(JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CollateralsApiConstants.PRODUCT_COLLATERAL_MAPPING_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        
        final Long collateralId = this.fromApiJsonHelper.extractLongNamed(CollateralsApiConstants.collateralIdParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.collateralIdParamName).value(collateralId).notNull();
        
        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(CollateralsApiConstants.productIdParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.productIdParamName).value(loanProductId).notNull();
        
        List<ProductCollateralsMappingData> productCollateralsMappingDataList = this.productCollateralsMappingReadPlatformService.retrieveOneWithProductAndCollateral(loanProductId, collateralId);
        
        if(productCollateralsMappingDataList.size()>0){
            throw  new ProductCollateralsMappinDuplicateMappingException(productCollateralsMappingDataList.get(0).getProductName(), productCollateralsMappingDataList.get(0).getCollateralName());
        }
        
       throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

}
