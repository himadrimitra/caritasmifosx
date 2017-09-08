package com.finflux.kyc.address.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.common.util.FinfluxCollectionUtils;
import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.address.data.AddressDataValidator;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.domain.Address;
import com.finflux.kyc.address.domain.AddressEntity;
import com.finflux.kyc.address.domain.AddressRepositoryWrapper;
import com.finflux.kyc.address.exception.AddressNotFoundException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class AddressWritePlatformServiceImpl implements AddressWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(AddressWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final AddressDataValidator validator;
    private final AddressBusinessValidators addressBusinessValidators;
    private final AddressDataAssembler assembler;
    private final AddressRepositoryWrapper repository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddressWritePlatformServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context,
            final FromJsonHelper fromApiJsonHelper, final AddressDataValidator validator,
            final AddressBusinessValidators addressBusinessValidators, final AddressDataAssembler assembler,
            final AddressRepositoryWrapper repository, final CodeValueRepositoryWrapper codeValueRepository,
            final BusinessEventNotifierService businessEventNotifierService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.validator = validator;
        this.addressBusinessValidators = addressBusinessValidators;
        this.assembler = assembler;
        this.repository = repository;
        this.codeValueRepository = codeValueRepository;
        this.businessEventNotifierService = businessEventNotifierService;
    }

    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    @Transactional
    @Override
    public CommandProcessingResult create(final Integer entityTypeEnum, final Long entityId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            this.addressBusinessValidators.validateAddressEntityIdAndEntityType(entityTypeEnum, entityId);

            validateEntityLockedOrNot(entityTypeEnum,entityId);
            
            this.validator.validateForCreate(entityTypeEnum, entityId, command.json());

            final List<Address> addresses = this.assembler.assembleCreateForm(entityTypeEnum, entityId, command);

            this.repository.save(addresses);

            final Map<String, Object> changes = new HashMap<String, Object>();
            final Set<Long> addressIds = new HashSet();
            for (final Address address : addresses) {
                addressIds.add(address.getId());
            }
            changes.put("resourceIds", addressIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(addresses.get(0).getId())//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void validateEntityLockedOrNot(final Integer entityType, final Long entityId) {
        final AddressEntityTypeEnums addressEntityTypeEnum = AddressEntityTypeEnums.fromInt(entityType);
        String sqlQueryForEntityLockedOrNot = null;
        if (addressEntityTypeEnum.isClients()) {
            sqlQueryForEntityLockedOrNot = "select c.is_locked from m_client c where c.id = ? ";
        }
        if (sqlQueryForEntityLockedOrNot != null) {
            try {
                final boolean isLocked = this.jdbcTemplate.queryForObject(sqlQueryForEntityLockedOrNot, Boolean.class, entityId);
                this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.ADDRESS_ADD,
                        FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, isLocked));
            } catch (final EmptyResultDataAccessException e) {}
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult update(final Long addressId, final Long entityId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            /**
             * Checking Address exists or not
             */
            final Address address = this.repository.findOneWithNotFoundDetection(addressId);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.ADDRESS_UPDATE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, address.isLocked()));
            final Set<AddressEntity> addressEntities = address.getAddressEntities();

            Integer entityTypeEnum = null;
            for (final AddressEntity addressEntity : addressEntities) {
                if (addressEntity.getParentAddressType() == null) {
                    entityTypeEnum = addressEntity.getEntityTypeEnum();
                    break;
                }
            }

            this.addressBusinessValidators.validateAddressEntityIdAndEntityType(entityTypeEnum, entityId);

            this.validator.validateForUpdate(entityTypeEnum, entityId, command.json());

            final Map<String, Object> changes = this.assembler.assembleUpdateForm(address, entityId, entityTypeEnum, command);

            this.repository.save(address);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(addressId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @SuppressWarnings("unused")
    @Transactional
    @Override
    public CommandProcessingResult delete(final Long addressId, final Long entityId, final JsonCommand command) {
        try {

            this.context.authenticatedUser();

            /**
             * Checking Address exists or not
             */
            final Address address = this.repository.findOneWithNotFoundDetection(addressId);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.ADDRESS_DELETE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, address.isLocked()));
            this.repository.delete(address);

            return new CommandProcessingResultBuilder() //
                    .withEntityId(addressId) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Override
    @SuppressWarnings("null")
    public void createOrUpdateAddress(final AddressEntityTypeEnums entityType, final Long entityId, final JsonCommand command) {
        final Integer entityTypeEnum = entityType.getValue();
        final JsonElement addressElement = this.fromApiJsonHelper.parse(command.jsonFragment(AddressApiConstants.addressesParamName));
        final JsonArray array = addressElement.getAsJsonArray();
        if (array != null && array.size() > 0) {
            final JsonObject createJsonObject = new JsonObject();
            final JsonArray createJsonArray = new JsonArray();
            for (int i = 0; i < array.size(); i++) {
                final JsonObject addressJsonObject = array.get(i).getAsJsonObject();
                if (addressJsonObject.has(AddressApiConstants.addressIdParamName)) {
                    /**
                     * Call Update Address Service
                     */
                    final JsonElement element = this.fromApiJsonHelper.parse(addressJsonObject.toString());
                    final JsonCommand addressCommand = JsonCommand.fromExistingCommand(command, element);
                    final Long addressId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.addressIdParamName, element);
                    update(addressId, entityId, addressCommand);
                } else {
                    addressJsonObject.addProperty(AddressApiConstants.entityIdParamName, entityId);
                    addressJsonObject.addProperty(AddressApiConstants.entityTypeEnumParamName, entityTypeEnum);
                    final JsonElement element = this.fromApiJsonHelper.parse(addressJsonObject.toString());
                    createJsonArray.add(element);
                }
            }
            if (createJsonArray != null && createJsonArray.size() > 0) {
                createJsonObject.add(AddressApiConstants.addressesParamName, createJsonArray);
                final JsonElement element = this.fromApiJsonHelper.parse(createJsonObject.toString());
                final JsonCommand addressCommand = JsonCommand.fromExistingCommand(command, element);
                /**
                 * Call Create Address Service
                 */
                create(entityType.getValue(), entityId, addressCommand);
            }
        }
    }

    /**
     * Guaranteed to throw an exception no matter what the data integrity issues
     * 
     * @param command
     * @param dve
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();

        if (realCause.getMessage().contains("f_entity_address_UNIQUE")) {
            final Long addressTypeId = command.longValueOfParameterNamed(AddressApiConstants.addressTypesParamName);
            final CodeValue addressType = this.codeValueRepository.findOneWithNotFoundDetection(addressTypeId);
            throw new PlatformDataIntegrityException("error.msg.address.type.duplicate",
                    "Address type `" + addressType.label() + "` already exists", "addressType", addressType.label());
        }

        logAsErrorUnexpectedDataIntegrityException(dve);

        throw new PlatformDataIntegrityException("error.msg.kyc.address.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}
