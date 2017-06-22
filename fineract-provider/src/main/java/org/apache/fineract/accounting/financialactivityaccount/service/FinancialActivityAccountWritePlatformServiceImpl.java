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
package org.apache.fineract.accounting.financialactivityaccount.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.financialactivityaccount.api.FinancialActivityAccountsJsonInputParams;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccount;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountPaymentTypeMapping;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountPaymentTypeMappingRepository;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.apache.fineract.accounting.financialactivityaccount.exception.DuplicateFinancialActivityAccountFoundException;
import org.apache.fineract.accounting.financialactivityaccount.exception.DuplicatePaymentTypeFoundInFinanacialAvtivityMappingException;
import org.apache.fineract.accounting.financialactivityaccount.exception.FinancialActivityAccountInvalidException;
import org.apache.fineract.accounting.financialactivityaccount.exception.FinancialActivityAccountNotFoundException;
import org.apache.fineract.accounting.financialactivityaccount.serialization.FinancialActivityAccountDataValidator;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.exception.GLAccountDuplicateException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.portfolio.paymenttype.exception.PaymentTypeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class FinancialActivityAccountWritePlatformServiceImpl implements FinancialActivityAccountWritePlatformService {

    private final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository;
    private final FinancialActivityAccountPaymentTypeMappingRepository financialActivityAccountPaymentTypeMappingRepository;
    private final FinancialActivityAccountDataValidator fromApiJsonDeserializer;
    private final GLAccountRepositoryWrapper glAccountRepositoryWrapper;
    private final static Logger logger = LoggerFactory.getLogger(FinancialActivityAccountWritePlatformServiceImpl.class);
    private final FromJsonHelper fromApiJsonHelper;
    private final PaymentTypeRepositoryWrapper paymentTypeRepository;
    boolean financialActivityAccountPaymentTypeMappingListChanges = false;

    @Autowired
    public FinancialActivityAccountWritePlatformServiceImpl(
            final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository,
            final FinancialActivityAccountDataValidator fromApiJsonDeserializer, final GLAccountRepositoryWrapper glAccountRepositoryWrapper,
            final FromJsonHelper fromApiJsonHelper, final PaymentTypeRepositoryWrapper paymentTypeRepository,
            final FinancialActivityAccountPaymentTypeMappingRepository financialActivityAccountPaymentTypeMappingRepository) {
        this.financialActivityAccountRepository = financialActivityAccountRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.glAccountRepositoryWrapper = glAccountRepositoryWrapper;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.paymentTypeRepository = paymentTypeRepository;
        this.financialActivityAccountPaymentTypeMappingRepository = financialActivityAccountPaymentTypeMappingRepository;
    }

    @Override
    public CommandProcessingResult createFinancialActivityAccountMapping(JsonCommand command) {
        try {

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final Integer financialActivityId = command
                    .integerValueSansLocaleOfParameterNamed(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue());
            final Long accountId = command.longValueOfParameterNamed(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue());
            final GLAccount glAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(accountId);
            FinancialActivityAccount financialActivityAccount = FinancialActivityAccount.createNew(glAccount, financialActivityId);

            validateFinancialActivityAndAccountMapping(financialActivityAccount);
            List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMappingList = createAdvancedMappingWthPaymentType(
                    command.json());
            if (financialActivityAccountPaymentTypeMappingList.size() > 0) {
                financialActivityAccount.addAllFinancialActivityAccountPaymentTypeMapping(financialActivityAccountPaymentTypeMappingList);
            }
            
            this.financialActivityAccountRepository.save(financialActivityAccount);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(financialActivityAccount.getId()) //
                    .build();
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            handleFinancialActivityAccountDataIntegrityIssues(command, dataIntegrityViolationException);
            return CommandProcessingResult.empty();
        }
    }

    /**
     * Validate that the GL Account is appropriate for the particular Financial
     * Activity Type
     **/
    private void validateFinancialActivityAndAccountMapping(FinancialActivityAccount financialActivityAccount) {
        FINANCIAL_ACTIVITY financialActivity = FINANCIAL_ACTIVITY.fromInt(financialActivityAccount.getFinancialActivityType());
        GLAccount glAccount = financialActivityAccount.getGlAccount();
        if (!financialActivity.getMappedGLAccountType().getValue().equals(glAccount.getType())) { throw new FinancialActivityAccountInvalidException(
                financialActivity, glAccount); }
    }
    

    @Override
    public CommandProcessingResult updateGLAccountActivityMapping(Long financialActivityAccountId, JsonCommand command) {
        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json());
            final FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                    .findOneWithNotFoundDetection(financialActivityAccountId);
            Map<String, Object> changes = findChanges(command, financialActivityAccount);

            if (changes.containsKey(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue())) {
                final Long accountId = command.longValueOfParameterNamed(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue());
                final GLAccount glAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(accountId);
                financialActivityAccount.updateGlAccount(glAccount);
            }

            if (changes.containsKey(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue())) {
                final Integer financialActivityId = command
                        .integerValueSansLocaleOfParameterNamed(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue());
                financialActivityAccount.updateFinancialActivityType(financialActivityId);
            }
           
            List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMappingList = updateAdvancedMappingWthPaymentType(
                    command.json(), financialActivityAccount, changes);
            if(changes.containsKey(FinancialActivityAccountsJsonInputParams.ADVANCED_FINANCIAL_ACTIVITY_MAPPING.getValue())){
            financialActivityAccount.updateFinancialActivityAccountPaymentTypeMapping(financialActivityAccountPaymentTypeMappingList);
            }
            
            if (!changes.isEmpty()) {
                validateFinancialActivityAndAccountMapping(financialActivityAccount);
                this.financialActivityAccountRepository.save(financialActivityAccount);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(financialActivityAccountId) //
                    .with(changes) //
                    .build();
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            handleFinancialActivityAccountDataIntegrityIssues(command, dataIntegrityViolationException);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult deleteGLAccountActivityMapping(Long financialActivityAccountId, JsonCommand command) {
        final FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                .findOneWithNotFoundDetection(financialActivityAccountId);
        this.financialActivityAccountRepository.delete(financialActivityAccount);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(financialActivityAccountId) //
                .build();
    }

    private void handleFinancialActivityAccountDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("financial_activity_type")) {
            final Integer financialActivityId = command
                    .integerValueSansLocaleOfParameterNamed(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue());
            throw new DuplicateFinancialActivityAccountFoundException(financialActivityId);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.glAccount.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource GL Account: " + realCause.getMessage());
    }

    public Map<String, Object> findChanges(JsonCommand command, FinancialActivityAccount financialActivityAccount) {

        Map<String, Object> changes = new HashMap<>();

        Long existingGLAccountId = financialActivityAccount.getGlAccount().getId();
        Integer financialActivityType = financialActivityAccount.getFinancialActivityType();

        // is the account Id changed?
        if (command.isChangeInLongParameterNamed(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue(), existingGLAccountId)) {
            final Long newValue = command.longValueOfParameterNamed(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue());
            changes.put(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue(), newValue);
        }

        // is the financial Activity changed
        if (command.isChangeInIntegerSansLocaleParameterNamed(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue(),
                financialActivityType)) {
            final Integer newValue = command
                    .integerValueSansLocaleOfParameterNamed(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue());
            changes.put(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ID.getValue(), newValue);
        }
        return changes;
    }
    
    private List<FinancialActivityAccountPaymentTypeMapping> createAdvancedMappingWthPaymentType(String json) {
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMappingList = new ArrayList<FinancialActivityAccountPaymentTypeMapping>();
        if (element.isJsonObject()) {
            if (topLevelJsonElement.has(FinancialActivityAccountsJsonInputParams.ADVANCED_FINANCIAL_ACTIVITY_MAPPING.getValue())) {
                JsonElement advancedFinacialActivityMapping = topLevelJsonElement
                        .get(FinancialActivityAccountsJsonInputParams.ADVANCED_FINANCIAL_ACTIVITY_MAPPING.getValue());
                JsonObject advancedFinacialActivityMappingElement = advancedFinacialActivityMapping.getAsJsonObject();
                if (advancedFinacialActivityMappingElement
                        .has(FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ACCOUNT_MAPPING.getValue())) {
                    final JsonArray array = advancedFinacialActivityMappingElement
                            .get(FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ACCOUNT_MAPPING.getValue()).getAsJsonArray();
                    for (int i = 0; i < array.size(); i++) {
                        final JsonObject finacialActivityMappingElement = array.get(i).getAsJsonObject();
                        FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping = createFinancialActivityAccountPaymentTypeMapping(
                                finacialActivityMappingElement);
                        financialActivityAccountPaymentTypeMappingList.add(financialActivityAccountPaymentTypeMapping);
                    }
                }
            }
        }
        if (financialActivityAccountPaymentTypeMappingList.size() > 1) {
            handleFinancialActivityAccountMappingDataIntegrityIssues(financialActivityAccountPaymentTypeMappingList);
        }
        return financialActivityAccountPaymentTypeMappingList;

    }
    
    private FinancialActivityAccountPaymentTypeMapping createFinancialActivityAccountPaymentTypeMapping(JsonObject finacialActivityMappingElement
            ){
        final Long glAccountId = this.fromApiJsonHelper.extractLongNamed(
                FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue(), finacialActivityMappingElement);
        final GLAccount glAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(glAccountId);
        final Long paymentTypeId = this.fromApiJsonHelper.extractLongNamed(
                FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ID.getValue(), finacialActivityMappingElement);
        final PaymentType paymentType = this.paymentTypeRepository.findOneWithNotFoundDetection(paymentTypeId);
        return  FinancialActivityAccountPaymentTypeMapping.createNew(glAccount, paymentType);
    }
    
    private List<FinancialActivityAccountPaymentTypeMapping> updateAdvancedMappingWthPaymentType(String json,
            final FinancialActivityAccount financialActivityAccount, Map<String, Object> changes) {
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        List<FinancialActivityAccountPaymentTypeMapping> existigFinancialActivityAccountPaymentTypeMappingList = new ArrayList<FinancialActivityAccountPaymentTypeMapping>();
        existigFinancialActivityAccountPaymentTypeMappingList = financialActivityAccount.getFinancialActivityAccountPaymentTypeMapping();
        Map<Long, FinancialActivityAccountPaymentTypeMapping> existigFinancialActivityAccountPaymentTypeMappingtMap = new HashMap<>();
        for (FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping : existigFinancialActivityAccountPaymentTypeMappingList) {
            existigFinancialActivityAccountPaymentTypeMappingtMap.put(financialActivityAccountPaymentTypeMapping.getId(),
                    financialActivityAccountPaymentTypeMapping);
        }
        final List<Map<String, Object>> financialActivityAccountPaymentTypeMappingChangesList = new ArrayList<>();
        changes.put(FinancialActivityAccountsJsonInputParams.ADVANCED_FINANCIAL_ACTIVITY_MAPPING.getValue(),
                financialActivityAccountPaymentTypeMappingChangesList);
        if (element.isJsonObject()) {
            if (topLevelJsonElement.has(FinancialActivityAccountsJsonInputParams.ADVANCED_FINANCIAL_ACTIVITY_MAPPING.getValue())) {
                JsonElement advancedFinacialActivityMapping = topLevelJsonElement
                        .get(FinancialActivityAccountsJsonInputParams.ADVANCED_FINANCIAL_ACTIVITY_MAPPING.getValue());
                JsonObject advancedFinacialActivityMappingElement = advancedFinacialActivityMapping.getAsJsonObject();
                if (advancedFinacialActivityMappingElement
                        .has(FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ACCOUNT_MAPPING.getValue())) {

                    final JsonArray paymentTypeAccountmappingArray = advancedFinacialActivityMappingElement
                            .get(FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ACCOUNT_MAPPING.getValue()).getAsJsonArray();

                    for (int i = 0; i < paymentTypeAccountmappingArray.size(); i++) {
                        final JsonObject finacialActivityMappingElementForUpdate = paymentTypeAccountmappingArray.get(i).getAsJsonObject();
                        final Long id = this.fromApiJsonHelper.extractLongNamed(FinancialActivityAccountsJsonInputParams.ID.getValue(),
                                finacialActivityMappingElementForUpdate);
                        final Boolean isDeleted = this.fromApiJsonHelper.extractBooleanNamed(
                                FinancialActivityAccountsJsonInputParams.DELETE.getValue(), finacialActivityMappingElementForUpdate);
                        final Map<String, Object> changeDetail = new HashMap<>(1);
                        if (id == null) {
                            FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping = createFinancialActivityAccountPaymentTypeMapping(
                                    finacialActivityMappingElementForUpdate);

                            existigFinancialActivityAccountPaymentTypeMappingList.add(financialActivityAccountPaymentTypeMapping);
                            final Map<String, Object> changeDetails = new HashMap<>(2);
                            changeDetails.put(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue(),
                                    financialActivityAccountPaymentTypeMapping.getGlAccount().getId());
                            changeDetails.put(FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ID.getValue(),
                                    financialActivityAccountPaymentTypeMapping.getPaymentType().getId());
                            changeDetail.put(FinancialActivityAccountsJsonInputParams.FINANCIAL_ACTIVITY_ACCOUNT_MAPPING.getValue(),
                                    changeDetails);
                        } else {
                            if (isDeleted != null && isDeleted) {
                                FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping = existigFinancialActivityAccountPaymentTypeMappingtMap
                                        .get(id);
                                if (financialActivityAccountPaymentTypeMapping == null) { throw new FinancialActivityAccountNotFoundException(
                                        id); }
                                existigFinancialActivityAccountPaymentTypeMappingList.remove(financialActivityAccountPaymentTypeMapping);
                                changeDetail.put(FinancialActivityAccountsJsonInputParams.ID.getValue(), id);
                                changeDetail.put("isDeleted", true);
                            } else {
                                FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping = existigFinancialActivityAccountPaymentTypeMappingtMap
                                        .get(id);
                                if (financialActivityAccountPaymentTypeMapping == null) { throw new FinancialActivityAccountNotFoundException(
                                        id); }
                                final Long glAccountId = this.fromApiJsonHelper.extractLongNamed(
                                        FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue(),
                                        finacialActivityMappingElementForUpdate);
                                final GLAccount glAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(glAccountId);
                                if (glAccount != financialActivityAccountPaymentTypeMapping.getGlAccount()) {
                                    financialActivityAccountPaymentTypeMapping.updateGlAccount(glAccount);
                                    changeDetail.put(FinancialActivityAccountsJsonInputParams.GL_ACCOUNT_ID.getValue(), glAccount.getId());
                                }
                                final Long paymentTypeId = this.fromApiJsonHelper.extractLongNamed(
                                        FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ID.getValue(),
                                        finacialActivityMappingElementForUpdate);
                                final PaymentType paymentType = this.paymentTypeRepository.findOneWithNotFoundDetection(paymentTypeId);
                                if (paymentType != financialActivityAccountPaymentTypeMapping.getPaymentType()) {
                                    financialActivityAccountPaymentTypeMapping.updatePaymentType(paymentType);
                                    changeDetail.put(FinancialActivityAccountsJsonInputParams.PAYMENT_TYPE_ID.getValue(),
                                            paymentType.getId());
                                }
                            }
                        }
                        financialActivityAccountPaymentTypeMappingChangesList.add(changeDetail);

                    }

                }
            }
        }
        if(existigFinancialActivityAccountPaymentTypeMappingList.size() > 1){
            handleFinancialActivityAccountMappingDataIntegrityIssues(existigFinancialActivityAccountPaymentTypeMappingList);
        }
        return existigFinancialActivityAccountPaymentTypeMappingList;
    }
    
    private void handleFinancialActivityAccountMappingDataIntegrityIssues(
            List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMappingList) {
        List<Long> paymentTypeList = new ArrayList<>();
        for (FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping : financialActivityAccountPaymentTypeMappingList) {
            if (paymentTypeList.contains(financialActivityAccountPaymentTypeMapping.getPaymentType()
                    .getId())) { throw new DuplicatePaymentTypeFoundInFinanacialAvtivityMappingException(
                            financialActivityAccountPaymentTypeMapping.getPaymentType().getId()); }
            paymentTypeList.add(financialActivityAccountPaymentTypeMapping.getPaymentType().getId());

        }

    }
}
