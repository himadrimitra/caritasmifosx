package com.finflux.familydetail.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface FamilyDetailsSummaryWritePlatromService {

    CommandProcessingResult create(final Long clientId, final JsonCommand command);

    CommandProcessingResult update(final Long clientId, final Long familyDetailsSummaryId, final JsonCommand command);

    CommandProcessingResult delete(final Long familyDetailsSummaryId, final Long clientId);
}