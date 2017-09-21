package com.finflux.familydetail.service;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.finflux.common.util.FinfluxCollectionUtils;
import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.data.FamilyDetailDataValidator;
import com.finflux.familydetail.domain.FamilyDetail;
import com.finflux.familydetail.domain.FamilyDetailsRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class FamilyDetailWritePlatformServiceImp implements FamilyDetailWritePlatromService {

    private final FamilyDetailsRepository familyDetailsRepository;
    private final PlatformSecurityContext context;
    private final FamilyDetailDataValidator validator;
    private final ClientRepositoryWrapper clientRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final FamilyDetailDataAssembler assembler;
    private final BusinessEventNotifierService businessEventNotifierService;

    @Autowired
    public FamilyDetailWritePlatformServiceImp(final FamilyDetailsRepository familyDetailsRepository,
            final PlatformSecurityContext context, final FamilyDetailDataValidator validator,
            final ClientRepositoryWrapper clientRepository, final FromJsonHelper fromApiJsonHelper,
            final FamilyDetailDataAssembler assembler, final BusinessEventNotifierService businessEventNotifierService) {
        this.familyDetailsRepository = familyDetailsRepository;
        this.context = context;
        this.validator = validator;
        this.clientRepository = clientRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.assembler = assembler;
        this.businessEventNotifierService = businessEventNotifierService;
    }

    @Transactional
    @Override
    public CommandProcessingResult createFamilyDeatails(final Long clientId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.FAMILY_DETAILS_ADD,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, client.isLocked()));
            this.validator.validateForCreate(command.json());
            final List<FamilyDetail> familyDetails = this.assembler.assembleCreateForm(client, command);
            this.familyDetailsRepository.save(familyDetails);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(client.getId()) //
                    .withEntityId(familyDetails.get(0).getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateFamilyDeatails(final Long clientId, final Long familyDetailsId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.clientRepository.findOneWithNotFoundDetection(clientId);
            final FamilyDetail familyDetail = this.familyDetailsRepository.findByIdAndClientId(familyDetailsId, clientId);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.FAMILY_DETAILS_UPDATE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, familyDetail.isLocked()));
            this.validator.validateForUpdate(command.json());
            final Map<String, Object> changes = this.assembler.assembleUpdateForm(familyDetail, command);
            if (!CollectionUtils.isEmpty(changes)) {
                this.familyDetailsRepository.save(familyDetail);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(familyDetail.getId()) //
                    .withClientId(command.getClientId()) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    public CommandProcessingResult deleteFamilyDeatails(final Long clientId, final Long familyDetailId) {
        try {
            this.clientRepository.findOneWithNotFoundDetection(clientId);
            final FamilyDetail familyDetail = this.familyDetailsRepository.findOne(familyDetailId);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.FAMILY_DETAILS_DELETE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, familyDetail.isLocked()));
            this.familyDetailsRepository.delete(familyDetail);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(familyDetailId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @Override
    @SuppressWarnings("null")
    public void createOrUpdateFamilyDeatails(final Long clientId, final JsonCommand command) {
        this.clientRepository.findOneWithNotFoundDetection(clientId);
        final JsonElement elements = this.fromApiJsonHelper.parse(command.jsonFragment(FamilyDetailsApiConstants.familyMembersParamName));
        final JsonArray array = elements.getAsJsonArray();
        if (array != null && array.size() > 0) {
            final JsonObject createJsonObject = new JsonObject();
            final JsonArray createJsonArray = new JsonArray();
            for (int i = 0; i < array.size(); i++) {
                final JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (jsonObject.has(FamilyDetailsApiConstants.idParamName)) {
                    /**
                     * Call Update Service
                     */
                    final JsonElement element = this.fromApiJsonHelper.parse(jsonObject.toString());
                    final JsonCommand newCommand = JsonCommand.fromExistingCommand(command, element);
                    final Long id = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.idParamName, element);
                    updateFamilyDeatails(clientId, id, newCommand);
                } else {
                    final JsonElement element = this.fromApiJsonHelper.parse(jsonObject.toString());
                    createJsonArray.add(element);
                }
            }
            if (createJsonArray != null && createJsonArray.size() > 0) {
                createJsonObject.add(FamilyDetailsApiConstants.familyMembersParamName, createJsonArray);
                final JsonElement element = this.fromApiJsonHelper.parse(createJsonObject.toString());
                final JsonCommand newCommand = JsonCommand.fromExistingCommand(command, element);
                /**
                 * Call Create Service
                 */
                createFamilyDeatails(clientId, newCommand);
            }
        }
    }

	@Override
	public CommandProcessingResult deleteFamilyMemberAssociation(final Long clientId, final Long familyDetailId) {
		try {
			this.context.authenticatedUser();
			this.clientRepository.findOneWithNotFoundDetection(clientId);
			final FamilyDetail familyDetail = this.familyDetailsRepository.findOne(familyDetailId);
			familyDetail.removeFamilyMemberAssociation();
			return new CommandProcessingResultBuilder() //
					.withEntityId(familyDetailId) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		}
	}

}
