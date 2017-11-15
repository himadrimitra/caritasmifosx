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
package org.apache.fineract.portfolio.paymenttype.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.portfolio.paymenttype.api.PaymentTypeApiResourceConstants;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeDataValidator;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.service.BankAccountDetailsWriteService;
import com.finflux.portfolio.external.service.ExternalServicesReadService;

@Service
public class PaymentTypeWriteServiceImpl implements PaymentTypeWriteService {

    private final PaymentTypeRepository repository;
    private final PaymentTypeRepositoryWrapper repositoryWrapper;
    private final PaymentTypeDataValidator fromApiJsonDeserializer;
    private final ExternalServicesReadService externalServicesReadService;
    private final BankAccountDetailsWriteService bankAccountDetailsWriteService;

    @Autowired
    public PaymentTypeWriteServiceImpl(final PaymentTypeRepository repository, final PaymentTypeRepositoryWrapper repositoryWrapper,
            final PaymentTypeDataValidator fromApiJsonDeserializer, final ExternalServicesReadService externalServicesReadService,
            final BankAccountDetailsWriteService bankAccountDetailsWriteService) {
        this.repository = repository;
        this.repositoryWrapper = repositoryWrapper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.externalServicesReadService = externalServicesReadService;
        this.bankAccountDetailsWriteService = bankAccountDetailsWriteService;
    }

    @Override
    public CommandProcessingResult createPaymentType(final JsonCommand command) {
        try {
            this.fromApiJsonDeserializer.validateForCreate(command.json());
            final String name = command.stringValueOfParameterNamed(PaymentTypeApiResourceConstants.NAME);
            final String description = command.stringValueOfParameterNamed(PaymentTypeApiResourceConstants.DESCRIPTION);
            final Boolean isCashPayment = command.booleanObjectValueOfParameterNamed(PaymentTypeApiResourceConstants.ISCASHPAYMENT);
            final Long position = command.longValueOfParameterNamed(PaymentTypeApiResourceConstants.POSITION);
            Long externalServiceId = null;
            if (command.hasParameter(PaymentTypeApiResourceConstants.externalServiceIdParamName)) {
                externalServiceId = command.longValueOfParameterNamed(PaymentTypeApiResourceConstants.externalServiceIdParamName);
                if (externalServiceId != null) {
                    this.externalServicesReadService.findOneWithNotFoundException(externalServiceId);
                }
            }
            final String bankDetails = command.jsonFragment(PaymentTypeApiResourceConstants.bankAccountDetailsParamName);
            final PaymentType newPaymentType = PaymentType.create(name, description, isCashPayment, position, externalServiceId);
            this.repository.save(newPaymentType);
            if (externalServiceId != null && bankDetails != null && !bankDetails.isEmpty()) {
                this.bankAccountDetailsWriteService.createBankAccountDetailAssociation(BankAccountDetailEntityType.PAYMENTTYPES,
                        newPaymentType.getId(), bankDetails);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(newPaymentType.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult updatePaymentType(final Long paymentTypeId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdate(command.json());
        final PaymentType paymentType = this.repositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
        final Map<String, Object> changes = paymentType.update(command);
        final Long previousExternalServiceId = paymentType.getExternalServiceId();
        if (command.isChangeInLongParameterNamed(PaymentTypeApiResourceConstants.externalServiceIdParamName,
                paymentType.getExternalServiceId())) {
            final Long externalServiceId = command.longValueOfParameterNamed(PaymentTypeApiResourceConstants.externalServiceIdParamName);
            this.externalServicesReadService.findOneWithNotFoundException(externalServiceId);
            paymentType.setExternalServiceId(externalServiceId);
        }

        if (previousExternalServiceId != null && paymentType.getExternalServiceId() == null) {
            this.bankAccountDetailsWriteService.deleteBankDetailAssociation(BankAccountDetailEntityType.PAYMENTTYPES, paymentTypeId);
        } else if (command.hasParameter(PaymentTypeApiResourceConstants.bankAccountDetailsParamName)) {
            final String bankDetails = command.jsonFragment(PaymentTypeApiResourceConstants.bankAccountDetailsParamName);
            if (previousExternalServiceId == null) {
                this.bankAccountDetailsWriteService.createBankAccountDetailAssociation(BankAccountDetailEntityType.PAYMENTTYPES,
                        paymentTypeId, bankDetails);
            } else {
                changes.putAll(this.bankAccountDetailsWriteService.updateBankAccountDetail(BankAccountDetailEntityType.PAYMENTTYPES,
                        paymentTypeId, bankDetails));
            }
        }

        if (!changes.isEmpty()) {
            this.repository.save(paymentType);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(command.entityId()).build();
    }

    @Override
    public CommandProcessingResult deletePaymentType(final Long paymentTypeId) {
        final PaymentType paymentType = this.repositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
        try {
            if (paymentType.getExternalServiceId() != null) {
                this.bankAccountDetailsWriteService.deleteBankDetailAssociation(BankAccountDetailEntityType.PAYMENTTYPES, paymentTypeId);
            }
            this.repository.delete(paymentType);
            this.repository.flush();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(null, e);
        }
        return new CommandProcessingResultBuilder().withEntityId(paymentType.getId()).build();
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("acc_product_mapping")) {
            throw new PlatformDataIntegrityException("error.msg.payment.type.association.exist",
                    "cannot.delete.payment.type.with.association");
        } else if (realCause.getMessage().contains("payment_type_id")) {
            throw new PlatformDataIntegrityException("error.msg.payment.type.association.exist",
                    "cannot.delete.payment.type.with.association");
        } else if (realCause.getMessage().contains("payment_name_unique")) {
            final String paymentTypeName = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.paymenttype.duplicate.paymentTypeName",
                    "PaymentType with paymentTypeName `" + paymentTypeName + "` already exists", "paymentTypeName", paymentTypeName);
        }
        throw new PlatformDataIntegrityException("error.msg.paymenttypes.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

}