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

import static org.apache.fineract.portfolio.savings.DepositsApiConstants.isCalendarInheritedParamName;
import static org.apache.fineract.portfolio.savings.DepositsApiConstants.recurringFrequencyParamName;
import static org.apache.fineract.portfolio.savings.DepositsApiConstants.recurringFrequencyTypeParamName;
import static org.apache.fineract.portfolio.savings.DepositsApiConstants.transferInterestToSavingsParamName;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepositoryWrapper;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.portfolio.account.domain.AccountAssociationType;
import org.apache.fineract.portfolio.account.domain.AccountAssociations;
import org.apache.fineract.portfolio.account.domain.AccountAssociationsRepository;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarType;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.group.exception.CenterNotActiveException;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.group.exception.GroupNotFoundException;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.DepositsApiConstants;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.DepositAccountDataValidator;
import org.apache.fineract.portfolio.savings.domain.DepositAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.FixedDepositAccount;
import org.apache.fineract.portfolio.savings.domain.FixedDepositAccountRepository;
import org.apache.fineract.portfolio.savings.domain.RecurringDepositAccount;
import org.apache.fineract.portfolio.savings.domain.RecurringDepositAccountRepository;
import org.apache.fineract.portfolio.savings.domain.RecurringDepositScheduleInstallment;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountCharge;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargeAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;
import org.apache.fineract.portfolio.savings.domain.SavingsProductRepository;
import org.apache.fineract.portfolio.savings.exception.SavingsProductNotFoundException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.common.util.ScheduleDateGeneratorUtil;

