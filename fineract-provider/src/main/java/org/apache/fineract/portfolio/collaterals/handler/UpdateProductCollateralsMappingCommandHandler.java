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
package org.apache.fineract.portfolio.collaterals.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.collaterals.service.ProductCollateralsMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME, action = "UPDATE")
public class UpdateProductCollateralsMappingCommandHandler implements NewCommandSourceHandler{
    private final ProductCollateralsMappingWritePlatformService productCollateralsMappingWritePlatformService;
    
    @Autowired
    public UpdateProductCollateralsMappingCommandHandler(ProductCollateralsMappingWritePlatformService productCollateralsMappingWritePlatformService) {
        this.productCollateralsMappingWritePlatformService = productCollateralsMappingWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        
        return this.productCollateralsMappingWritePlatformService.updateProductCollateralsMapping(command.entityId(), command.subentityId(), command);
    }
}