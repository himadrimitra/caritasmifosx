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
package org.apache.fineract.portfolio.group.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandProcessingService;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.exception.InvalidOfficeException;
import org.apache.fineract.organisation.office.exception.OfficeNotFoundException;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarType;
import org.apache.fineract.portfolio.calendar.exception.CalendarDateException;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.LoanStatusMapper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.api.GroupingTypesApiConstants;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupLevel;
import org.apache.fineract.portfolio.group.domain.GroupLevelRepository;
import org.apache.fineract.portfolio.group.domain.GroupRepositoryWrapper;
import org.apache.fineract.portfolio.group.domain.GroupTypes;
import org.apache.fineract.portfolio.group.exception.GroupAccountExistsException;
import org.apache.fineract.portfolio.group.exception.GroupHasNoStaffException;
import org.apache.fineract.portfolio.group.exception.GroupMemberCountNotInPermissibleRangeException;
import org.apache.fineract.portfolio.group.exception.GroupMustBePendingToBeDeletedException;
import org.apache.fineract.portfolio.group.exception.InvalidGroupLevelException;
import org.apache.fineract.portfolio.group.exception.InvalidGroupStateTransitionException;
import org.apache.fineract.portfolio.group.exception.UpdateStaffHierarchyException;
import org.apache.fineract.portfolio.group.serialization.GroupingTypesDataValidator;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.village.domain.Village;
import org.apache.fineract.portfolio.village.domain.VillageRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepository;