@Service
public class DepositApplicationProcessWritePlatformServiceJpaRepositoryImpl implements DepositApplicationProcessWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DepositApplicationProcessWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final SavingsAccountRepositoryWrapper savingAccountRepository;
    private final FixedDepositAccountRepository fixedDepositAccountRepository;
    private final RecurringDepositAccountRepository recurringDepositAccountRepository;
    private final DepositAccountAssembler depositAccountAssembler;
    private final DepositAccountDataValidator depositAccountDataValidator;
    private final AccountNumberGenerator accountNumberGenerator;
    private final ClientRepositoryWrapper clientRepository;
    private final GroupRepository groupRepository;
    private final SavingsProductRepository savingsProductRepository;
    private final NoteRepository noteRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final SavingsAccountApplicationTransitionApiJsonValidator savingsAccountApplicationTransitionApiJsonValidator;
    private final SavingsAccountChargeAssembler savingsAccountChargeAssembler;
    private final AccountAssociationsRepository accountAssociationsRepository;
    private final FromJsonHelper fromJsonHelper;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;
    private final HolidayRepositoryWrapper holidayRepository;
    private final SavingsAccountDomainService savingsAccountDomainService;

    @Autowired
    public DepositApplicationProcessWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final SavingsAccountRepositoryWrapper savingAccountRepository, final DepositAccountAssembler depositAccountAssembler,
            final DepositAccountDataValidator depositAccountDataValidator, final AccountNumberGenerator accountNumberGenerator,
            final ClientRepositoryWrapper clientRepository, final GroupRepository groupRepository,
            final SavingsProductRepository savingsProductRepository, final NoteRepository noteRepository,
            final StaffRepositoryWrapper staffRepository,
            final SavingsAccountApplicationTransitionApiJsonValidator savingsAccountApplicationTransitionApiJsonValidator,
            final SavingsAccountChargeAssembler savingsAccountChargeAssembler,
            final FixedDepositAccountRepository fixedDepositAccountRepository,
            final RecurringDepositAccountRepository recurringDepositAccountRepository,
            final AccountAssociationsRepository accountAssociationsRepository, final FromJsonHelper fromJsonHelper,
            final CalendarInstanceRepository calendarInstanceRepository, final ConfigurationDomainService configurationDomainService,
            final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository, final HolidayRepositoryWrapper holidayRepository,
            final SavingsAccountDomainService savingsAccountDomainService) {
        this.context = context;
        this.savingAccountRepository = savingAccountRepository;
        this.depositAccountAssembler = depositAccountAssembler;
        this.accountNumberGenerator = accountNumberGenerator;
        this.depositAccountDataValidator = depositAccountDataValidator;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.savingsProductRepository = savingsProductRepository;
        this.noteRepository = noteRepository;
        this.staffRepository = staffRepository;
        this.savingsAccountApplicationTransitionApiJsonValidator = savingsAccountApplicationTransitionApiJsonValidator;
        this.savingsAccountChargeAssembler = savingsAccountChargeAssembler;
        this.fixedDepositAccountRepository = fixedDepositAccountRepository;
        this.recurringDepositAccountRepository = recurringDepositAccountRepository;
        this.accountAssociationsRepository = accountAssociationsRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.configurationDomainService = configurationDomainService;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.holidayRepository = holidayRepository;
        this.savingsAccountDomainService = savingsAccountDomainService;
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
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(),
                    "Savings account with accountNo " + accountNo + " already exists", "accountNo", accountNo);

        } else if (realCause.getMessage().contains("sa_external_id_UNIQUE")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            errorCodeBuilder.append(".duplicate.externalId");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(),
                    "Savings account with externalId " + externalId + " already exists", "externalId", externalId);
        }

        errorCodeBuilder.append(".unknown.data.integrity.issue");
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Unknown data integrity issue with savings account.");
    }

    @Transactional
    @Override
    public CommandProcessingResult submitFDApplication(final JsonCommand command) {
        try {
            this.depositAccountDataValidator.validateFixedDepositForSubmit(command.json());
            final AppUser submittedBy = this.context.authenticatedUser();

            final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                    .isSavingsInterestPostingAtCurrentPeriodEnd();
            final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

            final FixedDepositAccount account = (FixedDepositAccount) this.depositAccountAssembler.assembleFrom(command, submittedBy,
                    DepositAccountType.FIXED_DEPOSIT);

            final MathContext mc = MathContext.DECIMAL64;
            final boolean isPreMatureClosure = false;

            account.updateMaturityDateAndAmountBeforeAccountActivation(mc, isPreMatureClosure, isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth);
            this.fixedDepositAccountRepository.save(account);

            if (account.isAccountNumberRequiresAutoGeneration()) {
                final AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository
                        .findByAccountType(EntityAccountType.SAVINGS);
                account.updateAccountNo(this.accountNumberGenerator.generate(account, accountNumberFormat));

                this.savingAccountRepository.save(account);
            }

            // Save linked account information
            final Long savingsAccountId = command.longValueOfParameterNamed(DepositsApiConstants.linkedAccountParamName);
            if (savingsAccountId != null) {
                final SavingsAccount savingsAccount = this.depositAccountAssembler.assembleFrom(savingsAccountId,
                        DepositAccountType.SAVINGS_DEPOSIT);
                this.depositAccountDataValidator.validatelinkedSavingsAccount(savingsAccount, account);
                final boolean isActive = true;
                final AccountAssociations accountAssociations = AccountAssociations.associateSavingsAccount(account, savingsAccount,
                        AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue(), isActive);
                this.accountAssociationsRepository.save(accountAssociations);
            }

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

    @Transactional
    @Override
    public CommandProcessingResult submitRDApplication(final JsonCommand command) {
        try {
            this.depositAccountDataValidator.validateRecurringDepositForSubmit(command.json());
            final AppUser submittedBy = this.context.authenticatedUser();
            final RecurringDepositAccount account = (RecurringDepositAccount) this.depositAccountAssembler.assembleFrom(command,
                    submittedBy, DepositAccountType.RECURRING_DEPOSIT);
            this.recurringDepositAccountRepository.save(account);
            if (account.isAccountNumberRequiresAutoGeneration()) {
                final AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository
                        .findByAccountType(EntityAccountType.SAVINGS);
                account.updateAccountNo(this.accountNumberGenerator.generate(account, accountNumberFormat));
            }
            final Long savingsId = account.getId();
            final CalendarInstance calendarInstance = getCalendarInstance(command, account);
            this.calendarInstanceRepository.save(calendarInstance);
            generateSchedule(calendarInstance, account);
            this.savingAccountRepository.save(account);
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

    private void generateSchedule(final CalendarInstance calendarInstance, final RecurringDepositAccount account) {
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();
        // FIXME: Avoid save separately (Calendar instance requires account
        // details)
        final MathContext mc = MathContext.DECIMAL64;
        final Calendar calendar = calendarInstance.getCalendar();
        final PeriodFrequencyType frequencyType = CalendarFrequencyType.from(CalendarUtils.getFrequency(calendar.getRecurrence()));
        Integer frequency = CalendarUtils.getInterval(calendar.getRecurrence());
        frequency = frequency == -1 ? 1 : frequency;

        final HolidayDetailDTO holidayDetails = this.savingsAccountDomainService.getHolidayDetails(account);
        account.generateSchedule(frequencyType, frequency, calendar, holidayDetails);
        final boolean isPreMatureClosure = false;
        account.updateMaturityDateAndAmount(mc, isPreMatureClosure, isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth,
                frequencyType, frequency, holidayDetails);
        account.validateApplicableInterestRate();
    }

    private CalendarInstance getCalendarInstance(final JsonCommand command, final RecurringDepositAccount account) {
        CalendarInstance calendarInstance = null;
        final boolean isCalendarInherited = command.booleanPrimitiveValueOfParameterNamed(isCalendarInheritedParamName);

        if (isCalendarInherited) {
            final Set<Group> groups = account.getClient().getGroups();
            Long groupId = null;
            if (groups.isEmpty()) {
                final String defaultUserMessage = "Client does not belong to group/center. Cannot follow group/center meeting frequency.";
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.recurring.deposit.account.cannot.create.not.belongs.to.any.groups.to.follow.meeting.frequency",
                        defaultUserMessage, account.clientId());
            } else if (groups.size() > 1) {
                final String defaultUserMessage = "Client belongs to more than one group. Cannot support recurring deposit.";
                throw new GeneralPlatformDomainRuleException("error.msg.recurring.deposit.account.cannot.create.belongs.to.multiple.groups",
                        defaultUserMessage, account.clientId());
            } else {
                final Group group = groups.iterator().next();
                final Group parent = group.getParent();
                Integer entityType = CalendarEntityType.GROUPS.getValue();
                if (parent != null) {
                    groupId = parent.getId();
                    entityType = CalendarEntityType.CENTERS.getValue();
                } else {
                    groupId = group.getId();
                }
                final CalendarInstance parentCalendarInstance = this.calendarInstanceRepository
                        .findByEntityIdAndEntityTypeIdAndCalendarTypeId(groupId, entityType, CalendarType.COLLECTION.getValue());
                if (parentCalendarInstance == null) {
                    final String defaultUserMessage = "Meeting frequency is not attached to the Group/Center to which the client belongs to.";
                    throw new GeneralPlatformDomainRuleException(
                            "error.msg.meeting.frequency.not.attached.to.group.to.which.client.belongs.to", defaultUserMessage,
                            account.clientId());
                }
                calendarInstance = CalendarInstance.from(parentCalendarInstance.getCalendar(), account.getId(),
                        CalendarEntityType.SAVINGS.getValue());
            }
        } else {
            final LocalDate calendarStartDate = account.depositStartDate();
            final Integer frequencyType = command.integerValueSansLocaleOfParameterNamed(recurringFrequencyTypeParamName);
            final PeriodFrequencyType periodFrequencyType = PeriodFrequencyType.fromInt(frequencyType);
            final Integer frequency = command.integerValueSansLocaleOfParameterNamed(recurringFrequencyParamName);

            final Integer repeatsOnDay = calendarStartDate.getDayOfWeek();
            final String title = "recurring_savings_" + account.getId();
            final Collection<Integer> repeatsOnDayOfMonth = null;

            final Calendar calendar = Calendar.createRepeatingCalendar(title, calendarStartDate, CalendarType.COLLECTION.getValue(),
                    CalendarFrequencyType.from(periodFrequencyType), frequency, repeatsOnDay, null, repeatsOnDayOfMonth);
            calendarInstance = CalendarInstance.from(calendar, account.getId(), CalendarEntityType.SAVINGS.getValue());
        }
        if (calendarInstance == null) {
            final String defaultUserMessage = "No valid recurring details available for recurring depost account creation.";
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.recurring.deposit.account.cannot.create.no.valid.recurring.details.available", defaultUserMessage,
                    account.clientId());
        }
        return calendarInstance;
    }

    @Transactional
    @Override
    public CommandProcessingResult modifyFDApplication(final Long accountId, final JsonCommand command) {
        try {
            this.depositAccountDataValidator.validateFixedDepositForUpdate(command.json());

            final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                    .isSavingsInterestPostingAtCurrentPeriodEnd();
            final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

            final Map<String, Object> changes = new LinkedHashMap<>(20);

            final FixedDepositAccount account = (FixedDepositAccount) this.depositAccountAssembler.assembleFrom(accountId,
                    DepositAccountType.FIXED_DEPOSIT);
            checkClientOrGroupActive(account);
            account.modifyApplication(command, changes);
            account.validateNewApplicationState(DateUtils.getLocalDateOfTenant(), DepositAccountType.FIXED_DEPOSIT.resourceName());

            if (!changes.isEmpty()) {
                updateFDAndRDCommonChanges(changes, command, account);
                final MathContext mc = MathContext.DECIMAL64;
                final boolean isPreMatureClosure = false;
                account.updateMaturityDateAndAmountBeforeAccountActivation(mc, isPreMatureClosure,
                        isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth);
                this.savingAccountRepository.save(account);
            }

            final boolean isLinkedAccRequired = command.booleanPrimitiveValueOfParameterNamed(transferInterestToSavingsParamName);

            // Save linked account information
            final Long savingsAccountId = command.longValueOfParameterNamed(DepositsApiConstants.linkedAccountParamName);
            AccountAssociations accountAssociations = this.accountAssociationsRepository.findBySavingsIdAndType(accountId,
                    AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue());
            if (savingsAccountId == null) {
                if (accountAssociations != null) {
                    if (this.fromJsonHelper.parameterExists(DepositsApiConstants.linkedAccountParamName, command.parsedJson())) {
                        this.accountAssociationsRepository.delete(accountAssociations);
                        changes.put(DepositsApiConstants.linkedAccountParamName, null);
                        if (isLinkedAccRequired) {
                            this.depositAccountDataValidator.throwLinkedAccountRequiredError();
                        }
                    }
                } else if (isLinkedAccRequired) {
                    this.depositAccountDataValidator.throwLinkedAccountRequiredError();
                }
            } else {
                boolean isModified = false;
                if (accountAssociations == null) {
                    isModified = true;
                } else {
                    final SavingsAccount savingsAccount = accountAssociations.linkedSavingsAccount();
                    if (savingsAccount == null || savingsAccount.getId() != savingsAccountId) {
                        isModified = true;
                    }
                }
                if (isModified) {
                    final SavingsAccount savingsAccount = this.depositAccountAssembler.assembleFrom(savingsAccountId,
                            DepositAccountType.SAVINGS_DEPOSIT);
                    this.depositAccountDataValidator.validatelinkedSavingsAccount(savingsAccount, account);
                    if (accountAssociations == null) {
                        final boolean isActive = true;
                        accountAssociations = AccountAssociations.associateSavingsAccount(account, savingsAccount,
                                AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue(), isActive);
                    } else {
                        accountAssociations.updateLinkedSavingsAccount(savingsAccount);
                    }
                    changes.put(DepositsApiConstants.linkedAccountParamName, savingsAccountId);
                    this.accountAssociationsRepository.save(accountAssociations);
                }
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(accountId) //
                    .withOfficeId(account.officeId()) //
                    .withClientId(account.clientId()) //
                    .withGroupId(account.groupId()) //
                    .withSavingsId(accountId) //
                    .with(changes) //
                    .build();
        } catch (final DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult modifyRDApplication(final Long accountId, final JsonCommand command) {
        try {
            this.depositAccountDataValidator.validateRecurringDepositForUpdate(command.json());

            final Map<String, Object> changes = new LinkedHashMap<>(20);

            final RecurringDepositAccount account = (RecurringDepositAccount) this.depositAccountAssembler.assembleFrom(accountId,
                    DepositAccountType.RECURRING_DEPOSIT);

            checkClientOrGroupActive(account);
            account.modifyApplication(command, changes);
            account.validateNewApplicationState(DateUtils.getLocalDateOfTenant(), DepositAccountType.RECURRING_DEPOSIT.resourceName());

            if (!changes.isEmpty()) {
                updateFDAndRDCommonChanges(changes, command, account);
                final CalendarInstance calendarInstance = this.calendarInstanceRepository.findByEntityIdAndEntityTypeIdAndCalendarTypeId(
                        accountId, CalendarEntityType.SAVINGS.getValue(), CalendarType.COLLECTION.getValue());
                generateSchedule(calendarInstance, account);
                this.savingAccountRepository.save(account);
            }

            // update calendar details
            if (!account.isCalendarInherited()) {
                final LocalDate calendarStartDate = account.depositStartDate();
                final Integer frequencyType = command.integerValueSansLocaleOfParameterNamed(recurringFrequencyTypeParamName);
                final PeriodFrequencyType periodFrequencyType = PeriodFrequencyType.fromInt(frequencyType);
                final Integer frequency = command.integerValueSansLocaleOfParameterNamed(recurringFrequencyParamName);
                final Integer repeatsOnDay = calendarStartDate.getDayOfWeek();
                final Collection<Integer> repeatsOnDayOfMonth = null;

                final CalendarInstance calendarInstance = this.calendarInstanceRepository.findByEntityIdAndEntityTypeIdAndCalendarTypeId(
                        accountId, CalendarEntityType.SAVINGS.getValue(), CalendarType.COLLECTION.getValue());
                final Calendar calendar = calendarInstance.getCalendar();
                calendar.updateRepeatingCalendar(calendarStartDate, CalendarFrequencyType.from(periodFrequencyType), frequency,
                        repeatsOnDay, null, repeatsOnDayOfMonth);
                this.calendarInstanceRepository.save(calendarInstance);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(accountId) //
                    .withOfficeId(account.officeId()) //
                    .withClientId(account.clientId()) //
                    .withGroupId(account.groupId()) //
                    .withSavingsId(accountId) //
                    .with(changes) //
                    .build();
        } catch (final DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    private void updateFDAndRDCommonChanges(final Map<String, Object> changes, final JsonCommand command, final SavingsAccount account) {

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

    }

    @Transactional
    @Override
    public CommandProcessingResult deleteApplication(final Long savingsId, final DepositAccountType depositAccountType) {

        final SavingsAccount account = this.depositAccountAssembler.assembleFrom(savingsId, depositAccountType);
        checkClientOrGroupActive(account);

        if (account.isNotSubmittedAndPendingApproval()) {
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                    .resource(depositAccountType.resourceName() + DepositsApiConstants.deleteApplicationAction);

            baseDataValidator.reset().parameter(DepositsApiConstants.activatedOnDateParamName)
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
    public CommandProcessingResult approveApplication(final Long savingsId, final JsonCommand command,
            final DepositAccountType depositAccountType) {

        final AppUser currentUser = this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateApproval(command.json());

        final SavingsAccount savingsAccount = this.depositAccountAssembler.assembleFrom(savingsId, depositAccountType);
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
    public CommandProcessingResult undoApplicationApproval(final Long savingsId, final JsonCommand command,
            final DepositAccountType depositAccountType) {

        this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateForUndo(command.json());

        final SavingsAccount savingsAccount = this.depositAccountAssembler.assembleFrom(savingsId, depositAccountType);
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
    public CommandProcessingResult rejectApplication(final Long savingsId, final JsonCommand command,
            final DepositAccountType depositAccountType) {

        final AppUser currentUser = this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateRejection(command.json());

        final SavingsAccount savingsAccount = this.depositAccountAssembler.assembleFrom(savingsId, depositAccountType);
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
    public CommandProcessingResult applicantWithdrawsFromApplication(final Long savingsId, final JsonCommand command,
            final DepositAccountType depositAccountType) {
        final AppUser currentUser = this.context.authenticatedUser();

        this.savingsAccountApplicationTransitionApiJsonValidator.validateApplicantWithdrawal(command.json());

        final SavingsAccount savingsAccount = this.depositAccountAssembler.assembleFrom(savingsId, depositAccountType);
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
    @Transactional
    public void updateScheduleDates(final Long savingsId, HolidayDetailDTO holidayDetailDTO, final LocalDate recalculateFrom) {
        final RecurringDepositAccount account = (RecurringDepositAccount) this.depositAccountAssembler.assembleFrom(savingsId,
                DepositAccountType.RECURRING_DEPOSIT);
        final List<Holiday> holidaysToBeProcess = this.holidayRepository.findByOfficeIdAndGreaterThanDate(account.officeId(),
                recalculateFrom.toDate());
        holidayDetailDTO = new HolidayDetailDTO(holidayDetailDTO, holidaysToBeProcess);
        final List<CalendarInstance> calendarInstances = (List<CalendarInstance>) this.calendarInstanceRepository
                .findByEntityIdAndEntityTypeId(account.getId(), CalendarEntityType.SAVINGS.getValue());
        if (calendarInstances != null && !calendarInstances.isEmpty()) {
            final CalendarInstance calendarInstance = calendarInstances.get(0);
            final Calendar calendar = calendarInstance.getCalendar();
            final CalendarFrequencyType calendarFrequencyType = CalendarUtils.getFrequency(calendar.getRecurrence());
            final PeriodFrequencyType frequencyType = CalendarFrequencyType.from(calendarFrequencyType);
            final Integer recurringEvery = CalendarUtils.getInterval(calendar.getRecurrence());
            LocalDate actualInstallmentDate = null;
            for (final RecurringDepositScheduleInstallment rdScheduleInstallment : account.depositScheduleInstallments()) {
                if (!rdScheduleInstallment.dueDate().isBefore(recalculateFrom)) {
                    if (actualInstallmentDate == null) {
                        actualInstallmentDate = rdScheduleInstallment.getLocalDateActualDueDate();
                    }
                    AdjustedDateDetailsDTO adjustedDateDetailsDTO = new AdjustedDateDetailsDTO(actualInstallmentDate, actualInstallmentDate,
                            actualInstallmentDate);
                    adjustedDateDetailsDTO = rdScheduleInstallment.updateDueDateBasedOnWorkingDaysAndHolidays(adjustedDateDetailsDTO,
                            holidayDetailDTO, frequencyType, recurringEvery);
                    actualInstallmentDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
                    actualInstallmentDate = ScheduleDateGeneratorUtil.generateNextScheduleDate(actualInstallmentDate, frequencyType,
                            recurringEvery);
                    actualInstallmentDate = CalendarUtils.adjustDate(actualInstallmentDate, account.depositStartDate(), frequencyType);
                }
            }
            final MathContext mc = MathContext.DECIMAL64;
            final boolean isPreMatureClosure = false;
            final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                    .isSavingsInterestPostingAtCurrentPeriodEnd();
            final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();
            account.updateMaturityDateAndAmount(mc, isPreMatureClosure, isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth, frequencyType, recurringEvery, holidayDetailDTO);
            this.savingAccountRepository.save(account);
        }
    }
}