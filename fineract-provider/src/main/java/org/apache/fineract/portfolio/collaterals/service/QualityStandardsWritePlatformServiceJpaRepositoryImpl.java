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
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.collaterals.data.CollateralDetailsData;
import org.apache.fineract.portfolio.collaterals.data.CollateralsDataValidator;
import org.apache.fineract.portfolio.collaterals.domain.Collaterals;
import org.apache.fineract.portfolio.collaterals.domain.CollateralsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.PledgeRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.QualityStandards;
import org.apache.fineract.portfolio.collaterals.domain.QualityStandardsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.exception.QualityStandardAttatchedToPledge;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QualityStandardsWritePlatformServiceJpaRepositoryImpl implements QualityStandardsWritePlatformService{
	
    private final PlatformSecurityContext context;
    private final CollateralsDataValidator fromApiJsonDeserializer;
    private final CollateralsRepositoryWrapper repositoryWrapper;
    private final QualityStandardsRepositoryWrapper qualityStandardsRepositoryWrapper;
    @Autowired
    public QualityStandardsWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final CollateralsDataValidator fromApiJsonDeserializer,
            final QualityStandardsRepositoryWrapper qualityStandardsRepositoryWrapper, final CollateralsRepositoryWrapper repositoryWrapper) {

        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.repositoryWrapper = repositoryWrapper;
        this.qualityStandardsRepositoryWrapper = qualityStandardsRepositoryWrapper;
    }
    
        
    @Transactional
    @Override
    public CommandProcessingResult createQualityStandards(final JsonCommand command) {
        final AppUser appUser = this.context.authenticatedUser();
        Date currentDate = new Date();
        this.fromApiJsonDeserializer.validateForCreateQualityStandards(command);
        final Long collateralId = command.longValueOfParameterNamed(CollateralsApiConstants.collateralIdParamName);
        final String name = command.stringValueOfParameterNamed(CollateralsApiConstants.nameParamName);
        final String description = command.stringValueOfParameterNamed(CollateralsApiConstants.descriptionParamName);
        final BigDecimal perecntagePrice = command.bigDecimalValueOfParameterNamed(CollateralsApiConstants.percentagePriceParamName);
        final BigDecimal absolutePrice = command.bigDecimalValueOfParameterNamed(CollateralsApiConstants.absolutePriceParamName);

        Collaterals collateral = this.repositoryWrapper.findOneWithNotFoundDetection(collateralId);

        QualityStandards qualityStandard = QualityStandards.createQualityStandards(collateral, name, description, perecntagePrice,
                absolutePrice, appUser, currentDate);
        if (qualityStandard != null) {
            this.qualityStandardsRepositoryWrapper.save(qualityStandard);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(collateral.getId())
                .withSubEntityId(qualityStandard.getId()).build();
    }

    @Transactional
    @Override
    public CommandProcessingResult updateQualityStandards(final Long collateralId, final Long qualityStandardsId, final JsonCommand command) {

        final AppUser appUser = this.context.authenticatedUser();
        this.fromApiJsonDeserializer.validateForUpdateQualityStandards(command);

        QualityStandards qualityStandardforUpdate = this.qualityStandardsRepositoryWrapper.findOneWithNotFoundDetection(qualityStandardsId);

        final Map<String, Object> changes = qualityStandardforUpdate.update(command);

        if (!changes.isEmpty()) {
            qualityStandardforUpdate.setUpdatedBy(appUser);
            qualityStandardforUpdate.setUpdatedDate(new Date());
            this.qualityStandardsRepositoryWrapper.saveAndFlush(qualityStandardforUpdate);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(collateralId) //
                .withSubEntityId(qualityStandardsId).with(changes).build();

    }

    @Transactional
    @Override
    public CommandProcessingResult deleteQualityStandards(final Long qualityStandardId) {
    	this.fromApiJsonDeserializer.validateForDeleteQualityStandards(qualityStandardId);
        final QualityStandards qualityStandard = this.qualityStandardsRepositoryWrapper.findOneWithNotFoundDetection(qualityStandardId);        
        if (qualityStandard != null) {
            this.qualityStandardsRepositoryWrapper.delete(qualityStandard);
        }

        return new CommandProcessingResultBuilder() //
                .withSubEntityId(qualityStandardId) //
                .build();

    }

}
