package com.finflux.kyc.address.service;

import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.exception.AddressEntityTypeNotSupportedException;

@Component
public class AddressBusinessValidators {

    private final ClientRepositoryWrapper clientRepository;

    @Autowired
    public AddressBusinessValidators(final ClientRepositoryWrapper clientRepository) {
        this.clientRepository = clientRepository;
    }

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
        } else if (entityTypeName.equalsIgnoreCase(AddressApiConstants.enumTypeClients)) {
            /**
             * entityId should belongs to {@link Client}
             */
            this.clientRepository.findOneWithNotFoundDetection(entityId);
        } else {
            /**
             * Requested Address Entity type not supported
             */
            throw new AddressEntityTypeNotSupportedException(entityTypeName);
        }
    }
}
