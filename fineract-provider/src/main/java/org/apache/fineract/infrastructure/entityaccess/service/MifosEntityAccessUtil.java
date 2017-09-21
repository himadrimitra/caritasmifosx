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

import java.util.Collection;
import java.util.Iterator;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.entityaccess.MifosEntityAccessConstants;
import org.apache.fineract.infrastructure.entityaccess.domain.MifosEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.domain.MifosEntityType;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MifosEntityAccessUtil {
    
    private final PlatformSecurityContext context;
    private final GlobalConfigurationRepositoryWrapper globalConfigurationRepository;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final MifosEntityAccessWriteService mifosEntityAccessWriteService;
    private final MifosEntityAccessReadService mifosEntityAccessReadService;

    @Autowired
    public MifosEntityAccessUtil (
    		final PlatformSecurityContext context,
    		final GlobalConfigurationRepositoryWrapper globalConfigurationRepository,
            final MifosEntityAccessWriteService mifosEntityAccessWriteService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final CodeValueRepositoryWrapper codeValueRepository,
            final MifosEntityAccessReadService mifosEntityAccessReadService) {
    	this.context = context;
        this.globalConfigurationRepository = globalConfigurationRepository;
        this.mifosEntityAccessWriteService = mifosEntityAccessWriteService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.codeValueRepository = codeValueRepository;
        this.mifosEntityAccessReadService = mifosEntityAccessReadService;
    }

	
	@Transactional
	public void checkConfigurationAndAddProductResrictionsForUserOffice (
			final MifosEntityAccessType mifosEntityAccessType,
			final MifosEntityType mifosEntityType,
			final Long productOrChargeId) {
		
		AppUser thisUser = this.context.authenticatedUser();
		
		// check if the office specific products are enabled. If yes, then save this product or charge against a specific office
        // i.e. this product or charge is specific for this office.
		
        final GlobalConfigurationProperty property = this.globalConfigurationRepository
        		.findOneByNameWithNotFoundDetection(
        				MifosEntityAccessConstants.GLOBAL_CONFIG_FOR_OFFICE_SPECIFIC_PRODUCTS);
        if (property.isEnabled() ) {
        	// If this property is enabled, then Mifos need to restrict access to this loan product to only the office of the current user            	
            final GlobalConfigurationProperty restrictToUserOfficeProperty = this.globalConfigurationRepository
            		.findOneByNameWithNotFoundDetection(
            				MifosEntityAccessConstants.GLOBAL_CONFIG_FOR_RESTRICT_PRODUCTS_TO_USER_OFFICE);
            
            if (restrictToUserOfficeProperty.isEnabled() ) {
            	final Long officeId = thisUser.getOffice().getId();
            	Collection<CodeValueData> codevalues = codeValueReadPlatformService.retrieveCodeValuesByCode(
            			MifosEntityAccessConstants.ENTITY_ACCESS_CODENAME);
            	if (codevalues != null) {
            		Iterator<CodeValueData> iterator = codevalues.iterator();
            		while(iterator.hasNext()) {
            			CodeValueData oneCodeValue = iterator.next();
            			if ( (oneCodeValue != null) &&
            					(oneCodeValue.getName().equals(mifosEntityAccessType.toStr())) ) {
            				CodeValue cv = codeValueRepository.findOneByCodeNameAndLabelWithNotFoundDetection(
            						MifosEntityAccessConstants.ENTITY_ACCESS_CODENAME,
            						mifosEntityAccessType.toStr()
            						);
            				if (cv != null) {
            					mifosEntityAccessWriteService.addNewEntityAccess(
            							MifosEntityType.OFFICE.getType(), officeId,
            							cv,
            							mifosEntityType.getType(), productOrChargeId);
            				}
            			}
            		}
            	}
            }
        }
		
	}
	
	public String getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled (
			MifosEntityType mifosEntityType) {
		String inClause = "";
		
		final GlobalConfigurationProperty property = this.globalConfigurationRepository
        		.findOneByNameWithNotFoundDetection(
        				MifosEntityAccessConstants.GLOBAL_CONFIG_FOR_OFFICE_SPECIFIC_PRODUCTS);
		
        if (property.isEnabled() ) {
        	// Get 'SQL In Clause' for fetching only products/charges that are relevant for current user's office
        	if (mifosEntityType.equals(MifosEntityType.SAVINGS_PRODUCT)) {
        		inClause = mifosEntityAccessReadService.
        				getSQLQueryInClauseIDList_ForSavingsProductsForOffice (
        				this.context.authenticatedUser().getOffice().getId(), false);
        	} else if (mifosEntityType.equals(MifosEntityType.LOAN_PRODUCT)) {
        		inClause = mifosEntityAccessReadService.
        				getSQLQueryInClauseIDList_ForLoanProductsForOffice (
        				this.context.authenticatedUser().getOffice().getId(), false);
        	} else if (mifosEntityType.equals(MifosEntityType.CHARGE)) {
        		inClause = mifosEntityAccessReadService.
        				getSQLQueryInClauseIDList_ForChargesForOffice(
        				this.context.authenticatedUser().getOffice().getId(), false);
        	}
        }
		return inClause;
	}
	
}