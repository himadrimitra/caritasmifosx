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
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants;
import org.apache.fineract.portfolio.collaterals.data.PledgeDataValidator;
import org.apache.fineract.portfolio.collaterals.domain.CollateralDetails;
import org.apache.fineract.portfolio.collaterals.domain.CollateralDetailsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.Collaterals;
import org.apache.fineract.portfolio.collaterals.domain.CollateralsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.PledgeRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.Pledges;
import org.apache.fineract.portfolio.collaterals.domain.QualityStandards;
import org.apache.fineract.portfolio.collaterals.domain.QualityStandardsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.exception.InvalidPledgeStateTransitionException;
import org.apache.fineract.portfolio.collaterals.exception.PledgeAssociateToLoanException;
import org.apache.fineract.portfolio.collaterals.exception.PledgeMustBeInInitiatedStateToDeleteException;
import org.apache.fineract.portfolio.collaterals.exception.UserValueExceedingSystemValueException;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class PledgeWritePlatformServiceJpaRepository implements PledgeWritePlatformService{
    
    private final PlatformSecurityContext context;
    private final PledgeDataValidator fromApiJsonDeserializer;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final CollateralsRepositoryWrapper collateralsRepositoryWrapper;
    private final QualityStandardsRepositoryWrapper qualityStandardsRepositoryWrapper;
    private final PledgeRepositoryWrapper pledgeRepository;
    private final CollateralDetailsRepositoryWrapper collateralDetailsRepository;
    
    @Autowired
    public PledgeWritePlatformServiceJpaRepository(final PlatformSecurityContext context, final PledgeDataValidator fromApiJsonDeserializer,
            final ClientRepositoryWrapper clientRepositoryWrapper, final LoanRepositoryWrapper loanRepositoryWrapper,
            final CollateralsRepositoryWrapper collateralsRepositoryWrapper, final QualityStandardsRepositoryWrapper qualityStandardsRepositoryWrapper,
            final PledgeRepositoryWrapper pledgeRepository, final CollateralDetailsRepositoryWrapper collateralDetailsRepository) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.collateralsRepositoryWrapper = collateralsRepositoryWrapper;
        this.qualityStandardsRepositoryWrapper = qualityStandardsRepositoryWrapper;
        this.pledgeRepository = pledgeRepository;
        this.collateralDetailsRepository = collateralDetailsRepository;
    }


    @Transactional
    @Override
    public CommandProcessingResult createPledge(final JsonCommand command) {
        
    	final AppUser appUser = this.context.authenticatedUser();
    	Date currentDate = new Date();
        this.fromApiJsonDeserializer.validateForCreate(command);
        
        final Long clientId = command.longValueOfParameterNamed(PledgeApiConstants.clientIdParamName);
        final Long loanId = command.longValueOfParameterNamed(PledgeApiConstants.loanIdParamName);
        final Long sealNumber = command.longValueOfParameterNamed(PledgeApiConstants.sealNumberParamName);
        final Integer status = command.integerValueOfParameterNamed(PledgeApiConstants.statusParamName);
        final BigDecimal  systemValue = command.bigDecimalValueOfParameterNamed(PledgeApiConstants.systemValueParamName);
        final BigDecimal  userValue = command.bigDecimalValueOfParameterNamed(PledgeApiConstants.userValueParamName);
        
        validateUserValueShouldNotExceedSytemValue(systemValue, userValue);
        
        Client client = null;
        Loan loan = null;
        if(clientId != null){
            client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
        }
        
        if(loanId != null){
            loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
        }
        
        final Set<CollateralDetails> collateralDetails = parseFromJson(command);
        
        final String pledgeNumber = generateRandomPledgeNumber();
        
        final Pledges pledge = Pledges.instance(client, loan, pledgeNumber, sealNumber, status, systemValue, userValue, null, null, collateralDetails, appUser, currentDate);
        
        if(pledge != null){
            this.pledgeRepository.save(pledge);
        }
        
        return new CommandProcessingResultBuilder() //
        .withCommandId(command.commandId()) //
        .withEntityId(pledge.getId()) //
        .build();
    }


    private String generateRandomPledgeNumber() {
        StringBuffer sb = new StringBuffer();
        for (int i=0;  i<16;  i++) {
           int index = (int)(Math.random()* PledgeApiConstants.ALPHA_NUM.length());
           if(i>0 && i%4 == 0){
              sb.append("-");
           }
           sb.append(PledgeApiConstants.ALPHA_NUM.charAt(index));

        }
        return sb.toString();
    }


    private void validateUserValueShouldNotExceedSytemValue(final BigDecimal systemValue, final BigDecimal userValue) { 
        if(systemValue.intValue() < userValue.intValue()){
            final String errorMessage = "user value " + userValue +" shouldn't be greter than system value " + systemValue + " for pledge";
            throw new UserValueExceedingSystemValueException("user.value.exceeding.system.value", errorMessage, systemValue);
        }
    }


    private Set<CollateralDetails> parseFromJson(final JsonCommand command) {
        
        Set<CollateralDetails> collateralDetailsData = new HashSet<>();
        if(command.parameterExists(PledgeApiConstants.collateralDetailsParamName)){
            final JsonArray collateralsDetails = command.arrayOfParameterNamed(PledgeApiConstants.collateralDetailsParamName);
            if(!collateralsDetails.isJsonNull() && collateralsDetails.size() > 0){
                
                for (int i = 0; i < collateralsDetails.size(); i++) {

                    final JsonObject jsonObject = collateralsDetails.get(i).getAsJsonObject();
                    Collaterals collateral = null;
                    QualityStandards qualityStandards = null;
                    String description = null;
                    BigDecimal grossWeight = null;
                    BigDecimal netWeight = null;
                    final Long collateralId = jsonObject.get(PledgeApiConstants.collateralIdParamName).getAsLong();
                    final Long qualityStandardId = jsonObject.get(PledgeApiConstants.qualityStandardIdParamName).getAsLong();
                    if(collateralId != null){
                        collateral = this.collateralsRepositoryWrapper.findOneWithNotFoundDetection(collateralId);
                    }
                    if(qualityStandardId != null){
                        qualityStandards = this.qualityStandardsRepositoryWrapper.findOneWithNotFoundDetection(qualityStandardId);
                    }
                    
                    if (jsonObject.has(PledgeApiConstants.descriptionParamName)) {
                        description = jsonObject.get(PledgeApiConstants.descriptionParamName).getAsString();    
                    }
                    if(jsonObject.has(PledgeApiConstants.grossWeightParamName)){
                        grossWeight = jsonObject.get(PledgeApiConstants.grossWeightParamName).getAsBigDecimal();
                    }
                    if(jsonObject.has(PledgeApiConstants.netWeightParamName)){
                        netWeight = jsonObject.get(PledgeApiConstants.netWeightParamName).getAsBigDecimal();
                    }
                    final BigDecimal systemPrice = jsonObject.get(PledgeApiConstants.systemPriceParamName).getAsBigDecimal();
                    final BigDecimal userPrice = jsonObject.get(PledgeApiConstants.userPriceParamName).getAsBigDecimal();
                    
                    collateralDetailsData.add(CollateralDetails.createNewWithoutLoan(collateral, qualityStandards, description, grossWeight, netWeight,
                            systemPrice, userPrice));
                    
                }
                
            }
            
        }
             
        return collateralDetailsData;
    }


    @Transactional
    @Override
    public CommandProcessingResult updatePledge(final Long pledgeId, final JsonCommand command) {
        
    	final AppUser appUser = this.context.authenticatedUser();
        this.fromApiJsonDeserializer.validateForUpdate(command);
        
        final Pledges pledgeForUpdate = this.pledgeRepository.findOneWithNotFoundDetection(pledgeId); 
        
        final BigDecimal systemValue = command.bigDecimalValueOfParameterNamed(PledgeApiConstants.systemValueParamName);
        final BigDecimal userValue = command.bigDecimalValueOfParameterNamed(PledgeApiConstants.userValueParamName);
        if(systemValue != null && userValue != null){
            validateUserValueShouldNotExceedSytemValue(systemValue, userValue);
        }
        
        final Long loanId = command.longValueOfParameterNamed(PledgeApiConstants.loanIdParamName);
        if(loanId != null){
            validatePledgeAssociatedToLoan(pledgeForUpdate);
            Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            pledgeForUpdate.updateLoanId(loan);
        }
        final Long clientId = command.longValueOfParameterNamed(PledgeApiConstants.clientIdParamName);
        if(clientId != null){
            Client client  = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            pledgeForUpdate.updateClientId(client);
        }
        
        final Set<CollateralDetails> collateralDetails = parseFromJson(command);
        
        final Map<String, Object> changes = pledgeForUpdate.update(command, collateralDetails);
        
        final String collateralParamName = "collateralDetails";
        if (changes.containsKey(collateralParamName)) {
            pledgeForUpdate.updateCollateralDetails(collateralDetails);
        }
      
        if (!changes.isEmpty()) {
        	pledgeForUpdate.setUpdatedBy(appUser);
        	pledgeForUpdate.setUpdatedDate(new Date());
            this.pledgeRepository.saveAndFlush(pledgeForUpdate);
        }
        
        return new CommandProcessingResultBuilder() //
            .withCommandId(command.commandId()) //
            .withEntityId(pledgeId) //
            .with(changes)
            .build();
    }


    private void validatePledgeAssociatedToLoan(final Pledges pledge) {
        if(pledge.isAssociatedToLoan()){
            final String errorMessage = pledge.getSealNumber()
                    + " pledge is already associated with loan Idenfier " +pledge.getloan().getAccountNumber()+" ";
            throw new PledgeAssociateToLoanException("attach", "loan.already.associated" , errorMessage);
        }
        
    }


    @Transactional
    @Override
    public CommandProcessingResult closePledge(final Long pledgeId, final JsonCommand command) {
        this.fromApiJsonDeserializer.validateForClosePledge(command);
        final Pledges pledge = this.pledgeRepository.findOneWithNotFoundDetection(pledgeId);
        final LocalDate closureDate = command.localDateValueOfParameterNamed(PledgeApiConstants.closureDateParamName);
        
        final AppUser currentUser = this.context.authenticatedUser();
        
        if(pledge.isAssociatedToLoan()){
            final String errorMessage = pledge.getSealNumber()
                    + " cannot be closed because of active loans associated with it.";
            throw new InvalidPledgeStateTransitionException("close", "active.loan.exist", errorMessage);
        }
        
        pledge.close(currentUser, closureDate);
        
        this.pledgeRepository.saveAndFlush(pledge);
        
        return new CommandProcessingResultBuilder() //
            .withEntityId(pledgeId) //
            .build();
    }


    @Transactional
    @Override
    public CommandProcessingResult deletePledge(final Long pledgeId) {
        
        final Pledges pledge = this.pledgeRepository.findOneWithNotFoundDetection(pledgeId);
        
        if(pledge.isNotInitiated()){
            throw new PledgeMustBeInInitiatedStateToDeleteException(pledgeId);
        }
        
        this.pledgeRepository.delete(pledge);
        
        return new CommandProcessingResultBuilder() //
            .withEntityId(pledgeId) //
            .build();
    }


    @Transactional
    @Override
    public CommandProcessingResult deleteCollateralDetails(final Long pledgeId, final Long collateralDetailId) {
        
        final CollateralDetails collateralDetail = this.collateralDetailsRepository.findOneWithNotFoundDetection(collateralDetailId);
        
        final Pledges pledge = this.pledgeRepository.findOneWithNotFoundDetection(pledgeId);
        
        if(pledge.isAssociatedToLoan()){
            final String errorMessage = " collateral Detail with id " +collateralDetailId + " cannot be deleted because pledge" +
                        " with id " +pledgeId+ "  is associated with loan Idenfier " + pledge.getloan().getAccountNumber() + " ";
            throw new PledgeAssociateToLoanException("delete", "loan.already.associated", errorMessage);
        }
        pledge.removeCollateralDetails(collateralDetail);
        Set<CollateralDetails> collateralDetails = pledge.getCollateralDetails();
        updateSystemAndUserCalculatedValues(pledge, collateralDetails);
        this.pledgeRepository.save(pledge);

        return new CommandProcessingResultBuilder() //
            .withEntityId(collateralDetailId) //
            .build();
    }


    private void updateSystemAndUserCalculatedValues(Pledges pledge, Set<CollateralDetails> collateralDetails) {
        BigDecimal totalSystemValue = new BigDecimal(0);
        BigDecimal totalUserValue = new BigDecimal(0);
        
        for(CollateralDetails collateralDetail : collateralDetails){
            totalSystemValue = totalSystemValue.add(collateralDetail.getSystemPrice());
            totalUserValue = totalUserValue.add(collateralDetail.getUserPrice());
        }
        pledge.updateSystemValue(totalSystemValue);
        pledge.updateUserValue(totalUserValue);  
    }    

}
