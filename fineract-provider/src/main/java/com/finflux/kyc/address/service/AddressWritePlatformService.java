package com.finflux.kyc.address.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import com.finflux.kyc.address.data.AddressEntityTypeEnums;

public interface AddressWritePlatformService {

    CommandProcessingResult create(final Integer entityTypeEnum, final Long entityId, final JsonCommand command);

    CommandProcessingResult update(final Long addressId, final Long entityId, final JsonCommand command);

    CommandProcessingResult delete(final Long addressId, final Long entityId, final JsonCommand command);

    void createOrUpdateAddress(final AddressEntityTypeEnums entityType, final Long entityId, final JsonCommand command);
}
