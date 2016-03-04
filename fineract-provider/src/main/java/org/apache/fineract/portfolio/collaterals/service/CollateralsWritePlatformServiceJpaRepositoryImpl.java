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
package org.apache.fineract.portfolio.collaterals.service;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.collaterals.data.CollateralsDataValidator;
import org.apache.fineract.portfolio.collaterals.domain.Collaterals;
import org.apache.fineract.portfolio.collaterals.domain.CollateralsRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CollateralsWritePlatformServiceJpaRepositoryImpl implements CollateralsWritePlatformService{

    private final PlatformSecurityContext context;
    private final CollateralsDataValidator fromApiJsonDeserializer;
    private final CollateralsRepositoryWrapper collateralsRepositoryWrapper;
    
    
    @Autowired
    public CollateralsWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final CollateralsDataValidator fromApiJsonDeserializer,
            final CollateralsRepositoryWrapper collateralsRepositoryWrapper) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.collateralsRepositoryWrapper = collateralsRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult createCollaterals(final JsonCommand command) {
        
        this.context.authenticatedUser();
        this.fromApiJsonDeserializer.validateForCreate(command);
        
        final String name = command.stringValueOfParameterNamed(CollateralsApiConstants.nameParamName);
        final String description  = command.stringValueOfParameterNamed(CollateralsApiConstants.descriptionParamName);
        final BigDecimal baseUnitPrice = command.bigDecimalValueOfParameterNamed(CollateralsApiConstants.baseUnitPriceParamName);
        final int typeClassifier = command.integerValueOfParameterNamed(CollateralsApiConstants.typeClassifierParamName);
        
        Collaterals collateral = Collaterals.createCollaterals(name, description, baseUnitPrice, typeClassifier);
        if(collateral != null){
            this.collateralsRepositoryWrapper.save(collateral);
        } 
        
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(collateral.getId()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult updateCollaterals(final Long collateralId, final JsonCommand command) {
        
        this.context.authenticatedUser();
        this.fromApiJsonDeserializer.validateForUpdate(command);
        
        Collaterals collateralForUpdate = this.collateralsRepositoryWrapper.findOneWithNotFoundDetection(collateralId);
        
        final Map<String, Object> changes = collateralForUpdate.update(command);
        
        if (!changes.isEmpty()) {
            this.collateralsRepositoryWrapper.saveAndFlush(collateralForUpdate);
        }
        
        return new CommandProcessingResultBuilder() //
            .withCommandId(command.commandId()) //
            .withEntityId(collateralId) //
            .with(changes)
            .build();
    }

}
