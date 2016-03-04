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


import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformServiceJpaRepositoryImpl;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.collaterals.data.ProductCollateralsMappingDataValidator;
import org.apache.fineract.portfolio.collaterals.domain.Collaterals;
import org.apache.fineract.portfolio.collaterals.domain.CollateralsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.ProductCollateralsMapping;
import org.apache.fineract.portfolio.collaterals.domain.ProductCollateralsMappingRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
@SuppressWarnings("unused")
@Service
public class ProductCollateralsMappingWritePlatformServiceJpaRepositoryImpl implements ProductCollateralsMappingWritePlatformService{
    private final static Logger logger = LoggerFactory.getLogger(ProductCollateralsMappingWritePlatformServiceJpaRepositoryImpl.class);
    private final PlatformSecurityContext context;
    private final ProductCollateralsMappingRepository productCollateralsMappingRepository;
    private final ProductCollateralsMappingDataValidator productCollateralsMappingDataValidator;
    private final CollateralsRepositoryWrapper repositoryWrapper;
    private final LoanProductRepository loanProductRepository;
    
    
    @Autowired
    private ProductCollateralsMappingWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final ProductCollateralsMappingRepository productCollateralsMappingRepository,
            final ProductCollateralsMappingDataValidator productCollateralsMappingDataValidator, final CollateralsRepositoryWrapper repositoryWrapper, final LoanProductRepository loanProductRepository) {
        this.context = context;
        this.productCollateralsMappingRepository = productCollateralsMappingRepository;
        this.productCollateralsMappingDataValidator = productCollateralsMappingDataValidator;
        this.repositoryWrapper = repositoryWrapper;
        this.loanProductRepository = loanProductRepository;
    }

    @SuppressWarnings("null")
    @Override
    public CommandProcessingResult createProductCollateralMapping(JsonCommand command) {
        try {            
            final AppUser appUser = this.context.authenticatedUser();
            this.productCollateralsMappingDataValidator.validateForCreate(command);
            final Long collateralId = command.longValueOfParameterNamed(CollateralsApiConstants.collateralIdParamName);
            final Long productId = command.longValueOfParameterNamed(CollateralsApiConstants.productIdParamName);
            
            LoanProduct loanProduct = this.loanProductRepository.findOne(productId);
            Collaterals collateral = this.repositoryWrapper.findOneWithNotFoundDetection(collateralId);
            
            ProductCollateralsMapping productCollateralsMapping = ProductCollateralsMapping.createProductCollateralsMapping(loanProduct, collateral);
            if(productCollateralsMapping != null){
                this.productCollateralsMappingRepository.save(productCollateralsMapping);
            } 
            
            return new CommandProcessingResultBuilder() 
                    .withCommandId(command.commandId()) 
                    .withEntityId(loanProduct.getId())
                    .withSubEntityId(productCollateralsMapping.getId())
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }

    }
    
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("unique_product_collateral_mapping")) {
            throw new PlatformDataIntegrityException("error.msg.product.collateral.mapping.duplicate", "Mapping is`"
                    + "` already exists");
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.product.collateral.mapping.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
    
    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

    @Override
    public CommandProcessingResult deleteProductCollateralsMapping(Long productCollateralsMappingId) {
        final ProductCollateralsMapping productCollateralsMapping  = this.productCollateralsMappingRepository.findOne(productCollateralsMappingId);
        if(productCollateralsMapping != null){
                this.productCollateralsMappingRepository.delete(productCollateralsMappingId);
        }

        return new CommandProcessingResultBuilder() //
                .withSubEntityId(productCollateralsMappingId) //
                .build();
    }

    @Override
    public CommandProcessingResult updateProductCollateralsMapping(Long loanProductId,Long productCollateralsMappingId, JsonCommand command) {
        try {
            final AppUser appUser = this.context.authenticatedUser();
            this.productCollateralsMappingDataValidator.validateForUpdate(command);
            
            ProductCollateralsMapping productCollateralsMapping  = this.productCollateralsMappingRepository.findOne(productCollateralsMappingId);
            
            final Map<String, Object> changes = productCollateralsMapping.update(command);
            
            if (changes.containsKey(CollateralsApiConstants.collateralIdParamName)) {
    
                final Long newCollatearlId = command.longValueOfParameterNamed(CollateralsApiConstants.collateralIdParamName);
                Collaterals newCollateral = null;
                if (newCollatearlId != null) {
                    newCollateral = this.repositoryWrapper.findOneWithNotFoundDetection(newCollatearlId);
                }
                productCollateralsMapping.setCollateral(newCollateral);
            }
            
            if (changes.containsKey(CollateralsApiConstants.productIdParamName)) {
    
                final Long newProductLoanId = command.longValueOfParameterNamed(CollateralsApiConstants.productIdParamName);
                LoanProduct loanProduct = null;
                if (newProductLoanId != null) {
                    loanProduct = this.loanProductRepository.findOne(newProductLoanId);
                }
                productCollateralsMapping.setProduct(loanProduct);
            }
            if (!changes.isEmpty()) {
                this.productCollateralsMappingRepository.saveAndFlush(productCollateralsMapping);
            }
            
            return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanProductId) //
                .withSubEntityId(productCollateralsMappingId)
                .with(changes)
                .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

}
