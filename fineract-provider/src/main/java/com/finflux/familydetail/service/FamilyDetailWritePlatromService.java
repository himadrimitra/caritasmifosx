package com.finflux.familydetail.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface FamilyDetailWritePlatromService {

    CommandProcessingResult createFamilyDeatails(Long clientId, JsonCommand command);

    CommandProcessingResult updateFamilyDeatails(Long clientId, Long familyDetailId, JsonCommand command);

    CommandProcessingResult deleteFamilyDeatails(Long familyDetailId, Long clientId);
    
    void createOrUpdateFamilyDeatails(final Long clientId, final JsonCommand command);
    
	CommandProcessingResult deleteFamilyMemberAssociation(final Long clientId, final Long familyDetailId);
}
