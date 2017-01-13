package com.finflux.kyc.address.service;

import java.util.Collection;

import com.finflux.kyc.address.data.AddressData;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.data.AddressTemplateData;

public interface AddressReadPlatformService {

    AddressTemplateData retrieveTemplate();

    AddressData retrieveOne(final String entityType, final Long entityId, final Long addressId);

    Collection<AddressData> retrieveAddressesByEntityTypeAndEntityId(final String entityType, final Long entityId);

    Long countOfAddressByEntityTypeAndEntityId(final AddressEntityTypeEnums entityType, final Long entityId);
}
