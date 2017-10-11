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
package org.apache.fineract.useradministration.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.useradministration.api.AppUserApiConstant;
import org.apache.fineract.useradministration.command.PermissionsCommand;
import org.apache.fineract.useradministration.domain.Permission;
import org.apache.fineract.useradministration.domain.PermissionRepository;
import org.apache.fineract.useradministration.domain.Role;
import org.apache.fineract.useradministration.domain.RoleBasedLimit;
import org.apache.fineract.useradministration.domain.RoleBasedLimitRepository;
import org.apache.fineract.useradministration.domain.RoleRepository;
import org.apache.fineract.useradministration.exception.PermissionNotFoundException;
import org.apache.fineract.useradministration.exception.RoleAssociatedException;
import org.apache.fineract.useradministration.exception.RoleNotFoundException;
import org.apache.fineract.useradministration.serialization.PermissionsCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class RoleWritePlatformServiceJpaRepositoryImpl implements RoleWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(RoleWritePlatformServiceJpaRepositoryImpl.class);
    private final PlatformSecurityContext context;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleDataValidator roleCommandFromApiJsonDeserializer;
    private final PermissionsCommandFromApiJsonDeserializer permissionsFromApiJsonDeserializer;
    private final RoleBasedLimitRepository roleBasedLimitRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public RoleWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final RoleRepository roleRepository,
            final PermissionRepository permissionRepository, final RoleDataValidator roleCommandFromApiJsonDeserializer,
            final PermissionsCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final RoleBasedLimitRepository roleBasedLimitRepository, final FromJsonHelper fromApiJsonHelper,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper) {
        this.context = context;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleCommandFromApiJsonDeserializer = roleCommandFromApiJsonDeserializer;
        this.permissionsFromApiJsonDeserializer = fromApiJsonDeserializer;
        this.roleBasedLimitRepository = roleBasedLimitRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.applicationCurrencyRepository = applicationCurrencyRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult createRole(final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            this.roleCommandFromApiJsonDeserializer.validateForCreate(command.json());

            /** Create the Role **/
            final Role role = Role.fromJson(command);
            this.roleRepository.save(role);

            /** Create Role based limits **/
            final Map<String, Object> changes = null;
            List<RoleBasedLimit> roleBasedLimits = createOrUpdateRoleBasedLimits(role, command, changes);

            /** Update role with limits **/
            if (!roleBasedLimits.isEmpty()) {
                role.getRoleBasedLimits().addAll(roleBasedLimits);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(role.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .build();
        }
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("unq_name")) {

            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.role.duplicate.name", "Role with name `" + name + "` already exists",
                    "name", name);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.role.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

//    @Caching(evict = { /*@CacheEvict(value = "users", allEntries = true),*/ @CacheEvict(value = "usersByUsername", allEntries = true) })
    @Transactional
    @Override
    public CommandProcessingResult updateRole(final Long roleId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            this.roleCommandFromApiJsonDeserializer.validateForUpdate(command.json());

            final Role role = this.roleRepository.findOne(roleId);
            if (role == null) { throw new RoleNotFoundException(roleId); }

            final Map<String, Object> changes = role.update(command);
            this.roleRepository.saveAndFlush(role);

            /** Update role based limits **/
            List<RoleBasedLimit> roleBasedLimits = createOrUpdateRoleBasedLimits(role, command, changes);
            if (!changes.isEmpty()) {
                role.getRoleBasedLimits().clear();
                role.getRoleBasedLimits().addAll(roleBasedLimits);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(roleId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .build();
        }
    }

//    @Caching(evict = { /*@CacheEvict(value = "users", allEntries = true),*/ @CacheEvict(value = "usersByUsername", allEntries = true) })
    @Transactional
    @Override
    public CommandProcessingResult updateRolePermissions(final Long roleId, final JsonCommand command) {
        this.context.authenticatedUser();

        final Role role = this.roleRepository.findOne(roleId);
        if (role == null) { throw new RoleNotFoundException(roleId); }

        final Collection<Permission> allPermissions = this.permissionRepository.findAll();

        final PermissionsCommand permissionsCommand = this.permissionsFromApiJsonDeserializer.commandFromApiJson(command.json());

        final Map<String, Boolean> commandPermissions = permissionsCommand.getPermissions();
        final Map<String, Object> changes = new HashMap<>();
        final Map<String, Boolean> changedPermissions = new HashMap<>();
        for (final String permissionCode : commandPermissions.keySet()) {
            final boolean isSelected = commandPermissions.get(permissionCode).booleanValue();

            final Permission permission = findPermissionByCode(allPermissions, permissionCode);
            final boolean changed = role.updatePermission(permission, isSelected);
            if (changed) {
                changedPermissions.put(permissionCode, isSelected);
            }
        }

        if (!changedPermissions.isEmpty()) {
            changes.put("permissions", changedPermissions);
            this.roleRepository.save(role);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(roleId) //
                .with(changes) //
                .build();
    }

    private Permission findPermissionByCode(final Collection<Permission> allPermissions, final String permissionCode) {

        if (allPermissions != null) {
            for (final Permission permission : allPermissions) {
                if (permission.hasCode(permissionCode)) { return permission; }
            }
        }
        throw new PermissionNotFoundException(permissionCode);
    }

    /**
     * Method for Delete Role
     */
    @Transactional
    @Override
    public CommandProcessingResult deleteRole(Long roleId) {

        try {
            /**
             * Checking the role present in DB or not using role_id
             */
            final Role role = this.roleRepository.findOne(roleId);
            if (role == null) { throw new RoleNotFoundException(roleId); }

            /**
             * Roles associated with users can't be deleted
             */
            final Integer count = this.roleRepository.getCountOfRolesAssociatedWithUsers(roleId);
            if (count > 0) { throw new RoleAssociatedException("error.msg.role.associated.with.users.deleted", roleId); }

            this.roleRepository.delete(role);
            return new CommandProcessingResultBuilder().withEntityId(roleId).build();
        } catch (final DataIntegrityViolationException e) {
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + e.getMostSpecificCause());
        }
    }

    /**
     * Method for disabling the role
     */
    @Transactional
    @Override
    public CommandProcessingResult disableRole(Long roleId) {
        try {
            /**
             * Checking the role present in DB or not using role_id
             */
            final Role role = this.roleRepository.findOne(roleId);
            if (role == null) { throw new RoleNotFoundException(roleId); }
            // if(role.isDisabled()){throw new RoleNotFoundException(roleId);}

            /**
             * Roles associated with users can't be disable
             */
            final Integer count = this.roleRepository.getCountOfRolesAssociatedWithUsers(roleId);
            if (count > 0) { throw new RoleAssociatedException("error.msg.role.associated.with.users.disabled", roleId); }

            /**
             * Disabling the role
             */
            role.disableRole();
            this.roleRepository.save(role);
            return new CommandProcessingResultBuilder().withEntityId(roleId).build();

        } catch (final DataIntegrityViolationException e) {
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + e.getMostSpecificCause());
        }
    }

    /**
     * Method for Enabling the role
     */
    @Transactional
    @Override
    public CommandProcessingResult enableRole(Long roleId) {
        try {
            /**
             * Checking the role present in DB or not using role_id
             */
            final Role role = this.roleRepository.findOne(roleId);
            if (role == null) { throw new RoleNotFoundException(roleId); }
            // if(!role.isEnabled()){throw new RoleNotFoundException(roleId);}

            role.enableRole();
            this.roleRepository.save(role);
            return new CommandProcessingResultBuilder().withEntityId(roleId).build();

        } catch (final DataIntegrityViolationException e) {
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + e.getMostSpecificCause());
        }
    }

    private List<RoleBasedLimit> createOrUpdateRoleBasedLimits(final Role role, final JsonCommand command, Map<String, Object> changes) {
        List<RoleBasedLimit> roleBasedLimits = new ArrayList<>();
        boolean changesMade = false;
        if (command.parameterExists(AppUserApiConstant.ROLE_BASED_LIMITS)) {
            JsonElement element = command.parsedJson();
            final JsonArray roleBasedLimitsArray = this.fromApiJsonHelper.extractJsonArrayNamed(AppUserApiConstant.ROLE_BASED_LIMITS,
                    element);
            final JsonObject topLevelJsonObject = element.getAsJsonObject();
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonObject);
            if (roleBasedLimitsArray != null && roleBasedLimitsArray.size() > 0) {
                for (int i = 0; i < roleBasedLimitsArray.size(); i++) {
                    final JsonObject jsonObject = roleBasedLimitsArray.get(i).getAsJsonObject();
                    /** Extract max approval Amount **/
                    final BigDecimal maxLoanApprovalAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                            AppUserApiConstant.LOAN_APPROVAL_AMOUNT_LIMIT, jsonObject, locale);
                    /** Extract Organizational Currency **/
                    final String currencyCode = this.fromApiJsonHelper.extractStringNamed(AppUserApiConstant.CURRENCY_CODE, jsonObject);
                    ApplicationCurrency applicationCurrency = applicationCurrencyRepository.findOneWithNotFoundDetection(currencyCode);
                    /**
                     * Find existing Role based limits / Create Role based
                     * limits and add to array if the same is not already found.
                     * Also check if changes are made to existing Role based
                     * limits
                     **/
                    RoleBasedLimit roleBasedLimit = null;
                    if (role.getId() != null) {
                        roleBasedLimit = roleBasedLimitRepository.findByRoleAndApplicationCurrency(role, applicationCurrency);
                    }
                    if (roleBasedLimit != null && maxLoanApprovalAmount.compareTo(roleBasedLimit.getMaxLoanApprovalAmount()) != 0) {
                        roleBasedLimit.setMaxLoanApprovalAmount(maxLoanApprovalAmount);
                        changesMade = true;
                    } else if (roleBasedLimit == null) {
                        roleBasedLimit = new RoleBasedLimit(role, applicationCurrency, maxLoanApprovalAmount);
                        changesMade = true;
                    }
                    roleBasedLimits.add(roleBasedLimit);
                }
            }

            if (changesMade) {
                if (changes != null) {
                    changes.put(AppUserApiConstant.ROLE_BASED_LIMITS, element.toString());
                }
                roleBasedLimitRepository.save(roleBasedLimits);
            }
        }
        return roleBasedLimits;
    }
}