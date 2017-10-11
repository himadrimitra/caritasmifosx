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
package org.apache.fineract.infrastructure.entityaccess.service;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.entityaccess.FineractEntityAccessConstants;
import org.apache.fineract.infrastructure.entityaccess.data.FineractEntityToEntityMappingData;
import org.apache.fineract.infrastructure.entityaccess.domain.*;
import org.apache.fineract.infrastructure.entityaccess.exception.NotOfficeSpecificProductException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Service
public class FineractEntityAccessUtil {
    
    private final PlatformSecurityContext context;
    private final ConfigurationDomainService configurationDomainService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final FineractEntityAccessWriteService fineractEntityAccessWriteService;
    private final FineractEntityAccessReadService fineractEntityAccessReadService;
    private final FineractEntityRelationRepositoryWrapper fineractEntityRelationRepositoryWrapper;
    private final FineractEntityToEntityMappingRepository fineractEntityToEntityMappingRepository;


    @Autowired
    public FineractEntityAccessUtil (
    		final PlatformSecurityContext context,
    		final ConfigurationDomainService configurationDomainService,
            final FineractEntityAccessWriteService fineractEntityAccessWriteService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final CodeValueRepositoryWrapper codeValueRepository,
            final FineractEntityAccessReadService fineractEntityAccessReadService,
            final FineractEntityRelationRepositoryWrapper fineractEntityRelationRepositoryWrapper,
            final FineractEntityToEntityMappingRepository fineractEntityToEntityMappingRepository) {
    	this.context = context;
        this.configurationDomainService = configurationDomainService;
        this.fineractEntityAccessWriteService = fineractEntityAccessWriteService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.codeValueRepository = codeValueRepository;
        this.fineractEntityAccessReadService = fineractEntityAccessReadService;
        this.fineractEntityRelationRepositoryWrapper = fineractEntityRelationRepositoryWrapper;
        this.fineractEntityToEntityMappingRepository = fineractEntityToEntityMappingRepository;
    }

	
	@Transactional
	public void checkConfigurationAndAddProductResrictionsForUserOffice (
			final FineractEntityAccessType fineractEntityAccessType,
			final Long productOrChargeId) {
		
		AppUser thisUser = this.context.authenticatedUser();
		
		// check if the office specific products are enabled. If yes, then save this product or charge against a specific office
        // i.e. this product or charge is specific for this office.
		
        final GlobalConfigurationPropertyData property = this.configurationDomainService
        		.getGlobalConfigurationPropertyData(
        				FineractEntityAccessConstants.GLOBAL_CONFIG_FOR_OFFICE_SPECIFIC_PRODUCTS);
        if (property.isEnabled() ) {
        	// If this property is enabled, then Fineract need to restrict access to this loan product to only the office of the current user            	
            final GlobalConfigurationPropertyData restrictToUserOfficeProperty = this.configurationDomainService
            		.getGlobalConfigurationPropertyData(
            				FineractEntityAccessConstants.GLOBAL_CONFIG_FOR_RESTRICT_PRODUCTS_TO_USER_OFFICE);
            
            if (restrictToUserOfficeProperty.isEnabled() ) {
            	final Long officeId = thisUser.getOffice().getId();
            					Date startDateFormapping = null;
            					Date endDateFormapping = null;
            					FineractEntityRelation fineractEntityRelation = fineractEntityRelationRepositoryWrapper
            							.findOneByCodeName(fineractEntityAccessType.toStr());
            					Long relId = fineractEntityRelation.getId();
            					final FineractEntityRelation mapId = this.fineractEntityRelationRepositoryWrapper
            							.findOneWithNotFoundDetection(relId);
            					final FineractEntityToEntityMapping newMap = FineractEntityToEntityMapping.newMap(mapId, officeId,
            							productOrChargeId, startDateFormapping, endDateFormapping, false);
            					this.fineractEntityToEntityMappingRepository.save(newMap);
            				}
        }
		
	}
	
	public String getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled (
			FineractEntityType fineractEntityType) {
		String inClause = "";
		
		final GlobalConfigurationPropertyData property = this.configurationDomainService
        		.getGlobalConfigurationPropertyData(
        				FineractEntityAccessConstants.GLOBAL_CONFIG_FOR_OFFICE_SPECIFIC_PRODUCTS);
		
        if (property.isEnabled() ) {
        	// Get 'SQL In Clause' for fetching only products/charges that are relevant for current user's office
        	if (fineractEntityType.equals(FineractEntityType.SAVINGS_PRODUCT)) {
        		inClause = fineractEntityAccessReadService.
        				getSQLQueryInClauseIDList_ForSavingsProductsForOffice (
        				this.context.authenticatedUser().getOffice().getId(), false);
        	} else if (fineractEntityType.equals(FineractEntityType.LOAN_PRODUCT)) {
        		inClause = fineractEntityAccessReadService.
        				getSQLQueryInClauseIDList_ForLoanProductsForOffice (
        				this.context.authenticatedUser().getOffice().getId(), false);
        	} else if (fineractEntityType.equals(FineractEntityType.CHARGE)) {
        		inClause = fineractEntityAccessReadService.
        				getSQLQueryInClauseIDList_ForChargesForOffice(
        				this.context.authenticatedUser().getOffice().getId(), false);
        	}
        }
		return inClause;
	}

	public void checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice (
		final FineractEntityAccessType fineractEntityAccessType,
		final Long productOrChargeId) {
    	checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(fineractEntityAccessType,
			Arrays.asList(new Long[] {productOrChargeId}));
	}

	public void checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice (
		final FineractEntityAccessType fineractEntityAccessType,
		final Collection<Long> productOrChargeIds) {

		if(null != productOrChargeIds && productOrChargeIds.size() > 0){
			final GlobalConfigurationPropertyData property = this.configurationDomainService
				.getGlobalConfigurationPropertyData(
					FineractEntityAccessConstants.GLOBAL_CONFIG_FOR_OFFICE_SPECIFIC_PRODUCTS);
			if (property.isEnabled() ) {
				FineractEntityType firstEntityType = FineractEntityType.OFFICE;
				FineractEntityRelation fineractEntityRelation = fineractEntityRelationRepositoryWrapper
					.findOneByCodeName(fineractEntityAccessType.toStr());
				Long entityRelationId = fineractEntityRelation.getId();
				Long userOfficeId = this.context.authenticatedUser().getOffice().getId();
				boolean includeAllSubOffices = false;

				Collection<FineractEntityToEntityMappingData> allowedMapping = fineractEntityAccessReadService
					.retrieveEntityAccessFor(firstEntityType, entityRelationId, userOfficeId,false);
				if(null == allowedMapping || allowedMapping.size() == 0){
					throw new NotOfficeSpecificProductException(productOrChargeIds, userOfficeId);
				}
				Collection<Long> invalidIds = new ArrayList<>();
				for (Long productOrChargeId:productOrChargeIds) {
					if(!isProductOrChargeIdValid(productOrChargeId, allowedMapping)){
						invalidIds.add(productOrChargeId);
					}
				}
				if(invalidIds.size() > 0){
					throw new NotOfficeSpecificProductException(invalidIds, userOfficeId);
				}
			}
		}
	}

	private boolean isProductOrChargeIdValid(Long productOrChargeId, Collection<FineractEntityToEntityMappingData> allowedMapping) {
		for (FineractEntityToEntityMappingData mapping:allowedMapping) {
			if(productOrChargeId == mapping.getToId()){
				return true;
			}
		}
		return false;
	}

}