package com.finflux.kyc.address.service;

import org.springframework.stereotype.Component;

import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.exception.AddressEntityTypeNotSupportedException;

@Component
public class AddressBusinessValidators {

    
    public AddressBusinessValidators() {}

    /**
     * Validate enityId matched with correspondent entityType or not
     * 
     * @param entityTypeEnum
     * @param entityId
     */
    public void validateAddressEntityIdAndEntityType(final Integer entityTypeEnum, final Long entityId) {
        final AddressEntityTypeEnums addressEntityTypeEnum = AddressEntityTypeEnums.fromInt(entityTypeEnum);
        final String entityTypeName = addressEntityTypeEnum.name();
        if (entityTypeEnum == 0) {
            throw new AddressEntityTypeNotSupportedException(entityTypeName);
        }
    }
}