@Service
public class GroupingTypesWritePlatformServiceJpaRepositoryImpl implements GroupingTypesWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(GroupingTypesWritePlatformServiceJpaRepositoryImpl.class);
    private boolean loadLazyEntities = true;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    private final PlatformSecurityContext context;
    private final GroupRepositoryWrapper groupRepository;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final OfficeRepository officeRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final NoteRepository noteRepository;
    private final GroupLevelRepository groupLevelRepository;
    private final GroupingTypesDataValidator fromApiJsonDeserializer;
    private final LoanRepository loanRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final SavingsAccountRepository savingsRepository;
    private final CommandProcessingService commandProcessingService;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final VillageRepositoryWrapper villageRepository; 
    private final LoanReadPlatformService loanReadPlatformService;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final LoanApplicationReferenceRepository loanApplicationReferenceRepository;
    @Autowired
    public GroupingTypesWritePlatformServiceJpaRepositoryImpl(final RoutingDataSource dataSource,final PlatformSecurityContext context,
            final GroupRepositoryWrapper groupRepository, final ClientRepositoryWrapper clientRepositoryWrapper,
            final OfficeRepository officeRepository, final StaffRepositoryWrapper staffRepository, final NoteRepository noteRepository,
            final GroupLevelRepository groupLevelRepository, final GroupingTypesDataValidator fromApiJsonDeserializer,
            final LoanRepository loanRepository, final SavingsAccountRepository savingsRepository,
            final CodeValueRepositoryWrapper codeValueRepository, final CommandProcessingService commandProcessingService,
            final CalendarInstanceRepository calendarInstanceRepository, final ConfigurationDomainService configurationDomainService,
            final SavingsAccountRepository savingsAccountRepository, final LoanRepositoryWrapper loanRepositoryWrapper, 
            final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository, final AccountNumberGenerator accountNumberGenerator,
            final VillageRepositoryWrapper villageRepository, final LoanReadPlatformService loanReadPlatformService,
            final BusinessEventNotifierService businessEventNotifierService,
            final LoanApplicationReferenceRepository loanApplicationReferenceRepository) {
        this.context = context;
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
        this.groupRepository = groupRepository;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.officeRepository = officeRepository;
        this.staffRepository = staffRepository;
        this.noteRepository = noteRepository;
        this.groupLevelRepository = groupLevelRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanRepository = loanRepository;
        this.savingsRepository = savingsRepository;
        this.codeValueRepository = codeValueRepository;
        this.commandProcessingService = commandProcessingService;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.configurationDomainService = configurationDomainService;
        this.savingsAccountRepository = savingsAccountRepository;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.villageRepository = villageRepository;
        this.loanReadPlatformService = loanReadPlatformService;
        this.businessEventNotifierService = businessEventNotifierService;
        this.loanApplicationReferenceRepository = loanApplicationReferenceRepository;
    }

    private CommandProcessingResult createGroupingType(final JsonCommand command, final GroupTypes groupingType, final Long centerId) {
        try {
            final String accountNo = command.stringValueOfParameterNamed(GroupingTypesApiConstants.accountNoParamName);
            final String name = command.stringValueOfParameterNamed(GroupingTypesApiConstants.nameParamName);
            final String externalId = command.stringValueOfParameterNamed(GroupingTypesApiConstants.externalIdParamName);
            

            final AppUser currentUser = this.context.authenticatedUser();
            Long officeId = null;
            Group parentGroup = null;

            if (centerId == null) {
                officeId = command.longValueOfParameterNamed(GroupingTypesApiConstants.officeIdParamName);
            } else {
                parentGroup = this.groupRepository.findOneWithNotFoundDetection(centerId,loadLazyEntities);
                officeId = parentGroup.officeId();
            }
            final Office groupOffice = this.officeRepository.findOne(officeId);
            if (groupOffice == null) { throw new OfficeNotFoundException(officeId); }

            final LocalDate activationDate = command.localDateValueOfParameterNamed(GroupingTypesApiConstants.activationDateParamName);
            final GroupLevel groupLevel = this.groupLevelRepository.findOne(groupingType.getId());

            validateOfficeOpeningDateisAfterGroupOrCenterOpeningDate(groupOffice, groupLevel, activationDate);
            Boolean inheritStaffFromParent = configurationDomainService.isLoanOfficerToCenterHierarchyEnabled();
            Staff staff = null;
            if (parentGroup != null && inheritStaffFromParent) {
                staff = parentGroup.getStaff();
            } else {
            final Long staffId = command.longValueOfParameterNamed(GroupingTypesApiConstants.staffIdParamName);
            if (staffId != null) {
                staff = this.staffRepository.findByOfficeHierarchyWithNotFoundDetection(staffId, groupOffice.getHierarchy());
            }
            }

            final Set<Client> clientMembers = assembleSetOfClients(officeId, command,inheritStaffFromParent,staff);

            final Set<Group> groupMembers = assembleSetOfChildGroups(officeId, command,inheritStaffFromParent,staff);
            final boolean active = command.booleanPrimitiveValueOfParameterNamed(GroupingTypesApiConstants.activeParamName);
            LocalDate submittedOnDate = DateUtils.getLocalDateOfTenant();
            if (active && submittedOnDate.isAfter(activationDate)) {
                submittedOnDate = activationDate;
            }
            if (command.hasParameter(GroupingTypesApiConstants.submittedOnDateParamName)) {
                submittedOnDate = command.localDateValueOfParameterNamed(GroupingTypesApiConstants.submittedOnDateParamName);
            }

            final Group newGroup = Group.newGroup(groupOffice, staff, parentGroup, groupLevel, name, externalId, active, activationDate,
                    clientMembers, groupMembers, submittedOnDate, currentUser, accountNo);
            

            boolean rollbackTransaction = false;
            if (newGroup.isActive()) {
                // validate Group creation rules for Group
                if (newGroup.isGroup()) {
                    validateGroupRules(newGroup);
                }

                if (newGroup.isCenter()) {
                    final CommandWrapper commandWrapper = new CommandWrapperBuilder().activateCenter(null).build();
                    rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, currentUser);
                } else {
                    final CommandWrapper commandWrapper = new CommandWrapperBuilder().activateGroup(null).build();
                    rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, currentUser);
                }
            }

            if (!newGroup.isCenter() && newGroup.hasActiveClients()) {
                final CommandWrapper commandWrapper = new CommandWrapperBuilder().associateClientsToGroup(newGroup.getId()).build();
                rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, currentUser);
            }
			// pre-save to generate id for use in group hierarchy
            this.groupRepository.save(newGroup);
			/*	
             * Generate hierarchy for a new center/group and all the child
             * groups if they exist
             */
            newGroup.generateHierarchy();
            
            associateVillageWithThisCenter(command, newGroup);

            /* Generate account number if required */
            generateAccountNumberIfRequired(newGroup);

            this.groupRepository.saveAndFlush(newGroup);
            newGroup.captureStaffHistoryDuringCenterCreation(staff, activationDate);
            if (inheritStaffFromParent && newGroup.getStaff() != null) {
                updateLoanAndSavingsOfficer(newGroup);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(groupOffice.getId()) //
                    .withGroupId(newGroup.getId()) //
                    .withEntityId(newGroup.getId()) //
                    .setRollbackTransaction(rollbackTransaction)//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleGroupDataIntegrityIssues(command, dve, groupingType);
            return CommandProcessingResult.empty();
        }
    }

    private void updateLoanAndSavingsOfficer(final Group group) {
        if (group.getStaff() == null) { return; }
        List<Long> allClientMemberIds = group.fetchAllClientMemerIds();
        List<Long> allGroupMemberIds = group.fetchAllGroupMemerIds();
        final boolean isClientMembersPresent = !allClientMemberIds.isEmpty();
        final boolean isGroupMembersPresent = !allGroupMemberIds.isEmpty();
        if (isClientMembersPresent || isGroupMembersPresent) {
            String updateLoanStaffSql = "update m_loan l set l.loan_officer_id = :staffId where ";
            String updateSavingStaffSql = "update m_savings_account s set s.field_officer_id = :staffId where ";
            Map<String, Object> paramMap = new HashMap<>(1);
            if (isClientMembersPresent) {
                updateLoanStaffSql = updateLoanStaffSql + " l.client_id in (:clientIds) ";
                updateSavingStaffSql = updateSavingStaffSql + " s.client_id in (:clientIds) ";
                paramMap.put("clientIds", allClientMemberIds);
                if (isGroupMembersPresent) {
                    updateLoanStaffSql = updateLoanStaffSql + " or ";
                    updateSavingStaffSql = updateSavingStaffSql + " or ";
                }
            }

            if (isGroupMembersPresent) {
                updateLoanStaffSql = updateLoanStaffSql + " l.group_id in (:groupIds)";
                updateSavingStaffSql = updateSavingStaffSql + " s.group_id in (:groupIds)";
                paramMap.put("groupIds", allGroupMemberIds);
            }

            paramMap.put("staffId", group.getStaff().getId());
            if (group.getStaff().isLoanOfficer()) {
                this.jdbcTemplate.update(updateLoanStaffSql, paramMap);
                this.jdbcTemplate.update(updateSavingStaffSql, paramMap);
            }
        }
    }

    private void generateAccountNumberIfRequired(Group newGroup){
    	if (newGroup.isAccountNumberRequiresAutoGeneration()) {
        	EntityAccountType entityAccountType = null;
        	AccountNumberFormat accountNumberFormat = null;
        	if(newGroup.isCenter()){
            	entityAccountType = EntityAccountType.CENTER;
            	accountNumberFormat = this.accountNumberFormatRepository
                        .findByAccountType(entityAccountType);
                newGroup.updateAccountNo(this.accountNumberGenerator.generateCenterAccountNumber(newGroup, accountNumberFormat));
        	}else {
            	entityAccountType = EntityAccountType.GROUP;
            	accountNumberFormat = this.accountNumberFormatRepository
                        .findByAccountType(entityAccountType);
                newGroup.updateAccountNo(this.accountNumberGenerator.generateGroupAccountNumber(newGroup, accountNumberFormat));
        	}
            
        }
    }
    @Transactional
    @Override
    public CommandProcessingResult createCenter(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForCreateCenter(command);

        final Long centerId = null;
        return createGroupingType(command, GroupTypes.CENTER, centerId);
    }

    @Transactional
    @Override
    public CommandProcessingResult createGroup(final Long centerId, final JsonCommand command) {

        if (centerId != null) {
            this.fromApiJsonDeserializer.validateForCreateCenterGroup(command);
        } else {
            this.fromApiJsonDeserializer.validateForCreateGroup(command);
        }

        return createGroupingType(command, GroupTypes.GROUP, centerId);
    }

    @Transactional
    @Override
    public CommandProcessingResult activateGroupOrCenter(final Long groupId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForActivation(command, GroupingTypesApiConstants.GROUP_RESOURCE_NAME);

            final AppUser currentUser = this.context.authenticatedUser();

            final Group group = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);

            if (group.isGroup()) {
                validateGroupRules(group);
            }

            final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");

            validateOfficeOpeningDateisAfterGroupOrCenterOpeningDate(group.getOffice(), group.getGroupLevel(), activationDate);
            group.activate(currentUser, activationDate);

            this.groupRepository.saveAndFlush(group);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(group.officeId()) //
                    .withGroupId(groupId) //
                    .withEntityId(groupId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleGroupDataIntegrityIssues(command, dve, GroupTypes.GROUP);
            return CommandProcessingResult.empty();
        }
    }

    private void validateGroupRules(final Group group) {
        Integer minClients = configurationDomainService.retrieveMinAllowedClientsInGroup();
        Integer maxClients = configurationDomainService.retrieveMaxAllowedClientsInGroup();
        boolean isGroupClientCountValid = group.isGroupsClientCountWithinMinMaxRange(minClients, maxClients);
        if (!isGroupClientCountValid) { throw new GroupMemberCountNotInPermissibleRangeException(group.getId(), minClients, maxClients); }
    }

    public void validateGroupRulesBeforeClientAssociation(final Group group) {
        Integer minClients = configurationDomainService.retrieveMinAllowedClientsInGroup();
        Integer maxClients = configurationDomainService.retrieveMaxAllowedClientsInGroup();
        boolean isGroupClientCountValid = group.isGroupsClientCountWithinMaxRange(maxClients);
        if (!isGroupClientCountValid) { throw new GroupMemberCountNotInPermissibleRangeException(group.getId(), minClients, maxClients); }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateCenter(final Long centerId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdateCenter(command);

        return updateGroupingType(centerId, command, GroupTypes.CENTER);
    }

    @Transactional
    @Override
    public CommandProcessingResult updateGroup(final Long groupId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdateGroup(command);

        return updateGroupingType(groupId, command, GroupTypes.GROUP);
    }

    private CommandProcessingResult updateGroupingType(final Long groupId, final JsonCommand command, final GroupTypes groupingType) {

        try {
            this.context.authenticatedUser();
            final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);
            final Long officeId = groupForUpdate.officeId();
            final Office groupOffice = groupForUpdate.getOffice();
            final String groupHierarchy = groupOffice.getHierarchy();

            this.context.validateAccessRights(groupHierarchy);

            final LocalDate activationDate = command.localDateValueOfParameterNamed(GroupingTypesApiConstants.activationDateParamName);

            validateOfficeOpeningDateisAfterGroupOrCenterOpeningDate(groupOffice, groupForUpdate.getGroupLevel(), activationDate);

            final Map<String, Object> actualChanges = groupForUpdate.update(command);

            Staff newStaff = null;
            Boolean inheritStaffFromParent = configurationDomainService.isLoanOfficerToCenterHierarchyEnabled();
            if (actualChanges.containsKey(GroupingTypesApiConstants.staffIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(GroupingTypesApiConstants.staffIdParamName);
                if (inheritStaffFromParent && groupForUpdate.getParent() != null) { throw new UpdateStaffHierarchyException(newValue); }

                if (newValue != null) {
                    newStaff = this.staffRepository.findByOfficeHierarchyWithNotFoundDetection(newValue, groupHierarchy);
                }
                groupForUpdate.updateStaff(newStaff);
                if (inheritStaffFromParent) {
                    groupForUpdate.updateStaffForAllChilds(newStaff);
                }
            }

            final GroupLevel groupLevel = this.groupLevelRepository.findOne(groupForUpdate.getGroupLevel().getId());

            /*
             * Ignoring parentId param, if group for update is super parent.
             * TODO Need to check: Ignoring is correct or need throw unsupported
             * param
             */
            if (!groupLevel.isSuperParent()) {

                Long parentId = null;
                final Group presentParentGroup = groupForUpdate.getParent();

                if (presentParentGroup != null) {
                    parentId = presentParentGroup.getId();
                }

                if (command.isChangeInLongParameterNamed(GroupingTypesApiConstants.centerIdParamName, parentId)) {

                    final Long newValue = command.longValueOfParameterNamed(GroupingTypesApiConstants.centerIdParamName);
                    actualChanges.put(GroupingTypesApiConstants.centerIdParamName, newValue);
                    Group newParentGroup = null;
                    if (newValue != null) {
                        newParentGroup = this.groupRepository.findOneWithNotFoundDetection(newValue, loadLazyEntities);

                        if (!newParentGroup.isOfficeIdentifiedBy(officeId)) {
                            final String errorMessage = "Group and parent group must have the same office";
                            throw new InvalidOfficeException("group", "attach.to.parent.group", errorMessage);
                        }
                        /*
                         * If Group is not super parent then validate group
                         * level's parent level is same as group parent's level
                         * this check makes sure new group is added at immediate
                         * next level in hierarchy
                         */

                        if (!groupForUpdate.getGroupLevel().isIdentifiedByParentId(newParentGroup.getGroupLevel().getId())) {
                            final String errorMessage = "Parent group's level is  not equal to child level's parent level ";
                            throw new InvalidGroupLevelException("add", "invalid.level", errorMessage);
                        }
                    }

                    groupForUpdate.setParent(newParentGroup);

                    // Parent has changed, re-generate 'Hierarchy' as parent is
                    // changed
                    groupForUpdate.generateHierarchy();

                }
            }
			if (configurationDomainService.isLoanOfficerToCenterHierarchyEnabled() && groupingType.equals(GroupTypes.CENTER)  
					&& actualChanges.containsKey(GroupingTypesApiConstants.staffIdParamName) && newStaff != null
					&& newStaff.isLoanOfficer()) {
				List<Group> groupMembers = groupForUpdate.getGroupMembers();
				if (groupMembers != null && !groupMembers.isEmpty()) {
					for (Group group : groupMembers) {
						group.updateStaff(newStaff);
						List<Loan> groupLoans = this.loanRepository.findByGroupId(group.getId());
						if (groupLoans != null && !groupLoans.isEmpty()) {
							for (Loan loan : groupLoans) {
								loan.updateLoanOfficer(newStaff);
							}
						}
						Set<Client> clientMembers = group.getActiveClientMembers();
						if (clientMembers != null && !clientMembers.isEmpty()) {
							for (Client client : clientMembers) {
								client.updateStaff(newStaff);
								List<Loan> clientLoans = this.loanRepositoryWrapper.findLoanByClientId(client.getId());
								if (clientLoans != null && !clientLoans.isEmpty()) {
									for (Loan loan : clientLoans) {
										loan.updateLoanOfficer(newStaff);
									}
								}
								List<LoanApplicationReference> clientLoanApplicationReference = 
										loanApplicationReferenceRepository.findLoanByClientId(client.getId());
								if(clientLoanApplicationReference != null && !clientLoanApplicationReference.isEmpty()){
									for(LoanApplicationReference loanApplicationReference : clientLoanApplicationReference){
										loanApplicationReference.updateLoanOfficer(newStaff);
									}
								}
							}
						}
					}
				}
			}

            /*
             * final Set<Client> clientMembers = assembleSetOfClients(officeId,
             * command); List<String> changes =
             * groupForUpdate.updateClientMembersIfDifferent(clientMembers); if
             * (!changes.isEmpty()) {
             * actualChanges.put(GroupingTypesApiConstants
             * .clientMembersParamName, changes); }
             */

            this.groupRepository.saveAndFlush(groupForUpdate);
            if (inheritStaffFromParent && groupForUpdate.getStaff() != null) {
                updateLoanAndSavingsOfficer(groupForUpdate);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(groupForUpdate.officeId()) //
                    .withGroupId(groupForUpdate.getId()) //
                    .withEntityId(groupForUpdate.getId()) //
                    .with(actualChanges) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleGroupDataIntegrityIssues(command, dve, groupingType);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult unassignGroupOrCenterStaff(final Long grouptId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        this.fromApiJsonDeserializer.validateForUnassignStaff(command.json());
        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(grouptId,loadLazyEntities);
        final Staff presentStaff = groupForUpdate.getStaff();
        Long presentStaffId = null;
        if (presentStaff == null) { throw new GroupHasNoStaffException(grouptId); }
        presentStaffId = presentStaff.getId();
        if (groupForUpdate.getParent() != null && configurationDomainService
                .isLoanOfficerToCenterHierarchyEnabled()) { throw new UpdateStaffHierarchyException(presentStaffId); }
        final String staffIdParamName = "staffId";
        if (!command.isChangeInLongParameterNamed(staffIdParamName, presentStaffId)) {
            groupForUpdate.unassignStaff();
        }
        this.groupRepository.saveAndFlush(groupForUpdate);

        actualChanges.put(staffIdParamName, null);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(groupForUpdate.getId()) //
                .withGroupId(groupForUpdate.officeId()) //
                .withEntityId(groupForUpdate.getId()) //
                .with(actualChanges) //
                .build();

    }

    @Override
    public CommandProcessingResult assignGroupOrCenterStaff(final Long groupId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

        this.fromApiJsonDeserializer.validateForAssignStaff(command.json());

        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);

        Staff staff = null;
        final Long staffId = command.longValueOfParameterNamed(GroupingTypesApiConstants.staffIdParamName);
        final boolean isLoanOfficerToCenterHierarchyEnabled  =  configurationDomainService.isLoanOfficerToCenterHierarchyEnabled() ;
		if (isLoanOfficerToCenterHierarchyEnabled && groupForUpdate.getParent() != null) {
			throw new UpdateStaffHierarchyException(staffId);
		}
        final boolean inheritStaffForClientAccounts = command
                .booleanPrimitiveValueOfParameterNamed(GroupingTypesApiConstants.inheritStaffForClientAccounts);
        staff = this.staffRepository.findByOfficeHierarchyWithNotFoundDetection(staffId, groupForUpdate.getOffice().getHierarchy());
        groupForUpdate.updateStaff(staff);

        if (inheritStaffForClientAccounts) {
            LocalDate loanOfficerReassignmentDate = DateUtils.getLocalDateOfTenant();
            /*
             * update loan officer for client and update loan officer for
             * clients loans and savings
             */
            Set<Client> clients = groupForUpdate.getClientMembers();
            if (clients != null) {
                for (Client client : clients) {
                    client.updateStaff(staff);
                    if (this.loanRepository.doNonClosedLoanAccountsExistForClient(client.getId())) {
                        for (final Loan loan : this.loanRepositoryWrapper.findLoanByClientId(client.getId())) {
                            if (loan.isDisbursed() && !loan.isClosed()) {
                                loan.reassignLoanOfficer(staff, loanOfficerReassignmentDate);
                            }
                        }
                    }
                    if (this.savingsAccountRepository.doNonClosedSavingAccountsExistForClient(client.getId())) {
                        for (final SavingsAccount savingsAccount : this.savingsAccountRepository
                                .findSavingAccountByClientId(client.getId())) {
                            if (!savingsAccount.isClosed()) {
                                savingsAccount.reassignSavingsOfficer(staff, loanOfficerReassignmentDate);
                            }
                        }
                    }
                }
            }
        }
        this.groupRepository.saveAndFlush(groupForUpdate);

        if (isLoanOfficerToCenterHierarchyEnabled && staff != null) {
            updateLoanAndSavingsOfficer(groupForUpdate);
        }

        actualChanges.put(GroupingTypesApiConstants.staffIdParamName, staffId);

        return new CommandProcessingResultBuilder() //
                .withOfficeId(groupForUpdate.officeId()) //
                .withEntityId(groupForUpdate.getId()) //
                .withGroupId(groupId) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteGroup(final Long groupId) {

        final Group groupForDelete = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);

        if (groupForDelete.isNotPending()) { throw new GroupMustBePendingToBeDeletedException(groupId); }

        final List<Note> relatedNotes = this.noteRepository.findByGroupId(groupId);
        this.noteRepository.deleteInBatch(relatedNotes);

        this.groupRepository.delete(groupForDelete);

        return new CommandProcessingResultBuilder() //
                .withOfficeId(groupForDelete.getId()) //
                .withGroupId(groupForDelete.officeId()) //
                .withEntityId(groupForDelete.getId()) //
                .build();
    }

    @Override
    public CommandProcessingResult closeGroup(final Long groupId, final JsonCommand command) {
        this.fromApiJsonDeserializer.validateForGroupClose(command);
        final Group group = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);
        final LocalDate closureDate = command.localDateValueOfParameterNamed(GroupingTypesApiConstants.closureDateParamName);
        final Long closureReasonId = command.longValueOfParameterNamed(GroupingTypesApiConstants.closureReasonIdParamName);

        final AppUser currentUser = this.context.authenticatedUser();

        final CodeValue closureReason = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                GroupingTypesApiConstants.GROUP_CLOSURE_REASON, closureReasonId);

        if (group.hasActiveClients()) {
            final String errorMessage = group.getGroupLevel().getLevelName()
                    + " cannot be closed because of active clients associated with it.";
            throw new InvalidGroupStateTransitionException(group.getGroupLevel().getLevelName(), "close", "active.clients.exist",
                    errorMessage);
        }

        validateLoansAndSavingsForGroupOrCenterClose(group, closureDate);

        group.close(currentUser, closureReason, closureDate);

        this.groupRepository.saveAndFlush(group);

        return new CommandProcessingResultBuilder() //
                .withGroupId(groupId) //
                .withEntityId(groupId) //
                .build();
    }

    private void validateLoansAndSavingsForGroupOrCenterClose(final Group groupOrCenter, final LocalDate closureDate) {
        final Collection<Loan> groupLoans = this.loanRepository.findByGroupId(groupOrCenter.getId());
        for (final Loan loan : groupLoans) {
            final LoanStatusMapper loanStatus = new LoanStatusMapper(loan.status().getValue());
            if (loanStatus.isOpen()) {
                final String errorMessage = groupOrCenter.getGroupLevel().getLevelName() + " cannot be closed because of non-closed loans.";
                throw new InvalidGroupStateTransitionException(groupOrCenter.getGroupLevel().getLevelName(), "close", "loan.not.closed",
                        errorMessage);
            } else if (loanStatus.isClosed() && loan.getClosedOnDate().after(closureDate.toDate())) {
                final String errorMessage = groupOrCenter.getGroupLevel().getLevelName()
                        + "closureDate cannot be before the loan closedOnDate.";
                throw new InvalidGroupStateTransitionException(groupOrCenter.getGroupLevel().getLevelName(), "close",
                        "date.cannot.before.loan.closed.date", errorMessage, closureDate, loan.getClosedOnDate());
            } else if (loanStatus.isPendingApproval()) {
                final String errorMessage = groupOrCenter.getGroupLevel().getLevelName() + " cannot be closed because of non-closed loans.";
                throw new InvalidGroupStateTransitionException(groupOrCenter.getGroupLevel().getLevelName(), "close", "loan.not.closed",
                        errorMessage);
            } else if (loanStatus.isAwaitingDisbursal()) {
                final String errorMessage = "Group cannot be closed because of non-closed loans.";
                throw new InvalidGroupStateTransitionException(groupOrCenter.getGroupLevel().getLevelName(), "close", "loan.not.closed",
                        errorMessage);
            }
        }

        final List<SavingsAccount> groupSavingAccounts = this.savingsRepository.findByGroupId(groupOrCenter.getId());

        for (final SavingsAccount saving : groupSavingAccounts) {
            if (saving.isActive() || saving.isSubmittedAndPendingApproval() || saving.isApproved()) {
                final String errorMessage = groupOrCenter.getGroupLevel().getLevelName()
                        + " cannot be closed with active savings accounts associated.";
                throw new InvalidGroupStateTransitionException(groupOrCenter.getGroupLevel().getLevelName(), "close",
                        "savings.account.not.closed", errorMessage);
            } else if (saving.isClosed() && saving.getClosedOnDate().isAfter(closureDate)) {
                final String errorMessage = groupOrCenter.getGroupLevel().getLevelName()
                        + " closureDate cannot be before the loan closedOnDate.";
                throw new InvalidGroupStateTransitionException(groupOrCenter.getGroupLevel().getLevelName(), "close",
                        "date.cannot.before.loan.closed.date", errorMessage, closureDate, saving.getClosedOnDate());
            }
        }
    }

    @Override
    public CommandProcessingResult closeCenter(final Long centerId, final JsonCommand command) {
        this.fromApiJsonDeserializer.validateForCenterClose(command);
        final Group center = this.groupRepository.findOneWithNotFoundDetection(centerId,loadLazyEntities);
        final LocalDate closureDate = command.localDateValueOfParameterNamed(GroupingTypesApiConstants.closureDateParamName);
        final Long closureReasonId = command.longValueOfParameterNamed(GroupingTypesApiConstants.closureReasonIdParamName);

        final CodeValue closureReason = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                GroupingTypesApiConstants.CENTER_CLOSURE_REASON, closureReasonId);

        final AppUser currentUser = this.context.authenticatedUser();

        if (center.hasActiveGroups()) {
            final String errorMessage = center.getGroupLevel().getLevelName()
                    + " cannot be closed because of active groups associated with it.";
            throw new InvalidGroupStateTransitionException(center.getGroupLevel().getLevelName(), "close", "active.groups.exist",
                    errorMessage);
        }

        validateLoansAndSavingsForGroupOrCenterClose(center, closureDate);

        center.close(currentUser, closureReason, closureDate);

        this.groupRepository.saveAndFlush(center);

        return new CommandProcessingResultBuilder() //
                .withEntityId(centerId) //
                .build();
    }

    private Set<Client> assembleSetOfClients(final Long groupOfficeId, final JsonCommand command,Boolean inheritStaffFromParent,Staff staff) {

        final Set<Client> clientMembers = new HashSet<>();
        final String[] clientMembersArray = command.arrayValueOfParameterNamed(GroupingTypesApiConstants.clientMembersParamName);

        if (!ObjectUtils.isEmpty(clientMembersArray)) {
            for (final String clientId : clientMembersArray) {
                final Long id = Long.valueOf(clientId);
                final Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(id);
                if (!client.isOfficeIdentifiedBy(groupOfficeId)) {
                    final String errorMessage = "Client with identifier " + clientId + " must have the same office as group.";
                    throw new InvalidOfficeException("client", "attach.to.group", errorMessage, clientId, groupOfficeId);
                }
                clientMembers.add(client);
                if (inheritStaffFromParent) {
                    client.updateStaff(staff);
                }
            }
        }

        return clientMembers;
    }

    private Set<Group> assembleSetOfChildGroups(final Long officeId, final JsonCommand command,Boolean inheritStaffFromParent,Staff staff) {

        final Set<Group> childGroups = new HashSet<>();
        final String[] childGroupsArray = command.arrayValueOfParameterNamed(GroupingTypesApiConstants.groupMembersParamName);

        if (!ObjectUtils.isEmpty(childGroupsArray)) {
            for (final String groupId : childGroupsArray) {
                final Long id = Long.valueOf(groupId);
                final Group group = this.groupRepository.findOneWithNotFoundDetection(id,loadLazyEntities);

                if (!group.isOfficeIdentifiedBy(officeId)) {
                    final String errorMessage = "Group and child groups must have the same office.";
                    throw new InvalidOfficeException("group", "attach.to.parent.group", errorMessage);
                }

                childGroups.add(group);
                if (inheritStaffFromParent) {
                    group.updateStaffForAllChilds(staff);
                }

            }
        }

        return childGroups;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleGroupDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve,
            final GroupTypes groupLevel) {

        String levelName = "Invalid";
        switch (groupLevel) {
            case CENTER:
                levelName = "Center";
            break;
            case GROUP:
                levelName = "Group";
            break;
            case INVALID:
            break;
        }

        final Throwable realCause = dve.getMostSpecificCause();
        String errorMessageForUser = null;
        String errorMessageForMachine = null;

        if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed(GroupingTypesApiConstants.externalIdParamName);
            errorMessageForUser = levelName + " with externalId `" + externalId + "` already exists.";
            errorMessageForMachine = "error.msg." + levelName.toLowerCase() + ".duplicate.externalId";
            throw new PlatformDataIntegrityException(errorMessageForMachine, errorMessageForUser,
                    GroupingTypesApiConstants.externalIdParamName, externalId);
        } else if (realCause.getMessage().contains("name")) {

            final String name = command.stringValueOfParameterNamed(GroupingTypesApiConstants.nameParamName);
            errorMessageForUser = levelName + " with name `" + name + "` already exists.";
            errorMessageForMachine = "error.msg." + levelName.toLowerCase() + ".duplicate.name";
            throw new PlatformDataIntegrityException(errorMessageForMachine, errorMessageForUser, GroupingTypesApiConstants.nameParamName,
                    name);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.group.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    @Override
    public CommandProcessingResult associateClientsToGroup(final Long groupId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForAssociateClients(command.json());

        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);
        Boolean inheritStaffFromParent = configurationDomainService.isLoanOfficerToCenterHierarchyEnabled();
        Staff staff = groupForUpdate.getStaff();
        final Set<Client> clientMembers = assembleSetOfClients(groupForUpdate.officeId(), command, inheritStaffFromParent, staff);
        final Map<String, Object> actualChanges = new HashMap<>();
        final List<String> changes = groupForUpdate.associateClients(clientMembers);

        if (groupForUpdate.isGroup()) {
            validateGroupRulesBeforeClientAssociation(groupForUpdate);
        }
        if (!changes.isEmpty()) {
            actualChanges.put(GroupingTypesApiConstants.clientMembersParamName, changes);
        }

        this.groupRepository.saveAndFlush(groupForUpdate);
        if (inheritStaffFromParent) {
            updateLoanAndSavingsOfficer(groupForUpdate);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(groupForUpdate.officeId()) //
                .withGroupId(groupForUpdate.getId()) //
                .withEntityId(groupForUpdate.getId()) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult disassociateClientsFromGroup(final Long groupId, final JsonCommand command) {
        this.fromApiJsonDeserializer.validateForDisassociateClients(command.json());

        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId,loadLazyEntities);
        Boolean inheritStaffFromParent = configurationDomainService.isLoanOfficerToCenterHierarchyEnabled();
        Staff staff = groupForUpdate.getStaff();
        final Set<Client> clientMembers = assembleSetOfClients(groupForUpdate.officeId(), command, inheritStaffFromParent, staff);

        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.CLIENT_DISASSOCIATE,
                            constructEntityMap(BUSINESS_ENTITY.CLIENT_DISASSOCIATE, clientMembers));

        // check if any client has got group loans
        checkForActiveJLGLoans(groupForUpdate.getId(), clientMembers);
        validateForJLGSavings(groupForUpdate.getId(), clientMembers);
        final Map<String, Object> actualChanges = new HashMap<>();
       
        final List<String> changes = groupForUpdate.disassociateClients(clientMembers);
        if (groupForUpdate.isGroup()) {
            validateGroupRules(groupForUpdate);
        }
        if (!changes.isEmpty()) {
            actualChanges.put(GroupingTypesApiConstants.clientMembersParamName, changes);
        }

        this.groupRepository.saveAndFlush(groupForUpdate);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(groupForUpdate.officeId()) //
                .withGroupId(groupForUpdate.getId()) //
                .withEntityId(groupForUpdate.getId()) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult associateGroupsToCenter(final Long centerId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForAssociateGroups(command.json());
        final Group centerForUpdate = this.groupRepository.findOneWithNotFoundDetection(centerId,loadLazyEntities);
        Boolean inheritStaffFromParent = configurationDomainService.isLoanOfficerToCenterHierarchyEnabled();
        Staff staff = centerForUpdate.getStaff();
        final Set<Group> groupMembers = assembleSetOfChildGroups(centerForUpdate.officeId(), command, inheritStaffFromParent, staff);
        checkGroupMembersMeetingSyncWithCenterMeeting(centerId, groupMembers);

        final Map<String, Object> actualChanges = new HashMap<>();
        final List<String> changes = centerForUpdate.associateGroups(groupMembers);
        if (!changes.isEmpty()) {
            actualChanges.put(GroupingTypesApiConstants.groupMembersParamName, changes);
        }
        this.groupRepository.saveAndFlush(centerForUpdate);
        if (inheritStaffFromParent) {
            updateLoanAndSavingsOfficer(centerForUpdate);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(centerForUpdate.officeId()) //
                .withGroupId(centerForUpdate.getId()) //
                .withEntityId(centerForUpdate.getId()) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult disassociateGroupsToCenter(final Long centerId, final JsonCommand command) {
        this.fromApiJsonDeserializer.validateForDisassociateGroups(command.json());

        final Group centerForUpdate = this.groupRepository.findOneWithNotFoundDetection(centerId,loadLazyEntities);
        Boolean inheritStaffFromParent = configurationDomainService.isLoanOfficerToCenterHierarchyEnabled();
        Staff staff = centerForUpdate.getStaff();
        final Set<Group> groupMembers = assembleSetOfChildGroups(centerForUpdate.officeId(), command, inheritStaffFromParent, staff);
        final Map<String, Object> actualChanges = new HashMap<>();
        validateGroupMembersHasLoans(groupMembers);
        final List<String> changes = centerForUpdate.disassociateGroups(groupMembers);
        if (!changes.isEmpty()) {
            actualChanges.put(GroupingTypesApiConstants.clientMembersParamName, changes);
        }

        this.groupRepository.saveAndFlush(centerForUpdate);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(centerForUpdate.officeId()) //
                .withGroupId(centerForUpdate.getId()) //
                .withEntityId(centerForUpdate.getId()) //
                .with(actualChanges) //
                .build();

    }

    @Transactional
    private void checkForActiveJLGLoans(final Long groupId, final Set<Client> clientMembers) {
        for (final Client client : clientMembers) {
            final Collection<Loan> loans = this.loanRepositoryWrapper.findActiveLoansByLoanIdAndGroupId(client.getId(), groupId);
            if (!CollectionUtils.isEmpty(loans)) {
                final String defaultUserMessage = "Client with identifier " + client.getId()
                        + " cannot be disassociated it has group loans.";
                throw new GroupAccountExistsException("disassociate", "client.has.group.loan", defaultUserMessage, client.getId(), groupId);
            }
        }
    }

    @Transactional
    private void validateForJLGSavings(final Long groupId, final Set<Client> clientMembers) {
        for (final Client client : clientMembers) {
            final Collection<SavingsAccount> savings = this.savingsRepository.findByClientIdAndGroupId(client.getId(), groupId);
            if (!CollectionUtils.isEmpty(savings)) {
                final String defaultUserMessage = "Client with identifier " + client.getId()
                        + " cannot be disassociated it has group savings.";
                throw new GroupAccountExistsException("disassociate", "client.has.group.saving", defaultUserMessage, client.getId(),
                        groupId);
            }
        }
    }

    public void validateOfficeOpeningDateisAfterGroupOrCenterOpeningDate(final Office groupOffice, final GroupLevel groupLevel,
            final LocalDate activationDate) {
        if (activationDate != null && groupOffice.getOpeningLocalDate().isAfter(activationDate)) {
            final String levelName = groupLevel.getLevelName();
            final String errorMessage = levelName
                    + " activation date should be greater than or equal to the parent Office's creation date " + activationDate.toString();
            throw new InvalidGroupStateTransitionException(levelName.toLowerCase(), "activate.date",
                    "cannot.be.before.office.activation.date", errorMessage, activationDate, groupOffice.getOpeningLocalDate());
        }
    }

    private void checkGroupMembersMeetingSyncWithCenterMeeting(final Long centerId, final Set<Group> groupMembers) {

        /**
         * Get parent(center) calendar
         */
        Calendar ceneterCalendar = null;
        final CalendarInstance parentCalendarInstance = this.calendarInstanceRepository.findByEntityIdAndEntityTypeIdAndCalendarTypeId(
                centerId, CalendarEntityType.CENTERS.getValue(), CalendarType.COLLECTION.getValue());
        if (parentCalendarInstance != null) {
            ceneterCalendar = parentCalendarInstance.getCalendar();
        }

        for (final Group group : groupMembers) {
            /**
             * Get child(group) calendar
             */
            Calendar groupCalendar = null;
            final CalendarInstance groupCalendarInstance = this.calendarInstanceRepository.findByEntityIdAndEntityTypeIdAndCalendarTypeId(
                    group.getId(), CalendarEntityType.GROUPS.getValue(), CalendarType.COLLECTION.getValue());
            if (groupCalendarInstance != null) {
                groupCalendar = groupCalendarInstance.getCalendar();
            }

            /**
             * Group shouldn't have a meeting when no meeting attached for
             * center
             */
            if (ceneterCalendar == null && groupCalendar != null) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.center.associating.group.not.allowed.with.meeting.attached.to.group", "Group with id " + group.getId()
                                + " is already associated with meeting", group.getId());
            }
            /**
             * Group meeting recurrence should match with center meeting
             * recurrence
             */
            else if (ceneterCalendar != null && groupCalendar != null) {

                if (!ceneterCalendar.getRecurrence().equalsIgnoreCase(groupCalendar.getRecurrence())) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.center.associating.group.not.allowed.with.different.meeting", "Group with id " + group.getId()
                                + " meeting recurrence doesnot matched with center meeting recurrence", group.getId()); }
            }
        }
    }
    
    private void associateVillageWithThisCenter(final JsonCommand command, final Group newGroup) {
        final Long villageId = command.longValueOfParameterNamed(GroupingTypesApiConstants.villageIdParamName);
        if (newGroup.isCenter() && villageId != null) {
            Village village = this.villageRepository.findOneWithNotFoundDetection(villageId);
            newGroup.setVillage(village);
            // Increment Village Counter
            village.incrementCount();
            this.villageRepository.saveAndFlush(village);
        }
    }
    
    private void validateGroupMembersHasLoans(final Set<Group> groupMembersSet) {
        for (final Group group : groupMembersSet) {
            final Collection<Long> loanIds = loanReadPlatformService.retrieveAllActiveSubmittedAprrovedGroupLoanIds(group.getId());
            if (!loanIds.isEmpty()) {
                final String defaultUserMessage = "Romoval of group from center is not possible since  it has  submitted and pending for approval, approved and active loans";
                throw new CalendarDateException("removal.of.group.not.possible.since.it.has.submitted.or.approved.or.active.loans",
                        defaultUserMessage, loanIds);
            }
        }
    }
 
    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }
}