package com.finflux.familydetail.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.familydetail.service.FamilyDetailWritePlatromService;

@Service
@CommandType(entity = "FAMILYMEMBERASSOCIATION", action = "DELETE")
public class DeleteFamilyMemberAssociationCommandHandler implements NewCommandSourceHandler {

	private final FamilyDetailWritePlatromService familyDetailWritePlatromService;

	@Autowired
	public DeleteFamilyMemberAssociationCommandHandler(
			final FamilyDetailWritePlatromService familyDetailWritePlatromService) {
		this.familyDetailWritePlatromService = familyDetailWritePlatromService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		return this.familyDetailWritePlatromService.deleteFamilyMemberAssociation(command.getClientId(), command.entityId());
	}

}
