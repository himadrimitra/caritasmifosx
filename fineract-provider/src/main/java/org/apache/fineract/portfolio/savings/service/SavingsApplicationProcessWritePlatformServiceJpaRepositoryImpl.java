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
package org.apache.fineract.portfolio.savings.service;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.productIdParamName;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandProcessingService;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepository;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.group.exception.CenterNotActiveException;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.group.exception.GroupNotFoundException;
import org.apache.fineract.portfolio.group.exception.UpdateStaffHierarchyException;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDataDTO;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDataValidator;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountCharge;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargeAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;
import org.apache.fineract.portfolio.savings.domain.SavingsProductRepository;
import org.apache.fineract.portfolio.savings.exception.SavingsProductNotFoundException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavingsApplicationProcessWritePlatformServiceJpaRepositoryImpl implements SavingsApplicationProcessWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(SavingsApplicationProcessWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final SavingsAccountRepositoryWrapper savingAccountRepository;
    private final SavingsAccountAssembler savingAccountAssembler;
    private final SavingsAccountDataValidator savingsAccountDataValidator;
    private final AccountNumberGenerator accountNumberGenerator;
    private final ClientRepositoryWrapper clientRepository;
    private final ClientRepository clientrepo;
    private final GroupRepository groupRepository;
    private final SavingsProductRepository savingsProductRepository;
    private final NoteRepository noteRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final StaffRepository staffRepo;
    private final SavingsAccountApplicationTransitionApiJsonValidator savingsAccountApplicationTransitionApiJsonValidator;
    private final SavingsAccountChargeAssembler savingsAccountChargeAssembler;
    private final CommandProcessingService commandProcessingService;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public SavingsApplicationProcessWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final SavingsAccountRepositoryWrapper savingAccountRepository, final SavingsAccountAssembler savingAccountAssembler,
            final SavingsAccountDataValidator savingsAccountDataValidator, final AccountNumberGenerator accountNumberGenerator,
            final ClientRepositoryWrapper clientRepository, final GroupRepository groupRepository,
            final SavingsProductRepository savingsProductRepository, final NoteRepository noteRepository,
            final StaffRepositoryWrapper staffRepository,
            final SavingsAccountApplicationTransitionApiJsonValidator savingsAccountApplicationTransitionApiJsonValidator,
            final SavingsAccountChargeAssembler savingsAccountChargeAssembler, final CommandProcessingService commandProcessingService,
            final SavingsAccountDomainService savingsAccountDomainService,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository, final ClientRepository clientrepo,
            final StaffRepository staffRepo,
            final FineractEntityAccessUtil fineractEntityAccessUtil, final ConfigurationDomainService configurationDomainService) {
        this.context = context;
        this.savingAccountRepository = savingAccountRepository;
        this.savingAccountAssembler = savingAccountAssembler;
        this.accountNumberGenerator = accountNumberGenerator;
        this.savingsAccountDataValidator = savingsAccountDataValidator;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.savingsProductRepository = savingsProductRepository;
        this.noteRepository = noteRepository;
        this.staffRepository = staffRepository;
        this.savingsAccountApplicationTransitionApiJsonValidator = savingsAccountApplicationTransitionApiJsonValidator;
        this.savingsAccountChargeAssembler = savingsAccountChargeAssembler;
        this.commandProcessingService = commandProcessingService;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.clientrepo = clientrepo;
        this.staffRepo = staffRepo;
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.configurationDomainService = configurationDomainService;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataAccessException dve) {

        final StringBuilder errorCodeBuilder = new StringBuilder("error.msg.").append(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("sa_account_no_UNIQUE")) {
            final String accountNo = command.stringValueOfParameterNamed("accountNo");
            errorCodeBuilder.append(".duplicate.accountNo");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Savings account with accountNo " + accountNo
                    + " already exists", "accountNo", accountNo);

        } else if (realCause.getMessage().contains("sa_externalid_UNIQUE")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            errorCodeBuilder.append(".duplicate.externalId");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Savings account with externalId " + externalId
                    + " already exists", "externalId", externalId);
        }

        errorCodeBuilder.append(".unknown.data.integrity.issue");
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Unknown data integrity issue with savings account.");
    }

    @Transactional
    @Override
    public CommandProcessingResult submitApplication(final JsonCommand command) {
        try {
            this.savingsAccountDataValidator.validateForSubmit(command.json());
            final AppUser submittedBy = this.context.authenticatedUser();

            final Long productId = command.longValueOfParameterNamed(productIdParamName);
            this.fineractEntityAccessUtil.checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(
                    FineractEntityAccessType.OFFICE_ACCESS_TO_SAVINGS_PRODUCTS, productId);
            Collection<Long> requestedChargeIds = extractChargeIds(command);
            this.fineractEntityAccessUtil
                    .checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(
                            FineractEntityAccessType.OFFICE_ACCESS_TO_CHARGES, requestedChargeIds);

            final SavingsAccount account = this.savingAccountAssembler.assembleFrom(command, submittedBy);
            this.savingAccountRepository.save(account);

            generateAccountNumber(account);

            final Long savingsId = account.getId();
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(savingsId) //
                    .withOfficeId(account.officeId()) //
                    .withClientId(account.clientId()) //
                    .withGroupId(account.groupId()) //
                    .withSavingsId(savingsId) //
                    .build();
        } catch (final DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void generateAccountNumber(final SavingsAccount account) {
        if (account.isAccountNumberRequiresAutoGeneration()) {
            final AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository.findByAccountType(EntityAccountType.SAVINGS);
            account.updateAccountNo(this.accountNumberGenerator.generate(account, accountNumberFormat));

            this.savingAccountRepository.save(account);
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult modifyApplication(final Long savingsId, final JsonCommand command) {
        try {
            this.savingsAccountDataValidator.validateForUpdate(command.json());

            if(command.hasParameter(productIdParamName)){
                Long productId = command.longValueOfParameterNamed(productIdParamName);
                this.fineractEntityAccessUtil
                        .checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(
                                FineractEntityAccessType.OFFICE_ACCESS_TO_SAVINGS_PRODUCTS, productId);
            }
            Collection<Long> requestedChargeIds = extractChargeIds(command);
            this.fineractEntityAccessUtil
                    .checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(
                            FineractEntityAccessType.OFFICE_ACCESS_TO_CHARGES, requestedChargeIds);

            final Map<String, Object> changes = new LinkedHashMap<>(20);

            final SavingsAccount account = this.savingAccountAssembler.assembleFrom(savingsId);
            checkClientOrGroupActive(account);
            account.modifyApplication(command, changes);
            account.validateNewApplicationState(DateUtils.getLocalDateOfTenant(), SAVINGS_ACCOUNT_RESOURCE_NAME);
            account.validateAccountValuesWithProduct();

            if (!changes.isEmpty()) {

                if (changes.containsKey(SavingsApiConstants.clientIdParamName)) {
                    final Long clientId = command.longValueOfParameterNamed(SavingsApiConstants.clientIdParamName);
                    if (clientId != null) {
                        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
                        if (client.isNotActive()) { throw new ClientNotActiveException(clientId); }
                        account.update(client);
                    } else {
                        final Client client = null;
                        account.update(client);
                    }
                }

                if (changes.containsKey(SavingsApiConstants.groupIdParamName)) {
                    final Long groupId = command.longValueOfParameterNamed(SavingsApiConstants.groupIdParamName);
                    if (groupId != null) {
                        final Group group = this.groupRepository.findOne(groupId);
                        if (group == null) { throw new GroupNotFoundException(groupId); }
                        if (group.isNotActive()) {
                            if (group.isCenter()) { throw new CenterNotActiveException(groupId); }
                            throw new GroupNotActiveException(groupId);
                        }
                        account.update(group);
                    } else {
                        final Group group = null;
                        account.update(group);
                    }
                }

                if (changes.containsKey(SavingsApiConstants.productIdParamName)) {
                    final Long productId = command.longValueOfParameterNamed(SavingsApiConstants.productIdParamName);
                    final SavingsProduct product = this.savingsProductRepository.findOne(productId);
                    if (product == null) { throw new SavingsProductNotFoundException(productId); }

                    account.update(product);
                }

                if (changes.containsKey(SavingsApiConstants.fieldOfficerIdParamName)) {
                    final Long fieldOfficerId = command.longValueOfParameterNamed(SavingsApiConstants.fieldOfficerIdParamName);
                    if (configurationDomainService
                            .isLoanOfficerToCenterHierarchyEnabled()) { throw new UpdateStaffHierarchyException(fieldOfficerId); }
                    Staff fieldOfficer = null;
                    if (fieldOfficerId != null) {
                        fieldOfficer = this.staffRepository.findOneWithNotFoundDetection(fieldOfficerId);
                    } else {
                        changes.put(SavingsApiConstants.fieldOfficerIdParamName, "");
                    }
                    account.update(fieldOfficer);
                }

                if (changes.containsKey("charges")) {
                    final Set<SavingsAccountCharge> charges = this.savingsAccountChargeAssembler.fromParsedJson(command.parsedJson(),
                            account.getCurrency().getCode());
                    final boolean updated = account.update(charges);
                    if (!updated) {
                        changes.remove("charges");
                    }
                }

                this.savingAccountRepository.saveAndFlush(account);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(savingsId) //
                    .withOfficeId(account.officeId()) //
                    .withClientId(account.clientId()) //
                    .withGroupId(account.groupId()) //
                    .withSavingsId(savingsId) //
                    .with(changes) //
                    .build();
        } catch (final DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteApplication(final Long savingsId) {

        final SavingsAccount account = this.savingAccountAssembler.assembleFrom(savingsId);
        checkClientOrGroupActive(account);

        if (account.isNotSubmittedAndPendingApproval()) {
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                    .resource(SAVINGS_ACCOUNT_RESOURCE_NAME + SavingsApiConstants.deleteApplicationAction);

            baseDataValidator.reset().parameter(SavingsApiConstants.activatedOnDateParamName)
                    .failWithCodeNoParameterAddedToErrorCode("not.in.submittedandpendingapproval.state");

            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        }

        final List<Note> relatedNotes = this.noteRepository.findBySavingsAccountId(savingsId);
        this.noteRepository.deleteInBatch(relatedNotes);

        this.savingAccountRepository.delete(account);

        return new CommandProcessingResultBuilder() //
                .withEntityId(savingsId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult approveApplication(final Long savingsId, final JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateApproval(command.json());

        final SavingsAccount savingsAccount = this.savingAccountAssembler.assembleFrom(savingsId);
        checkClientOrGroupActive(savingsAccount);

        final Map<String, Object> changes = savingsAccount.approveApplication(currentUser, command, DateUtils.getLocalDateOfTenant());
        if (!changes.isEmpty()) {
            this.savingAccountRepository.save(savingsAccount);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.savingNote(savingsAccount, noteText);
                changes.put("note", noteText);
                this.noteRepository.save(note);
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savingsId) //
                .withOfficeId(savingsAccount.officeId()) //
                .withClientId(savingsAccount.clientId()) //
                .withGroupId(savingsAccount.groupId()) //
                .withSavingsId(savingsId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult undoApplicationApproval(final Long savingsId, final JsonCommand command) {

        this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateForUndo(command.json());

        final SavingsAccount savingsAccount = this.savingAccountAssembler.assembleFrom(savingsId);
        checkClientOrGroupActive(savingsAccount);

        final Map<String, Object> changes = savingsAccount.undoApplicationApproval();
        if (!changes.isEmpty()) {
            this.savingAccountRepository.save(savingsAccount);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.savingNote(savingsAccount, noteText);
                changes.put("note", noteText);
                this.noteRepository.save(note);
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savingsId) //
                .withOfficeId(savingsAccount.officeId()) //
                .withClientId(savingsAccount.clientId()) //
                .withGroupId(savingsAccount.groupId()) //
                .withSavingsId(savingsId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult rejectApplication(final Long savingsId, final JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateRejection(command.json());

        final SavingsAccount savingsAccount = this.savingAccountAssembler.assembleFrom(savingsId);
        checkClientOrGroupActive(savingsAccount);

        final Map<String, Object> changes = savingsAccount.rejectApplication(currentUser, command, DateUtils.getLocalDateOfTenant());
        if (!changes.isEmpty()) {
            this.savingAccountRepository.save(savingsAccount);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.savingNote(savingsAccount, noteText);
                changes.put("note", noteText);
                this.noteRepository.save(note);
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savingsId) //
                .withOfficeId(savingsAccount.officeId()) //
                .withClientId(savingsAccount.clientId()) //
                .withGroupId(savingsAccount.groupId()) //
                .withSavingsId(savingsId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult applicantWithdrawsFromApplication(final Long savingsId, final JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateApplicantWithdrawal(command.json());

        final SavingsAccount savingsAccount = this.savingAccountAssembler.assembleFrom(savingsId);
        checkClientOrGroupActive(savingsAccount);

        final Map<String, Object> changes = savingsAccount.applicantWithdrawsFromApplication(currentUser, command,
                DateUtils.getLocalDateOfTenant());
        if (!changes.isEmpty()) {
            this.savingAccountRepository.save(savingsAccount);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.savingNote(savingsAccount, noteText);
                changes.put("note", noteText);
                this.noteRepository.save(note);
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savingsId) //
                .withOfficeId(savingsAccount.officeId()) //
                .withClientId(savingsAccount.clientId()) //
                .withGroupId(savingsAccount.groupId()) //
                .withSavingsId(savingsId) //
                .with(changes) //
                .build();
    }

    private void checkClientOrGroupActive(final SavingsAccount account) {
        final Client client = account.getClient();
        if (client != null) {
            if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
        }
        final Group group = account.group();
        if (group != null) {
            if (group.isNotActive()) {
                if (group.isCenter()) { throw new CenterNotActiveException(group.getId()); }
                throw new GroupNotActiveException(group.getId());
            }
        }
    }

    @Override
    public CommandProcessingResult createActiveApplication(final SavingsAccountDataDTO savingsAccountDataDTO) {

        final CommandWrapper commandWrapper = new CommandWrapperBuilder().savingsAccountActivation(null).build();
        boolean rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, savingsAccountDataDTO.getAppliedBy());

        final SavingsAccount account = this.savingAccountAssembler.assembleFrom(savingsAccountDataDTO.getClient(),
                savingsAccountDataDTO.getGroup(), savingsAccountDataDTO.getSavingsProduct(), savingsAccountDataDTO.getStaff(),
                savingsAccountDataDTO.getApplicationDate(), savingsAccountDataDTO.getAppliedBy());
        account.approveAndActivateApplication(savingsAccountDataDTO.getApplicationDate().toDate(), savingsAccountDataDTO.getAppliedBy());
        Money amountForDeposit = account.activateWithBalance();

        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();

        if (amountForDeposit.isGreaterThanZero()) {
            this.savingAccountRepository.save(account);
        }
        this.savingsAccountWritePlatformService.processPostActiveActions(account, savingsAccountDataDTO.getFmt(), existingTransactionIds,
                existingReversedTransactionIds);
        this.savingAccountRepository.save(account);

        generateAccountNumber(account);
        // post journal entries for activation charges
        this.savingsAccountDomainService.postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return new CommandProcessingResultBuilder() //
                .withSavingsId(account.getId()) //
                .setRollbackTransaction(rollbackTransaction)//
                .build();
    }

    @Override
    public CommandProcessingResult createApplication(final SavingsAccountDataDTO savingsAccountDataDTO) {

        final CommandWrapper commandWrapper = new CommandWrapperBuilder().savingsAccountActivation(null).build();
        boolean rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, savingsAccountDataDTO.getAppliedBy());

        final SavingsAccount account = this.savingAccountAssembler.assembleFrom(savingsAccountDataDTO.getClient(),
                savingsAccountDataDTO.getGroup(), savingsAccountDataDTO.getSavingsProduct(), savingsAccountDataDTO.getStaff(),
                savingsAccountDataDTO.getApplicationDate(), savingsAccountDataDTO.getAppliedBy());

        this.savingAccountRepository.save(account);

        generateAccountNumber(account);

        return new CommandProcessingResultBuilder() //
                .withSavingsId(account.getId()) //
                .setRollbackTransaction(rollbackTransaction)//
                .build();
    }

    @Override
    public CommandProcessingResult createOrActivateSavingsApplication(JsonCommand command) {
        // TODO Auto-generated method stub
        try {

            final AppUser submittedBy = this.context.authenticatedUser();
            this.savingsAccountDataValidator.validateForCreateActiveSavingsApplication(command.json());
            CommandProcessingResult commandProcessingResult = CommandProcessingResult.empty();

            final Long clientId = command.longValueOfParameterNamed(SavingsApiConstants.clientIdParamName);
            final Long productId = command.longValueOfParameterNamed(SavingsApiConstants.productIdParamName);
            final Date date = command.DateValueOfParameterNamed(SavingsApiConstants.dateParamName);
            final boolean isactive = command.booleanPrimitiveValueOfParameterNamed(SavingsApiConstants.activeParamName);
            final Long fieldOfficerId = command.longValueOfParameterNamed(SavingsApiConstants.fieldOfficerIdParamName);

            final Locale locale = command.extractLocale();
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
            final SavingsProduct product = this.savingsProductRepository.findOne(productId);
            final Client client = this.clientrepo.findOne(clientId);
            final Group group = null;
            Staff staff = null;
            if (configurationDomainService.isLoanOfficerToCenterHierarchyEnabled() && client.getStaff().isLoanOfficer()) {
                staff = client.getStaff();
            } else if (fieldOfficerId != null) {
                staff = this.staffRepo.findOne(fieldOfficerId);
            }

            SavingsAccountDataDTO savingsAccountDataDTO = new SavingsAccountDataDTO(client, group, product, staff, new LocalDate(date),
                    submittedBy, fmt);
            if (!isactive) {
                commandProcessingResult = this.createApplication(savingsAccountDataDTO);
            } else {
                commandProcessingResult = this.createActiveApplication(savingsAccountDataDTO);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(commandProcessingResult.getSavingsId()) //
                    .withClientId(clientId) //
                    .withSavingsId(commandProcessingResult.getSavingsId()) //
                    .build();
        } catch (final DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private Collection<Long> extractChargeIds(JsonCommand command) {
        Collection<Long> chargeIds = new ArrayList<>();
        JsonArray charges = command.arrayOfParameterNamed("charges");
        if(null != charges && charges.size() > 0) {
            for (JsonElement charge : charges) {
                chargeIds.add(charge.getAsJsonObject().get("chargeId").getAsLong());
            }
        }
        return chargeIds;
    }
}