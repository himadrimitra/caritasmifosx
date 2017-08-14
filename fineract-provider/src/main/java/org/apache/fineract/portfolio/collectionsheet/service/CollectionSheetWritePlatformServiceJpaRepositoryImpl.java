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
package org.apache.fineract.portfolio.collectionsheet.service;

import static org.apache.fineract.portfolio.collectionsheet.CollectionSheetConstants.transactionDateParamName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.service.ClientChargeWritePlatformService;
import org.apache.fineract.portfolio.collectionsheet.CollectionSheetConstants;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetBulkDisbursalCommand;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetBulkRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetClientChargeRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.data.CollectionSheetTransactionDataValidator;
import org.apache.fineract.portfolio.collectionsheet.domain.CollectionSheet;
import org.apache.fineract.portfolio.collectionsheet.domain.CollectionSheetRepository;
import org.apache.fineract.portfolio.collectionsheet.domain.CollectionSheetTransactionDetails;
import org.apache.fineract.portfolio.collectionsheet.serialization.CollectionSheetBulkDisbursalCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.collectionsheet.serialization.CollectionSheetBulkRepaymentCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.collectionsheet.serialization.CollectionSheetChargeRepaymentCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.portfolio.meeting.service.MeetingWritePlatformService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionDTO;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class CollectionSheetWritePlatformServiceJpaRepositoryImpl implements CollectionSheetWritePlatformService {

    private final LoanWritePlatformService loanWritePlatformService;
    private final CollectionSheetBulkRepaymentCommandFromApiJsonDeserializer bulkRepaymentCommandFromApiJsonDeserializer;
    private final CollectionSheetBulkDisbursalCommandFromApiJsonDeserializer bulkDisbursalCommandFromApiJsonDeserializer;
    private final CollectionSheetTransactionDataValidator transactionDataValidator;
    private final MeetingWritePlatformService meetingWritePlatformService;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final ClientChargeWritePlatformService clientChargeWritePlatformService;
    private final CollectionSheetChargeRepaymentCommandFromApiJsonDeserializer ChargeRepaymentCommandFromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final CollectionSheetRepository collectionSheetRepository;
    

    @Autowired
    public CollectionSheetWritePlatformServiceJpaRepositoryImpl(final LoanWritePlatformService loanWritePlatformService,
            final CollectionSheetBulkRepaymentCommandFromApiJsonDeserializer bulkRepaymentCommandFromApiJsonDeserializer,
            final CollectionSheetBulkDisbursalCommandFromApiJsonDeserializer bulkDisbursalCommandFromApiJsonDeserializer,
            final CollectionSheetTransactionDataValidator transactionDataValidator,
            final MeetingWritePlatformService meetingWritePlatformService, 
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final SavingsAccountAssembler savingsAccountAssembler,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final ClientChargeWritePlatformService clientChargeWritePlatformService,
            final CollectionSheetChargeRepaymentCommandFromApiJsonDeserializer ChargeRepaymentCommandFromApiJsonDeserializer,
            final FromJsonHelper fromApiJsonHelper, final CollectionSheetRepository collectionSheetRepository) {
        this.loanWritePlatformService = loanWritePlatformService;
        this.bulkRepaymentCommandFromApiJsonDeserializer = bulkRepaymentCommandFromApiJsonDeserializer;
        this.bulkDisbursalCommandFromApiJsonDeserializer = bulkDisbursalCommandFromApiJsonDeserializer;
        this.transactionDataValidator = transactionDataValidator;
        this.meetingWritePlatformService = meetingWritePlatformService;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.clientChargeWritePlatformService = clientChargeWritePlatformService;
        this.ChargeRepaymentCommandFromApiJsonDeserializer = ChargeRepaymentCommandFromApiJsonDeserializer;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.collectionSheetRepository = collectionSheetRepository;
    }

    @Override
    public CommandProcessingResult updateCollectionSheet(final JsonCommand command) {

        this.transactionDataValidator.validateTransaction(command);

        final Map<String, Object> changes = new HashMap<>();
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }
        JsonElement searchParmeterJson = null;
        Long officeId = null;
        Long staffId = null;
        Long groupId = null;
        Long centerId = null;
        LocalDate transactioDate = null;
        Date meetingDate = null;
        final JsonObject parentObject = this.fromApiJsonHelper.parse(command.json()).getAsJsonObject();
        if (parentObject.has(CollectionSheetConstants.searchParamsParamName)) {
            searchParmeterJson = parentObject.getAsJsonObject(CollectionSheetConstants.searchParamsParamName);
            officeId = this.fromApiJsonHelper.extractLongNamed(CollectionSheetConstants.officeIdParamName, searchParmeterJson);
            staffId = this.fromApiJsonHelper.extractLongNamed(CollectionSheetConstants.staffIdParamName, searchParmeterJson);
            groupId = this.fromApiJsonHelper.extractLongNamed(CollectionSheetConstants.groupIdParamName, searchParmeterJson);
            centerId = this.fromApiJsonHelper.extractLongNamed(CollectionSheetConstants.centerIdParamName, searchParmeterJson);
            transactioDate = this.fromApiJsonHelper.extractLocalDateNamed(transactionDateParamName, parentObject);

        }
        meetingDate = (transactioDate != null) ? transactioDate.toDate() : meetingDate;
        List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails = new ArrayList<>();
        CollectionSheet collectionSheet = CollectionSheet.formCollectionSheet(officeId, staffId, groupId, centerId,
                collectionSheetTransactionDetails, meetingDate);
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        changes.putAll(updateBulkReapayments(command, paymentDetail, collectionSheetTransactionDetails));

        changes.putAll(updateBulkDisbursals(command, collectionSheetTransactionDetails));

        changes.putAll(updateSavingsDepositAndWithdraw(command, paymentDetail, collectionSheetTransactionDetails));

        changes.putAll(updateClientCharges(command, paymentDetail, collectionSheetTransactionDetails));

        this.meetingWritePlatformService.updateCollectionSheetAttendance(command);
        if (searchParmeterJson != null) {
            this.collectionSheetRepository.save(collectionSheet);
            Long collectionSheetId = collectionSheet.getId();
            changes.put("collectionSheetId", collectionSheetId);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(command.entityId()) //
                .withGroupId(command.entityId()) //
                .with(changes).with(changes).build();
    }

    @Override
    public CommandProcessingResult saveIndividualCollectionSheet(final JsonCommand command) {

        this.transactionDataValidator.validateIndividualCollectionSheet(command);

        final Map<String, Object> changes = new HashMap<>();
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }

        final PaymentDetail paymentDetail = null;
        List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails = new ArrayList<>();
        changes.putAll(updateBulkReapayments(command, paymentDetail, collectionSheetTransactionDetails));

        changes.putAll(updateBulkDisbursals(command, collectionSheetTransactionDetails));

        changes.putAll(updateSavingsDepositAndWithdraw(command,paymentDetail, collectionSheetTransactionDetails));

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(command.entityId()) //
                .withGroupId(command.entityId()) //
                .with(changes).with(changes).build();
    }

    private Map<String, Object> updateBulkReapayments(final JsonCommand command, final PaymentDetail paymentDetail,
            final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails) {
        final Map<String, Object> changes = new HashMap<>();
        final CollectionSheetBulkRepaymentCommand bulkRepaymentCommand = this.bulkRepaymentCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json(), paymentDetail);
        changes.putAll(this.loanWritePlatformService.makeLoanBulkRepayment(bulkRepaymentCommand, collectionSheetTransactionDetails));
        return changes;
    }

    private Map<String, Object> updateBulkDisbursals(final JsonCommand command,
            final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails) {
        final Map<String, Object> changes = new HashMap<>();
        final CollectionSheetBulkDisbursalCommand bulkDisbursalCommand = this.bulkDisbursalCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        changes.putAll(
                this.loanWritePlatformService.bulkLoanDisbursal(command, bulkDisbursalCommand, false, collectionSheetTransactionDetails));
        return changes;
    }

    private Map<String, Object> updateSavingsDepositAndWithdraw(final JsonCommand command, final PaymentDetail paymentDetail,
            final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails) {
        final Map<String, Object> changes = new HashMap<>();
        List<Long> savingsTransactionIds = new ArrayList<>();
        final Map<Long, List<SavingsAccountTransactionDTO>> savingstransactions = this.savingsAccountAssembler
                .assembleBulkSavingsAccountDepositAndWithdrawTransactionDTOs(command, paymentDetail);
        savingsTransactionIds
                .addAll(this.savingsAccountWritePlatformService.depositAndWithdraw(savingstransactions, collectionSheetTransactionDetails));
        changes.put("savingsTransactions", savingsTransactionIds);
        return changes;
    }
    
    private Map<String, Object> updateClientCharges(final JsonCommand command, PaymentDetail paymentDetail,
            final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails) {
        final Map<String, Object> changes = new HashMap<>();
        final CollectionSheetClientChargeRepaymentCommand chargeRepaymentCommand = this.ChargeRepaymentCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());

        changes.putAll(this.clientChargeWritePlatformService.payChargeFromCollectionsheet(chargeRepaymentCommand, paymentDetail,
                collectionSheetTransactionDetails));

        return changes;
    }

}
