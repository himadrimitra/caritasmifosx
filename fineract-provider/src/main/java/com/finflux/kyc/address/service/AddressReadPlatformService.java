package com.finflux.kyc.address.service;

import java.util.Collection;

import com.finflux.kyc.address.data.AddressData;
import com.finflux.kyc.address.data.AddressTemplateData;

public interface AddressReadPlatformService {

    AddressTemplateData retrieveTemplate();

    AddressData retrieveOne(final String entityType, final Long entityId, final Long addressId);

    Collection<AddressData> retrieveByentityTypeAndEntityId(final String entityType, final Long entityId);
}
