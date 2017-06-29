package com.finflux.pdcm.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface PostDatedChequeDetailWritePlatformService {

    CommandProcessingResult create(final Integer entityType, final Long entityId, final JsonCommand command);

    CommandProcessingResult update(final Long pdcId, final JsonCommand command);

    CommandProcessingResult delete(final Long pdcId, final JsonCommand command);

    CommandProcessingResult presentPDC(final JsonCommand command);

    CommandProcessingResult bouncedPDC(final JsonCommand command);

    CommandProcessingResult clearPDC(final JsonCommand command);

    CommandProcessingResult cancelPDC(final JsonCommand command);

    CommandProcessingResult returnPDC(final JsonCommand command);

    CommandProcessingResult undoPDC(final JsonCommand command);

}