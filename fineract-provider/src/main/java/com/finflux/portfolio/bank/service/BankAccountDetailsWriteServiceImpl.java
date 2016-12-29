package com.finflux.portfolio.bank.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.api.BankAccountDetailConstants;
import com.finflux.portfolio.bank.data.BankAccountDetailDataValidator;
import com.finflux.portfolio.bank.domain.BankAccountAssociationDetailsRepositoryWrapper;
import com.finflux.portfolio.bank.domain.BankAccountDetailAssociations;
import com.finflux.portfolio.bank.domain.BankAccountDetailAssociationsRepository;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.domain.BankAccountDetailStatus;
import com.finflux.portfolio.bank.domain.BankAccountDetails;
import com.finflux.portfolio.bank.domain.BankAccountDetailsRepository;
import com.finflux.portfolio.bank.domain.BankAccountDetailsRepositoryWrapper;
import com.finflux.portfolio.bank.exception.BankAccountDetailAssociationExistsException;
import com.google.gson.JsonElement;

@Service
public class BankAccountDetailsWriteServiceImpl implements BankAccountDetailsWriteService {

    private final BankAccountDetailsRepository repository;
    private final BankAccountDetailsRepositoryWrapper repositoryWrapper;
    private final BankAccountDetailDataValidator fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final BankAccountDetailAssociationsRepository accountDetailAssociationsRepository;
    private final BankAccountAssociationDetailsRepositoryWrapper bankAccountAssociationDetailsRepositoryWrapper;

    @Autowired
    public BankAccountDetailsWriteServiceImpl(BankAccountDetailsRepository repository,
            BankAccountDetailsRepositoryWrapper repositoryWrapper, BankAccountDetailDataValidator fromApiJsonDeserializer,
            final FromJsonHelper fromApiJsonHelper, final BankAccountDetailAssociationsRepository accountDetailAssociationsRepository,
            final BankAccountAssociationDetailsRepositoryWrapper bankAccountAssociationDetailsRepositoryWrapper) {
        this.repository = repository;
        this.repositoryWrapper = repositoryWrapper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.accountDetailAssociationsRepository = accountDetailAssociationsRepository;
        this.bankAccountAssociationDetailsRepositoryWrapper = bankAccountAssociationDetailsRepositoryWrapper;
    }

    @Override
    public CommandProcessingResult create(JsonCommand command) {
        String json = command.json();
        BankAccountDetailEntityType type = BankAccountDetailEntityType.fromInt(command.getEntityTypeId());
        BankAccountDetailAssociations accountDetails = createBankAccountDetailAssociation(type, command.entityId(), json);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(accountDetails.getId()).build();
    }

    private BankAccountDetails createBankAccountDetail(String json) {
        this.fromApiJsonDeserializer.validateForCreate(json);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.nameParameterName, element);

        final String accountNumber = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.accountNumberParameterName,
                element);

        final String ifscCode = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.ifscCodeParameterName, element);

        final String mobileNumber = this.fromApiJsonHelper
                .extractStringNamed(BankAccountDetailConstants.mobileNumberParameterName, element);

        final String email = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.emailParameterName, element);
        final String bankName = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.bankNameParameterName, element);
        final String bankCity = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.bankCityParameterName, element);

        BankAccountDetails accountDetails = BankAccountDetails.create(name, accountNumber, ifscCode, mobileNumber, email,
                bankName, bankCity);
        accountDetails.updateStatus(BankAccountDetailStatus.ACTIVE.getValue());
        return accountDetails;
    }

    @Override
    public BankAccountDetailAssociations createBankAccountDetailAssociation(final BankAccountDetailEntityType entityType,
            final Long entityId, String json) {
        BankAccountDetailAssociations existingAccountDetails = this.accountDetailAssociationsRepository.findByEntityIdAndEntityTypeId(
                entityId, entityType.getValue());
        if (existingAccountDetails != null) { throw new BankAccountDetailAssociationExistsException(entityId, entityType.getValue()); }
        BankAccountDetails accountDetails = createBankAccountDetail(json);
        BankAccountDetailAssociations associations = new BankAccountDetailAssociations(accountDetails, entityType.getValue(), entityId);
        this.accountDetailAssociationsRepository.save(associations);
        return associations;
    }

    @Override
    public CommandProcessingResult update(JsonCommand command) {
        String json = command.json();
        BankAccountDetailEntityType type = BankAccountDetailEntityType.fromInt(command.getEntityTypeId());
        Map<String, Object> changes = updateBankAccountDetail(type, command.entityId(), json);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(command.entityId()).with(changes)
                .build();
    }

    @Override
    public Map<String, Object> updateBankAccountDetail(final BankAccountDetailEntityType entityType, final Long entityId, String json) {
        this.fromApiJsonDeserializer.validateForUpdate(json);
        BankAccountDetailAssociations accountDetailAssociations = this.bankAccountAssociationDetailsRepositoryWrapper
                .findOneWithNotFoundDetection(entityId, entityType.getValue());
        BankAccountDetails accountDetails = accountDetailAssociations.getBankAccountDetails();
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        boolean creaeNewAccountDetail = false;
        if (this.fromApiJsonHelper.parameterExists(BankAccountDetailConstants.accountNumberParameterName, element)) {
            final String accountNumber = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.accountNumberParameterName,
                    element);
            if (!accountNumber.equalsIgnoreCase(accountDetails.getAccountNumber())) {
                creaeNewAccountDetail = true;
            }
        }

        if (creaeNewAccountDetail) {
            accountDetails.updateStatus(BankAccountDetailStatus.DELETED.getValue());
            this.repository.save(accountDetails);
            accountDetails = BankAccountDetails.copy(accountDetails);
            accountDetailAssociations.setBankAccountDetails(accountDetails);
        }
        JsonCommand command = JsonCommand.from(this.fromApiJsonHelper, element, null);
        final Map<String, Object> changes = accountDetails.update(command);
        if (!changes.isEmpty()) {
            this.accountDetailAssociationsRepository.save(accountDetailAssociations);
        }
        return changes;
    }

    @Override
    public CommandProcessingResult delete(Long bankAccountDetailId) {
        final BankAccountDetails accountDetails = this.repositoryWrapper.findOneWithNotFoundDetection(bankAccountDetailId);
        accountDetails.updateStatus(BankAccountDetailStatus.DELETED.getValue());
        this.repository.save(accountDetails);
        return new CommandProcessingResultBuilder().withEntityId(bankAccountDetailId).build();
    }

    @Override
    public Long deleteBankDetailAssociation(final BankAccountDetailEntityType entityType, final Long entityId) {
        BankAccountDetailAssociations accountDetailAssociations = this.bankAccountAssociationDetailsRepositoryWrapper
                .findOneWithNotFoundDetection(entityId, entityType.getValue());
        Long id = accountDetailAssociations.getId();
        this.accountDetailAssociationsRepository.delete(accountDetailAssociations);
        return id;
    }

    @Override
    public CommandProcessingResult delete(JsonCommand jsonCommand) {
        BankAccountDetailEntityType type = BankAccountDetailEntityType.fromInt(jsonCommand.getEntityTypeId());
        Long id = deleteBankDetailAssociation(type, jsonCommand.entityId());
        return new CommandProcessingResultBuilder().withEntityId(id).build();
    }

}
